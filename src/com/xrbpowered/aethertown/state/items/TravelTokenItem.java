package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.levelInfo;

import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class TravelTokenItem extends Item {

	public final NamedLevelRef destination;
	
	public TravelTokenItem(NamedLevelRef ref, double time) {
		super(ItemType.travelToken, time);
		this.destination = ref;
	}

	public TravelTokenItem(LevelInfo level) {
		this(new NamedLevelRef(level), WorldTime.time);
	}

	@Override
	public String getFullName() {
		return String.format("%s for %s", super.getFullName(), destination.getFullName());
	}
	
	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return String.format(
			"<p>Use at monuments to instantly travel to a location. "+
			"Monuments can be used only once every 2h.</p>"+
			"<p>Destination:<br><span class=\"w\">%s</span>%s</p>",
			destination.getFullName(),
			markDot(tile, alt) ? "<br>(You are here)": "");
	}

	@Override
	public boolean markDot(Tile tile, boolean alt) {
		return levelInfo.isRef(destination);
	}
}
