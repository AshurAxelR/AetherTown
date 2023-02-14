package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.world.region.LevelInfo;

import static com.xrbpowered.aethertown.world.region.LevelInfo.baseSize;

public class HeightGuide {

	public final LevelInfo info;
	
	public final HeightGuideChunk[][] chunks;

	public HeightGuide(LevelInfo info) {
		this.info = info;
		this.chunks = new HeightGuideChunk[info.size][info.size];
	}
	
	public int gety(int x, int z) {
		return chunks[x/baseSize][z/baseSize].y[x%baseSize][z%baseSize];
	}
	
	public HeightGuide generate() {
		for(int cx=0; cx<info.size; cx++)
			for(int cz=0; cz<info.size; cz++) {
				chunks[cx][cz] = new HeightGuideChunk(this, cx, cz).generate(false);
			}
		return this;
	}

}
