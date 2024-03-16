package com.xrbpowered.aethertown.actions.menus;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenuDialog;
import com.xrbpowered.aethertown.world.Tile;

public class HotelActionMenu extends TileActionMenu {

	public static final TileActionMenu menu = new HotelActionMenu();

	private HotelActionMenu() {
		TileActionMenu reception = new TileActionMenu();
		reception.addAction(new DummyAction("Check in").setDelay(5).setCost(500));
		reception.addAction(new DummyAction("Check out").setEnabled(false));
		reception.addMenu("MAPS", MapsMenu.menu);
		reception.addAction(new DummyAction("Collect earnings").setEnabled(false));
		addMenu("RECEPTION", reception);

		TileActionMenu bar = new TileActionMenu();
		bar.addAction(new InspirationAction("Drink tea/coffee", 1).setDelay(15).setCost(150));
		bar.addAction(new InspirationAction("Drink tea/coffee with dessert", 2).setDelay(20).setCost(350));
		bar.addAction(new InspirationAction("Eat main course", 4).setDelay(40).setCost(750));
		bar.addAction(new InspirationAction("Eat starter and main course", 5).setDelay(50).setCost(1000));
		bar.addAction(new DummyAction("Hang out").setDelay(20));
		addMenu("RESTAURANT", bar);

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
