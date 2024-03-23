package com.xrbpowered.aethertown.world.gen.plot.houses;

import java.util.Random;

import com.xrbpowered.aethertown.actions.HouseTileAction;
import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;

class HotelRole extends HouseRole {
	
	HotelRole(String title, HouseTileAction action) {
		super(title, colorHotel, action,
			new ArchitectureStyle(2, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.hotel, IllumPattern.hotelRooms),
			new ArchitectureStyle(3, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.hotel, IllumPattern.hotelRooms)
		);
	}
	
	@Override
	public ArchitectureStyle arch(HouseGenerator house, Random random) {
		return isCityHouse(house, random) ? arch[1] : arch[0];
	}
	
	@Override
	public IllumTileComponent getSign() {
		return ArchitectureTileSet.hotelSign;
	}
	
	@Override
	public float getSignY() {
		return 9f;
	}
}