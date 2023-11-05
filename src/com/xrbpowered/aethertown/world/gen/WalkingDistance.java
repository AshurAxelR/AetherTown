package com.xrbpowered.aethertown.world.gen;

import java.util.LinkedList;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Level;

public class WalkingDistance {

	private class DistToken {
		public int x, z, dist;
		public DistToken(int x, int z, int dist) {
			this.x = x;
			this.z = z;
			this.dist = dist;
		}
	}
	
	public final Level level;
	public final int levelSize;
	
	public final int[][] map;
	
	public WalkingDistance(Level level) {
		this.level = level;
		this.levelSize = level.levelSize;
		this.map = new int[levelSize][levelSize];
	}

	public void reset(int maxDist) {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++)
				map[x][z] = maxDist+1;
	}
	
	public void calculate(int x0, int z0, int maxDist) {
		reset(maxDist);
		LinkedList<DistToken> tokens = new LinkedList<>();
		tokens.add(new DistToken(x0, z0, 0));
		while(!tokens.isEmpty()) {
			DistToken t = tokens.removeFirst();
			if(!StreetConnector.isAnyPath(level, t.x, t.z))
				continue;
			if(t.dist>=map[t.x][t.z])
				continue;
			map[t.x][t.z] = t.dist;
			for(Dir d : Dir.values()) {
				tokens.add(new DistToken(t.x+d.dx, t.z+d.dz, t.dist+1));
			}
		}
	}

}
