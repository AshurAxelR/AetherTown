package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.actions.WaitAction;
import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.ui.dialogs.SleepDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class RoomMenu extends TileActionMenu {

	public RoomMenu() {
		addAction(new WaitAction("Relax", 60));
		addAction(new InspirationAction("Shower", 2).setInsCooldown(GlobalCooldowns.showerIns.daily()).setDelay(10));
		addAction(new TileAction("Sleep") {
			@Override
			protected void onSuccess(Tile tile, boolean alt) {
				SleepDialog.show();
			}
		});
	}

}
