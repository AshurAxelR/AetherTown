package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.*;

import com.xrbpowered.aethertown.state.RegionVisits;
import com.xrbpowered.aethertown.ui.dialogs.RegionMapDialog;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class RegionMapItem extends Item {

	public final long regionSeed;
	
	public RegionMapItem(long regionSeed, double time) {
		super(ItemType.regionMap, time);
		this.regionSeed = regionSeed;
	}

	public RegionMapItem(long regionSeed) {
		this(regionSeed, WorldTime.time);
	}

	@Override
	public String getUseActionName() {
		return "VIEW";
	}
	
	@Override
	public boolean useItem(Tile tile, boolean alt) {
		RegionMapDialog.show(regionCache.get(regionSeed));
		return true;
	}
	
	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return String.format(
			"<p><span class=\"w\">%s</span>%s</p>",
			RegionVisits.getRegionTitle(regionSeed, false),
			markDot() ? " (this region)" : "");
	}
	
	@Override
	public boolean markDot() {
		return region.seed == regionSeed;
	}
}
