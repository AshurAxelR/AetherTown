package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.levelInfo;

import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.world.region.LevelInfo;

public class TravelTokenItem extends Item {

	public final NamedLevelRef destination;
	
	public TravelTokenItem(NamedLevelRef ref) {
		super(ItemType.travelToken);
		this.destination = ref;
	}

	public TravelTokenItem(LevelInfo level) {
		this(new NamedLevelRef(level));
	}

	@Override
	public String getInfoHtml() {
		return String.format(
			"<p>Use at monuments to instantly travel to a location. "+
			"Monuments can be used only once every 2h.</p>"+
			"<p>Destination:<br><span class=\"w\">%s</span>%s</p>",
			destination.getFullName(),
			markDot() ? "<br>(You are here)": "");
	}

	@Override
	public boolean markDot() {
		return levelInfo.isRef(destination);
	}
}
