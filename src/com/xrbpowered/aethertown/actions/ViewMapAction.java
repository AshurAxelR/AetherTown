package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.ui.dialogs.LevelMapDialog;
import com.xrbpowered.aethertown.world.Tile;

public class ViewMapAction extends TileAction {

	public static final ViewMapAction action = new ViewMapAction();

	@Override
	public String getName() {
		return "View map";
	}

	@Override
	public void performAt(Tile tile) {
		LevelMapDialog.show(tile.level, false);
	}

}
