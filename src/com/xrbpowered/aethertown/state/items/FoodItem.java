package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.player;

import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.Inventory;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class FoodItem extends Item {

	public enum FoodItemType {
		water(new InspirationAction("Bottle of water", 0).setDelay(5).setCooldown(GlobalCooldowns.drink.hours(0.5))),
		snack(new InspirationAction("Snack", 0).setDelay(10).setCooldown(GlobalCooldowns.eat.hours(0.5))),
		homeCooked(new InspirationAction("Home-cooked meal", 4).setDelay(25).setCooldown(GlobalCooldowns.eat.hours(3))),
		
		takeawaySandwich(takeaway("sandwich")),
		takeawayBurrito(takeaway("burrito")),
		takeawayBurger(takeaway("burger")),
		takeawayPizza(takeaway("pizza")),
		takeawayChinese(takeaway("noodles")),
		takeawayChicken(takeaway("chicken meal"));
		
		public final TileAction action;
		
		private FoodItemType(TileAction action) {
			this.action = action;
		}
		
		public String getName() {
			return action.name;
		}
	}
	
	private static TileAction takeaway(String name) {
		return new InspirationAction("Takeaway "+name, 3).setDelay(20).setCooldown(GlobalCooldowns.eat.hours(2.5));
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
	
	public static boolean isSameItem(Item aitem, FoodItemType food) {
		if(aitem.type==ItemType.foodItem) {
			FoodItem item = (FoodItem) aitem;
			if(item.food==food)
				return true;
		}
		return false;
	}
	
	public static int findFoodType(Inventory inv, FoodItemType food) {
		for(int i=0; i<inv.size; i++) {
			Item aitem = inv.get(i);
			if(aitem==null)
				break;
			if(isSameItem(aitem, food))
				return i;
		}
		return -1;
	}

	public static Inventory findFoodType(HomeData home, FoodItemType food) {
		if(findFoodType(player.backpack, food)>=0)
			return player.backpack;
		for(Inventory inv : home.storage) {
			if(findFoodType(inv, food)>=0)
				return inv;
		}
		return null;
	}
	
	public static boolean hasFoodType(HomeData home, FoodItemType food) {
		return findFoodType(home, food)!=null;
	}
	
	public static int countFoodType(Inventory inv, FoodItemType food) {
		int count = 0;
		for(int i=0; i<inv.size; i++) {
			Item aitem = inv.get(i);
			if(aitem==null)
				break;
			if(isSameItem(aitem, food))
				count++;
		}
		return count;
	}

	public static int countFoodType(HomeData home, FoodItemType food) {
		int count = countFoodType(player.backpack, food);
		for(Inventory inv : home.storage)
			count += countFoodType(inv, food);
		return count;
	}
}
