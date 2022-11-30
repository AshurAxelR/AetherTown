package com.xrbpowered.aethertown.utils;

import java.util.Random;

public enum Dir {

	north(0, -1),
	east(1, 0),
	south(0, 1),
	west(-1, 0);
	
	public final int dx, dz;
	
	private Dir(int dx, int dz) {
		this.dx = dx;
		this.dz = dz;
	}
	
	public Dir cw() {
		return values()[(ordinal()+1)%4];
	}

	public Dir flip() {
		return values()[(ordinal()+2)%4];
	}

	public Dir ccw() {
		return values()[(ordinal()+3)%4];
	}
	
	public Dir apply(Dir d) {
		return values()[(ordinal()+d.ordinal())%4];
	}

	public Dir[] next() {
		return next[ordinal()];
	}

	public Corner leftCorner() {
		return Corner.values()[(ordinal()+1)%4];
	}

	public Corner rightCorner() {
		return Corner.values()[ordinal()];
	}

	public static Dir random(Random random) {
		return values()[random.nextInt(4)];
	}
	
	public static final Dir[][] next = {
		{north, east, west},
		{east, south, north},
		{south, west, east},
		{west, north, south}
	};
}
