package com.xrbpowered.aethertown.utils;

public class RandomSeed {
	
	public static boolean useLegacy = false;
	
	private RandomSeed() {}

	public static int smallHashXY(int x, int y) {
		return x * 32323 + y * 31;
	}
	
	public static long hashSeed(long seed, long add) {
		// Multiply by Knuth's MMIX random (LCG) and add offset
		seed *= seed * 6364136223846793005L + 1442695040888963407L;
		seed += add;
		if(useLegacy)
			return seed;
		
		// mix 64-bits into 48-bit seed for Java random
		seed = (seed >>> 16) ^ (seed & 0xffffL);
		
		// nextSeed = nextLong() using Java random (LCG)
		final long multiplier = 0x5DEECE66DL;
	    final long addend = 0xBL;
	    final long mask = (1L << 48) - 1;
		seed = (seed ^ multiplier) & mask;
		seed = (seed * multiplier + addend) & mask;
		long hi = seed >>> 16;
		seed = (seed * multiplier + addend) & mask;
		long low = seed >>> 16;
		return (hi << 32) + low;
	}

	public static long seedX(long seed, long x) {
		seed = hashSeed(seed, x);
		seed = hashSeed(seed, x);
		if(!useLegacy)
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
