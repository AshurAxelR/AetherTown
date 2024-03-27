package com.xrbpowered.aethertown.world.gen.plot.houses;

import com.xrbpowered.aethertown.actions.HouseTileAction;
import com.xrbpowered.aethertown.actions.menus.FoodActionMenu;
import com.xrbpowered.aethertown.state.items.FoodItem.FoodItemType;
import com.xrbpowered.aethertown.world.gen.plot.houses.ArchitectureTileSet.DoorInfo;

public class FastFoodRole extends LocalShopRole {

	public FastFoodRole(String title, DoorInfo door, HouseTileAction action) {
		super(title, colorFoodSmall, action, door, true);
	}

	public FastFoodRole(String title, FoodItemType takeaway) {
		this(title, DoorInfo.fastFood, new HouseTileAction(FoodActionMenu.createFastFoodMenu(takeaway)));
	}

}
