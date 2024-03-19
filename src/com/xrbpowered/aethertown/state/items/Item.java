package com.xrbpowered.aethertown.state.items;

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

}
