package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.ui.dialogs.LevelMapDialog;
import com.xrbpowered.aethertown.world.Tile;

public class NavBoxAction extends TileAction {

	public static final NavBoxAction action = new NavBoxAction();

	public NavBoxAction() {
		super("View map");
	}

	@Override
	public void onSuccess(Tile tile, boolean alt) {
		LevelMapDialog.show(tile.level, false);
	}

}
