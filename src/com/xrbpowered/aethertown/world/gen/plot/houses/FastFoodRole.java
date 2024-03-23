package com.xrbpowered.aethertown.world.gen.plot.houses;

import com.xrbpowered.aethertown.actions.HouseTileAction;
import com.xrbpowered.aethertown.world.gen.plot.houses.ArchitectureTileSet.DoorInfo;

public class FastFoodRole extends LocalShopRole {

	public FastFoodRole(String title, DoorInfo door, HouseTileAction action) {
		super(title, colorFoodSmall, action, door, true);
	}

	public FastFoodRole(String title) {
		this(title, DoorInfo.fastFood, HouseTileAction.fastFood);
	}

}
