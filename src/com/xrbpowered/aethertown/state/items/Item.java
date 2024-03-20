package com.xrbpowered.aethertown.state.items;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.xrbpowered.aethertown.state.NamedLevelRef;

public abstract class Item implements Comparable<Item> {

	public final ItemType type;
	
	public Item(ItemType type) {
		this.type = type;
	}
	
	public void useItem() {
	}
	
	public String getName() {
		return type.name;
	}

	public String getUseActionName() {
		return null;
	}
	
	public String getInfoHtml() {
		return null;
	}
	
	@Override
	public int compareTo(Item o) {
		return type.compareTo(o.type);
	}

	public static Item load(DataInputStream in) throws IOException {
		ItemType type = ItemType.values()[in.readInt()];
		switch(type) {
			case travelToken:
				return new TravelTokenItem(NamedLevelRef.load(in));
			case map:
				return new LevelMapItem(NamedLevelRef.load(in));
			case regionMap:
				return new RegionMapItem(in.readLong());
		}
		return null; // should not happen
	}

	public static void save(DataOutputStream out, Item item) throws IOException {
		out.writeInt(item.type.ordinal());
		switch(item.type) {
			case travelToken:
				NamedLevelRef.save(out, ((TravelTokenItem) item).destination);
				return;
			case map:
				NamedLevelRef.save(out, ((LevelMapItem) item).level);
				return;
			case regionMap:
				out.writeLong(((RegionMapItem) item).regionSeed);
				return;
		}
	}
}
