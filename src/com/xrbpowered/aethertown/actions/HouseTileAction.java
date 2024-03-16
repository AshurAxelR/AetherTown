package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.menus.HotelActionMenu;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.region.HouseRole;

public class HouseTileAction extends EnterTileAction {

	public static final HouseTileAction hotelAction = new HouseTileAction(HotelActionMenu.menu);

	public HouseTileAction(TileActionMenu menu) {
		super(menu);
	}
	
	@Override
	public String getLabel(Tile tile, boolean alt) {
		return alt ? (name + " " + getMenuTitle(tile, alt)) : name;
	}

	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		return !house(tile).isClosed(alt);
	}
	
	protected HouseGenerator house(Tile tile) {
		return (HouseGenerator) tile.sub.parent;
	}
	
	@Override
	public String getMenuTitle(Tile tile, boolean alt) {
		HouseRole role = house(tile).getRole(alt);
		return role==HouseRole.residential ? "Home" : role.title;
	}

	@Override
	public String getSubtitle(Tile tile, boolean alt) {
		HouseGenerator house = house(tile);
		HouseRole role = house.getRole(alt);
		if(role==HouseRole.residential || role==HouseRole.hotel || role==HouseRole.inn)
			return house.getAddress();
		else {
			IllumLayer illum = house.arch.getIllumLayer(alt ? 1 : 0);
			if(illum.open==0 && illum.close==24)
				return "Open 24h";
			else
				return String.format("Open %02d:00\u2014%02d:00", illum.open, illum.close);
		}
	}
	
	@Override
	protected void onFail(Tile tile, boolean alt) {
		HouseGenerator house = house(tile);
		if(house.isClosed(alt))
			showToast("%s is closed until %02d:00", house.getRole(alt).title, house.arch.getIllumLayer(alt ? 1 : 0).open);
	}

	public static TileAction getAction(HouseRole role) {
		if(role==null)
			return null;
		return hotelAction;
	}

}
