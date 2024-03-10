package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.xrbpowered.aethertown.world.region.LevelInfo;

public class Bookmarks {

	private static final String formatId = "AetherTown.Bookmarks.1";
	
	public static final int numBookmarks = 10;

	public static final NamedLevelRef[] bookmarks = new NamedLevelRef[numBookmarks];
	
	private Bookmarks() {}

	public static boolean isBookmarked(LevelInfo level) {
		for(int i=0; i<numBookmarks; i++) {
			if(level.isRef(bookmarks[i]))
				return true;
		}
		return false;
	}
	
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
	
	public static boolean load(InputStream ins) {
		try {
			DataInputStream in = new DataInputStream(ins);
			
			if(!formatId.equals(in.readUTF()))
				throw new IOException("Bad file format");
			
			int count = 0;
			reset();
			int num = in.readInt();
			for(int i=0; i<num; i++) {
				if(in.readBoolean()) { // not null
					long seed = in.readLong();
					int x = in.readShort();
					int z = in.readShort();
					String name = in.readUTF();
					if(i<numBookmarks) {
						bookmarks[i] = new NamedLevelRef(seed, x, z, name);
						count++;
					}
				}
			}
			
			System.out.printf("Bookmarks loaded: %d\n", count);
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't load bookmarks: "+e.getMessage());
			reset();
			return false;
		}
	}
	
	public static boolean save(OutputStream outs) {
		try {
			DataOutputStream out = new DataOutputStream(outs);
			
			out.writeUTF(formatId);
			out.writeInt(numBookmarks);
			for(int i=0; i<numBookmarks; i++) {
				NamedLevelRef ref = bookmarks[i];
				if(ref!=null) {
					out.writeBoolean(true);
					out.writeLong(ref.regionSeed);
					out.writeShort(ref.x);
					out.writeShort(ref.z);
					out.writeUTF(ref.name);
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
