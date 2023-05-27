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
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public abstract class FenceGenerator {

	public static enum FenceType {
		none,
		handrail
	}
	
	public static TileComponent handrailPole;
	private static TileComponent handrail;

	public static void createComponents() {
		handrail = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/handrail.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/fences/handrail.png", false, true, false));
		handrailPole = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/handrail_pole.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0xd5ceba));
	}
	
	public static boolean needsHandrail(Tile tile, Dir d, int dy0, int dy1) {
		Corner c0 = d.leftCorner();
		Corner c1 = d.rightCorner();
		Tile adj = tile.getAdj(d);
		if(adj==null)
			return false;
		if(adj.t==Hill.template || (adj instanceof StreetTile && ((StreetTile) adj).bridge && adj.d!=d && adj.d!=d.flip())) {
			int[] yloc = tile.level.h.yloc(adj.x, adj.z);
			int miny = MathUtils.min(yloc);
			if(tile.basey>=miny+8)
				return true;
		}
		return tile.t.getFenceY(tile, c0)>adj.t.getFenceY(adj, c0.flipOver(d))+dy0 ||
				tile.t.getFenceY(tile, c1)>adj.t.getFenceY(adj, c1.flipOver(d))+dy1;
	}

	public static boolean needsHandrail(Tile tile, Dir d) {
		return needsHandrail(tile, d, 0, 0);
	}

	public static void addHandrails(Tile tile) {
		Dir dsrc = tile.d.flip();
		for(Dir d : Dir.values()) {
			if(d!=dsrc) {
				if(needsHandrail(tile, d))
					tile.setFence(d, FenceType.handrail);
			}
			else {
				Tile src = tile.getAdj(d);
				if(src!=null && needsHandrail(tile, d, 1, 1)) {
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
				default:
					break;
			}
		}
	}
	
}
