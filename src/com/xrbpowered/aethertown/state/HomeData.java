package com.xrbpowered.aethertown.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.xrbpowered.aethertown.utils.RandomSeed;
import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseRole;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class HomeData {

	private static final String formatId = "AetherTown.HomeData.0";
	
	private static HashMap<Integer, HomeData> homes = new HashMap<>();
	
	public final HouseTileRef ref;
	
	// TODO home improvements
	// TODO home inventory
	
	private HomeData(HouseGenerator house) {
		this.ref = new HouseTileRef(house);
	}
	
	public static int totalClaimed() {
		return homes.size();
	}
	
	public static boolean hasLocalHome(LevelInfo level) {
		HomeData h = homes.get(level.hashCode());
		return h!=null && h.ref.level.isLevel(level);
	}

	public static HomeData getLocal(LevelInfo level) {
		HomeData h = homes.get(level.hashCode());
		if(h!=null && h.ref.level.isLevel(level))
			return h;
		else
			return null;
	}

	public static HomeData claim(HouseGenerator house) {
		LevelInfo level = house.startToken.level.info;
		if(hasLocalHome(level))
			return null;
		HomeData home = new HomeData(house);
		homes.put(level.hashCode(), home);
		return home;
	}
	
	// TODO abandon: remove all keys

	public static ArrayList<HouseGenerator> selectRandomRes(Level level, int count) {
		ArrayList<HouseGenerator> res = new ArrayList<>();
		for(HouseGenerator h : level.houses) {
			if(h.role==HouseRole.residential || h.addRole==HouseRole.residential)
				res.add(h);
		}
		int num = res.size();
		if(num==0)
			return null;
		if(count>num)
			count = num;
		
		Random random = new Random(RandomSeed.seedX(level.info.seed+6973L, WorldTime.getDay()));
		Shuffle shuffle = new Shuffle(num);
		ArrayList<HouseGenerator> out = new ArrayList<>();
		for(int i=0; i<count; i++)
			out.add(res.get(shuffle.next(random)));
		return out;
	}
	
}
