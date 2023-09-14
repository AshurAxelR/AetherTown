package com.xrbpowered.aethertown.world.region;

import java.util.HashMap;
import java.util.HashSet;

import com.xrbpowered.aethertown.AetherTown;

public class RegionCache {

	private static final int targetCapacity = 4;
	
	public final RegionMode mode;
	public final PortalSystem portals;
	
	private HashMap<Long, Region> regions = new HashMap<>();
	private int capacity = targetCapacity;
	
	public RegionCache(RegionMode mode) {
		this.mode = mode;
		this.portals = new PortalSystem(this);
	}
	
	public void verifyBookmarks(LevelInfo[] bookmarks) {
		for(Region r : regions.values())
			r.bookmark = false;
		for(LevelInfo level : bookmarks) {
			if(level!=null)
				level.region.bookmark = true;
		}
	}
	
	public void cleanup() {
		HashMap<Long, Region> old = regions;
		regions = new HashMap<>();
		for(Region r : old.values()) {
			if(r.bookmark)
				regions.put(r.seed, r);
		}
		HashSet<Region> used = AetherTown.levelCache.regionsInUse();
		for(Region r : used)
			regions.put(r.seed, r);
		capacity = regions.size() + targetCapacity;
	}
	
	public Region get(long seed) {
		seed = PortalSystem.getRegionSeed(seed);
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