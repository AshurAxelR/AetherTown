package com.xrbpowered.aethertown.house;

public class HousePlot {

	public final int width, depth;
	public HouseFloor[] floors;

	public HousePlot(int width, int depth, int floors) {
		this.width = width;
		this.depth = depth;
		this.floors = new HouseFloor[floors];
		for(int i=0; i<floors; i++)
			this.floors[i] = new HouseFloor(this, i);
	}
	
	public boolean isInside(int x, int z) {
		return x>=0 && x<width && z>=0 && z<depth;
	}


}
