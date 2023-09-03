package com.xrbpowered.aethertown.world.region;

public enum RegionMode {

	linear(512, 128),
	oneLevel(16, 16),
	smallPeak(16, 16);
	
	public final int sizex, sizez;
	
	private RegionMode(int sizex, int sizez) {
		this.sizex = sizex;
		this.sizez = sizez;
	}
	
}
