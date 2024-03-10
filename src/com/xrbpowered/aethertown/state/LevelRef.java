package com.xrbpowered.aethertown.state;

import java.util.Objects;

import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.RegionCache;

public class LevelRef {

	public final long regionSeed;
	public final int x, z;
	
	public LevelRef(long seed, int x, int z) {
		this.regionSeed = seed;
		this.x = x;
		this.z = z;
	}
	
	public LevelRef(LevelInfo level) {
		this.regionSeed = level.region.seed;
		this.x = level.x0;
		this.z = level.z0;
	}
	
	public LevelInfo find(Region region) {
		if(region==null || region.seed!=regionSeed)
			return null;
		else
			return region.getLevel(x, z);
	}
	
	public LevelInfo find(RegionCache regions) {
		return find(regions.get(regionSeed));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(regionSeed, x, z);
	}
	
	@Override
	public boolean equals(Object obj) {
		LevelRef info = (LevelRef) obj;
		return this.regionSeed==info.regionSeed &&
				this.x==info.x && this.z==info.z;
	}
	
}
