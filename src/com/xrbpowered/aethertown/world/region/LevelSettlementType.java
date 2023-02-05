package com.xrbpowered.aethertown.world.region;

public enum LevelSettlementType {

	none("Inn", 0, 0),
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
	
}
