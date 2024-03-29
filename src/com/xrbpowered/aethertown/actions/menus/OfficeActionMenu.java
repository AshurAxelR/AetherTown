package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.LeisureActions;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.Earnings;
import com.xrbpowered.aethertown.ui.dialogs.ConfirmDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class OfficeActionMenu extends TileActionMenu {

	public static final TileAction workAction = new TileAction("Work") {
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			String report = Earnings.work();
			ConfirmDialog.show("Work", report, 350);
			super.onSuccess(tile, alt);
		}
	}.setDelay(120);
	
	public OfficeActionMenu() {
		addAction(workAction);
		addAction(LeisureActions.playVideoGames);
		addAction(FoodActionMenu.drinkAction);
	}

}
