package com.xrbpowered.aethertown.world.region;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.HeightGuideChunk;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;

public class Region {

	private static final int maxGeneratorAttempts = 3;
	
	public static final int sizex = 512;
	public static final int sizez = 128;
	
	public final long seed;
	public LevelInfo[][] map = null;
	
	public LevelInfo startLevel = null;
	
	public Region(long seed) {
		this.seed = seed;
	}
	
	public LevelInfo getLevel(int x, int z) {
		if(!isInside(x, z) || map[x][z]==null)
			return LevelInfo.createNullLevel(this, x, z);
		else
			return map[x][z];
	}
	
	public LevelTerrainModel getTerrain(int x, int z) {
		if(!isInside(x, z) || map[x][z]==null)
			return LevelTerrainModel.nullTerrain;
		else
			return map[x][z].terrain;
	}
	
	public HeightGuideChunk draftHG(int x, int z) {
		return new HeightGuideChunk(this, x, z).generate(true);
	}
	
	public boolean hasConnection(int x, int z, Dir d) {
		if(!isInside(x, z) || map[x][z]==null)
			return false;
		for(LevelConnection conn : map[x][z].conns) {
			if(conn.d==d && conn.getRegionX()==x && conn.getRegionZ()==z)
				return true;
		}
		return false;
	}
	
	private void resetGenerator() {
		map = new LevelInfo[sizex][sizez];
		startLevel = null;
	}
	
	public void generate() {
		Random random = new Random(seed);
		for(int att = 0; att<maxGeneratorAttempts; att++) {
			if(att>0)
				System.out.printf("Retrying...\nAttempt #%d\n", att+1);
			try {
				generate(random);
				return;
			}
			catch (GeneratorException e) {
				System.err.printf("Generation failed: %s\n", e.getMessage());
			}
		}
		throw new RuntimeException("Generator attempts limit reached");
	}

	private void generate(Random random) {
		resetGenerator();
		new RegionPaths(this, random).generatePaths();
	}

	public void connectLevels(int x, int z, Dir d) {
		map[x][z].addConn(x, z, d);
		LevelInfo level = map[x+d.dx][z+d.dz];
		level.addConn(x+d.dx, z+d.dz, d.flip());
	}
	
	public static boolean isInside(int x, int z) {
		return (x>=0 && x<sizex && z>=0 && z<sizez);
	}
}
