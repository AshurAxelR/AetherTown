package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.world.Tile;

public abstract class TileAction {

	public abstract String getName();
	public abstract void performAt(Tile tile);

}
