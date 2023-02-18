package com.xrbpowered.aethertown.world.region;

public enum LevelTerrainType {

	bottom(-110, -80, -80, -80, -100),
	hill(-100, 100, 20, -60, -80);
	
	public final int miny, maxy;
	public final int starty, conny;
	public final int edgey;
	
	private LevelTerrainType(int miny, int maxy, int starty, int conny, int edgey) {
		this.miny = miny;
		this.maxy = maxy;
		this.starty = starty;
		this.conny = conny;
		this.edgey = edgey;
	}
	
	public static LevelTerrainType nullTerrain = bottom;
	
}
