package com.xrbpowered.aethertown.state.items;

import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HouseTileRef;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class HouseKeyItem extends Item {

	public final HouseTileRef house;
	
	public HouseKeyItem(HouseTileRef house, double time) {
		super(ItemType.houseKey, time);
		this.house = house;
	}
	
	public HouseKeyItem(HomeData home) {
		this(home.ref, WorldTime.time);
	}
	
	@Override
	public String getFullName() {
		return String.format("%s for %s", super.getFullName(), house.getFullAddress());
	}

	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return String.format(
			"<p>A door key for your home at<br>"+
			"<span class=\"w\">%s</span></p>"+
			"<p>If you lose a key, you can always request a copy at Civic Centre.</p>",
			house.getFullAddress());
	}
	
	@Override
	public boolean markDot(Tile tile, boolean alt) {
		return house.level.isLevel(tile.level.info); 
	}
}
