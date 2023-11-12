package com.xrbpowered.aethertown.world;

import static com.xrbpowered.aethertown.utils.MathUtils.lerp;

import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.MathUtils;

public class HeightMap {

	public static final int[] yloc = new int[4];
	
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

	public int[] yloc(int x, int z) {
		yloc[0] = y[x][z];
		yloc[1] = y[x+1][z];
		yloc[2] = y[x+1][z+1];
		yloc[3] = y[x][z+1];
		return yloc;
	}
	
	private static class CalcY {
		public int max = 0;
		public boolean first = true;
		public int minf = 0;
		public int maxFix = 0;
		
		private void prepare(Level level, int x, int z) {
			max = 0;
			first = true;
			minf = 0;
			maxFix = 0;
			for(Corner c : Corner.values()) {
				int fix, y;
				int cx = x+c.tx;
				int cz = z+c.tz;
				
				if(level.isInside(cx, cz)) {
					Tile t = level.map[cx][cz];
					if(t==null)
						continue;
					fix = t.t.getFixedYStrength();
					y = t.getGroundY(c.flip());
				}
				else {
					if(cx<0) cx = 0;
					if(cx>level.levelSize) cx = level.levelSize;
					if(cz<0) cz = 0;
					if(cz>level.levelSize) cz = level.levelSize;
					fix = 0;
					y = level.heightGuide.gety(cx, cz);
				}
				
				if(fix>0) {
					if(fix>maxFix || (y<minf && fix==maxFix)) {
						minf = y;
						maxFix = fix;
					}
				}
				else {
					if(first || y>max) {
						max = y;
						first = false;
					}
				}
			}
		}
		
		private int calc(Level level, int x, int z) {
			prepare(level, x, z);
			if(first)
				return minf;
			else if(maxFix==0)
				return max;
			else if(minf>max+4) {
				if(minf>max+6)
					return max+2;
				else
					return max;
			}
			else
				return minf;
		}
		
		private int applyBlur(Level level, int x, int z, int blury) {
			prepare(level, x, z);
			if(first)
				return minf;
			else if(maxFix==0)
				return blury;
			else if(minf>blury+4)
				return blury;
			else
				return minf;
		}

	}
	
	private static CalcY calcy = new CalcY();

	private static int blur(int y, int y1, int y2) {
		int min = Math.min(y1, y2);
		int max = Math.max(y1, y2);
		if(y>min-8 && y>max)
			return max;
		if(y>max+1)
			return max+1;
		if(y<min-2)
			return min-2;
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
		int d = MathUtils.maxDelta(y00, y01, y10, y11);
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
				this.y[x][z] = calcy.calc(level, x, z);
			}
		if(blur) {
			for(int x=1; x<levelSize; x++)
				for(int z=1; z<levelSize; z++) {
					int blury = blur(this.y[x][z], this.y[x][z-1], this.y[x+1][z], this.y[x][z+1], this.y[x-1][z]);
					this.y[x][z] = calcy.applyBlur(level, x, z, blury);
				}
		}
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				this.diag[x][z] = calcDiag(y[x][z], y[x][z+1], y[x+1][z], y[x+1][z+1]);
			}
	}
	
	public static int tiley(Tile tile, Corner c) {
		int x = tile.x+c.tx+1;
		int z = tile.z+c.tz+1;
		return tile.level.h.y[x][z];
	}


}
