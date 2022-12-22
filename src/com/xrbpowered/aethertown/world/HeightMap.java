package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.utils.Corner;

import static com.xrbpowered.aethertown.utils.MathUtils.lerp;

public class HeightMap {

	public final Level level;
	public final int levelSize;
	
	public int[][] y;
	public boolean[][] diag;

	public HeightMap(Level level) {
		this.level = level;
		this.levelSize = level.levelSize;
		this.y = new int[levelSize+1][levelSize+1];
		this.diag = new boolean[levelSize][levelSize];
	}
	
	public boolean isFlat(int x, int z) {
		return y[x][z]==y[x+1][z] && y[x][z]==y[x][z+1] && y[x][z]==y[x+1][z+1];
	}
	
	public float gety(int x, int z, float sx, float sz) {
		float y00 = Tile.ysize*y[x][z];
		float y01 = Tile.ysize*y[x][z+1];
		float y10 = Tile.ysize*y[x+1][z];
		float y11 = Tile.ysize*y[x+1][z+1];
		
		if(diag[x][z]) {
			if(sx>=sz)
				return sz==1 ? y11 : lerp(lerp(y00, y11, sz), lerp(y10, y11, sz), 1-(1-sx)/(1-sz));
			else
				return sz==0 ? y00 : lerp(lerp(y00, y01, sz), lerp(y00, y11, sz), sx/sz);
		}
		else {
			if(sx+sz<=1)
				return sz==1 ? y10 : lerp(lerp(y00, y01, sz), lerp(y10, y01, sz), sx/(1-sz));
			else
				return sz==0 ? y10 : lerp(lerp(y10, y01, sz), lerp(y10, y11, sz), 1-(1-sx)/sz);
		}
	}

	private static int calcy(Level level, int x, int z) {
		int max = 0;
		boolean first = true;
		int minf = 0;
		boolean firstf = true;
		for(Corner c : Corner.values()) {
			if(level.isInside(x+c.tx, z+c.tz)) {
				Tile t = level.map[x+c.tx][z+c.tz];
				if(t==null)
					continue;
				if(first || t.basey>max) {
					max = t.basey;
					first = false;
				}
				if(t.t.isFixedY() && (firstf || t.basey<minf)) {
					minf = t.basey;
					firstf = false;
				}
			}
		}
		return firstf ? max : minf;
	}
	
	private static int blur(int y, int y1, int y2) {
		int min = Math.min(y1, y2);
		int max = Math.max(y1, y2);
		if(y>min-8 && y>max)
			return max;
		if(y>max+2)
			return max+2;
		if(y<min-4)
			return min-4;
		return y;
	}
	
	private static int blur(int y, int yn, int ye, int ys, int yw) {
		int yns = blur(y, yn, ys);
		int yew = blur(y, ye, yw);
		return (yns+yew)/2;
	}
	
	private static boolean calcDiag(int y00, int y01, int y10, int y11) {
		int d1 = Math.abs(y00-y11); 
		int d2 = Math.abs(y01-y10);
		int d = maxDelta(y00, y01, y10, y11);
		if(d>d1+d2) {
			int miny1 = Math.min(y00, y11);
			int miny2 = Math.min(y01, y10);
			if(miny1<miny2)
				d1 = -1;
			else
				d2 = -1;
		}
		return d1<d2;
	}
	
	public void calculate(boolean blur) {
		for(int x=0; x<=levelSize; x++)
			for(int z=0; z<=levelSize; z++) {
				this.y[x][z] = calcy(level, x, z);
			}
		if(blur) {
			for(int x=1; x<levelSize; x++)
				for(int z=1; z<levelSize; z++) {
					this.y[x][z] = blur(this.y[x][z], this.y[x][z-1], this.y[x+1][z], this.y[x][z+1], this.y[x-1][z]);
				}
		}
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				this.diag[x][z] = calcDiag(y[x][z], y[x][z+1], y[x+1][z], y[x+1][z+1]);
			}
	}


	public static int maxDelta(int... y) {
		int miny = y[0];
		int maxy = y[0];
		for(int i=1; i<y.length; i++) {
			if(y[i]<miny)
				miny = y[i];
			if(y[i]>maxy)
				maxy = y[i];
		}
		return maxy-miny;
	}
}
