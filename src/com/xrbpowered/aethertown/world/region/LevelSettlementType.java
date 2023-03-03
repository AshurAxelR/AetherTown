package com.xrbpowered.aethertown.world.region;

import java.util.Random;

import com.xrbpowered.aethertown.utils.WRandom;

public enum LevelSettlementType {

	none("Park", 0, 0),
	inn("Inn", 1, 2),
	outpost("Outpost", 4, 10),
	village("Village", 10, 30),
	smallTown("Town", 30, 50),
	largeTown("Town", 30, 80);
	// city
	
	public final String title;
	public final int minHouses, maxHouses;
	
	private LevelSettlementType(String title, int minHouses, int maxHouses) {
		this.title = title;
		this.minHouses = minHouses;
		this.maxHouses = maxHouses;
	}
	
	public LevelSettlementType demote() {
		if(this==none)
			return this;
		else
			return values()[ordinal()-1];
	}
	
	public int getStreetMargin(int levelSize) {
		int size = (int)Math.ceil(Math.sqrt(maxHouses*40)/4.0)*4;
		if(size<20) size = 20;
		return (levelSize-size)/2;
	}
	
	private static final WRandom[] w = {
		new WRandom(0.6, 0.2, 0.15, 0.05, 0, 0),
		new WRandom(0.2, 0.1, 0.15, 0.3, 0.2, 0.05),
		new WRandom(0, 0, 0, 0.1, 0.3, 0.6),
	};
	public static LevelSettlementType random(int levelSize, Random random) {
		return values()[w[levelSize-1].next(random)];
	}
	
}
