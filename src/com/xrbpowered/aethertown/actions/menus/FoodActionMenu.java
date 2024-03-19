package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.DummyAction;
import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;

public class FoodActionMenu extends TileActionMenu {

	public static final TileActionMenu bar = createBarRestaurantMenu(false);
	public static final TileActionMenu restaurant = createBarRestaurantMenu(true);
	public static final TileActionMenu fastFood = createFastFoodMenu();
	
	private FoodActionMenu() {
	}
	
	private static TileActionMenu createBarRestaurantMenu(boolean restaurant) {
		FoodActionMenu food = new FoodActionMenu();
		
		food.addAction(new InspirationAction("Drink tea/coffee", 1).setDelay(15).setCost(150).setCooldown(GlobalCooldowns.drink, 1));
		
		food.addAction(new InspirationAction("Drink tea/coffee with dessert", 2) {
			@Override
			protected void applyCost(com.xrbpowered.aethertown.world.Tile tile, boolean alt) {
				super.applyCost(tile, alt);
				GlobalCooldowns.eat.pushBackH(1);
			}
		}.setDelay(20).setCost(350).setCooldown(GlobalCooldowns.drink, 2));
		
		if(restaurant) {
			food.addAction(new InspirationAction("Eat main course", 4).setDelay(40).setCost(750).setCooldown(GlobalCooldowns.eat, 3));
			food.addAction(new InspirationAction("Eat starter and main course", 5).setDelay(50).setCost(1000).setCooldown(GlobalCooldowns.eat, 4));
		}
		
		food.addAction(new DummyAction("Hang out").setDelay(20));
		return food;
	}
	
	private static TileActionMenu createFastFoodMenu() {
		FoodActionMenu food = new FoodActionMenu();
		food.addAction(new InspirationAction("Eat-in meal", 3).setDelay(30).setCost(650).setCooldown(GlobalCooldowns.eat, 2.5));
		food.addAction(new DummyAction("Takeaway meal").setEnabled(false).setDelay(10).setCost(650));
		food.addAction(new DummyAction("Buy Soda bottle").setEnabled(false).setCost(100));
		food.addAction(new DummyAction("Buy Snack").setEnabled(false).setCost(150));
		food.addAction(new DummyAction("Hang out").setDelay(20));
		return food;
	}

}
