package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.ui;

import com.xrbpowered.aethertown.actions.menus.HotelActionMenu;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenuDialog;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.ChurchGenerator;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.tiles.ChurchT;
import com.xrbpowered.aethertown.world.tiles.HouseT;

public class EnterTileAction extends TileAction {

	public static final EnterTileAction hotelAction = new EnterTileAction(HotelActionMenu.menu	);
	
	public final TileActionMenu menu;
	
	public EnterTileAction(TileActionMenu menu) {
		super("Enter");
		this.menu = menu;
	}
	
	public String getMenuTitle(Tile tile) {
		if(tile.t instanceof HouseT) {
			HouseGenerator house = (HouseGenerator) tile.sub.parent;
			return house.role.title;
		}
		else if(tile.t==ChurchT.template)
			return ((ChurchGenerator) tile.sub.parent).getInfo();
		else
			return null;
	}

	public String getAddress(Tile tile) {
		if(tile.t instanceof HouseT) {
			HouseGenerator house = (HouseGenerator) tile.sub.parent;
			return String.format("%d, %s", house.index+1, tile.level.info.name);
		}
		else
			return tile.level.info.name;
	}
	
	@Override
	public void performAt(Tile tile) {
		new TileActionMenuDialog(ui, menu, tile, getMenuTitle(tile), getAddress(tile));
		ui.reveal();
	}
	
	public static TileAction getHouseAction(HouseGenerator house) {
		return hotelAction;
	}

}
