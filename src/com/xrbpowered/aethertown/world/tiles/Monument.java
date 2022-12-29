package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;

public class Monument extends TileTemplate {

	private static final Color statueColor = new Color(0x75abae); // new Color(0x353433);
	
	public static TileComponent pillar, statue;

	public Monument() {
		super(statueColor);
	}
	
	private static boolean isFlex(Tile tile) {
		return tile.data!=null && (Boolean)tile.data;
	}
	
	@Override
	public float gety(Tile tile, float sx, float sz) {
		if(isFlex(tile))
			return tile.level.h.gety(tile.x, tile.z, sx, sz);
		else
			return super.gety(tile, sx, sz);
	}
	
	@Override
	public void createComponents() {
		pillar = new TileComponent(
				ObjMeshLoader.loadObj("pillar.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0xd5ceba));
		statue = new TileComponent(
				ObjMeshLoader.loadObj("statue_cube.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(statueColor));
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		TileObjectInfo info = new TileObjectInfo(tile);
		Street.street.addInstance(info);
		pillar.addInstance(info);
		statue.addInstance(info);
		renderer.terrain.addWalls(tile);
		renderer.pointLights.setLight(tile, -0.35f*tile.d.dx, 9f, -0.35f*tile.d.dz, 6f);
	}
	
}
