package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.utils.Dir;

public class Tile {

	public static final float size = 4f; // 4m = 160px = 16x25cm stairs
	public static final float ysize = 0.525f; // 52.5cm = 3*17.5cm stairs or 21px = 3*7px stairs. 1 floor = 6y = 315cm = 126px = 18 stairs
	
	public final Template t;

	public Level level = null;
	public int x, z;
	public int basey = 0;
	public Dir d;
	
	public Tile(Template t, Dir d) {
		this.t = t;
		this.d = d;
	}
	
	public void place(Level level, int x, int y, int z) {
		this.level = level;
		this.x = x;
		this.basey = y;
		this.z = z;
		level.map[x][z] = this;
	}

	public void place(Token t) {
		place(t.level, t.x, t.y, t.z);
	}
	
	public int geth() {
		return level.h.y[x][z];
	}

	public Tile getAdj(int dx, int dz) {
		int x = this.x + dx;
		int z = this.z + dz;
		return Level.isInside(x, z) ? level.map[x][z] : null;
	}
	
	public Tile getAdj(Dir d) {
		int x = this.x + d.dx;
		int z = this.z + d.dz;
		return Level.isInside(x, z) ? level.map[x][z] : null; 
	}

	public Template getAdjT(int dx, int dz) {
		Tile tile = getAdj(dx, dz);
		return tile!=null ? tile.t : null; 
	}

	public Template getAdjT(Dir d) {
		Tile tile = getAdj(d);
		return tile!=null ? tile.t : null; 
	}

	public static Tile getAdj(Level level, int tx, int tz, int dx, int dz) {
		int x = tx + dx;
		int z = tz + dz;
		return Level.isInside(x, z) ? level.map[x][z] : null; 
	}
	
	public static Tile getAdj(Level level, int tx, int tz, Dir d) {
		int x = tx + d.dx;
		int z = tz + d.dz;
		return Level.isInside(x, z) ? level.map[x][z] : null; 
	}

	public static Template getAdjT(Level level, int tx, int tz, int dx, int dz) {
		Tile tile = getAdj(level, tx, tz, dx, dz);
		return tile!=null ? tile.t : null; 
	}

	public static Template getAdjT(Level level, int tx, int tz, Dir d) {
		Tile tile = getAdj(level, tx, tz, d);
		return tile!=null ? tile.t : null; 
	}
}
