package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.menus.CivicCentreActionMenu;
import com.xrbpowered.aethertown.actions.menus.FoodActionMenu;
import com.xrbpowered.aethertown.actions.menus.GroceriesActionMenu;
import com.xrbpowered.aethertown.actions.menus.HotelActionMenu;
import com.xrbpowered.aethertown.actions.menus.LibraryActionMenu;
import com.xrbpowered.aethertown.actions.menus.MapsMenu;
import com.xrbpowered.aethertown.actions.menus.OfficeActionMenu;
import com.xrbpowered.aethertown.actions.menus.ShopActionMenu;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseRole;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.aethertown.world.tiles.HouseT;

public class HouseTileAction extends EnterTileAction {

	public static final HouseTileAction civicCentre = new HouseTileAction(new CivicCentreActionMenu(true, true));
	public static final HouseTileAction postOffice = new HouseTileAction(new CivicCentreActionMenu(false, true));
	public static final HouseTileAction hospital = new HouseTileAction(new CivicCentreActionMenu(false, false));
	public static final HouseTileAction hotel = new HouseTileAction(new HotelActionMenu(false));
	public static final HouseTileAction inn = new HouseTileAction(new HotelActionMenu(true));
	
	public static final HouseTileAction museum = new HouseTileAction(new TileActionMenu(
		LeisureActions.viewMuseum,
		LeisureActions.portalKnowledge,
		MapsMenu.regionMapAction
	));
	public static final HouseTileAction library = new HouseTileAction(new LibraryActionMenu());
	public static final HouseTileAction concertHall = new HouseTileAction(new TileActionMenu(
		LeisureActions.playMusic("Guitar", true),
		LeisureActions.playMusic("Piano", true),
		LeisureActions.watchMovies(true)
	));

	public static final HouseTileAction office = new HouseTileAction(OfficeActionMenu.menu);

	public static final HouseTileAction restaurant = new HouseTileAction(FoodActionMenu.restaurant);
	public static final HouseTileAction coffeeShop = new HouseTileAction(FoodActionMenu.cafeteria);
	
	public static final HouseTileAction localShop = new HouseTileAction(GroceriesActionMenu.groceries);
	public static final HouseTileAction supermarket = new HouseTileAction(ShopActionMenu.supermarket);
	public static final HouseTileAction giftShop = new HouseTileAction(ShopActionMenu.giftShop);

	public HouseTileAction(TileActionMenu menu) {
		super(menu);
	}
	
	@Override
	public String getLabel(Tile tile, boolean alt) {
		return alt ? (name + " " + getMenuTitle(tile, alt)) : name;
	}

	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		return !house(tile).isClosed(alt, 0f);
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
			if(illum.getOpenTime()==0 && illum.getCloseTime()==24)
				return "Open 24h";
			else
				return String.format("Open %02d:00\u2014%02d:00", illum.getOpenTime(), illum.getCloseTime());
		}
	}
	
	@Override
	protected void onFail(Tile tile, boolean alt) {
		HouseGenerator house = house(tile);
		if(house.isClosed(alt, 0f))
			showToast("%s is closed until %02d:00", house.getRole(alt).title, house.arch.getIllumLayer(alt ? 1 : 0).getOpenTime());
	}

	public static boolean isClosed(Tile tile, boolean alt) {
		if(tile.t==HouseT.template)
			return ((HouseGenerator) tile.sub.parent).isClosed(alt, 0);
		else
			return false;
	}

	public static boolean isClosingSoon(Tile tile, boolean alt, TileAction action) {
		if(tile.t==HouseT.template) {
			float addTime = (action==null) ? 0f : action.getDelay(tile, alt) * (float) WorldTime.minute;
			return ((HouseGenerator) tile.sub.parent).isClosed(alt, addTime);
		}
		else
			return false;
	}

}
