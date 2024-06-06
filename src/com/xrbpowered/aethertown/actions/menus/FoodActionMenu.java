package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.BuyFoodAction;
import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.actions.WaitAction;
import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.state.items.FoodItem.FoodItemType;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class FoodActionMenu extends TileActionMenu {

	public static final TileAction drinkAction = new InspirationAction("Drink tea/coffee", 2).setDelay(15).setCost(150).setCooldown(GlobalCooldowns.drink.hours(1));
	public static final TileAction freeDrinkAction = new InspirationAction("Drink tea/coffee", 2).setDelay(15).setCooldown(GlobalCooldowns.drink.hours(1));
	
	public static final TileAction dessertAction = new InspirationAction("Drink tea/coffee with dessert", 4) {
		@Override
		public void applyCost(Tile tile, boolean alt) {
			super.applyCost(tile, alt);
			GlobalCooldowns.eat.pushBackH(1);
		}
	}.setDelay(20).setCost(400).setCooldown(GlobalCooldowns.drink.hours(2));
	
	public static final TileAction hangOutAction = new WaitAction("Hang out", 20);

	public static final TileActionMenu bar = createBarRestaurantMenu(false);
	public static final TileActionMenu restaurant = createBarRestaurantMenu(true);
	public static final TileActionMenu cafeteria = createFastFoodMenu(null);

	private FoodActionMenu() {
	}
	
	private static TileActionMenu createBarRestaurantMenu(boolean restaurant) {
		FoodActionMenu food = new FoodActionMenu();
		food.addAction(drinkAction);
		food.addAction(dessertAction);
		
		if(restaurant) {
			food.addAction(new InspirationAction("Eat main course", 8).setDelay(40).setCost(750).setCooldown(GlobalCooldowns.eat.hours(3)));
			food.addAction(new InspirationAction("Eat starter and main course", 10).setDelay(50).setCost(1000).setCooldown(GlobalCooldowns.eat.hours(4)));
		}
		
		food.addAction(hangOutAction);
		return food;
	}
	
	public static TileActionMenu createFastFoodMenu(FoodItemType takeaway) {
		FoodActionMenu food = new FoodActionMenu();
		if(takeaway==null) {
			food.addAction(drinkAction);
			food.addAction(dessertAction);
		}
		else {
			food.addAction(new InspirationAction("Eat-in meal", 6).setDelay(30).setCost(550).setCooldown(GlobalCooldowns.eat.hours(2.5)));
			food.addAction(new BuyFoodAction(takeaway, 550).setDelay(10));
		}
		food.addAction(GroceriesActionMenu.buyWaterBottleAction);
		food.addAction(GroceriesActionMenu.buySnackAction);
		food.addAction(hangOutAction);
		return food;
	}

}
