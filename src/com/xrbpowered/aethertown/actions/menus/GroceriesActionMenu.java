package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.BuyFoodAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.items.FoodItem.FoodItemType;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;

public class GroceriesActionMenu extends TileActionMenu {
	
	public static final TileAction buyWaterBottleAction = new BuyFoodAction(FoodItemType.waterBottle, 50);
	public static final TileAction buySnackAction = new BuyFoodAction(FoodItemType.snack, 150);
	
	private GroceriesActionMenu() {
	}

}
