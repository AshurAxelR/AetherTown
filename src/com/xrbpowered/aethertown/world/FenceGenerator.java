package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public abstract class FenceGenerator {

	public static enum FenceType {
		none,
		handrail,
		stepsOut
	}
	
	public static TileComponent handrailPole, steps;
	private static TileComponent handrail, stepsCorner;

	public static void createComponents() {
		handrail = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/handrail.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/fences/handrail.png", false, true, false));
		handrailPole = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/handrail_pole.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0xd5ceba));
		steps = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/steps_out.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(Street.streetColor));
		stepsCorner = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/steps_out_c.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(Street.streetColor));
	}
	
	public static FenceType needsHandrail(Tile tile, Dir d, int dy0, int dy1) {
		Corner c0 = d.leftCorner();
		Corner c1 = d.rightCorner();
		Tile adj = tile.getAdj(d);
		if(adj==null)
			return FenceType.none;
		if(adj.t==Hill.template || (adj instanceof StreetTile && ((StreetTile) adj).bridge && adj.d!=d && adj.d!=d.flip())) {
			int[] yloc = tile.level.h.yloc(adj.x, adj.z);
			int miny = MathUtils.min(yloc);
			if(tile.basey>=miny+(adj.t==Hill.template ? 8 : 4))
				return FenceType.handrail;
		}
		int h0 = tile.t.getFenceY(tile, c0) - (adj.t.getFenceY(adj, c0.flipOver(d))+dy0);
		int h1 = tile.t.getFenceY(tile, c1) - (adj.t.getFenceY(adj, c1.flipOver(d))+dy1);
		if(h0>1 || h1>1)
			return FenceType.handrail;
		if(h0>0 || h1>0) {
			if(adj.basey>=tile.basey-1)
				return FenceType.stepsOut;
			else
				return FenceType.handrail;
		}
		return FenceType.none;
	}

	public static FenceType needsHandrail(Tile tile, Dir d) {
		return needsHandrail(tile, d, 0, 0);
	}

	public static void addHandrails(Tile tile) {
		for(Dir d : Dir.values()) {
			tile.setFence(d, needsHandrail(tile, d));
		}
	}

	public static void createHandrailPoles(LevelRenderer r, Tile tile, Dir d, int dy0, int dy1) {
		Corner c0 = d.leftCorner();
		handrailPole.addInstance(r, new TileObjectInfo(tile, 0.5f*c0.dx, dy0, 0.5f*c0.dz));
		Corner c1 = d.rightCorner();
		handrailPole.addInstance(r, new TileObjectInfo(tile, 0.5f*c1.dx, dy1, 0.5f*c1.dz));
	}
	
	public static void createHandrail(LevelRenderer r, Tile tile, Dir d) {
		handrail.addInstance(r, new TileObjectInfo(tile).rotate(d));
		createHandrailPoles(r, tile, d, 0, 0);
	}
	
	public static void createFences(LevelRenderer r, Tile tile) {
		for(Dir d : Dir.values()) {
			switch(tile.getFence(d)) {
				case handrail:
					createHandrail(r, tile, d);
					break;
				case stepsOut:
					steps.addInstance(r, new TileObjectInfo(tile).rotate(d));
					if(tile.getFence(d.cw())==FenceType.stepsOut)
						stepsCorner.addInstance(r, new TileObjectInfo(tile).rotate(d));
					break;
				default:
					break;
			}
		}
	}
	
	public static float getFenceYOut(int basey, float sout) {
		float w = 0.75f/Tile.size;
		if(sout<w) {
			float y0 = Tile.ysize*basey;
			float y1 = Tile.ysize*(basey-1);
			return MathUtils.lerp(y0, y1, sout/w);
		}
		else {
			return Float.NEGATIVE_INFINITY;
		}
	}
	
	private static boolean checkPole(Tile tile, Dir d, Dir ds) {
		boolean pole = tile.getFence(ds)==FenceType.handrail;
		if(!pole) {
			Tile adjl = tile.getAdj(ds);
			if(adjl!=null) {
			pole = adjl.basey>=tile.basey && adjl.getFence(d)==FenceType.handrail;
				if(!pole) {
					adjl = adjl.getAdj(d);
					pole = adjl!=null && adjl.basey>=tile.basey && adjl.getFence(ds.flip())==FenceType.handrail;
				}
			}
		}
		// TODO check adj wall, including house
		return pole;
	}
	
	public static boolean fillFenceGaps(Tile tile) {
		boolean upd = false;
		for(Dir d : Dir.values()) {
			if(d==tile.d.flip() || tile.getFence(d)!=FenceType.none)
				continue;
			Tile adj = tile.getAdj(d);
			if(adj!=null && (adj.d==d || tile.basey==adj.basey || (adj.t instanceof StreetSlope)) && adj.t!=Hill.template)
				continue;
			boolean polel = checkPole(tile, d, d.ccw());
			boolean poler = checkPole(tile, d, d.cw());
			if(polel && poler) {
				tile.setFence(d, FenceType.handrail);
				upd = true;
			}
		}
		return upd;
	}
	
}
