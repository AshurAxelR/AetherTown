package com.xrbpowered.aethertown.world.gen;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.TerrainChunkBuilder;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Hill.HillTile;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public abstract class Fences {

	public static enum FenceType {
		none,
		handrail,
		stepsOut,
		retainWall
	}
	
	public static TileComponent handrailPole, steps, retWall;
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
		retWall = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/ret_wall.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(TerrainBuilder.wallColor));
	}
	
	public static FenceType getFenceType(Tile tile, Dir d, int dy0, int dy1, int h) {
		Corner cl = d.leftCorner();
		Corner cr = d.rightCorner();
		Tile adj = tile.getAdj(d);
		if(adj==null)
			return FenceType.none;
		if(adj.t==Hill.template || (adj instanceof StreetTile && ((StreetTile) adj).bridge && adj.d!=d && adj.d!=d.flip())) {
			int[] yloc = tile.level.h.yloc(adj.x, adj.z);
			int miny = MathUtils.min(yloc);
			if(adj.t==Hill.template && ((HillTile) adj).maxDelta>=TerrainChunkBuilder.cliffDelta && tile.basey-h<=miny)
				return FenceType.retainWall;
			if(tile.basey>=miny+(adj.t==Hill.template ? 8 : 4))
				return FenceType.handrail;
		}
		int hl = tile.t.getFenceY(tile, cl) - (adj.t.getFenceY(adj, cl.flipOver(d))+dy0);
		int hr = tile.t.getFenceY(tile, cr) - (adj.t.getFenceY(adj, cr.flipOver(d))+dy1);
		if(hl>1 || hr>1)
			return FenceType.handrail;
		if(hl>0 || hr>0) {
			if(adj.basey>=tile.basey-1 || (tile.t instanceof StreetSlope))
				return FenceType.stepsOut;
			else
				return FenceType.handrail;
		}
		return FenceType.none;
	}

	public static FenceType getFenceType(Tile tile, Dir d) {
		return getFenceType(tile, d, 0, 0, 0);
	}

	public static void addFences(Tile tile) {
		for(Dir d : Dir.values()) {
			tile.setFence(d, getFenceType(tile, d));
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
				case retainWall:
					retWall.addInstance(r, new TileObjectInfo(tile).rotate(d));
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
			Tile adjs = tile.getAdj(ds);
			if(adjs!=null) {
				pole = adjs.basey>=tile.basey && (adjs.getFence(d)==FenceType.handrail || adjs.getFence(d)==FenceType.retainWall);
				if(!pole) {
					adjs = adjs.getAdj(d);
					pole = adjs!=null && adjs.basey>=tile.basey && (adjs.getFence(ds.flip())==FenceType.handrail || adjs.getFence(ds.flip())==FenceType.retainWall);
				}
			}
		}
		return pole;
	}

	private static boolean checkPole(Tile tile, Corner c) {
		int y = tile.t.getFenceY(tile, c);
		int max = y;
		for(Corner ca : Corner.values()) {
			int x = tile.x+c.tx+ca.tx+1;
			int z = tile.z+c.tz+ca.tz+1;
			if(tile.level.isInside(x, z)) {
				Tile adj = tile.level.map[x][z];
				if(adj!=null) {
					int ay = adj.t.getFenceY(adj, ca.flip());
					if(ay>max)
						max = ay;
				}
			}
		}
		return max>y;
	}

	public static boolean fillFenceGaps(Tile tile) {
		boolean upd = false;
		for(Dir d : Dir.values()) {
			if(d==tile.d.flip() || tile.getFence(d)!=FenceType.none)
				continue;
			Tile adj = tile.getAdj(d);
			if(adj==null || (adj.d==d || tile.basey==adj.basey || (adj.t instanceof StreetSlope)) && adj.t!=Hill.template)
				continue;
			boolean polel = checkPole(tile, d, d.ccw());
			boolean poler = checkPole(tile, d, d.cw());
			if((polel || poler) && adj.t!=HouseT.template && adj.getFence(d.flip())==FenceType.none) {
				polel |= checkPole(tile, d.rightCorner()); // FIXME dir.corner is wrong?
				poler |= checkPole(tile, d.leftCorner());
			}
			if(polel && poler) {
				tile.setFence(d, FenceType.handrail);
				upd = true;
			}
		}
		return upd;
	}
	
}
