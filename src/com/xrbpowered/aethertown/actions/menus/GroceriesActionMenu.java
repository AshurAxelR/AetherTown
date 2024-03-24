package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.BuyFoodAction;
import com.xrbpowered.aethertown.actions.DummyAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.items.FoodItem.FoodItemType;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;

public class GroceriesActionMenu extends TileActionMenu {
	
	public static final TileAction buyWaterBottleAction = new BuyFoodAction(FoodItemType.water, 50);
	public static final TileAction buySnackAction = new BuyFoodAction(FoodItemType.snack, 150);

	public static final TileActionMenu groceries = createGroceriesMenu();
	
	private GroceriesActionMenu() {
	}

	private static TileActionMenu createGroceriesMenu() {
		TileActionMenu shop = new TileActionMenu();
		shop.addAction(new DummyAction("Buy Groceries").setEnabled(false).setCost(500).setDelay(5));
		shop.addAction(GroceriesActionMenu.buyWaterBottleAction);
		shop.addAction(GroceriesActionMenu.buySnackAction);
		return shop;
	}

}
