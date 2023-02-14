package com.xrbpowered.aethertown.world.region;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.HeightGuideChunk;

public class Region {

	public static final int sizex = 1024;
	public static final int sizez = 128;
	
	public final long seed;
	public LevelInfo[][] map = null;
	
	public LevelInfo startLevel = null;
	public ArrayList<LevelInfo> displayLevels = new ArrayList<>(); // temporary
	
	public Region(long seed) {
		this.seed = seed;
	}
	
	public LevelInfo getLevel(int x, int z) {
		if(!isInside(x, z) || map[x][z]==null)
			return LevelInfo.createNullLevel(this, x, z);
		else
			return map[x][z];
	}
	
	public LevelTerrainType getTerrain(int x, int z) {
		if(!isInside(x, z) || map[x][z]==null)
			return LevelTerrainType.nullTerrain;
		else
			return map[x][z].terrain;
	}
	
	public HeightGuideChunk draftHG(int x, int z) {
		return new HeightGuideChunk(this, x, z).generate(true);
	}
	
	public void generate() {
		map = new LevelInfo[sizex][sizez];
		Random random = new Random(seed);
		// new RegionPaths(this, random).generatePaths();
		
		// temporary:
		int x = sizez/2;
		int z = sizez/2;
		LevelInfo level;
		level = new LevelInfo(this, x, z, 2, random.nextLong()).setSettlement(LevelSettlementType.smallTown);
		level.place();
		startLevel = level;
		displayLevels.add(level);
		level = new LevelInfo(this, x-2, z, 2, random.nextLong()).setSettlement(LevelSettlementType.village);
		level.place();
		displayLevels.add(level);
		level = new LevelInfo(this, x+2, z, 2, random.nextLong()).setSettlement(LevelSettlementType.village);
		level.place();
		displayLevels.add(level);
		connectLevels(x, z, Dir.west);
		connectLevels(x+1, z, Dir.east);
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
