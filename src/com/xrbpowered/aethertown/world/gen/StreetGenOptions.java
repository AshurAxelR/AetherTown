package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.WRandom;

public class StreetGenOptions {

	public static final int minLen = StreetGenerator.streetGap; 

	public static final StreetGenOptions hill = new StreetGenOptions(0.1, 0.4, 0.3, 0.2);
	public static final StreetGenOptions low = new StreetGenOptions(0.2, 0.4, 0.3, 0.1);
	public static final StreetGenOptions flat = new StreetGenOptions(0.4, 0.4, 0.1, 0.1).stepsLen(minLen, 3, 2).extend(0.4, 0.3, 0.3);
	public static final StreetGenOptions peak = new StreetGenOptions(0.1, 0.3, 0.2, 0.4).flatLen(2, 3, 3).extend(0.1, 0.5, 0.4);

	public static final StreetGenOptions defaultOpt = hill;

	private static final int[] dyopt = {0, -1, 1, -2, 2, -4, 4};

	public final WRandom dyoptw;
	public WRandom wcross = new WRandom(0.25, 0.4, 0.35);
	
	public int flatMinLen = 3;
	public int flatMaxLenBase = 3;
	public int flatMaxLenRand = 5;
	public int stepsMinLen = minLen; 
	public int stepsMaxLenBase = 2;
	public int stepsMaxLenRand = 4;

	public StreetGenOptions(double w0, double w1, double w2, double w4) {
		this.dyoptw = new WRandom(w0*2.0, w1, w1,w2, w2, w4, w4);
	}
	
	public StreetGenOptions flatLen(int min, int maxBase, int maxRand) {
		flatMinLen = min;
		flatMaxLenBase = maxBase;
		flatMaxLenRand = maxRand;
		return this;
	}

	public StreetGenOptions stepsLen(int min, int maxBase, int maxRand) {
		stepsMinLen = min;
		stepsMaxLenBase = maxBase;
		stepsMaxLenRand = maxRand;
		return this;
	}
	
	public StreetGenOptions extend(double wcross, double wbridge, double wbasic) {
		this.wcross = new WRandom(wcross, wbridge, wbasic);
		return this;
	}

	public int getDy(Random random) {
		return dyopt[dyoptw.next(random)];
	}
	
	public int getMinLen(int absdy) {
		return (absdy>1) ? stepsMinLen : flatMinLen;
	}
	
	public int getMaxLen(int absdy, Random random) {
		return (absdy>1) ? stepsMaxLenBase+random.nextInt(stepsMaxLenRand) : flatMaxLenBase+random.nextInt(flatMaxLenRand);
	}

}
