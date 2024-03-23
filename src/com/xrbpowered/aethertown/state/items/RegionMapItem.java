package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.*;

import com.xrbpowered.aethertown.state.RegionVisits;
import com.xrbpowered.aethertown.ui.dialogs.RegionMapDialog;

public class RegionMapItem extends Item {

	public final long regionSeed;
	
	public RegionMapItem(long regionSeed) {
		super(ItemType.regionMap);
		this.regionSeed = regionSeed;
	}


	@Override
	public String getUseActionName() {
		return "VIEW";
	}
	
	@Override
	public void useItem() {
		RegionMapDialog.show(regionCache.get(regionSeed));
	}
	
	@Override
	public String getInfoHtml() {
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
