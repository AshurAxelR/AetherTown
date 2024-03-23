package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.ChurchGenerator;

public class EnterChurchAction extends EnterTileAction {

	public static final EnterChurchAction action = new EnterChurchAction(createMenu());
	
	public EnterChurchAction(TileActionMenu menu) {
		super(menu);
	}
	
	@Override
	public String getMenuTitle(Tile tile, boolean alt) {
		return String.format("St. %s's", ((ChurchGenerator) tile.sub.parent).name);
	}

	private static TileActionMenu createMenu() {
		TileActionMenu m = new TileActionMenu();
		m.addAction(new InspirationAction("Look around", 3, 3).oncePerTile().setDelay(10));
		m.addAction(new InspirationAction("Pray", 5).setInsCooldown(GlobalCooldowns.prayIns.daily()).setDelay(15));
		m.addAction(new WaitAction("Shelter", 30));
		return m;
	}

}
