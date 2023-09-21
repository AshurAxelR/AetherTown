package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;

public class Monument extends Plaza {

	private static final Color statueColor = new Color(0xf2f4ea); // new Color(0x75abae); // new Color(0x353433);
	
	public static final Monument template = new Monument();
	
	public static TileComponent pillar, statue;

	protected boolean canGenerate(Token t) {
		return t.level.isInside(t.x, t.z) && t.isFree() && t.level.overlapsHeight(t.x, t.y, t.z, 4);
	}
	
	@Override
	public void createComponents() {
		pillar = new TileComponent(
				ObjMeshLoader.loadObj("models/monument/pillar.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0xd5ceba));
		statue = new TileComponent(
				ObjMeshLoader.loadObj("models/monument/angel.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(statueColor));
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		super.createGeometry(tile, r);
		TileObjectInfo info = new TileObjectInfo(tile);
		pillar.addInstance(r, info);
		statue.addInstance(r, new TileObjectInfo(tile, 0, 10.3f, 0));
		r.pointLights.setLight(tile, -0.35f*tile.d.dx, 9f, -0.35f*tile.d.dz, 5f);
		r.blockLighting.addLight(IllumLayer.alwaysOn, tile, tile.basey+9, Street.lampLightColor, 0.4f, false);
	}
	
}
