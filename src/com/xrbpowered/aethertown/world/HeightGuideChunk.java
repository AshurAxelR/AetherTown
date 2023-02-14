package com.xrbpowered.aethertown.world;

import static com.xrbpowered.aethertown.world.region.LevelInfo.baseSize;

import com.xrbpowered.aethertown.world.region.Region;

public class HeightGuideChunk {

	public final Region region;
	public final int rx, rz;
	public final HeightGuide parent;
	public final int cx, cz;
	public final int[][] y = new int[baseSize+1][baseSize+1];
	
	public HeightGuideChunk(Region region, int rx, int rz) {
		this.region = region;
		this.rx = rx;
		this.rz = rz;
		this.parent = null;
		this.cx = -1;
		this.cz = -1;
	}

	public HeightGuideChunk(HeightGuide parent, int cx, int cz) {
		this.region = parent.info.region;
		this.rx = parent.info.x0 + cx;
		this.rz = parent.info.z0 + cz;
		this.parent = parent;
		this.cx = cx;
		this.cz = cz;
	}
	
	/*private int delta(Random random, float amp) {
		return Math.round(random.nextFloat()*amp*2f-amp);
	}
	
	private void plasma(int x0, int z0, int size, float amp, Random random) {
		int x1 = x0+size;
		int z1 = z0+size;
		int mx = x0+size/2;
		int mz = z0+size/2;
		
		if(z0>0) y[mx][z0] = (y[x0][z0]+y[x1][z0])/2 + delta(random, amp); // +amp
		if(x0>0) y[x0][mz] = (y[x0][z0]+y[x0][z1])/2 + delta(random, amp); // +amp
		y[mx][z1] = (y[x0][z1]+y[x1][z1])/2 + delta(random, amp); // +amp
		y[x1][mz] = (y[x1][z0]+y[x1][z1])/2 + delta(random, amp); // +amp
		y[mx][mz] = (y[mx][z0]+y[mx][z1]+y[x0][mz]+y[x1][mz])/4 + delta(random, amp); // +amp
		
		if(size>2) {
			plasma(x0, z0, size/2, amp*0.75f, random);
			plasma(mx, z0, size/2, amp*0.75f, random);
			plasma(x0, mz, size/2, amp*0.75f, random);
			plasma(mx, mz, size/2, amp*0.75f, random);
		}
	}
	
	public HeightGuideChunk generate(boolean draft) {
		Random random = new Random(RandomSeed.seedXY(region.seed+91271L, rx, rz));
		
		if(!draft) {
			HeightGuideChunk west = (parent==null || cx==0) ? region.draftHG(rx-1, rz) : parent.chunks[cx-1][cz];
			for(int z=0; z<baseSize; z++)
				y[0][z] = west.y[baseSize][z];
			HeightGuideChunk north = (parent==null || cz==0) ? region.draftHG(rx, rz-1) : parent.chunks[cx][cz-1];
			for(int x=0; x<baseSize; x++)
				y[x][0] = north.y[x][baseSize];
		}
		
		LevelTerrainType[][] t = new LevelTerrainType[3][3];
		for(int dx=-1; dx<=1; dx++)
			for(int dz=-1; dz<=1; dz++) {
				t[dx+1][dz+1] = region.getTerrain(rx+dx, rz+dz);
			}
		
		y[0][baseSize] = (t[0][1].edgey+t[0][2].edgey+t[1][1].edgey+t[1][2].edgey)/4;
		y[baseSize][0] = (t[1][0].edgey+t[1][1].edgey+t[2][0].edgey+t[2][1].edgey)/4;
		y[baseSize][baseSize] = (t[1][1].edgey+t[1][2].edgey+t[2][1].edgey+t[2][2].edgey)/4;
		plasma(0, 0, baseSize, 20f, random); // TODO avg amp
		return this;
	}*/
	
	public HeightGuideChunk generate(boolean draft) {
		for(int x=0; x<=baseSize; x++)
			for(int z=0; z<=baseSize; z++) {
				y[x][z] = -100;
			}
		return this;
	}

}
