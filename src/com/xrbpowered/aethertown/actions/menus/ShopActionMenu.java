package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.DummyAction;
import com.xrbpowered.aethertown.actions.GetItemAction;
import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.actions.WaitAction;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.ItemType;
import com.xrbpowered.aethertown.state.items.TrinketItem;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class ShopActionMenu extends TileActionMenu {

	public static final TileAction buyNewClothesAction = new WaitAction("Buy new clothes", 10).setCost(2500);

	public static final TileActionMenu supermarket = createSupermarketMenu();
	public static final TileActionMenu clothesShop = createClothesShopMenu();
	public static final TileActionMenu giftShop = createGiftShopMenu();
	
	private ShopActionMenu(int browseIns) {
		if(browseIns>0)
			addAction(new InspirationAction("Browse", browseIns).oncePerTile().setDelay(15));
	}

	private static TileActionMenu createSupermarketMenu() {
		ShopActionMenu shop = new ShopActionMenu(1);
		shop.addMenu("FOOD ISLE", GroceriesActionMenu.groceries);
		shop.addAction(buyNewClothesAction);
		shop.addAction(homeImprovement("Kitchenware"));
		return shop;
	}

	private static TileActionMenu createClothesShopMenu() {
		ShopActionMenu shop = new ShopActionMenu(2);
		shop.addAction(buyNewClothesAction);
		return shop;
	}
	
	private static TileActionMenu createGiftShopMenu() {
		ShopActionMenu shop = new ShopActionMenu(3);
		
		shop.addAction(new GetItemAction("Buy", ItemType.trinket) {
			@Override
			protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
				if(aitem instanceof TrinketItem) {
					TrinketItem item = (TrinketItem) aitem;
					if(item.location.isLevel(tile.level.info))
						return true;
				}
				return false;
			}
			@Override
			protected Item generateItem(Tile tile, boolean alt) {
				return new TrinketItem(tile.level.info);
			}
		}.setCost(250));
		
		shop.addMenu("CAFETERIA", FoodActionMenu.cafeteria);
		return shop;
	}
	
	// TODO homeShop
	// TODO Tech Store
	// TODO Book Shop
	// TODO Art Shop
	// TODO Music Shop
	
	private static TileAction homeImprovement(String name) {
		return new DummyAction("Home improvement: "+name).setEnabled(false);
	}
}
