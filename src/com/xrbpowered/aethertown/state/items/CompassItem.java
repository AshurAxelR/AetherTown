package com.xrbpowered.aethertown.state.items;

import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class CompassItem extends Item {

	public CompassItem(double time) {
		super(ItemType.compass, time);
	}

	public CompassItem() {
		super(ItemType.compass, WorldTime.time);
	}
	
	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return "<p>Press <b>C</b> to show compass while walking.</p>";
	}

}
