package com.xrbpowered.aethertown.world.region;

import java.util.LinkedList;
import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.utils.RandomSeed;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.HeightGuideChunk;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.region.PortalSystem.PortalInfo;

public class Region {

	private static final int maxGeneratorAttempts = 3;
	
	public final RegionCache cache;
	public final int sizex;
	public final int sizez;
	
	public final long seed;
	public LevelInfo[][] map = null;
	
	public LevelInfo[] portals = null;
	public LevelInfo startLevel = null;
	
	public Region(RegionCache cache, long seed) {
		this.cache = cache;
		this.sizex = cache.mode.sizex;
		this.sizez = cache.mode.sizez;
		this.seed = seed;
	}

	public long seedXZ(int x, int z, int offs, long add) {
		LevelInfo level = isInside(x, z) ? map[x][z] : null;
		if(level!=null && level.fixed)
			return level.seed;
		else
			return RandomSeed.seedXYZ(this.seed+add, x, z, offs);
	}
	
	public long seedXZ(int x, int z, long add) {
		return seedXZ(x, z, 0, add);
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
		portals = cache.portals.numPortals==0 ? null : new LevelInfo[cache.portals.numPortals];
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

	private boolean expand(Random random, int minAdj, boolean all) {
		LinkedList<LevelInfo> add = new LinkedList<>();
		for(int x=1; x<sizex-1; x++)
			for(int z=1; z<sizez-1; z++) {
				if(map[x][z]!=null)
					continue;
				int countAdj = 0;
				for(Dir8 d : Dir8.values()) {
					if(map[x+d.dx][z+d.dz]!=null)
						countAdj++;
				}
				if(countAdj>=minAdj && (all || countAdj-minAdj+1>=random.nextInt(6))) {
					if(countAdj<=5 && random.nextInt(2)==0) {
						for(Dir d : Dir.shuffle(random)) {
							Dir cw = d.cw();
							int x1 = x+d.dx+cw.dx;
							int z1 = z+d.dz+cw.dz;
							if(map[x1][z1]==null && map[x+d.dx][z+d.dz]==null && map[x+cw.dx][z+cw.dz]==null) {
								LevelInfo level = new LevelInfo(this, (x1<x) ? x1 : x, (z1<z) ? z1 : z, 2, random.nextLong());
								level.setTerrain(LevelTerrainModel.random(level, random));
								add.add(level);
								break;
							}
						}
					}
					LevelInfo level = new LevelInfo(this, x, z, 1, random.nextLong());
					level.setTerrain(LevelTerrainModel.random(level, random));
					add.add(level);
				}
			}
		
		int placed = 0;
		for(LevelInfo level : add) {
			if(level.isFree()) {
				level.place();
				placed++;
			}
		}
		return placed>0;
	}
	
	private void checkPeaks() {
		// hack: fixing low-to-peak by lowering size 1 peaks
		for(int x=1; x<sizex-1; x++)
			for(int z=1; z<sizez-1; z++) {
				LevelInfo level = map[x][z];
				if(level==null || level.size>1)
					continue;
				if(level.terrain==LevelTerrainModel.peak) {
					for(LevelConnection conn : level.conns) {
						LevelInfo adj = map[x+conn.d.dx][z+conn.d.dz];
						if(adj.terrain==LevelTerrainModel.low || adj.terrain==LevelTerrainModel.bottom) {
							level.setTerrain(LevelTerrainModel.hill);
							break;
						}
					}
				}
				else if(level.terrain==LevelTerrainModel.low && level.settlement.minHouses>0) {
					for(Dir d : Dir.values()) {
						LevelInfo adj = map[x+d.dx][z+d.dz];
						if(adj!=null && adj.terrain==LevelTerrainModel.peak) {
							level.setTerrain(LevelTerrainModel.hill);
							break;
						}
					}
				}
			}
	}
	
	private void generate(Random random) {
		resetGenerator();
		cache.mode.coreGenerate(this, random);
		
		checkPeaks();
		expand(random, 2, true);
		expand(random, 4, true);
		expand(random, 5, false);
	}

	public void connectLevels(int x, int z, Dir d) {
		map[x][z].addConn(x, z, d);
		LevelInfo level = map[x+d.dx][z+d.dz];
		level.addConn(x+d.dx, z+d.dz, d.flip());
	}
	
	public boolean generatePortal(int index, int x0, int z0, Dir d) {
		for(int x=x0-1; x<=x0+1; x++)
			for(int z=z0-1; z<=z0+1; z++) {
				if(!isInside(x, z) || map[x][z]!=null)
					return false;
			}
		
		int portalSeed = cache.portals.getPortalSeed(this.seed, index) * 9;
		PortalInfo portal = cache.portals.createPortalInfo(this.seed, index);
		if(portal.d!=d)
			throw new RuntimeException("Portal direction mismatch");
		
		for(int dx=-1; dx<=1; dx++)
			for(int dz=-1; dz<=1; dz++) {
				int x = x0+dx;
				int z = z0+dz;
				LevelInfo level = new LevelInfo(this, x, z, 1, portalSeed, true);
				level.setTerrain(LevelTerrainModel.lowest);
				level.place();
				if(x==x0 && z==z0) {
					level.portal = portal;
					portals[index] = level;
				}
				portalSeed++;
			}
		
		connectLevels(x0, z0, d);
		connectLevels(x0, z0, d.flip());
		return true;
	}
	
	public boolean isInside(int x, int z) {
		return (x>=0 && x<sizex && z>=0 && z<sizez);
	}
}
