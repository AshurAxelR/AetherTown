package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;

public interface ObserverPointProvider {

	public TileObjectInfo[][] getObserverPoints(Tile tile, boolean alt);
	
}
