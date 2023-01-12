package com.xrbpowered.aethertown.world.region;

import java.util.ArrayList;

import com.xrbpowered.aethertown.utils.Dir;

public class LevelInfo {

	public static class LevelConnection {
		public Dir d;
		public int i;
	}
	
	public final Region region;
	public final int x0, z0;
	public final int size;
	
	public ArrayList<LevelConnection> conns = new ArrayList<>();
	
	public LevelInfo(Region region, int x, int z, int size) {
		this.region = region;
		this.x0 = x;
		this.z0 = z;
		this.size = size;
	}

	public boolean isFree() {
		for(int x=0; x<size; x++)
			for(int z=0; z<size; z++) {
				if(!Region.isInside(x0+x, z0+z) || region.map[x0+x][z0+z]!=null)
					return false;
			}
		return true;
	}

	public void place() {
		for(int x=0; x<size; x++)
			for(int z=0; z<size; z++)
				region.map[x0+x][z0+z] = this;
	}
	
	public boolean addConn(int x, int z, Dir d) {
		LevelConnection conn = new LevelConnection();
		conn.d = d;
		if(d.dz==0)
			conn.i = z-z0;
		else
			conn.i = x-x0;
		if(conn.i<0 || conn.i>=size)
			return false;
		for(LevelConnection c : conns) {
			if(c.d==conn.d && c.i==conn.i)
				return true;
		}
		conns.add(conn);
		return true;
	}
	
	@Override
	public int hashCode() {
		return x0*Region.sizez+z0;
	}
	
}
