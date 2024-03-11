package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.showToast;

import com.xrbpowered.aethertown.world.Tile;

public class ThrowCoinAction extends TileAction {

	public static final ThrowCoinAction action = new ThrowCoinAction();

	@Override
	public String getName() {
		return "Throw coin";
	}

	@Override
	public void performAt(Tile tile) {
		showToast("+1 inspiration");
	}

}
