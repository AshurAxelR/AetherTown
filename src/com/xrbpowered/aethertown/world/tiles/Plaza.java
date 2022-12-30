package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;

public class Plaza extends TileTemplate {

	public Plaza() {
		super(Street.streetColor);
	}

	public Plaza(Color minimapColor) {
		super(minimapColor);
	}

	@Override
	public void createComponents() {
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		Street.street.addInstance(new TileObjectInfo(tile));
		renderer.terrain.addWalls(tile);
	}

}
