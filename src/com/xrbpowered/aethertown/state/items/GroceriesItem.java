package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.player;

import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.Inventory;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class GroceriesItem extends Item {

	public GroceriesItem(double time) {
		super(ItemType.groceries, time);
	}

	public GroceriesItem() {
		super(ItemType.groceries, WorldTime.time);
	}

	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return "<p>Use to cook meal at home if you have kitchenware.<br>"+
				"One groceries item produces two home-cooked meals.</p>";
	}
	
	public static Inventory findGroceries(HomeData home) {
		if(player.backpack.has(ItemType.groceries))
			return player.backpack;
		for(Inventory inv : home.storage) {
			if(inv.has(ItemType.groceries))
				return inv;
		}
		return null;
	}

	public static boolean hasGroceries(HomeData home) {
		return findGroceries(home)!=null;
	}
}
