package com.xrbpowered.aethertown.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class RegionVisits {

	private static final String path = SaveState.savePath+"visits.dat";
	private static final String formatId = "AetherTown.RegionVisits.0";
	
	private static HashMap<Long, RegionVisits> regions = new HashMap<>();

	public final int index;
	public final HashSet<LevelRef> visited = new HashSet<>();
	
	private RegionVisits(int index) {
		this.index = index;
	}

	public static void load(long uid) {
		try(ZipInputStream zip = new ZipInputStream(new FileInputStream(new File(path)))) {
			zip.getNextEntry();
			DataInputStream in = new DataInputStream(zip);
			
			if(!formatId.equals(in.readUTF()))
				throw new RuntimeException("Bad file format: "+path);
			if(in.readLong()!=uid)
				throw new RuntimeException("Save state UID mismatch");
			
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
					r.visited.add(new LevelRef(seed, x, z));
				}
				totalVisited += numLevels;
			}
			
			RegionVisits.regions = regions;
			System.out.println(path+" loaded");
			System.out.printf("Visited %d levels in %d regions\n", totalVisited, numRegions);
		}
		catch(FileNotFoundException e) {
			System.err.println("No data for region visits");
			RegionVisits.regions.clear();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void save(long uid) {
		try(ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(path)))) {
			zip.putNextEntry(new ZipEntry("RegionVisits"));
			DataOutputStream out = new DataOutputStream(zip);
			
			out.writeUTF(formatId);
			out.writeLong(uid);
			out.writeInt(regions.size());
			for(Map.Entry<Long, RegionVisits> e : regions.entrySet()) {
				out.writeLong(e.getKey());
				RegionVisits r = e.getValue();
				out.writeInt(r.index);
				out.writeInt(r.visited.size());
				for(LevelRef level : r.visited) {
					out.writeShort(level.x);
					out.writeShort(level.z);
				}
			}
			
			zip.closeEntry();
			System.out.println(path+" saved");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getRegionTitle(Region region) {
		RegionVisits r = regions.get(region.seed);
		return (r==null) ? "Region" : "Region "+WorldTime.romanNumeral(r.index+1);
	}
	
	public static boolean isVisited(LevelInfo level) {
		RegionVisits r = regions.get(level.region.seed);
		return r!=null && r.visited.contains(level.ref);
	}

	public static void visit(LevelInfo level) {
		long seed = level.region.seed;
		RegionVisits r = regions.get(seed);
		if(r==null) {
			r = new RegionVisits(regions.size());
			regions.put(seed, r);
			System.out.printf("Region *%d is now known as Region %s\n",
					seed%10000L, getRegionTitle(level.region));
		}
		r.visited.add(level.ref);
	}
}
