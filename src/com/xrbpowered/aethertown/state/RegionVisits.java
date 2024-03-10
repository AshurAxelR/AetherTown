package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class RegionVisits {

	private static final String formatId = "AetherTown.RegionVisits.0";
	
	private static HashMap<Long, RegionVisits> regions = new HashMap<>();

	public final int index;
	private final HashMap<Integer, LevelRef> visited = new HashMap<>();
	
	private RegionVisits(int index) {
		this.index = index;
	}
	
	public void add(LevelRef ref) {
		visited.put(ref.hashCode(), ref);
	}

	public static boolean load(InputStream ins) {
		try {
			DataInputStream in = new DataInputStream(ins);
			
			if(!formatId.equals(in.readUTF()))
				throw new IOException("Bad file format");
			
			HashMap<Long, RegionVisits> regions = new HashMap<>();
			int totalVisited = 0;
			int numRegions = in.readInt();
			for(int i=0; i<numRegions; i++) {
				long seed = in.readLong();
				int index = in.readInt();
				RegionVisits r = new RegionVisits(index);
				regions.put(seed, r);
				int numLevels = in.readInt();
				for(int j=0; j<numLevels; j++) {
					int x = in.readShort();
					int z = in.readShort();
					r.add(new LevelRef(seed, x, z));
				}
				totalVisited += numLevels;
			}
			
			RegionVisits.regions = regions;
			System.out.printf("Region visits loaded: visited %d levels in %d regions\n", totalVisited, numRegions);
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't load region visits: "+e.getMessage());
			RegionVisits.regions.clear();
			return false;
		}
	}
	
	public static boolean save(OutputStream outs) {
		try {
			DataOutputStream out = new DataOutputStream(outs);
			
			out.writeUTF(formatId);
			out.writeInt(regions.size());
			for(Map.Entry<Long, RegionVisits> e : regions.entrySet()) {
				out.writeLong(e.getKey());
				RegionVisits r = e.getValue();
				out.writeInt(r.index);
				out.writeInt(r.visited.size());
				for(LevelRef level : r.visited.values()) {
					out.writeShort(level.x);
					out.writeShort(level.z);
				}
			}
			
			System.out.println("Region visits saved");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't save region visits: "+e.getMessage());
			return false;
		}
	}
	
	public static String getRegionTitle(long regionSeed, boolean brief) {
		RegionVisits r = regions.get(regionSeed);
		if(r==null)
			return brief ? "" : "Region";
		else {
			String n = WorldTime.romanNumeral(r.index+1);
			return brief ? String.format("R%s.", n) : String.format("Region %s", n);
		}
	}
	
	public static boolean isVisited(LevelInfo level) {
		RegionVisits r = regions.get(level.region.seed);
		return r!=null && r.visited.containsKey(level.hashCode());
	}

	public static void visit(LevelInfo level) {
		long seed = level.region.seed;
		RegionVisits r = regions.get(seed);
		if(r==null) {
			r = new RegionVisits(regions.size());
			regions.put(seed, r);
			System.out.printf("Region *%d is now known as %s\n",
					seed%10000L, getRegionTitle(level.region.seed, false));
		}
		r.add(new LevelRef(level));
	}
}
