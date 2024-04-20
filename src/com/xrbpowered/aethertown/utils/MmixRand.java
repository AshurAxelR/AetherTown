package com.xrbpowered.aethertown.utils;

public class MmixRand extends Rand {

	public MmixRand(long seed) {
		super(seed);
	}

	@Override
	public int bits() {
		return 64;
	}
	
	@Override
	public long getMask() {
		return -1L;
	}

	@Override
	protected long lcg(long seed) {
		return seed * 6364136223846793005L + 1442695040888963407L;
	}

	@Override
	protected int next(int bits) {
		return (int)(next() >>> (64 - bits));
	}

}
