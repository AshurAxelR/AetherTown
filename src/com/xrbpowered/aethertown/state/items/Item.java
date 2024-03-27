package com.xrbpowered.aethertown.state.items;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.xrbpowered.aethertown.state.HouseTileRef;
import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.state.items.FoodItem.FoodItemType;
import com.xrbpowered.aethertown.world.Tile;

public abstract class Item implements Comparable<Item> {

	public final ItemType type;
	public final double timestamp;
	
	public Item(ItemType type, double time) {
		this.type = type;
		this.timestamp = time;
	}
	
	public boolean isConsumable() {
		return false;
	}
	
	public boolean isUseEnabled(Tile tile, boolean alt) {
		return true;
	}
	
	public boolean useItem(Tile tile, boolean alt) {
		return true;
	}
	
	public String getName() {
		return type.name;
	}
	
	public String getFullName() {
		return getName();
	}

	public String getUseActionName() {
		return null;
	}
	
	public String getInfoHtml(Tile tile, boolean alt) {
		return null;
	}
	
	public boolean markDot(Tile tile, boolean alt) {
		return false;
	}
	
	@Override
	public int compareTo(Item o) {
		int res = type.compareTo(o.type);
		if(res==0)
			res = Double.compare(timestamp, o.timestamp);
		return res;
	}

	public static Item load(DataInputStream in) throws IOException {
		ItemType type = ItemType.values()[in.readInt()];
		double time = in.readDouble();
		switch(type) {
			case travelToken:
				return new TravelTokenItem(NamedLevelRef.load(in), time);
			case houseKey:
				return new HouseKeyItem(HouseTileRef.load(in), time);
			case map:
				return new LevelMapItem(NamedLevelRef.load(in), time);
			case regionMap:
				return new RegionMapItem(in.readLong(), time);
			case trinket:
				return new TrinketItem(NamedLevelRef.load(in), time);
			case foodItem:
				return new FoodItem(FoodItemType.values()[in.readInt()], time);
			case groceries:
				return new GroceriesItem(time);
		}
		return null; // should not happen
	}

	public static void save(DataOutputStream out, Item aitem) throws IOException {
		out.writeInt(aitem.type.ordinal());
		out.writeDouble(aitem.timestamp);
		switch(aitem.type) {
			case travelToken:
				NamedLevelRef.save(out, ((TravelTokenItem) aitem).destination);
				return;
			case houseKey:
				HouseTileRef.save(out, ((HouseKeyItem) aitem).house);
				return;
			case map:
				NamedLevelRef.save(out, ((LevelMapItem) aitem).level);
				return;
			case regionMap:
				out.writeLong(((RegionMapItem) aitem).regionSeed);
				return;
			case trinket:
				NamedLevelRef.save(out, ((TrinketItem) aitem).location);
				return;
			case foodItem:
				out.writeInt(((FoodItem) aitem).food.ordinal());
				return;
			case groceries:
				return;
		}
	}
}
