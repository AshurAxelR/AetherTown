package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.menus.FoodActionMenu;
import com.xrbpowered.aethertown.actions.menus.OfficeActionMenu;
import com.xrbpowered.aethertown.actions.menus.RoomMenu;
import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HomeImprovement;
import com.xrbpowered.aethertown.state.Inventory;
import com.xrbpowered.aethertown.state.items.FoodItem;
import com.xrbpowered.aethertown.state.items.FoodItem.FoodItemType;
import com.xrbpowered.aethertown.state.items.GroceriesItem;
import com.xrbpowered.aethertown.state.items.HouseKeyItem;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;

public class EnterHomeAction extends HouseTileAction {

	public static class ReqImprovementAction extends ProxyAction {
		public final HomeImprovement improvement;
		
		public ReqImprovementAction(HomeImprovement imp, TileAction action) {
			super(action);
			this.improvement = imp;
		}
		
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			return home.improvements.contains(improvement) && action.isEnabled(tile, alt);
		}
		
		@Override
		protected void onFail(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			if(!home.improvements.contains(improvement))
				showToast("Missing "+improvement.name);
			else
				action.onFail(tile, alt);
		}
	}
	
	private static final TileAction eatAction = new ProxyAction(FoodItemType.homeCooked.action) {
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			return FoodItem.hasFoodType(home, FoodItemType.homeCooked) && action.isEnabled(tile, alt);
		}
		
		@Override
		public String getLabel(Tile tile, boolean alt) {
			String name = "Eat " + FoodItemType.homeCooked.getName();
			HomeData home = HomeData.getLocal(tile.level.info);
			int count = FoodItem.countFoodType(home, FoodItemType.homeCooked);
			if(count > 0)
				return String.format("%s (have %d)", name, count);
			else
				return name;
		}
		
		@Override
		protected void onFail(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			if(!FoodItem.hasFoodType(home, FoodItemType.homeCooked))
				showToast("No item: "+FoodItemType.homeCooked.getName());
			else
				action.onFail(tile, alt);
		}
		
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			super.onSuccess(tile, alt);
			HomeData home = HomeData.getLocal(tile.level.info);
			Inventory inv = FoodItem.findFoodType(home, FoodItemType.homeCooked);
			inv.remove(FoodItem.findFoodType(inv, FoodItemType.homeCooked));
		}
	};
	
	private static final TileAction cookAction = new TileAction("Cook") {
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			if(!home.improvements.contains(HomeImprovement.kitchenware))
				return false;
			if(!GroceriesItem.hasGroceries(home))
				return false;
			if(home.storage[0].getFreeSlots()<2)
				return false;
			return true;
		}
		
		@Override
		protected void onFail(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			if(!home.improvements.contains(HomeImprovement.kitchenware))
				showToast("Missing "+HomeImprovement.kitchenware.name);
			else if(!GroceriesItem.hasGroceries(home))
				showToast("Requires groceries");
			else if(home.storage[0].getFreeSlots()<2)
				showToast("Kitchen inventory full");
			else
				super.onFail(tile, alt);
		}
		
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			super.onSuccess(tile, alt);
			HomeData home = HomeData.getLocal(tile.level.info);
			Inventory inv = GroceriesItem.findGroceries(home);
			inv.remove(GroceriesItem.findGroceries(inv));
			home.storage[0].put(new FoodItem(FoodItemType.homeCooked));
			home.storage[0].put(new FoodItem(FoodItemType.homeCooked));
			showToast(String.format("2x %s added", FoodItemType.homeCooked.getName()));
		}
	}.setDelay(40);
	
	
	public static final EnterHomeAction action = new EnterHomeAction();
	
	private EnterHomeAction() {
		super(createHomeMenu());
	}
	
	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		HomeData home = HomeData.getLocal(tile.level.info);
		return home!=null &&
				home.ref.isHouse((HouseGenerator) tile.sub.parent) &&
				HouseKeyItem.hasKey(home.ref);
	}
	
	@Override
	protected void onFail(Tile tile, boolean alt) {
		showToast("Requires key");
	}
	
	private static TileActionMenu createHomeMenu() {
		TileActionMenu home = new TileActionMenu();
		
		TileActionMenu kitchen = new TileActionMenu();
		kitchen.addAction(FoodActionMenu.freeDrinkAction);
		kitchen.addAction(eatAction);
		kitchen.addAction(cookAction);
		home.addMenu("KITCHEN", kitchen);

		TileActionMenu living = new TileActionMenu();
		living.addAction(new ReqImprovementAction(HomeImprovement.boardGames, LeisureActions.playBoardGames));
		living.addAction(new ReqImprovementAction(HomeImprovement.guitar, LeisureActions.playMusic("Guitar", false)));
		living.addAction(new ReqImprovementAction(HomeImprovement.piano, LeisureActions.playMusic("Piano", false)));
		living.addAction(new ReqImprovementAction(HomeImprovement.tv, LeisureActions.watchMovies(false)));
		living.addAction(new ReqImprovementAction(HomeImprovement.console, LeisureActions.playVideoGames));
		home.addMenu("LIVING ROOM", living);

		TileActionMenu office = new TileActionMenu();
		office.addAction(new ReqImprovementAction(HomeImprovement.computer, OfficeActionMenu.workAction));
		office.addAction(new ReqImprovementAction(HomeImprovement.computer, LeisureActions.playVideoGames));
		office.addAction(new ReqImprovementAction(HomeImprovement.art, LeisureActions.paintArt));
		office.addAction(new ReqImprovementAction(HomeImprovement.books, LeisureActions.study));
		office.addAction(new ReqImprovementAction(HomeImprovement.books, LeisureActions.readBooks));
		home.addMenu("OFFICE", office);

		home.addMenu("BEDROOM", new RoomMenu());

		return home;
	}

}
