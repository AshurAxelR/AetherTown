package com.xrbpowered.aethertown.world.region;

import java.util.HashMap;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.utils.LegacyRand;
import com.xrbpowered.aethertown.utils.MmixRand;
import com.xrbpowered.aethertown.utils.Rand;
import com.xrbpowered.aethertown.utils.RandomSeed;

public class RegionCache {

	private static final int targetCapacity = 8;

	private static boolean legacyRandom;

	public final RegionMode mode;
	public final PortalSystem portals;
	
	
	private HashMap<Long, Region> regions = new HashMap<>();
	private int capacity = targetCapacity;
	
	public RegionCache(RegionMode mode, boolean legacyRandom) {
		RegionCache.legacyRandom = legacyRandom;
		RandomSeed.useLegacy = legacyRandom;
		
		this.mode = mode;
		this.portals = PortalSystem.create(this);
		checkBits();
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
	
	private void checkBits() {
		int pbits = this.portals.bits();
		int rbits = getRand(0L).bits();
		if(pbits > rbits)
			System.err.printf("Portal system bits (%d) exceeds random seed bits (%d)\n", pbits, rbits);
	}
	
	public static Rand getRand(long seed) {
		return legacyRandom ? new LegacyRand(seed) : new MmixRand(seed);
	}
	
}
