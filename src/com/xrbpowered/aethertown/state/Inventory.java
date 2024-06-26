package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseRole;
import com.xrbpowered.aethertown.world.tiles.HouseT;

public class Inventory {

	public static final int backpackSize = 12; 
	
	public final transient String name;
	public final int size;
	private final Item[] slots;
	private int free;
	
	public Inventory(int size, String name) {
		this.size = size;
		this.name = name;
		this.slots = new Item[size];
		this.free = size;
	}

	public Inventory() {
		this(backpackSize, "BACKPACK");
	}

	public boolean isFull() {
		return free==0;
	}
	
	public int getFreeSlots() {
		return free;
	}
	
	public boolean isEmptySlot(int index) {
		return slots[index]==null;
	}
	
	public void sort() {
		Arrays.sort(slots, new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				if(o1==null && o2==null)
					return 0;
				else if(o1==null)
					return 1;
				else if(o2==null)
					return -1;
				else
					return o1.compareTo(o2);
			}
		});
	}
	
	public boolean put(Item item) {
		if(free>0) {
			slots[size-free] = item;
			free--;
			sort();
			return true;
		}
		else
			return false;
	}
	
	public Item get(int index) {
		return slots[index];
	}
	
	public Item remove(int index, boolean sort) {
		if(slots[index]!=null) {
			Item item = slots[index];
			slots[index] = null;
			free++;
			sort();
			return item;
		}
		else
			return null;
	}

	public Item remove(int index) {
		return remove(index, true);
	}

	public void loadItems(DataInputStream in) throws IOException {
		if(in.readByte()!=size)
			throw new IOException("Inventory size mismatch");
		int n = in.readByte();
		for(int i=0; i<size; i++) {
			slots[i] = (i<n) ? Item.load(in) : null;
		}
		free = size - n;
		sort();
	}

	public void saveItems(DataOutputStream out) throws IOException {
		out.writeByte(size);
		out.writeByte(size - free);
		for(int i=0; i<size; i++) {
			if(slots[i]!=null)
				Item.save(out, slots[i]);
		}
	}
	
	public static Inventory[] getTileInventory(Tile tile, boolean alt) {
		if(tile.t==HouseT.template) {
			HouseGenerator house = (HouseGenerator) tile.sub.parent;
			if(house.getRole(alt)!=HouseRole.residential)
				return null;
			HomeData home = HomeData.forHouse(house);
			return home==null ? null : home.storage;
		}
		else
			return null;
	}
}
