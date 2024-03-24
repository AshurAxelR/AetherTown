package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;
import com.xrbpowered.aethertown.world.tiles.HouseT;

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

	public boolean isTile(Tile tile) {
		return tile.t==HouseT.template && isHouse((HouseGenerator) tile.sub.parent);
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, LevelVisit.tileHash(x, z));
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
