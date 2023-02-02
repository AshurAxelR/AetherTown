package com.xrbpowered.aethertown.world.stars;

import java.util.Random;

import com.xrbpowered.aethertown.utils.RandomSeed;
import com.xrbpowered.aethertown.utils.Shuffle;

public class GateNetwork {

	public static final int baseHi = 12;
	public static final int baseLo = 7;
	public static final int base = baseHi*baseLo; // 84
	public static final long maxSeed = 208215748530929664L; // 84^9

	public final long masterKey;
	
	private final int innerMask;
	private final int[] timeMap;

	public GateNetwork(long masterKey) {
		this.masterKey = masterKey;
		Random random = new Random(masterKey);
		this.innerMask = random.nextInt(base);
		this.timeMap = createTimeMap(random);
	}

	public GateNetwork() {
		this(new Random().nextLong());
	}
	
	public long travelWest(long seed, int time) {
		int e = getEastGate(seed);
		int w = getWestGate(seed);
		int o = outerWest(e, w, time);
		int gateKey = makeGateKey(o, w, e);
		return makeSeed(masterKey, seed, gateKey, w, o);
	}
	
	public long travelEast(long seed, int time) {
		int e = getEastGate(seed);
		int w = getWestGate(seed);
		int o = outerEast(w, e, time);
		int gateKey = makeGateKey(w, e, o);
		return makeSeed(masterKey, seed, gateKey, o, e);
	}
	
	public long randomTravel(Random random) {
		return random.nextLong() & (maxSeed-1L);
	}

	private int outerWest(int outerEast, int inner, int time) {
		return dmask(dmask(outerEast, timeMap[time]), dmask(inner, innerMask));
	}

	private int outerEast(int outerWest, int inner, int time) {
		return dmask(dmask(outerWest, dmask(inner, innerMask)), timeMap[time]);
	}
	
	private static int[] createTimeMap(Random random) {
		Shuffle sh = new Shuffle(base);
		int[] out = new int[base];
		for(int i=0; i<base; i++)
			out[i] = sh.next(random);
		return out;
	}
	
	private static int dmask(int x, int m, int base) {
		return (m - x + base) % base;
	}

	private static int dmask(int x, int m) {
		return dmask(x%baseLo, m%baseLo, baseLo) + dmask(x/baseLo, m/baseLo, baseHi)*baseLo;
	}
	
	private static long dmaskAll(long x, long m, int digits) {
		long res = 0;
		long mul = 1;
		for(int i=0; i<digits; i++) {
			res = res + mul*dmask((int)(x%base), (int)(m%base));
			x /= base;
			m /= base;
			mul *= base;
		}
		return res;
	}
	
	private static int zmask(int x, int base) {
		return (x*2) % base;
	}
	
	private static int zmask(int x) {
		return zmask(x%baseLo, baseLo) + zmask(x/baseLo, baseHi)*baseLo;
	}
	
	private static long zmaskSet(long x, int[] m) {
		long res = 0;
		long mul = 1;
		for(int i=0; i<m.length; i++) {
			int v = zmask((int)(x%base));
			if(m[i]>0)
				v = m[i]-1;
			res = res + mul*v;
			x /= base;
			mul *= base;
		}
		return res;
	}
	
	public static int getDigit(long seed, int d) {
		for(int i=0; i<d; i++)
			seed /= base;
		return (int)(seed % base);
	}
	
	public static int getWestGate(long seed) {
		return getDigit(seed, 0);
	}

	public static int getEastGate(long seed) {
		return getDigit(seed, 1);
	}
	
	private static int makeGateKey(int outerWest, int inner, int outerEast) {
		return outerEast*base*base + outerWest*base + inner;
	}
	
	private static long makeSeedMask(long salt, int gateKey, long seed) {
		long gateSeed = RandomSeed.seedX(salt, gateKey);
		// return gateSeed & (maxSeed-1L);
		Random random = new Random(gateSeed);
		int[] m = new int[7];
		int d0 = random.nextInt(7);
		m[d0] = random.nextInt(base)+1;
		int d1;
		do {
			d1 = random.nextInt(7);
		} while(d0==d1);
		m[d1] = random.nextInt(base)+1;
		return zmaskSet(seed, m);
	}
	
	private static long makeSeed(long salt, long prevSeed, int gateKey, int eastGate, int westGate) {
		final long base2 = base*base;
		long seed = prevSeed/base2;
		long mask = makeSeedMask(salt, gateKey, seed);
		seed = dmaskAll(seed, mask, 7);
		seed *= base2;
		seed += eastGate*base + westGate;
		return seed;
	}

}
