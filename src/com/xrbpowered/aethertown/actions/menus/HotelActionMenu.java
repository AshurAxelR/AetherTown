package com.xrbpowered.aethertown.actions.menus;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.DummyAction;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenuDialog;
import com.xrbpowered.aethertown.world.Tile;

public class HotelActionMenu extends TileActionMenu {

	public HotelActionMenu(boolean inn) {
		TileActionMenu reception = new TileActionMenu();
		reception.addAction(new DummyAction("Check in").setDelay(5).setCost(500));
		reception.addAction(new DummyAction("Check out").setEnabled(false));
		reception.addMenu("MAPS", MapsMenu.menu);
		reception.addAction(new DummyAction("Collect earnings").setEnabled(false));
		addMenu("RECEPTION", reception);

		if(inn)
			addMenu("RESTAURANT", FoodActionMenu.restaurant);
		else
			addMenu("BAR", FoodActionMenu.bar);

		TileActionMenu room = new TileActionMenu() {
			@Override
			public boolean isEnabled(Tile tile) {
				return false;
			}
			@Override
			public void disabledAction(Tile tile, TileActionMenuDialog dialog) {
				showToast("Requires room key");
			}
		};
		room.addAction(new DummyAction("Relax"));
		room.addAction(new DummyAction("Shower"));
		room.addAction(new DummyAction("Nap"));
		room.addAction(new DummyAction("Sleep"));
		addMenu("ROOM", room, 5);
	}
	
}