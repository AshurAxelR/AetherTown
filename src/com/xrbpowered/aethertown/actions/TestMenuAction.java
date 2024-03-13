package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.ui;

import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenuDialog;
import com.xrbpowered.aethertown.world.Tile;

public class TestMenuAction extends TileAction {

	public static final TestMenuAction action = new TestMenuAction();

	private static final TileActionMenu menu = createMenu();
	
	@Override
	public String getName() {
		return "Enter";
	}

	@Override
	public void performAt(Tile tile) {
		new TileActionMenuDialog(ui, menu, tile);
		ui.reveal();
	}
	
	private static TileActionMenu createMenu() {
		TileActionMenu maps = new TileActionMenu();
		maps.addAction(ViewMapAction.action);
		
		TileActionMenu top = new TileActionMenu();
		top.addMenu("MAPS", maps);
		top.addAction(ThrowCoinAction.action);
		return top;
	}

}
