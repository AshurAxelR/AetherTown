package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;

public class HouseTileRef {

	public final NamedLevelRef level;
	public final int x, z;
	public final int houseIndex;
	
	public HouseTileRef(NamedLevelRef level, int x, int z, int houseIndex) {
		this.level = level;
		this.x = x;
		this.z = z;
		this.houseIndex = houseIndex;
	}

	public HouseTileRef(HouseGenerator house) {
		this.level = new NamedLevelRef(house.startToken.level.info);
		this.x = house.startToken.x;
		this.z = house.startToken.z;
		this.houseIndex = house.index;
	}

	public String getFullAddress() {
		return String.format("%s %d, %s", RegionVisits.getRegionTitle(level.regionSeed, true), houseIndex+1, level.name);
	}
	
	public boolean isHouse(HouseGenerator house) {
		if(x==house.startToken.x && z==house.startToken.z && level.isLevel(house.startToken.level.info)) {
			if(house.index!=houseIndex) {
				System.err.printf("House tile reference mismatch at [%d, %d]\n", x, z);
				return false;
			}
			else
				return true;
		}
		else
			return false;
	}

	@Override
	public boolean equals(Object obj) {
		HouseTileRef ref = (HouseTileRef) obj;
		return this.x==ref.x && this.z==ref.z &&
				this.houseIndex==ref.houseIndex && level.equals(ref.level);
	}

	public static HouseTileRef load(DataInputStream in) throws IOException {
		NamedLevelRef level = NamedLevelRef.load(in);
		int x = in.readShort();
		int z = in.readShort();
		int index = in.readShort();
		return new HouseTileRef(level, x, z, index);
	}

	public static void save(DataOutputStream out, HouseTileRef ref) throws IOException {
		NamedLevelRef.save(out, ref.level);
		out.writeShort(ref.x);
		out.writeShort(ref.z);
		out.writeShort(ref.houseIndex);
	}
}
