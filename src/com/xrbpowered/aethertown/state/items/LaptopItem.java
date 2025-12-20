package com.xrbpowered.aethertown.state.items;

import com.xrbpowered.aethertown.actions.menus.OfficeActionMenu;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class LaptopItem extends Item {

	public LaptopItem(double time) {
		super(ItemType.laptop, time);
	}

	public LaptopItem() {
		super(ItemType.laptop, WorldTime.time);
	}
	
	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return "<p>Use to work for 90 min anywhere you like.</p>";
	}
	
	@Override
	public String getUseActionName() {
		return "WORK";
	}
	
	@Override
	public boolean useItem(Tile tile, boolean alt) {
		OfficeActionMenu.doWork();
		return true;
	}
}
