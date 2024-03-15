package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.ui.dialogs.LevelMapDialog;
import com.xrbpowered.aethertown.ui.dialogs.RegionMapDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class MapsMenu extends TileActionMenu {

	public static final TileActionMenu menu = new MapsMenu(); 
	
	private MapsMenu() {
		addAction(new TileAction("View map") {
			@Override
			public void performAt(Tile tile) {
				LevelMapDialog.show(tile.level, true);
			}
		});
		
		addAction(new TileAction("View region map") {
			@Override
			public void performAt(Tile tile) {
				RegionMapDialog.show(tile.level);
			}
		});
		
		addAction(new TileAction("Get map") {
			@Override
			public void performAt(Tile tile) {
				// TODO add item
			}
		});
		
		addAction(new TileAction("Get region map") {
			@Override
			public void performAt(Tile tile) {
				// TODO add item
			}
		});
		
		addAction(new TileAction("Get level token") {
			@Override
			public void performAt(Tile tile) {
				// TODO add item
			}
		});
	}

}
