package com.xrbpowered.aethertown.world.tiles;

import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;

public class Fountain extends Plaza {

	public static final Fountain template = new Fountain();
	
	private static TileComponent fountain;
	
	@Override
	public TileAction getTileAction(Tile tile) {
		return InspirationAction.throwCoin;
	}
	
	@Override
	public int getBlockY(Tile tile) {
		return tile.basey+3;
	}
	
	@Override
	public void createComponents() {
		fountain = new TileComponent(
				ObjMeshLoader.loadObj("models/monument/fountain.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.getPalette());
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		TileObjectInfo info = new TileObjectInfo(tile);
		Street.street.addInstance(r, info);
		fountain.addInstance(r, info);
		r.terrain.addWalls(tile);
		Fences.createFences(r, tile);
	}


}
