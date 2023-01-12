package com.xrbpowered.aethertown.world.region;

import java.util.ArrayList;

import com.xrbpowered.aethertown.utils.Dir;

public class LevelInfo {

	public static final int baseSize = 64;
	
	public class LevelConnection {
		public Dir d;
		public int i;
		public int offs = 0;
		public int basey = 0;
		
		public LevelConnection(Dir d, int i) {
			this.d = d;
			this.i = i;
		}
		
		public int getLevelI() {
			int li = baseSize*i + baseSize/2 + offs;
			if(d==Dir.south || d==Dir.west)
				li = getLevelSize() - 1 - li;
			return li;
		}
		
		public int getX() {
			if(d.dx==0)
				return baseSize*i + baseSize/2 + offs;
			else if(d.dx<0)
				return 0;
			else
				return getLevelSize()-1;
		}
		
		public int getZ() {
			if(d.dz==0)
				return baseSize*i + baseSize/2 + offs;
			else if(d.dz<0)
				return 0;
			else
				return getLevelSize()-1;
		}
	}
	
	public final Region region;
	public final int x0, z0;
	public final int size;
	public final long seed;
	
	public ArrayList<LevelConnection> conns = new ArrayList<>();
	
	public LevelInfo(int size, long seed) {
		this(null, 0, 0, size, seed);
	}
	
	public LevelInfo(Region region, int x, int z, int size, long seed) {
		this.region = region;
		this.x0 = x;
		this.z0 = z;
		this.size = size;
		this.seed = seed;
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
	
	public void addConn(Dir d, int i) {
		LevelConnection conn = new LevelConnection(d, i);
		for(LevelConnection c : conns) {
			if(c.d==conn.d && c.i==conn.i)
				return;
		}
		conns.add(conn);
		return;
	}
	
	public boolean addConn(int x, int z, Dir d) {
		int i = (d.dz==0) ? z-z0 : x-x0;
		if(i<0 || i>=size)
			return false;
		addConn(d, i);
		return true;
	}
	
	public int getLevelSize() {
		return size*baseSize;
	}
	
	@Override
	public int hashCode() {
		return x0*Region.sizez+z0;
	}
	
}
