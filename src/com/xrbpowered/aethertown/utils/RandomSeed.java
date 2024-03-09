package com.xrbpowered.aethertown.utils;

public class RandomSeed {
	
	private RandomSeed() {}

	public static long hashSeed(long seed, long add) {
		// Multiply by Knuth's Random (Linear congruential generator) and add offset
		seed *= seed * 6364136223846793005L + 1442695040888963407L;
		seed += add;
		return seed;
	}

	public static long seedX(long seed, long x) {
		seed = hashSeed(seed, x);
		seed = hashSeed(seed, x);
		return seed;
	}
	
	public static long seedXY(long seed, long x, long y) {
		seed = hashSeed(seed, x);
		seed = hashSeed(seed, y);
		seed = hashSeed(seed, x);
		seed = hashSeed(seed, y);
		return seed;
	}

	public static long seedXYZ(long seed, long x, long y, long z) {
		seed = hashSeed(seed, x);
		seed = hashSeed(seed, y);
		seed = hashSeed(seed, z);
		seed = hashSeed(seed, x);
		seed = hashSeed(seed, y);
		seed = hashSeed(seed, z);
		return seed;
	}

}
