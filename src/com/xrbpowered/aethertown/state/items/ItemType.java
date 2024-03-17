package com.xrbpowered.aethertown.state.items;

public enum ItemType {

	travelToken("Travel Token"),
	houseKey("House Key"),
	roomKey("Room Key"),
	map("Map"),
	regionMap("Region Map");
	
	public final String name;
	
	private ItemType(String name) {
		this.name = name;
	}
	
	/*public Item createItem() {
		// TODO only for loading
		switch(this) {
			case travelToken:
				return new TravelTokenItem();
			default:
				throw new UnsupportedOperationException();
		}
	}*/
	
}
