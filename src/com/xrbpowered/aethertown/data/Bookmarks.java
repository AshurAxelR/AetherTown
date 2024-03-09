package com.xrbpowered.aethertown.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.RegionCache;

public class Bookmarks {

	private static final String formatId = "AetherTown.Bookmarks.0";
	
	public static final int numBookmarks = 10;

	public static LevelInfo[] bookmarks = new LevelInfo[numBookmarks];
	
	private Bookmarks() {}

	private static LevelRef[] savedBookmarks = null;
	
	public static boolean isEmpty() {
		for(int i=0; i<numBookmarks; i++) {
			if(bookmarks[i] != null)
				return false;
		}
		return true;
	}
	
	public static void reset() {
		for(int i=0; i<numBookmarks; i++)
			bookmarks[i] = null;
	}
	
	public static void init(RegionCache regions) {
		reset();
		if(savedBookmarks!=null && AetherTown.settings.allowBookmaks) {
			int n = Math.min(numBookmarks, savedBookmarks.length);
			for(int i=0; i<n; i++) {
				LevelRef v = savedBookmarks[i];
				if(v!=null)
					bookmarks[i] = v.find(regions);
			}
			regions.verifyBookmarks(bookmarks);
		}
		savedBookmarks = null;
	}
	
	public static boolean load(InputStream ins) {
		try {
			DataInputStream in = new DataInputStream(ins);
			
			if(!formatId.equals(in.readUTF()))
				throw new IOException("Bad file format");
			
			int count = 0;
			int num = Math.min(in.readInt(), numBookmarks);
			savedBookmarks = new LevelRef[num];
			for(int i=0; i<num; i++) {
				if(in.readBoolean()) { // not null
					long seed = in.readLong();
					int x = in.readShort();
					int z = in.readShort();
					savedBookmarks[i] = new LevelRef(seed, x, z);
					count++;
				}
			}
			
			System.out.printf("Bookmarks loaded: %d\n", count);
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't load bookmarks: "+e.getMessage());
			savedBookmarks = null;
			return false;
		}
	}
	
	public static boolean save(OutputStream outs) {
		try {
			DataOutputStream out = new DataOutputStream(outs);
			
			out.writeUTF(formatId);
			out.writeInt(numBookmarks);
			for(int i=0; i<numBookmarks; i++) {
				LevelInfo info = bookmarks[i];
				if(info!=null) {
					out.writeBoolean(true);
					out.writeLong(info.region.seed);
					out.writeShort(info.x0);
					out.writeShort(info.z0);
				}
				else {
					out.writeBoolean(false);
				}
			}
			
			System.out.println("Bookmarks saved");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't save bookmarks: "+e.getMessage());
			return false;
		}
	}
}
