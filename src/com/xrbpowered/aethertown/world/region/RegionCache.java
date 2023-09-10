package com.xrbpowered.aethertown.world.region;

import java.util.HashMap;

public class RegionCache {

	public final RegionMode mode;
	public final PortalSystem portals;
	
	private HashMap<Long, Region> regions = new HashMap<>();
	
	public RegionCache(RegionMode mode) {
		this.mode = mode;
		this.portals = new PortalSystem(this);
	}
	
	public Region get(long seed) {
		Region r = regions.get(seed);
		if(r==null) {
			// TODO clean up unused regions
			r = new Region(this, seed);
			regions.put(seed, r);
		}
		return r;
	}

}
