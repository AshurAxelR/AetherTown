package com.xrbpowered.aethertown.world.region;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.region.PortalSystem.PortalInfo;

public class LevelInfo {

	public static final int baseSize = 64;
	
	public class LevelConnection {
		public Dir d;
		public int i;
		public int offs = 0;
		
		public LevelConnection(Dir d, int i) {
			this.d = d;
			this.i = i;
		}
		
		public LevelInfo getAdj() {
			return region.map[getRegionX()+d.dx][getRegionZ()+d.dz];
		}
		
		public int getRegionX() {
			if(d.dx==0)
				return x0+i;
			else if(d.dx>0)
				return x0+size-1;
			else
				return x0;
		}

		public int getRegionZ() {
			if(d.dz==0)
				return z0+i;
			else if(d.dz>0)
				return z0+size-1;
			else
				return z0;
		}

		public int getLevelI() {
			int li = baseSize*i + baseSize/2 + offs;
			if(d==Dir.south || d==Dir.west)
				li = getLevelSize() - 1 - li;
			return li;
		}
		
		public int getLevelX() {
			if(d.dx==0)
				return baseSize*i + baseSize/2 + offs;
			else if(d.dx<0)
				return 0;
			else
				return getLevelSize()-1;
		}
		
		public int getLevelZ() {
			if(d.dz==0)
				return baseSize*i + baseSize/2 + offs;
			else if(d.dz<0)
				return 0;
			else
				return getLevelSize()-1;
		}
		
		public int getY() {
			return (terrain.conny+getAdj().terrain.conny)/2;
		}
	}
	
	public final Region region;
	public final int x0, z0;
	public final int size;
	public final long seed;
	public final boolean fixed;
	public String name;
	
	public LevelTerrainModel terrain = LevelTerrainModel.hill;
	public LevelSettlementType settlement = LevelSettlementType.none;
	public PortalInfo portal = null;
	
	public ArrayList<LevelConnection> conns = new ArrayList<>();
	
	public boolean visited = false;
	
	public LevelInfo(Region region, int x, int z, int size, long seed, boolean fixed) {
		this.region = region;
		this.x0 = x;
		this.z0 = z;
		this.size = size;
		this.seed = seed;
		this.fixed = fixed;
		updateName();
	}

	public LevelInfo(Region region, int x, int z, int size, long seed) {
		this(region, x, z, size, seed, false);
	}

	private void updateName() {
		this.name = LevelNames.next(new Random(seed+6173L), settlement);
	}
	
	public LevelInfo setTerrain(LevelTerrainModel terrain) {
		this.terrain = terrain;
		return this;
	}
	
	public LevelInfo setSettlement(LevelSettlementType settlement) {
		if(fixed)
			return this;
		int levelSize = getLevelSize();
		while(settlement.getStreetMargin(levelSize)<14)
			settlement = settlement.demote();
		this.settlement = settlement;
		updateName();
		return this;
	}
	
	public boolean isPortal() {
		return portal!=null;
	}

	public boolean isFree() {
		for(int x=0; x<size; x++)
			for(int z=0; z<size; z++) {
				if(!region.isInside(x0+x, z0+z) || region.map[x0+x][z0+z]!=null)
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
		final int prime = 31;
		int result = 1;
		result = prime * result + x0;
		result = prime * result + z0;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		LevelInfo info = (LevelInfo) obj;
		return this.region==info.region && // ok, as long as RegionCache is used
				this.x0==info.x0 && this.z0==info.z0 && this.size==info.size;
	}
	
	public static LevelInfo createNullLevel(Region region, int x, int z) {
		long seed = region.seedXZ(x, z, 6799L);
		return new LevelInfo(region, x, z, 1, seed).setTerrain(LevelTerrainModel.nullTerrain);
	}
	
}
