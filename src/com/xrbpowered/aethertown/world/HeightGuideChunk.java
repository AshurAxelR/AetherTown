package com.xrbpowered.aethertown.world;

import static com.xrbpowered.aethertown.world.region.LevelInfo.baseSize;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.RandomSeed;
import com.xrbpowered.aethertown.world.region.LevelTerrainModel;
import com.xrbpowered.aethertown.world.region.Region;

public class HeightGuideChunk {

	private static final float plasmaAmp = 40f;
	private static final float plasmaAmpScale = 0.7f;
	
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
	
	private int delta(Random random, float amp) {
		return Math.round(random.nextFloat()*amp-amp/2f);
	}

	private int delta(int rx, int rz, int offs, float amp) {
		return delta(new Random(RandomSeed.seedXYZ(region.seed+394634L, rx, rz, offs)), amp);
	}

	private void plasma(int x0, int z0, int size, float amp, Random random) {
		int x1 = x0+size;
		int z1 = z0+size;
		int mx = x0+size/2;
		int mz = z0+size/2;
		
		y[mx][z1] = (y[x0][z1]+y[x1][z1])/2 + delta(random, amp);
		y[x1][mz] = (y[x1][z0]+y[x1][z1])/2 + delta(random, amp);
		y[mx][mz] = (y[mx][z0]+y[mx][z1]+y[x0][mz]+y[x1][mz])/4; // + delta(random, amp);
		
		if(size>2) {
			amp *= plasmaAmpScale;
			plasma(x0, z0, size/2, amp, random);
			plasma(mx, z0, size/2, amp, random);
			plasma(x0, mz, size/2, amp, random);
			plasma(mx, mz, size/2, amp, random);
		}
	}
	
	public HeightGuideChunk generate(boolean draft) {
		Random random = new Random(RandomSeed.seedXY(region.seed+91271L, rx, rz));
		
		LevelTerrainModel[][] t = new LevelTerrainModel[3][3];
		for(int dx=-1; dx<=1; dx++)
			for(int dz=-1; dz<=1; dz++) {
				t[dx+1][dz+1] = region.getTerrain(rx+dx, rz+dz);
			}
		
		if(!draft) {
			HeightGuideChunk west = (parent==null || cx==0) ? region.draftHG(rx-1, rz) : parent.chunks[cx-1][cz];
			for(int z=0; z<=baseSize; z++)
				y[0][z] = west.y[baseSize][z];
			HeightGuideChunk north = (parent==null || cz==0) ? region.draftHG(rx, rz-1) : parent.chunks[cx][cz-1];
			for(int x=0; x<=baseSize; x++)
				y[x][0] = north.y[x][baseSize];
		}
		else {
			y[0][baseSize] = (t[0][1].edgey+t[0][2].edgey+t[1][1].edgey+t[1][2].edgey)/4 + delta(rx-1, rz, 0, plasmaAmp);
			y[baseSize][0] = (t[1][0].edgey+t[1][1].edgey+t[2][0].edgey+t[2][1].edgey)/4 + delta(rx, rz-1, 0, plasmaAmp);
		}
		
		int m = baseSize/2;
		y[baseSize][baseSize] = (t[1][1].edgey+t[1][2].edgey+t[2][1].edgey+t[2][2].edgey)/4 + delta(rx, rz, 0, plasmaAmp);
		if(region.hasConnection(rx, rz, Dir.east))
			y[baseSize][m] = (t[1][1].conny+t[2][1].conny)/2;
		else
			y[baseSize][m] = (t[1][1].edgey+t[2][1].edgey)/2 + delta(rx, rz, 1, plasmaAmp);
		if(region.hasConnection(rx, rz, Dir.south))
			y[m][baseSize] = (t[1][1].conny+t[1][2].conny)/2;
		else
			y[m][baseSize] = (t[1][1].edgey+t[1][2].edgey)/2 + delta(rx, rz, 10, plasmaAmp);
		
		y[m][m] = (y[0][m]+y[baseSize][m]+y[m][0]+y[m][baseSize])/4 + delta(rx, rz, 11, plasmaAmp);
		
		plasma(0, 0, m, plasmaAmp, random);
		plasma(m, 0, m, plasmaAmp, random);
		plasma(0, m, m, plasmaAmp, random);
		plasma(m, m, m, plasmaAmp, random);
		
		// for the purpose of HeightMap.calcy
		for(int z=0; z<=baseSize; z++) {
			if(y[baseSize-1][z]>y[baseSize][z])
				y[baseSize-1][z] = y[baseSize][z];
		}
		for(int x=0; x<=baseSize; x++) {
			if(y[x][baseSize-1]>y[x][baseSize])
				y[x][baseSize-1] = y[x][baseSize];
		}
		return this;
	}

}
