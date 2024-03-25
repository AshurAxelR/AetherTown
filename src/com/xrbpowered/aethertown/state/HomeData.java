package com.xrbpowered.aethertown.state;

import static com.xrbpowered.aethertown.AetherTown.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Random;

import com.xrbpowered.aethertown.state.items.HouseKeyItem;
import com.xrbpowered.aethertown.utils.RandomSeed;
import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseRole;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class HomeData {

	private static final String formatId = "AetherTown.HomeData.0";
	
	private static LinkedHashMap<Integer, HomeData> homes = new LinkedHashMap<>();
	
	public final HouseTileRef ref;
	
	private transient int index = -1;
	
	// TODO home improvements
	// TODO home inventory

	private HomeData(HouseTileRef ref) {
		this.ref = ref;
	}

	private HomeData(HouseGenerator house) {
		this.ref = new HouseTileRef(house);
	}
	
	public int getIndex() {
		return index;
	}
	
	public static boolean load(InputStream ins) {
		try {
			DataInputStream in = new DataInputStream(ins);
			
			if(!formatId.equals(in.readUTF()))
				throw new IOException("Bad file format");
			
			LinkedHashMap<Integer, HomeData> homes = new LinkedHashMap<>();
			int numHomes = in.readInt();
			for(int i=0; i<numHomes; i++) {
				HouseTileRef ref = HouseTileRef.load(in);
				HomeData home = new HomeData(ref);
				home.index = i;
				homes.put(ref.level.hashCode(), home);
			}
			
			HomeData.homes = homes;
			System.out.printf("Homes loaded (%d claimed)\n", numHomes);
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't load homes");
			e.printStackTrace();
			HomeData.homes.clear();
			return false;
		}
	}
	
	public static boolean save(OutputStream outs) {
		try {
			DataOutputStream out = new DataOutputStream(outs);
			
			out.writeUTF(formatId);
			out.writeInt(homes.size());
			for(HomeData home : homes.values()) {
				HouseTileRef.save(out, home.ref);
			}
			
			System.out.println("Homes saved");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't save homes");
			e.printStackTrace();
			return false;
		}
	}
	
	private static void updateIndices() {
		int index = 0;
		for(HomeData home : homes.values()) {
			home.index = index++;
		}
	}
	
	public static int totalClaimed() {
		return homes.size();
	}
	
	public static Collection<HomeData> list() {
		return homes.values();
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
	
	public static HomeData forHouse(HouseTileRef ref) {
		HomeData h = homes.get(ref.level.hashCode());
		if(h!=null && h.ref.equals(ref))
			return h;
		else
			return null;
	}

	public static HomeData claim(HouseGenerator house) {
		LevelInfo level = house.startToken.level.info;
		if(hasLocalHome(level))
			return null;
		HomeData home = new HomeData(house);
		home.index = homes.size();
		homes.put(level.hashCode(), home);
		return home;
	}
	
	public static boolean  abandon(HomeData home) {
		if(!homes.remove(home.ref.level.hashCode(), home))
			return false;
		updateIndices();
		HouseKeyItem.removeKeys(player.backpack, home.ref); // TODO remove from all inventories
		return true;
	}

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
		out.sort(new Comparator<HouseGenerator>() {
			@Override
			public int compare(HouseGenerator o1, HouseGenerator o2) {
				return o1.index - o2.index;
			}
		});
		return out;
	}
	
}
