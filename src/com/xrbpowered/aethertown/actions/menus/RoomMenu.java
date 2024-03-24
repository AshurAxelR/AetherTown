package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.InspirationAction;
import com.xrbpowered.aethertown.actions.WaitAction;
import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;

public class RoomMenu extends TileActionMenu {

	public RoomMenu() {
		addAction(new WaitAction("Relax", 60));
		addAction(new InspirationAction("Shower", 2).setInsCooldown(GlobalCooldowns.showerIns.daily()).setDelay(10));
		addAction(new InspirationAction("Sleep", 1).setDelay(60)); // TODO sleep UI
	}

}
