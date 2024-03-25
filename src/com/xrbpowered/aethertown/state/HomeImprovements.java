package com.xrbpowered.aethertown.state;

public enum HomeImprovements {

	kitchenware("Kitchenware"),
	boardGames("Board games"),
	guitar("Guitar"),
	piano("Piano"),
	tv("TV"),
	console("Game console"),
	computer("Computer"),
	art("Art supplies"),
	books("Book collection");
	
	public final String name;
	
	private HomeImprovements(String name) {
		this.name = name;
	}
}
