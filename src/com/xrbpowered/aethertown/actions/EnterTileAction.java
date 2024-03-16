package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.ui;

import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenuDialog;
import com.xrbpowered.aethertown.world.Tile;

public class EnterTileAction extends TileAction {

	public final TileActionMenu menu;
	
	public EnterTileAction(TileActionMenu menu) {
		super("Enter");
		this.menu = menu;
	}
	
	public String getMenuTitle(Tile tile, boolean alt) {
		// if(tile.t==ChurchT.template)
		//     return ((ChurchGenerator) tile.sub.parent).getInfo();
		return null;
	}

	public String getSubtitle(Tile tile, boolean alt) {
		return tile.level.info.name;
	}
	
	@Override
	protected void onSuccess(Tile tile, boolean alt) {
		new TileActionMenuDialog(ui, menu, tile, alt, getMenuTitle(tile, alt), getSubtitle(tile, alt));
		ui.reveal();
	}
	
}
