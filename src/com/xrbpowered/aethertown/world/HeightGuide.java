package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.tiles.Street;

import static com.xrbpowered.aethertown.world.region.LevelInfo.baseSize;

public class HeightGuide {

	public final LevelInfo info;
	
	public final HeightGuideChunk[][] chunks;

	public HeightGuide(LevelInfo info) {
		this.info = info;
		this.chunks = new HeightGuideChunk[info.size][info.size];
	}
	
	public int gety(int x, int z) {
		int levelSize = info.getLevelSize();
		int cx = (x==levelSize) ? info.size-1 : x/baseSize; 
		int cz = (z==levelSize) ? info.size-1 : z/baseSize; 
		return chunks[cx][cz].y[x-cx*baseSize][z-cz*baseSize];
	}
	
	public HeightGuide generate() {
		for(int cx=0; cx<info.size; cx++)
			for(int cz=0; cz<info.size; cz++) {
				chunks[cx][cz] = new HeightGuideChunk(this, cx, cz).generate(false);
			}
		return this;
	}
	
	private static void checkTerrainBorder(Level level, int x, int z, int x1, int z1) {
		for(int ax=x-1; ax<x+1; ax++)
			for(int az=z-1; az<z+1; az++) {
				if(level.isInside(ax, az) && level.map[ax][az]!=null && Street.isAnyPath(level.map[ax][az].t)) {
					// System.err.printf(" - level [%d, %d] skip terrain check at [%d, %d]: road at [%d, %d]\n", level.info.x0, level.info.z0, x, z, ax, az);
					return;
				}
			}
		int h = level.h.y[x][z];
		int g0 = level.heightGuide.gety(x, z);
		int g1 = (x1<0 || z1<0) ? g0 : level.heightGuide.gety(x1, z1);
		int g = Math.max(g0, g1);
		if(h!=g) {
			// System.err.printf(" - level [%d, %d] border terrain mismatch at [%d, %d]: %d fixed to %d\n", level.info.x0, level.info.z0, x, z, h, g);
			level.h.y[x][z] = g;
		}
	}

	public static void checkTerrainBorders(Level level) {
		for(int i=0; i<=level.levelSize; i++) {
			checkTerrainBorder(level, i, 0, i-1, 0);
			checkTerrainBorder(level, i, level.levelSize, i-1, level.levelSize);
			checkTerrainBorder(level, 0, i, 0, i-1);
			checkTerrainBorder(level, level.levelSize, i, level.levelSize, i-1);
		}
	}

}
