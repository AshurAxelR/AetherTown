package com.xrbpowered.aethertown.state.items;

import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class TrinketItem extends Item {

	public final NamedLevelRef location;
	public final double timestamp;

	public TrinketItem(NamedLevelRef ref, double time) {
		super(ItemType.trinket);
		this.location = ref;
		this.timestamp = time;
	}
	
	public TrinketItem(LevelInfo level) {
		this(new NamedLevelRef(level), WorldTime.time);
	}

	@Override
	public String getInfoHtml() {
		return String.format(
			"<p>A piece of memorabilia.</p>"+
			"<p>Bought on <span class=\"w\">%s</span><br>"+
			"in <span class=\"w\">%s</span></p>",
			WorldTime.getFormattedTimestamp(timestamp),
			location.getFullName());
	}
}
