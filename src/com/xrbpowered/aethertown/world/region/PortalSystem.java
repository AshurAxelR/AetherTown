package com.xrbpowered.aethertown.world.region;

import java.security.InvalidParameterException;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class PortalSystem {

	private static final int maxUpdateDist = 8;
	private static final Dir[] preferredPortalDirs = {Dir.north, Dir.east, Dir.south};
	
	public Region otherRegion = null;

	public static class PortalInfo {
		public int index;
		public Dir d;
		public int otherIndex;
	}
	
	public final RegionCache regions;
	public final int numPortals;
	public final int period;
	
	private int phase = -1;
	private LevelInfo portal = null;
	
	public PortalSystem(RegionCache regions) {
		this.regions = regions;
		int n = regions.mode.getNumPortals();
		if(n<4 || n==6)
			this.numPortals = n;
		else
			throw new InvalidParameterException("Unsupported number of portals: "+n);
		this.period = n>0 ? 42/n : 0;
	}
	
	public static LevelInfo findNearestPortal(LevelInfo level) {
		if(level.isPortal())
			return level;
		if(level.region.portals==null)
			return null;
		int min = maxUpdateDist;
		LevelInfo p = null;
		for(LevelInfo info : level.region.portals) {
			int dist = MathUtils.mdist(level.x0, level.z0, info.x0, info.z0);
			if(dist<min) {
				min = dist;
				p = info;
			}
		}
		return p;
	}
	
	public void updateOtherRegion() {
		if(numPortals==0 || portal==null)
			return;
		System.out.printf("PortalSystem.nearestPortal[%d, %d] (index=%d)\n", portal.x0, portal.z0, portal.portal.index);
		long seed = getOtherSeed(AetherTown.region.seed, portal.portal.index, phase);
		System.out.printf("otherRegion: %d\n", seed);
		// TODO update otherRegion
	}
	
	public void updateTime() {
		int ph = WorldTime.getDayOfYear() % period;
		if(ph==phase)
			return;
		phase = ph;
		portal = findNearestPortal(AetherTown.levelInfo);
		updateOtherRegion();
	}

	public void updateLevel() {
		LevelInfo p = findNearestPortal(AetherTown.levelInfo);
		if(p==portal)
			return;
		portal = p;
		phase = WorldTime.getDayOfYear() % period;
		updateOtherRegion();
	}

	public long getOtherSeed(long seed, int index, int phase) {
		int n = 0;
		int d = 0;
		switch(numPortals) {
			case 1:
			case 3:
				n = phase/2;
				d = (phase&1)==0 ? +1 : -1;
				break;
			case 2:
				n = phase;
				d = index==0 ? +1 : -1;
				break;
			case 6:
				n = phase;
				d = index<3 ? +1 : -1;
				break;
		}
		return alterTrigram(seed, n, d);
	}
	
	public int getPortalSeed(long seed, int index) {
		switch(numPortals) {
			case 2:
				if(index==0)
					return getTrigram(seed, 0);
				else
					return (getTrigram(seed, 0)-1)&7;
			case 3:
				return index;
			case 6:
				if(index<3)
					return getTrigram(seed, index);
				else
					return (getTrigram(seed, index-3)-1)&7;
			default:
				return 0;
		}
	}

	public Dir getPortalDir(long seed, int index) {
		int i = 0;
		boolean flip = false;
		switch(numPortals) {
			case 1:
				flip = (getTrigram(seed, 0) & 1)==1;
				break;
			case 2:
				flip = index==1;
				break;
			case 3:
				i = index;
				flip = (getTrigram(seed, index) & 1)==1;
				break;
			case 6:
				i = index%3;
				flip = index>=3;
				break;
		}
		Dir d = preferredPortalDirs[i];
		return flip ? d.flip() : d;
	}
	
	public int getOtherIndex(int index) {
		switch(numPortals) {
			case 2:
				return 1-index;
			case 3:
				return index;
			case 6:
				return (index+3)%6;
			default:
				return 0;
		}
	}
	
	public PortalInfo createPortalInfo(long seed, int index) {
		PortalInfo p = new PortalInfo();
		p.index = index;
		p.d = getPortalDir(seed, index);
		p.otherIndex = getOtherIndex(index);
		return p;
	}
	
	public static int getTrigram(long seed, int n) {
		return (int)(seed>>(n*3)) & 7;
	}

	public static long alterTrigram(long seed, int n, int d) {
		long tri = (getTrigram(seed, n)+d)&7;
		long mask = ~(7L<<(n*3));
		return (seed&mask) | (tri<<(n*3));
	}

}
