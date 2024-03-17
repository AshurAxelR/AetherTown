package com.xrbpowered.aethertown.state;

import java.util.Arrays;
import java.util.Comparator;

import com.xrbpowered.aethertown.state.items.Item;

public class Inventory {

	public static final int backpackSize = 12; 
	
	public final int size;
	
	private final Item[] slots;
	
	private int free;
	
	public Inventory(int size) {
		this.size = size;
		this.slots = new Item[size];
		this.free = size;
	}

	public Inventory() {
		this(backpackSize);
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
	
	private void sort() {
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
	
	public Item remove(int index) {
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
	
}
