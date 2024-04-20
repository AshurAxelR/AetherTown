package com.xrbpowered.aethertown.utils;

public abstract class Rand {

	protected long seed;

	private double nextGaussian;
	private boolean haveNextGaussian = false;

	public Rand(long seed) {
		setSeed(seed);
	}

	protected long scrambleSeed(long seed) {
		return RandomSeed.hashSeed(seed, 0L);
	}

	public final void setSeed(long seed) {
		this.seed = scrambleSeed(seed);
		this.haveNextGaussian = false;
	}

	public abstract int bits();
	
	public long getMask() {
		return (1L << bits()) - 1;
	}
	
	protected abstract long lcg(long seed);
	
	protected long next() {
		seed = lcg(seed);
		return seed;
	}
	
	protected abstract int next(int bits);

	public int nextInt(int bound) {
		if(bound<=0)
			throw new IllegalArgumentException();
		if((bound & (bound - 1))==0)
			return (int) ((bound * (long) next(31))>>31);
		else {
			int bits, val;
			do {
				bits = next(31);
				val = bits % bound;
			} while(bits - val + (bound - 1)<0);
			return val;
		}
	}

	public long nextLong() {
		return ((long) (next(32))<<32) + next(32);
	}

	public boolean nextBoolean() {
		return next(1)!=0;
	}

	public float nextFloat() {
		return next(24) / ((float) (1<<24));
	}

	public double nextDouble() {
		return (((long) (next(26))<<27) + next(27)) * (0x1.0p-53);
	}

	public double nextGaussian() {
		// See Knuth, ACP, Section 3.4.1.C Algorithm P
		// The polar method, due to G. E. P. Box, M. E. Muller, and G. Marsaglia.
		// (See Annals Math. Stat. 29 (1958), 610-611; and Boeing Scientific Res. Lab. report Dl-82-0203
		if(haveNextGaussian) {
			haveNextGaussian = false;
			return nextGaussian;
		}
		else {
			double v1, v2, s;
			do {
				v1 = 2.0*nextDouble()-1.0;
				v2 = 2.0*nextDouble()-1.0;
				s = v1*v1 + v2*v2;
			} while(s>=1 || s==0);
			
			// StrictMath ensures bit-to-bit repeatable results
			double mul = StrictMath.sqrt(-2.0*StrictMath.log(s) / s);
			nextGaussian = v2 * mul;
			haveNextGaussian = true;
			return v1 * mul;
		}
	}

}
