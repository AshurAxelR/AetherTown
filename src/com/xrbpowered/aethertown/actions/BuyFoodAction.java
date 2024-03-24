package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.state.items.FoodItem;
import com.xrbpowered.aethertown.state.items.FoodItem.FoodItemType;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.world.Tile;

public class BuyFoodAction extends GetItemAction {

	public final FoodItemType food;
	
	public BuyFoodAction(FoodItemType food, int cost) {
		super("Buy "+food.getName());
		this.food = food;
		setCost(cost);
	}

	@Override
	protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
		if(aitem instanceof FoodItem) {
			FoodItem item = (FoodItem) aitem;
			if(item.food==this.food)
				return true;
		}
		return false;
	}

	@Override
	protected Item generateItem(Tile tile, boolean alt) {
		return new FoodItem(food);
	}

}
