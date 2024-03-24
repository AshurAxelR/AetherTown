package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.menus.FoodActionMenu;
import com.xrbpowered.aethertown.actions.menus.RoomMenu;
import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;

public class EnterHomeAction extends HouseTileAction {

	public static final EnterHomeAction action = new EnterHomeAction();

	private EnterHomeAction() {
		super(createHomeMenu());
	}
	
	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		// TODO check key
		HomeData home = HomeData.getLocal(tile.level.info);
		return home!=null && home.ref.isHouse((HouseGenerator) tile.sub.parent);
	}
	
	@Override
	protected void onFail(Tile tile, boolean alt) {
		showToast("Requires key");
	}
	
	private static TileActionMenu createHomeMenu() {
		// TODO complete home menu
		TileActionMenu home = new TileActionMenu();
		
		TileActionMenu kitchen = new TileActionMenu();
		kitchen.addAction(FoodActionMenu.freeDrinkAction);
		kitchen.addAction(new DummyAction("Eat").setEnabled(false)); // TODO eat at home
		kitchen.addAction(new DummyAction("Cook").setEnabled(false)); // TODO cooking action
		home.addMenu("KITCHEN", kitchen);

		TileActionMenu living = new TileActionMenu();
		living.addAction(LeisureActions.playBoardGames);
		living.addAction(LeisureActions.playMusic(false));
		living.addAction(LeisureActions.watchMovies(false));
		home.addMenu("LIVING ROOM", living);

		TileActionMenu office = new TileActionMenu();
		office.addAction(new DummyAction("Work").setEnabled(false)); // TODO work action
		office.addAction(LeisureActions.playVideoGames);
		office.addAction(LeisureActions.study);
		office.addAction(LeisureActions.readBooks);
		home.addMenu("OFFICE", office, 5);

		home.addMenu("BEDROOM", new RoomMenu(), 5);

		return home;
	}

}
