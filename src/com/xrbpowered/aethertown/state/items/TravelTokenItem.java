package com.xrbpowered.aethertown.state.items;

import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.world.region.LevelInfo;

public class TravelTokenItem extends Item {

	public final NamedLevelRef destination;
	
	public TravelTokenItem(LevelInfo level) {
		super(ItemType.travelToken);
		this.destination = new NamedLevelRef(level);
	}
	
	@Override
	public String getInfoHtml() {
		return String.format(
			"<p>Use at monuments to instantly travel to a location. "+
			"Monuments can be used only once every 2h.</p>"+
			"<p>Destination:<br><span class=\"w\">%s</span></p>",
			destination.getFullName());
	}
	
}
