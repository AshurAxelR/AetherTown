package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.utils.Dir;

public class HeightLimiter {

	public static final int maxCliff = 18; //24;
	public static final int maxWall = 12; //8;
	public static final int maxBridge = 18; //8;
	public static final int maxEdge = 4; // 8
	public static final int maxHeight = 200;

	private static final int maxUpdate = 120;

	public final Level level;
	public final int levelSize;
	
	public int[][] miny;
	public int[][] maxy;

	private boolean dirty;
	
	public HeightLimiter(Level level) {
		this.level = level;
		this.levelSize = level.levelSize;
		this.miny = new int[levelSize][levelSize];
		this.maxy = new int[levelSize][levelSize];
		reset();
		dirty = false;
	}
	
	public void reset() {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				int dy = Level.edgeDist(levelSize, x, z)*maxEdge;
				miny[x][z] = level.heightGuide.gety(x, z);
				maxy[x][z] = Math.min(maxHeight, level.heightGuide.gety(x, z)+dy);
			}
	}
	
	public void invalidate() {
		dirty = true;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void revalidate() {
		if(!dirty)
			return;
		reset();
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				Tile tile = level.map[x][z];
				if(tile==null)
					continue;
				((TileTemplate) tile.t).updateHeightLimit(new Token(level, x, tile.basey, z, tile.d));
			}
		dirty = false;
	}
	
	public boolean isInside(int x, int z) {
		return x>=0 && x<levelSize && z>=0 && z<levelSize;
	}

	public void updateAt(int cx, int cy, int cz, int limDown, int limUp, int limR) {
		int diffDown = 0;
		int diffUp = 0;
		for(int q=1; diffUp<maxUpdate || diffDown<maxUpdate; q++) {
			diffDown += (q<=limR) ? limDown : maxCliff;
			diffUp += (q<=limR) ? limUp: maxCliff;
			int low = cy - diffDown;
			int high = cy +diffUp;
			
			boolean upd = false;
			for(Dir d : Dir.values()) {
				int x0 = cx-q*(d.dx+d.dz);
				int z0 = cz-q*(d.dx+d.dz);
				for(int i=0; i<q*2; i++) {
					int x = x0 + i*d.dx;
					int z = z0 + i*d.dz;
					if(!level.isInside(x, z))
						continue;
					if(miny[x][z]<low) {
						miny[x][z] = low;
						upd = true;
					}
					if(maxy[x][z]>high) {
						maxy[x][z] = high;
						upd = true;
					}
				}
			}
			if(!upd)
				break;
		}
	}
	
	public static void updateAt(Token t, int limDown, int limUp, int limR) {
		t.level.heightLimiter.updateAt(t.x, t.y, t.z, limDown, limUp, limR);
	}

	public static void updateAt(Token t, int lim, int limR) {
		t.level.heightLimiter.updateAt(t.x, t.y, t.z, lim, lim, limR);
	}

}
