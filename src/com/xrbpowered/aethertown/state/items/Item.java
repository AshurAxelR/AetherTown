package com.xrbpowered.aethertown.state.items;

public abstract class Item implements Comparable<Item> {

	public final ItemType type;
	
	public Item(ItemType type) {
		this.type = type;
	}
	
	public String getName() {
		return type.name;
	}

	@Override
	public int compareTo(Item o) {
		return type.compareTo(o.type);
	}

}
