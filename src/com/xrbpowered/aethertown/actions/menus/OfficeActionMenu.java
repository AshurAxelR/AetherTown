package com.xrbpowered.aethertown.actions.menus;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.LeisureActions;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.state.Earnings;
import com.xrbpowered.aethertown.ui.dialogs.ConfirmDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenuDialog;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class OfficeActionMenu extends TileActionMenu {

	public static final TileAction workAction = new TileAction("Work") {
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			String report = Earnings.work();
			ConfirmDialog.show("Work", report, 350);
			super.onSuccess(tile, alt);
		}
	}.setDelay(90);
	
	public static final OfficeActionMenu menu = new OfficeActionMenu();
	
	private OfficeActionMenu() {
		addAction(workAction);
		addAction(LeisureActions.playVideoGames);
		addAction(FoodActionMenu.freeDrinkAction);
	}

	@Override
	public boolean isEnabled(Tile tile) {
		return IllumLayer.officeHours.isActive(WorldTime.getHourOfDay());
	}
	
	@Override
	public void disabledAction(Tile tile, TileActionMenuDialog dialog) {
		showToast("Office closed until %02d:00", IllumLayer.officeHours.open);
	}

}
