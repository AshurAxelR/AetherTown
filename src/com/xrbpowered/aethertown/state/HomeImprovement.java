package com.xrbpowered.aethertown.state;

import java.util.EnumSet;
import java.util.Random;

import com.xrbpowered.aethertown.utils.RandomSeed;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;

public enum HomeImprovement {

	kitchenware("Kitchenware", 750, 0.75f),
	boardGames("Board games", 550, 0.25f),
	guitar("Guitar", 1500, 0.05f),
	piano("Piano", 3000, 0.15f),
	tv("TV", 3500, 0.6f),
	console("Game console", 2000, 0.15f, tv),
	computer("Computer", 7000, 0.3f),
	art("Art supplies", 1500, 0.05f),
	books("Book collection", 1000, 0.9f);
	
	public final String name;
	public final int cost;
	public final HomeImprovement requires;
	private final float probability;
	
	private HomeImprovement(String name, int cost, float probability, HomeImprovement req) {
		this.name = name;
		this.cost = cost;
		this.probability = probability;
		this.requires = req;
	}

	private HomeImprovement(String name, int cost, float probability) {
		this(name, cost, probability, null);
	}

	public static EnumSet<HomeImprovement> generateDefaults(HouseGenerator house) {
		Random random = new Random(RandomSeed.seedX(house.startToken.level.info.seed+8704943L, house.index));
		EnumSet<HomeImprovement> set = EnumSet.noneOf(HomeImprovement.class);
		for(HomeImprovement imp : HomeImprovement.values()) {
			if(imp.requires!=null && !set.contains(imp.requires))
				continue;
			if(random.nextFloat()<imp.probability)
				set.add(imp);
		}
		return set;
	}

	public static int getBits(EnumSet<HomeImprovement> set) {
		int bits = 0;
		for(HomeImprovement imp : set)
			bits |= 1 << imp.ordinal();
		return bits;
	}

	public static void fromBits(int bits, EnumSet<HomeImprovement> set) {
		for(HomeImprovement imp : HomeImprovement.values()) {
			if((bits & (1 << imp.ordinal()))>0)
				set.add(imp);
		}
	}
}
