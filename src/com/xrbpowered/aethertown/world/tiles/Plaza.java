package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.gen.Fences;

public class Plaza extends TileTemplate {

	public static final Color plazaColor = new Color(0xd5ceba);
	
	public static final Plaza template = new Plaza();
	public static final Plaza tunnelSideTemplate = new Plaza(false);
	
	public final boolean fences;
	
	public Plaza(boolean fences) {
		this.fences = fences;
	}
	
	public Plaza() {
		this(true);
	}
	
	@Override
	public void createComponents() {
	}

	@Override
	public void decorateTile(Tile tile, Random random) {
		if(fences)
			Fences.addFences(tile);
	}
	
	@Override
	public boolean postDecorateTile(Tile tile, Random random) {
		if(fences)
			return Fences.fillFenceGaps(tile);
		else
			return false;
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		r.terrain.addFlatTile(TerrainMaterial.plaza, tile);
		r.terrain.addWalls(tile);
		if(fences)
			Fences.createFences(r, tile);
	}

}
