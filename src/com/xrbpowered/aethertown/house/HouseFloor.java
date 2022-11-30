package com.xrbpowered.aethertown.house;

public class HouseFloor {

	public final int floor;
	public Room[][] map;
	
	public HouseFloor(HousePlot plot, int floor) {
		this.floor = floor;
		this.map = new Room[plot.width][plot.depth];
	}

}
