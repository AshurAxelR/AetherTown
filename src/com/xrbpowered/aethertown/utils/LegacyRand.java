package com.xrbpowered.aethertown.utils;

public class LegacyRand extends Rand {

    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

	public LegacyRand(long seed) {
		super(seed);
	}
	
	@Override
	public int bits() {
		return 48;
	}
	
	@Override
	public long getMask() {
		return mask;
	}

	@Override
	protected long scrambleSeed(long seed) {
		return (seed ^ multiplier) & mask;
	}
	
	@Override
	protected long lcg(long seed) {
		return (seed * multiplier + addend) & mask;
	}

	@Override
	protected int next(int bits) {
		return (int)(next() >>> (48 - bits));
	}

}
