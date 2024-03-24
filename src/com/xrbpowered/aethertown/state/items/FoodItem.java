package com.xrbpowered.aethertown.state.items;

import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class FoodItem extends Item {

	public enum FoodItemType {
		waterBottle(new InspirationAction("Water bottle", 0).setDelay(5).setCooldown(GlobalCooldowns.drink.hours(0.5))),
		snack(new InspirationAction("Snack", 0).setDelay(10).setCooldown(GlobalCooldowns.eat.hours(0.5))),
		takeawayMeal(new InspirationAction("Takeaway meal", 3).setDelay(20).setCooldown(GlobalCooldowns.eat.hours(2.5))),
		homeCooked(new InspirationAction("Home-cooked meal", 4).setDelay(25).setCooldown(GlobalCooldowns.eat.hours(3)));
		
		public final TileAction action;
		
		private FoodItemType(TileAction action) {
			this.action = action;
		}
		
		public String getName() {
			return action.name;
		}
	}
	
	public final FoodItemType food;
	
	public FoodItem(FoodItemType food, double time) {
		super(ItemType.foodItem, time);
		this.food = food;
	}

	public FoodItem(FoodItemType food) {
		this(food, WorldTime.time);
	}

	@Override
	public boolean isConsumable() {
		return true;
	}
	
	@Override
	public boolean isUseEnabled(Tile tile, boolean alt) {
		return food.action.isEnabled(tile, alt);
	}

	@Override
	public boolean useItem(Tile tile, boolean alt) {
		return food.action.performAt(tile, alt);
	}
	
	@Override
	public String getName() {
		return food.getName();
	}
	
	@Override
	public String getUseActionName() {
		return food.action.getCooldown().cooldown==GlobalCooldowns.drink ? "DRINK" : "EAT";
	}
	
	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return "<p>Consumable food item.<br>"+food.action.getCostInfo(tile, alt)+"</p>";
	}
	
	@Override
	public int compareTo(Item o) {
		int res = type.compareTo(o.type);
		if(res==0)
			res = -food.compareTo(((FoodItem) o).food);
		if(res==0)
			res = Double.compare(timestamp, o.timestamp);
		return res;
	}

}
