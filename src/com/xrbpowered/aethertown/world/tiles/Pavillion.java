package com.xrbpowered.aethertown.world.tiles;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.Lamps;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;

public class Pavillion extends Plaza {

	public static final Pavillion template = new Pavillion();
	
	private static TileComponent pavillion, pavBenches;
	
	@Override
	public int getBlockY(Tile tile) {
		return tile.basey+8;
	}
	
	@Override
	public void createComponents() {
		pavillion = new TileComponent(
				ObjMeshLoader.loadObj("models/pavillion/pavillion.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.getPalette());
		pavBenches = new TileComponent(
				ObjMeshLoader.loadObj("models/pavillion/pav_benches.obj", 0, 1f, ObjectShader.vertexInfo, null),
				Bench.benchTexture);
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		TileObjectInfo info = new TileObjectInfo(tile);
		Street.street.addInstance(r, info);
		pavillion.addInstance(r, info);
		pavBenches.addInstance(r, info);
		r.pointLights.setLight(tile, 0, 4, 0, 4f);
		r.blockLighting.addLight(IllumLayer.alwaysOn, tile, tile.basey+4, Lamps.lampLightColor, 0.3f, true);
	}


}
