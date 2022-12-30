package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;

public class Monument extends Plaza {

	private static final Color statueColor = new Color(0x75abae); // new Color(0x353433);
	
	public static TileComponent pillar, statue;

	public Monument() {
		super(statueColor);
	}

	protected boolean canGenerate(Token t) {
		return t.level.isInside(t.x, t.z) && t.isFree() && t.level.overlapsHeight(t.x, t.y, t.z, 4);
	}
	
	@Override
	public void createComponents() {
		pillar = new TileComponent(
				ObjMeshLoader.loadObj("models/monument/pillar.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0xd5ceba));
		statue = new TileComponent(
				ObjMeshLoader.loadObj("models/monument/statue_cube.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(statueColor));
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		super.createGeometry(tile, renderer, random);
		TileObjectInfo info = new TileObjectInfo(tile);
		pillar.addInstance(info);
		statue.addInstance(info);
		renderer.pointLights.setLight(tile, -0.35f*tile.d.dx, 9f, -0.35f*tile.d.dz, 6f);
	}
	
}
