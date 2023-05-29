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
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public abstract class FenceGenerator {

	public static enum FenceType {
		none,
		handrail,
		stairsOut
	}
	
	public static TileComponent handrailPole;
	private static TileComponent handrail;
	private static TileComponent stairs;

	public static void createComponents() {
		handrail = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/handrail.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/fences/handrail.png", false, true, false));
		handrailPole = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/handrail_pole.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0xd5ceba));
		stairs = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/stairs_out.obj", 0, 1f, ObjectShader.vertexInfo, null),
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
				return FenceType.stairsOut;
			else
				return FenceType.handrail;
		}
		return FenceType.none;
	}

	public static FenceType needsHandrail(Tile tile, Dir d) {
		return needsHandrail(tile, d, 0, 0);
	}

	public static void addHandrails(Tile tile) {
		Dir dsrc = tile.d.flip();
		for(Dir d : Dir.values()) {
			if(d!=dsrc) {
				tile.setFence(d, needsHandrail(tile, d));
			}
			else {
				Tile src = tile.getAdj(d);
				if(src!=null && needsHandrail(tile, d, 1, 1)==FenceType.handrail) {
					tile.setFence(d, FenceType.handrail);
				}
				else {
					// TODO add entry stairs in place of handrails
				}
			}
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
				case stairsOut:
					stairs.addInstance(r, new TileObjectInfo(tile).rotate(d));
					break;
				default:
					break;
			}
		}
	}
	
}
