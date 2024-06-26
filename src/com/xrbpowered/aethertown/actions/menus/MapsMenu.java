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

	public static final TileAction regionMapAction = createRegionMapAction(false);
	
	public static final TileActionMenu menu = new MapsMenu(true); 
	public static final TileActionMenu noToken = new MapsMenu(false); 
	
	private static TileAction createRegionMapAction(boolean level) {
		return new TileAction("View region map") {
			@Override
			public void onSuccess(Tile tile, boolean alt) {
				if(level)
					RegionMapDialog.show(tile.level);
				else
					RegionMapDialog.show(tile.level.info.region);
			}
		};
	}
	
	private MapsMenu(boolean token) {
		addAction(new TileAction("View map") {
			@Override
			public void onSuccess(Tile tile, boolean alt) {
				LevelMapDialog.show(tile.level, true);
			}
		});
		
		addAction(createRegionMapAction(true));
		
		addAction(new GetItemAction("Get", ItemType.map) {
			@Override
			protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
				return LevelMapItem.isSameItem(aitem, tile.level.info);
			}
			@Override
			protected Item generateItem(Tile tile, boolean alt) {
				return new LevelMapItem(tile.level.info);
			}
		});
		
		addAction(new GetItemAction("Get", ItemType.regionMap) {
			@Override
			protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
				return RegionMapItem.isSameItem(aitem, tile.level.info.region.seed);
			}
			@Override
			protected Item generateItem(Tile tile, boolean alt) {
				return new RegionMapItem(tile.level.info.region.seed);
			}
		});
		
		if(token) {
			addAction(new GetItemAction("Get", ItemType.travelToken) {
				@Override
				protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
					if(aitem.type==ItemType.travelToken) {
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

}
