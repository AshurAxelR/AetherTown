package com.xrbpowered.aethertown.utils;

import java.util.Random;

public enum Dir {

	north(0, -1),
	east(1, 0),
	south(0, 1),
	west(-1, 0);
	
	public final int dx, dz;
	public final float rotation;
	
	private Dir(int dx, int dz) {
		this.dx = dx;
		this.dz = dz;
		this.rotation = (float)Math.PI*ordinal()*0.5f;
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

	public Dir unapply(Dir d) {
		return values()[(ordinal()+4-d.ordinal())%4];
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

	public static Dir[] shuffle(Random random) {
		return permutations[random.nextInt(permutations.length)];
	}

	public static Dir[] shuffle(Rand random) {
		return permutations[random.nextInt(permutations.length)];
	}

	public static final Dir[][] next = {
		{north, east, west},
		{east, south, north},
		{south, west, east},
		{west, north, south}
	};
	
	private static final Dir[][] permutations = {
		{north, east, south, west},
		{east, north, south, west},
		{south, north, east, west},
		{north, south, east, west},
		{east, south, north, west},
		{south, east, north, west},
		{south, east, west, north},
		{east, south, west, north},
		{west, south, east, north},
		{south, west, east, north},
		{east, west, south, north},
		{west, east, south, north},
		{west, north, south, east},
		{north, west, south, east},
		{south, west, north, east},
		{west, south, north, east},
		{north, south, west, east},
		{south, north, west, east},
		{east, north, west, south},
		{north, east, west, south},
		{west, east, north, south},
		{east, west, north, south},
		{north, west, east, south},
		{west, north, east, south}
	};
}
