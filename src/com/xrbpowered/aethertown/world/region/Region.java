package com.xrbpowered.aethertown.world.region;

import java.util.Random;

public class Region {

	public static final int sizex = 1024;
	public static final int sizez = 128;
	
	public final long seed;
	public final LevelInfo[][] map;
	
	public Region(long seed) {
		this.map = new LevelInfo[sizex][sizez];
		this.seed = seed;
	}
	
	public void generate() {
		Random random = new Random(seed);
		new RegionPaths(this, random).generatePaths();
	}

	public static boolean isInside(int x, int z) {
		return (x>=0 && x<sizex && z>=0 && z<sizez);
	}
}
