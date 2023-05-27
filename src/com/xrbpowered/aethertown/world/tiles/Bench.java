package com.xrbpowered.aethertown.world.tiles;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.FenceGenerator;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class Bench extends Plaza {

	public static final Bench templatePlaza = new Bench(true);
	public static final Bench templatePark = new Bench(false);
	
	public static TileComponent bench;

	public final boolean plaza;
	
	public Bench(boolean plaza) {
		this.plaza = plaza;
	}
	
	@Override
	public void createComponents() {
		bench = new TileComponent(
				ObjMeshLoader.loadObj("models/bench/bench.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/bench/bench.png", false, true, false)).setCulling(false);
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		float dout;
		if(plaza) {
			super.createGeometry(tile, r);
			dout = 0.25f;
		}
		else {
			r.terrain.addWalls(tile);
			r.terrain.addFlatTile(Park.grassColor.color(), tile);
			FenceGenerator.createFences(r, tile);
			dout = -0.25f;
		}
		bench.addInstance(r, new TileObjectInfo(tile, dout, 0));
	}
}
