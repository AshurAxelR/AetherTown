package com.xrbpowered.aethertown.world.region;

public enum LevelTerrainType {

	bottom(-110, -80, -80, -80, -100, 10),
	hill(-100, 100, 20, -80, -40, 20);
	
	public final int miny, maxy;
	public final int starty, conny;
	public final int edgey, edgeAmp;
	
	private LevelTerrainType(int miny, int maxy, int starty, int conny, int edgey, int edgeAmp) {
		this.miny = miny;
		this.maxy = maxy;
		this.starty = starty;
		this.conny = conny;
		this.edgey = edgey;
		this.edgeAmp = edgeAmp;
	}
	
	public static LevelTerrainType nullTerrain = bottom;
	
}
