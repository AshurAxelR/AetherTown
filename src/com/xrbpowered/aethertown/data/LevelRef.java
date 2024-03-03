package com.xrbpowered.aethertown.data;

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
	
	public LevelInfo find(RegionCache regions) {
		Region r = regions.get(regionSeed);
		if(r.isInside(x, z))
			return r.map[x][z];
		else
			return null;
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


	public String format() {
		return String.format("%d,%d,%d", regionSeed, x, z);
	}
	
	public static LevelRef parseValue(String v) {
		String[] vs = v.split(",");
		if(vs.length!=3)
			return null;
		try {
			long seed = Long.parseLong(vs[0]);
			if(seed<0L)
				return null;
			int x = Integer.parseInt(vs[1]);
			int z = Integer.parseInt(vs[2]);
			return new LevelRef(seed, x, z);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
}
