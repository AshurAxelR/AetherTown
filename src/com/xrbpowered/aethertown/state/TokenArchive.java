package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.xrbpowered.aethertown.state.items.TravelTokenItem;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class TokenArchive {

	private static final String formatId = "AetherTown.TokenArchive.1";
	
	public static class ArchiveEntry implements Comparable<ArchiveEntry> {
		public final NamedLevelRef destination;
		public final double timestamp;
		
		public ArchiveEntry(NamedLevelRef ref, double time) {
			this.destination = ref;
			this.timestamp = time;
		}
		
		public ArchiveEntry(NamedLevelRef ref) {
			this(ref, WorldTime.time);
		}
		
		public int getRegionIndex() {
			return RegionVisits.getRegionIndex(destination.regionSeed);
		}
		
		@Override
		public int compareTo(ArchiveEntry o) {
			int res = Integer.compare(getRegionIndex(), o.getRegionIndex());
			if(res==0)
				res = Double.compare(timestamp, o.timestamp);
			return res;
		}
	}
	
	private static HashMap<LevelRef, ArchiveEntry> archive = new HashMap<>();
	
	private TokenArchive() {
	}

	public static void reset() {
		archive.clear();
	}
	
	public static boolean isEmpty() {
		return archive.isEmpty();
	}
	
	public static int count() {
		return archive.size();
	}
	
	public static boolean contains(LevelRef ref) {
		return archive.containsKey(ref);
	}
	
	public static boolean add(TravelTokenItem token) {
		ArchiveEntry t = new ArchiveEntry(token.destination);
		return archive.putIfAbsent(token.destination, t) == null;
	}
	
	public static boolean remove(LevelRef ref) {
		return archive.remove(ref)!=null;
	}
	
	public static ArrayList<ArchiveEntry> getList() {
		ArrayList<ArchiveEntry> list = new ArrayList<>(archive.size());
		list.addAll(archive.values());
		list.sort(null);
		return list;
	}
	
	public static boolean load(InputStream ins) {
		try {
			DataInputStream in = new DataInputStream(ins);
			
			if(!formatId.equals(in.readUTF()))
				throw new IOException("Bad file format");
			
			HashMap<LevelRef, ArchiveEntry> archive = new HashMap<>();
			int num = in.readInt();
			for(int i=0; i<num; i++) {
				NamedLevelRef ref = NamedLevelRef.load(in);
				double time = in.readDouble();
				archive.put(ref, new ArchiveEntry(ref, time));
			}
			
			TokenArchive.archive = archive;
			System.out.println("Archive loaded");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't load archive");
			e.printStackTrace();
			reset();
			return false;
		}
	}
	
	public static boolean save(OutputStream outs) {
		try {
			DataOutputStream out = new DataOutputStream(outs);
			
			out.writeUTF(formatId);
			ArrayList<ArchiveEntry> list = getList();
			out.writeInt(list.size());
			for(ArchiveEntry e : list) {
				NamedLevelRef.save(out, e.destination);
				out.writeDouble(e.timestamp);
			}
			
			System.out.println("Archive saved");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't save archive");
			e.printStackTrace();
			return false;
		}
	}
	
}
