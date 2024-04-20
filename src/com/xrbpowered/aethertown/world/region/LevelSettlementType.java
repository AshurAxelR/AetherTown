package com.xrbpowered.aethertown.world.region;

import com.xrbpowered.aethertown.utils.Rand;
import com.xrbpowered.aethertown.utils.WRandom;

public enum LevelSettlementType {

	none("Park", 0, 0, 0),
	inn("Inn", 1, 2, 0),
	outpost("Outpost", 4, 10, 1),
	village("Village", 10, 30, 2),
	smallTown("Town", 30, 50, 3),
	largeTown("Town", 30, 80, 3),
	smallCity("City", 90, 120, 5),
	mediumCity("City", 90, 160, 5),
	largeCity("City", 90, 200, 5);
	
	public final String title;
	public final int minHouses, maxHouses;
	public final int claimOptions;
	
	private LevelSettlementType(String title, int minHouses, int maxHouses, int claimOptions) {
		this.title = title;
		this.minHouses = minHouses;
		this.maxHouses = maxHouses;
		this.claimOptions = claimOptions;
	}
	
	public LevelSettlementType demote() {
		if(this==none)
			return this;
		else
			return values()[ordinal()-1];
	}
	
	public int getStreetMargin(int levelSize, boolean nocap) {
		int size = (int)Math.ceil(Math.sqrt(maxHouses*40)/4.0)*4;
		if(!nocap && size<20) size = 20;
		return (levelSize-size)/2;
	}

	public int getStreetMargin(int levelSize) {
		return getStreetMargin(levelSize, false);
	}

	private static final WRandom[] w = {
		new WRandom(0.7, 0.2, 0.05, 0.05),
		new WRandom(0.4, 0.2, 0.04, 0.15, 0.1, 0.1, 0.01),
		new WRandom(0, 0, 0, 0.05, 0.25, 0.55, 0.2, 0.05),
	};
	public static LevelSettlementType random(int levelSize, Rand random) {
		return values()[w[levelSize-1].next(random)];
	}
	
}
