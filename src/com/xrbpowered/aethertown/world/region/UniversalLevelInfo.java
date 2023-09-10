package com.xrbpowered.aethertown.world.region;

public class UniversalLevelInfo {

	public final long regionSeed;
	public final int x, z;
	
	public UniversalLevelInfo(long seed, int x, int z) {
		this.regionSeed = seed;
		this.x = x;
		this.z = z;
	}

	public UniversalLevelInfo(LevelInfo info) {
		this.regionSeed = info.region.seed;
		this.x = info.x0;
		this.z = info.z0;
	}
	
	public LevelInfo find(RegionCache regions) {
		Region r = regions.get(regionSeed);
		if(r.isInside(x, z))
			return r.map[x][z];
		else
			return null;
	}

	public String format() {
		return String.format("%d,%d,%d", regionSeed, x, z);
	}
	
	public static UniversalLevelInfo parseValue(String v) {
		String[] vs = v.split(",");
		if(vs.length!=3)
			return null;
		try {
			long seed = Long.parseLong(vs[0]);
			if(seed<0L)
				return null;
			int x = Integer.parseInt(vs[1]);
			int z = Integer.parseInt(vs[2]);
			return new UniversalLevelInfo(seed, x, z);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
}
