package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.xrbpowered.aethertown.world.region.LevelInfo;

public class NamedLevelRef extends LevelRef {
	
	public final String name;

	public NamedLevelRef(long seed, int x, int z, String name) {
		super(seed, x, z);
		this.name = name;
	}

	public NamedLevelRef(LevelInfo level) {
		super(level);
		this.name = level.name;
	}
	
	public String getFullName() {
		return String.format("%s %s", RegionVisits.getRegionTitle(regionSeed, true), name);
	}

	public static NamedLevelRef load(DataInputStream in) throws IOException {
		long seed = in.readLong();
		int x = in.readShort();
		int z = in.readShort();
		String name = in.readUTF();
		return new NamedLevelRef(seed, x, z, name);
	}

	public static void save(DataOutputStream out, NamedLevelRef ref) throws IOException {
		out.writeLong(ref.regionSeed);
		out.writeShort(ref.x);
		out.writeShort(ref.z);
		out.writeUTF(ref.name);
	}
}
