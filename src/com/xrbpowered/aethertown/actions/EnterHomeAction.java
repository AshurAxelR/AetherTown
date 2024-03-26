package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.menus.FoodActionMenu;
import com.xrbpowered.aethertown.actions.menus.RoomMenu;
import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HomeImprovement;
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
		kitchen.addAction(new DummyAction("Eat").setEnabled(false)); // TODO eat at home
		kitchen.addAction(new DummyAction("Cook").setEnabled(false)); // TODO cooking action
		home.addMenu("KITCHEN", kitchen);

		TileActionMenu living = new TileActionMenu();
		living.addAction(new ReqImprovementAction(HomeImprovement.boardGames, LeisureActions.playBoardGames));
		living.addAction(new ReqImprovementAction(HomeImprovement.guitar, LeisureActions.playMusic("Guitar", false)));
		living.addAction(new ReqImprovementAction(HomeImprovement.piano, LeisureActions.playMusic("Piano", false)));
		living.addAction(new ReqImprovementAction(HomeImprovement.tv, LeisureActions.watchMovies(false)));
		living.addAction(new ReqImprovementAction(HomeImprovement.console, LeisureActions.playVideoGames));
		home.addMenu("LIVING ROOM", living);

		TileActionMenu office = new TileActionMenu();
		office.addAction(new DummyAction("Work").setEnabled(false)); // TODO work action
		office.addAction(new ReqImprovementAction(HomeImprovement.computer, LeisureActions.playVideoGames));
		office.addAction(new ReqImprovementAction(HomeImprovement.art, LeisureActions.paintArt));
		office.addAction(new ReqImprovementAction(HomeImprovement.books, LeisureActions.study));
		office.addAction(new ReqImprovementAction(HomeImprovement.books, LeisureActions.readBooks));
		home.addMenu("OFFICE", office, 5);

		home.addMenu("BEDROOM", new RoomMenu(), 5);

		return home;
	}

}
