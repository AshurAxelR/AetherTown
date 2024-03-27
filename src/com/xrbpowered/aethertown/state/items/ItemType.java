package com.xrbpowered.aethertown.state.items;

public enum ItemType {

	travelToken("Travel Token"),
	houseKey("House Key"),
	map("Map"),
	regionMap("Region Map"),
	trinket("Trinket"),
	foodItem(null),
	groceries("Groceries");
	
	public final String name;
	
	private ItemType(String name) {
		this.name = name;
	}

}
