package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.utils.Dir;

public class HeightLimiter {

	private static final int hardLimit = 2000;
	private static final int maxUpdate = 120;
	public static int maxCliff = 18; //24;
	public static int maxWall = 12; //8;
	
	public final Level level;
	public final int levelSize;
	
	public int[][] miny;
	public int[][] maxy;
	
	public HeightLimiter(Level level) {
		this.level = level;
		this.levelSize = level.levelSize;
		this.miny = new int[levelSize][levelSize];
		this.maxy = new int[levelSize][levelSize];
		reset();
	}
	
	public void reset() {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				miny[x][z] = -hardLimit;
				maxy[x][z] = hardLimit;
			}
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
