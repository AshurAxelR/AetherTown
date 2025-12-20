package com.xrbpowered.aethertown.actions.menus;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.GetItemAction;
import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.actions.LeisureActions;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.actions.WaitAction;
import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HomeImprovement;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.ItemType;
import com.xrbpowered.aethertown.state.items.LaptopItem;
import com.xrbpowered.aethertown.state.items.TrinketItem;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class ShopActionMenu extends TileActionMenu {

	public static class BuyHomeImprovementAction extends TileAction {
		public final HomeImprovement improvement;
		
		public BuyHomeImprovementAction(HomeImprovement improvement, boolean post) {
			super("For home: "+improvement.name);
			this.improvement = improvement;
			reqHome();
			setCost(improvement.cost + (post ? 500 : 0));
			setDelay(post ? 10 : 5);
		}

		public BuyHomeImprovementAction(HomeImprovement improvement) {
			this(improvement, false);
		}

		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			if(home!=null && home.improvements.contains(improvement))
				return false;
			else
				return super.isEnabled(tile, alt);
		}
		
		@Override
		protected void onFail(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			if(home!=null && home.improvements.contains(improvement))
				showToast("Already installed at home");
			else
				super.onFail(tile, alt);
		}
		
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			super.onSuccess(tile, alt);
			HomeData home = HomeData.getLocal(tile.level.info);
			home.improvements.add(improvement);
		}
	}
	
	public static final TileAction buyNewClothesAction = new WaitAction("Buy new clothes", 10).setCost(2500);
	public static final TileAction buyKitchenwareAction = new BuyHomeImprovementAction(HomeImprovement.kitchenware);

	public static final TileActionMenu supermarket = createSupermarketMenu();
	public static final TileActionMenu giftShop = createGiftShopMenu();

	public static final TileActionMenu clothesShop = new ShopActionMenu(2, buyNewClothesAction);
	public static final TileActionMenu homeShop = new ShopActionMenu(1, buyKitchenwareAction);
	
	public static final TileActionMenu techShop = new ShopActionMenu(2,
			new BuyHomeImprovementAction(HomeImprovement.tv),
			new BuyHomeImprovementAction(HomeImprovement.console),
			new BuyHomeImprovementAction(HomeImprovement.computer),
			new GetItemAction("Buy", ItemType.laptop) {
				@Override
				protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
					return (aitem instanceof LaptopItem);
				}
				@Override
				protected Item generateItem(Tile tile, boolean alt) {
					return new LaptopItem();
				}
			}.setCost(15000));

	public static final TileActionMenu bookShop = new ShopActionMenu(3,
			new BuyHomeImprovementAction(HomeImprovement.books),
			new BuyHomeImprovementAction(HomeImprovement.boardGames),
			LeisureActions.playBoardGames);

	public static final TileActionMenu artShop = new ShopActionMenu(5, new BuyHomeImprovementAction(HomeImprovement.art));

	public static final TileActionMenu musicShop = new ShopActionMenu(3,
			new BuyHomeImprovementAction(HomeImprovement.guitar),
			new BuyHomeImprovementAction(HomeImprovement.piano),
			LeisureActions.playMusic("guitar", false),
			LeisureActions.playMusic("piano", false));

	private ShopActionMenu(int browseIns, TileAction... actions) {
		if(browseIns>0)
			addAction(new InspirationAction("Browse", browseIns).oncePerTile().setDelay(15));
		for(TileAction action : actions)
			addAction(action);
	}

	private static TileActionMenu createSupermarketMenu() {
		ShopActionMenu shop = new ShopActionMenu(1);
		shop.addMenu("FOOD ISLE", GroceriesActionMenu.groceries);
		shop.addAction(buyNewClothesAction);
		shop.addAction(buyKitchenwareAction);
		shop.addMenu("MAPS", MapsMenu.noToken);
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
		shop.addMenu("MAPS", MapsMenu.noToken);
		return shop;
	}

}
