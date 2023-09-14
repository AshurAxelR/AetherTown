package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.world.FenceGenerator.FenceType;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;

public class Tile {

	public static final float size = 4f; // 4m = 160px = 16x25cm stairs
	public static final float ysize = 0.525f; // 52.5cm = 3*17.5cm stairs or 21px = 3*7px stairs. 1 floor = 6y = 315cm = 126px = 18 stairs
	
	public class SubInfo {
		public int i, j;
		public PlotGenerator parent;
	}
	
	public final TileTemplate t;

	public Level level = null;
	public int x, z;
	public int basey = 0;
	public Dir d;
	public SubInfo sub = null;
	
	private FenceType[] fences = new FenceType[] {FenceType.none, FenceType.none, FenceType.none, FenceType.none};
	
	protected Tile(TileTemplate t) {
		this.t = t;
	}
	
	public Tile makeSub(PlotGenerator parent, int i, int j) {
		sub = new SubInfo();
		sub.parent = parent;
		sub.i = i;
		sub.j = j;
		return this;
	}
	
	public void place(Level level, int x, int y, int z, Dir d) {
		this.level = level;
		this.x = x;
		this.basey = y;
		this.z = z;
		this.d = d;
		level.map[x][z] = this;
	}

	public void place(Token t) {
		place(t.level, t.x, t.y, t.z, t.d);
	}

	public int getGroundY() {
		return t.getGroundY(this);
	}
	
	public boolean hasFence(Dir d) {
		return fences[d.ordinal()]!=FenceType.none;
	}
	
	public FenceType getFence(Dir d) {
		return fences[d.ordinal()];
	}

	public void setFence(Dir d, FenceType fence) {
		fences[d.ordinal()] = fence;
	}

	public Tile getAdj(int dx, int dz) {
		int x = this.x + dx;
		int z = this.z + dz;
		return level.isInside(x, z) ? level.map[x][z] : null;
	}
	
	public Tile getAdj(Dir d) {
		int x = this.x + d.dx;
		int z = this.z + d.dz;
		return level.isInside(x, z) ? level.map[x][z] : null; 
	}

	public TileTemplate getAdjT(int dx, int dz) {
		Tile tile = getAdj(dx, dz);
		return tile!=null ? tile.t : null; 
	}

	public TileTemplate getAdjT(Dir d) {
		Tile tile = getAdj(d);
		return tile!=null ? tile.t : null; 
	}
	
	public int getAdjBlockY() {
		int max = basey;
		for(Dir8 d : Dir8.values()) {
			Tile adj = getAdj(d.dx, d.dz);
			if(adj!=null) {
				int y = adj.t.getBlockY(adj);
				if(y>max)
					max = y;
			}
		}
		return max;
	}

	public static Tile getAdj(Level level, int tx, int tz, int dx, int dz) {
		int x = tx + dx;
		int z = tz + dz;
		return level.isInside(x, z) ? level.map[x][z] : null; 
	}
	
	public static Tile getAdj(Level level, int tx, int tz, Dir d) {
		int x = tx + d.dx;
		int z = tz + d.dz;
		return level.isInside(x, z) ? level.map[x][z] : null; 
	}

	public static TileTemplate getAdjT(Level level, int tx, int tz, int dx, int dz) {
		Tile tile = getAdj(level, tx, tz, dx, dz);
		return tile!=null ? tile.t : null; 
	}

	public static TileTemplate getAdjT(Level level, int tx, int tz, Dir d) {
		Tile tile = getAdj(level, tx, tz, d);
		return tile!=null ? tile.t : null; 
	}
}
