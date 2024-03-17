package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.GetItemAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.ItemType;
import com.xrbpowered.aethertown.state.items.TravelTokenItem;
import com.xrbpowered.aethertown.ui.dialogs.LevelMapDialog;
import com.xrbpowered.aethertown.ui.dialogs.RegionMapDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class MapsMenu extends TileActionMenu {

	public static final TileActionMenu menu = new MapsMenu(); 
	
	private MapsMenu() {
		addAction(new TileAction("View map") {
			@Override
			public void onSuccess(Tile tile, boolean alt) {
				LevelMapDialog.show(tile.level, true);
			}
		});
		
		addAction(new TileAction("View region map") {
			@Override
			public void onSuccess(Tile tile, boolean alt) {
				RegionMapDialog.show(tile.level);
			}
		});
		
		addAction(new TileAction("Get map") {
			@Override
			public void onSuccess(Tile tile, boolean alt) {
				// TODO add item
			}
		});
		
		addAction(new TileAction("Get region map") {
			@Override
			public void onSuccess(Tile tile, boolean alt) {
				// TODO add item
			}
		});
		
		addAction(new GetItemAction("Get", ItemType.travelToken) {
			@Override
			protected boolean isSameItem(Item item, Tile tile, boolean alt) {
				if(item instanceof TravelTokenItem) {
					TravelTokenItem tt = (TravelTokenItem) item;
					if(tt.destination.isLevel(tile.level.info))
						return true;
				}
				return false;
			}
			@Override
			protected Item generateItem(Tile tile, boolean alt) {
				return new TravelTokenItem(tile.level.info);
			}
		});
	}

}
