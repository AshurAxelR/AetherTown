package com.xrbpowered.aethertown.world.region;

import java.util.HashMap;

import com.xrbpowered.aethertown.AetherTown;

public class RegionCache {

	private static final int targetCapacity = 8;
	
	public final RegionMode mode;
	public final PortalSystem portals;
	
	private HashMap<Long, Region> regions = new HashMap<>();
	private int capacity = targetCapacity;
	
	public RegionCache(RegionMode mode) {
		this.mode = mode;
		this.portals = PortalSystem.create(this);
	}
	
	public void cleanup() {
		regions = new HashMap<>();
		for(Region r : AetherTown.levelCache.regionsInUse())
			regions.put(r.seed, r);
		capacity = regions.size() + targetCapacity;
	}
	
	public Region get(long seed) {
		seed = portals.getRegionSeed(seed);
		Region r = regions.get(seed);
		if(r==null) {
			if(regions.size()>=capacity)
				cleanup();
			r = new Region(this, seed);
			r.generate();
			regions.put(seed, r);
		}
		return r;
	}
	
}
