package com.xrbpowered.aethertown.world.tiles;

import com.xrbpowered.aethertown.actions.ObserverAction;
import com.xrbpowered.aethertown.actions.ObserverPointProvider;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.aethertown.world.gen.Lamps;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;

public class Pavillion extends Plaza implements ObserverPointProvider {

	public static final Pavillion template = new Pavillion();
	
	private static TileComponent pavillion, pavBenches;
	
	public Pavillion() {
		super(false);
	}
	
	@Override
	public TileAction getTileAction(Tile tile) {
		return ObserverAction.sit;
	}

	@Override
	public int getBlockY(Tile tile) {
		return tile.basey+8;
	}
	
	@Override
	public float getYIn(Tile tile, float sx, float sz, float prevy) {
		return Tile.ysize*(tile.basey+1);
	}
	
	@Override
	public float getYOut(Tile tile, Dir d, float sout, float sx, float sz, float prevy) {
		return Fences.getFenceYOut(tile.basey+1, sout);
	}
	
	@Override
	public void createComponents() {
		pavillion = new TileComponent(
				ObjMeshLoader.loadObj("models/pavillion/pavillion.obj", 0, 1f, ObjectShader.vertexInfo),
				TexColor.getPalette());
		pavBenches = new TileComponent(
				ObjMeshLoader.loadObj("models/pavillion/pav_benches.obj", 0, 1f, ObjectShader.vertexInfo),
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

	@Override
	public TileObjectInfo[][] getObserverPoints(Tile tile, boolean alt) {
		TileObjectInfo[][] points = new TileObjectInfo[1][4];
		for(int i=0; i<4; i++) {
			float a = (i+0.5f) * (float)Math.PI / 2f;
			float dx = 0.45f * (float)Math.cos(a);
			float dz = 0.45f * (float)Math.sin(a);
			points[0][i] = new TileObjectInfo(tile, dx, Bench.benchSitY+1, dz)
					.rotate(a-(float)Math.PI / 2f);
		}
		return points;
	}

}
