package com.xrbpowered.aethertown.state;

import com.xrbpowered.aethertown.world.region.LevelInfo;

public class NamedLevelRef extends LevelRef {
	
	public final String name;

	public NamedLevelRef(long seed, int x, int z, String name) {
		super(seed, x, z);
		this.name = name;
	}

	public NamedLevelRef(LevelInfo level) {
		super(level);
		this.name = level.name;
	}
	
	public String getFullName() {
		return String.format("%s %s", RegionVisits.getRegionTitle(regionSeed, true), name);
	}

}
