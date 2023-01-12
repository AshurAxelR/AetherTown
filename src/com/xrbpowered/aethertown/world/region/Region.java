package com.xrbpowered.aethertown.world.region;

import java.util.Random;

public class Region {

	public static final int sizex = 1024;
	public static final int sizez = 128;
	
	public final long seed;
	public LevelInfo[][] map = null;
	
	public Region(long seed) {
		this.seed = seed;
	}
	
	public void generate() {
		map = new LevelInfo[sizex][sizez];
		Random random = new Random(seed);
		new RegionPaths(this, random).generatePaths();
	}

	public static boolean isInside(int x, int z) {
		return (x>=0 && x<sizex && z>=0 && z<sizez);
	}
}
