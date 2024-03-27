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
	
	public static int findGroceries(Inventory inv) {
		for(int i=0; i<inv.size; i++) {
			Item aitem = inv.get(i);
			if(aitem==null)
				break;
			if(aitem.type==ItemType.groceries)
				return i;
		}
		return -1;
	}

	public static Inventory findGroceries(HomeData home) {
		if(findGroceries(player.backpack)>=0)
			return player.backpack;
		for(Inventory inv : home.storage) {
			if(findGroceries(inv)>=0)
				return inv;
		}
		return null;
	}

	public static boolean hasGroceries(HomeData home) {
		return findGroceries(home)!=null;
	}
}
