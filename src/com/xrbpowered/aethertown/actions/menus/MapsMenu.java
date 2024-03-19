package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.GetItemAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.ItemType;
import com.xrbpowered.aethertown.state.items.LevelMapItem;
import com.xrbpowered.aethertown.state.items.RegionMapItem;
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
		
		addAction(new GetItemAction("Get", ItemType.map) {
			@Override
			protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
				if(aitem instanceof LevelMapItem) {
					LevelMapItem item = (LevelMapItem) aitem;
					if(item.level.isLevel(tile.level.info))
						return true;
				}
				return false;
			}
			@Override
			protected Item generateItem(Tile tile, boolean alt) {
				return new LevelMapItem(tile.level.info);
			}
		});
		
		addAction(new GetItemAction("Get", ItemType.regionMap) {
			@Override
			protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
				if(aitem instanceof RegionMapItem) {
					RegionMapItem item = (RegionMapItem) aitem;
					if(item.regionSeed==tile.level.info.region.seed)
						return true;
				}
				return false;
			}
			@Override
			protected Item generateItem(Tile tile, boolean alt) {
				return new RegionMapItem(tile.level.info.region.seed);
			}
		});
		
		addAction(new GetItemAction("Get", ItemType.travelToken) {
			@Override
			protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
				if(aitem instanceof TravelTokenItem) {
					TravelTokenItem item = (TravelTokenItem) aitem;
					if(item.destination.isLevel(tile.level.info))
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
