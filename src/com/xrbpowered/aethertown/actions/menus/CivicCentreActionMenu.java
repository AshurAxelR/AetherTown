package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.DummyAction;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;

public class CivicCentreActionMenu extends TileActionMenu {

	public CivicCentreActionMenu(boolean civicCentre) {
		addMenu("MAPS", MapsMenu.menu);
		addAction(new DummyAction("Collect earnings").setEnabled(false));
		addAction(new DummyAction("Order goods").setEnabled(false));

		if(civicCentre) {
			TileActionMenu res = new TileActionMenu();
			res.addAction(new DummyAction("Claim home").setDelay(10));
			res.addAction(new DummyAction("Recover key").setEnabled(false));
			res.addAction(new DummyAction("Abandon home").setEnabled(false));
			addMenu("Residential Services", res);
		}
	}

}
