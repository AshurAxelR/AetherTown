package com.xrbpowered.aethertown.state.items;

import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.world.region.LevelInfo;

public class TravelTokenItem extends Item {

	public final NamedLevelRef destination;
	
	public TravelTokenItem(LevelInfo level) {
		super(ItemType.travelToken);
		this.destination = new NamedLevelRef(level);
	}

}
