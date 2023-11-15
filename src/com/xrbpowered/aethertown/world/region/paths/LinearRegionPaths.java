package com.xrbpowered.aethertown.world.region.paths;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelSettlementType;
import com.xrbpowered.aethertown.world.region.LevelTerrainModel;
import com.xrbpowered.aethertown.world.region.Region;

public class LinearRegionPaths {

	private static class PathToken {
		public int x, z;
		public Dir enter;
		
		public PathToken(int x, int z, Dir enter) {
			this.x = x;
			this.z = z;
			this.enter = enter;
		}
	}

	public final Region region;
	public final Random random;
	
	private PathToken token;

	public LinearRegionPaths(Region region, Random random) {
		this.region = region;
		this.random = random;
	}
	
	private LevelInfo checkSize(PathToken t, int s) {
		Dir d = t.enter;
		if(d==null)
			d = Dir.east;
		int x0 = t.x;
		int z0 = t.z;
		if(d.dx==0) {
			x0 -= random.nextInt(s);
			if(d.dz<0)
				z0 -= s-1;
		}
		else {
			z0 -= random.nextInt(s);
			if(d.dx<0)
				x0 -= s-1;
		}
		LevelInfo level = new LevelInfo(region, x0, z0, s, random.nextLong());
		if(!level.isFree()) {
			if(s==1)
				return null;
			else
				return checkSize(t, s-1);
		}
		return level;
	}
	
	private static final WRandom wnextDir = new WRandom(1, 0.5, 0.5, 0.5, 0.5);
	
	private boolean zprob(double rnd, int z, int sign) {
		double sz = sign*(z-region.sizez/2)*2/(double)region.sizez;
		double tan = 0.001*Math.tan(sz*Math.PI/2.0);
		return rnd-0.5 < tan;
	}
	
	private Dir nextDir(Dir d, int z) {
		switch (wnextDir.next(random)) {
			case 0:
				return d==null ? Dir.east : d;
			case 1:
				return d!=Dir.south && zprob(random.nextFloat(), z, 1) ? Dir.north : Dir.west;
			case 2:
				return d!=Dir.north && zprob(random.nextFloat(), z, -1) ? Dir.south : Dir.west;
			case 3:
				return d==Dir.west ? Dir.north : Dir.east;
			case 4:
				return d==Dir.west ? Dir.south : Dir.east;
			default:
				return Dir.east;
		}
	}
	
	private PathToken nextToken(LevelInfo level, Dir d, int att) {
		int dx = d.dx;
		int dz = d.dz;
		if(dx>0)
			dx *= level.size;
		else if(dx==0)
			dx = random.nextInt(level.size);
		if(dz>0)
			dz *= level.size;
		else if(dz==0)
			dz = random.nextInt(level.size);
		PathToken nt = new PathToken(level.x0+dx, level.z0+dz, d);
		if(!region.isInside(nt.x, nt.z)) {
			if(att>10)
				GeneratorException.raise("Path out of region bounds");
			return nextToken(level, d, att+1);
		}
		else
			return nt;
	}

	private static final WRandom wsize = new WRandom(0.5, 1, 0.05);

	public void generatePaths() {
		token = new PathToken(region.sizez/2, region.sizez/2, null);
		while(token!=null) {
			PathToken t = token;
			token = null;
			int s = (region.startLevel==null) ? 1 : wsize.next(random)+1;
			LevelInfo level = checkSize(t, s);
			if(level==null) {
				region.connectLevels(t.x, t.z, t.enter.flip());
				if(token==null)
					token = nextToken(region.map[t.x][t.z], Dir.east, 0);
				continue;
			}
			
			level.place();
			if(t.enter!=null)
				region.connectLevels(t.x, t.z, t.enter.flip());

			Dir d;
			if(region.startLevel==null) {
				region.startLevel = level;
				level.setTerrain(LevelTerrainModel.low);
				d = Dir.east;
			}
			else {
			 	level.setSettlement(LevelSettlementType.random(level.size, random));
			 	level.setTerrain(LevelTerrainModel.random(level, random));
			 	d = nextDir(t.enter, t.z);
			}
			
			if(t.x<region.sizex-region.sizez/2)
				token = nextToken(level, d, 0);
		}
	}

}
