package com.xrbpowered.aethertown.world.region;

import java.security.InvalidParameterException;
import java.util.Random;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.state.RegionVisits;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public abstract class PortalSystem {

	private static final int maxUpdateDist = 4;
	private static final Dir[] preferredPortalDirs = {Dir.north, Dir.east, Dir.south};

	public static class PortalInfo {
		public int index;
		public Dir d;
		public int otherIndex;
		
		public String getName() {
			return String.format("Portal %s.", WorldTime.romanNumeral(index+1));
		}
	}
	
	private static class Two extends PortalSystem {
		public Two(RegionCache regions) {
			super(regions, 2, 42/2, 0x7fff_ffff_ffff_ffffL);
		}

		@Override
		public long getOtherSeed(long seed, int index, int phase) {
			int d = index==0 ? +1 : -1;
			return alterTrigram(seed, phase, d);
		}

		@Override
		public Dir getPortalDir(int index) {
			Dir d = preferredPortalDirs[0];
			return (index==1) ? d.flip() : d;
		}
		
		@Override
		public int getOtherIndex(int index) {
			return 1-index;
		}
	}

	private static class Six extends PortalSystem {
		public Six(RegionCache regions) {
			super(regions, 6, 42/6, 0x7fff_ffff_ffff_ffffL);
		}

		@Override
		public long getOtherSeed(long seed, int index, int phase) {
			int n = phase * 3 + (index % 3);
			int d = index<3 ? +1 : -1;
			return alterTrigram(seed, n, d);
		}

		@Override
		public Dir getPortalDir(int index) {
			Dir d = preferredPortalDirs[index%3];
			return (index>=3) ? d.flip() : d;
		}
		
		@Override
		public int getOtherIndex(int index) {
			return (index+3)%6;
		}
	}

	public Region otherRegion = null;
	public LevelInfo otherLevel = null;

	public final RegionCache regions;
	public final int numPortals;
	public final int period;
	public final long regionSeedMask;
	
	private int phase = -1;
	private LevelInfo portal = null;
	private boolean portalPrimed = false;

	private PortalSystem(RegionCache regions, int n, int period, long seedMask) {
		this.regions = regions;
		this.numPortals = n;
		if(WorldTime.daysInYear%period!=0)
			throw new InvalidParameterException("Portal period is not year-aligned: "+period);
		this.period = period;
		this.regionSeedMask = seedMask;
	}

	private static PortalSystem create(RegionCache regions, int n) {
		switch(n) {
			case 2:
				return new Two(regions);
			case 6:
				return new Six(regions);
			default:
				throw new InvalidParameterException("Unsupported number of portals: "+n);
		}
	}

	public static PortalSystem create(RegionCache regions) {
		return create(regions, regions.mode.getNumPortals());
	}
	
	public int getPhase() {
		return phase;
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
	
	private int calcPhase() {
		return period<1 ? 0 : WorldTime.getDayOfYear() % period;
	}
	
	public void updateOtherRegion() {
		if(numPortals==0 || portal==null) {
			otherRegion = null;
			otherLevel = null;
			return;
		}
		System.out.printf("PortalSystem.nearestPortal(%d) at [%d, %d]\n", portal.portal.index, portal.x0, portal.z0);
		long seed = getOtherSeed(AetherTown.region.seed, portal.portal.index, phase);
		System.out.printf("otherRegion: %dL\n", seed);
		otherRegion = regions.get(seed);
		otherLevel = otherRegion.portals[portal.portal.otherIndex];
		portalPrimed = false;
		
		AetherTown.levelCache.addAllAdj(AetherTown.levelInfo, false, false);
	}
	
	public void updateTime() {
		int ph = calcPhase();
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
		phase = calcPhase();
		updateOtherRegion();
	}

	public void updateWalk(int x, int z) {
		int portalx = portal.getLevelSize()/2;
		int portalz = portal.getLevelSize()/2;
		if(portalx==x && portalz==z) {
			if(!portalPrimed)
				System.out.println("Portal primed.");
			portalPrimed = true;
		}
		else if(portalPrimed) {
			int tx = (portalx-x)*portal.portal.d.dx;
			int tz = (portalz-z)*portal.portal.d.dz;
			if(tx>0 || tz>0) {
				System.out.println("Portal triggered.");
				AetherTown.aether.activatePortal();
			}
			else
				System.out.println("Portal left.");
			portalPrimed = false;
		}
	}
	
	public abstract long getOtherSeed(long seed, int index, int phase);
	public abstract Dir getPortalDir(int index);
	public abstract int getOtherIndex(int index);
	
	public long getRegionSeed(long seed) {
		return seed<0L ? new Random().nextLong() & regionSeedMask : seed;
	}

	public PortalInfo createPortalInfo(int index) {
		PortalInfo p = new PortalInfo();
		p.index = index;
		p.d = getPortalDir(index);
		p.otherIndex = getOtherIndex(index);
		return p;
	}
	
	public String createKnowledgeReport(Region region) {
		int phase = calcPhase();
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("<p>%s portal knowledge for %s<br>%d day period</p>",
				RegionVisits.getRegionTitle(region.seed, false),
				WorldTime.getFormattedDate(), period));
		sb.append("<table style=\"width:100%\">");
		for(int i=0; i<numPortals; i++) {
			long seed = getOtherSeed(region.seed, i, phase);
			sb.append("<tr><td class=\"w\" style=\"width:20%;text-align:center\">");
			sb.append(WorldTime.romanNumeral(i+1));
			sb.append("</td>");
			for(int j=3; j>=0; j--) {
				String s = String.format("%04X", (seed >> (j*16)) & 0xffffL);
				sb.append("<td style=\"width:20%;text-align:center\">");
				sb.append(s);
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	private boolean roundtripTestSeed(long seed, int index, int phase) {
		long otherSeed = getOtherSeed(seed, index, phase);
		int otherIndex = getOtherIndex(index);
		long back = getOtherSeed(otherSeed, otherIndex, phase);
		// System.out.printf("seed=%016X, other=%016X, back=%016X\n", seed, otherSeed, back);
		return back==seed;
	}

	private boolean roundtripTestIndex(int index) {
		int otherIndex = getOtherIndex(index);
		return getOtherIndex(otherIndex)==index;
	}

	private boolean roundtripTestDir(int index) {
		return getPortalDir(index)==getPortalDir(getOtherIndex(index)).flip();
	}

	public static int getTrigram(long seed, int n) {
		return (int)(seed>>(n*3)) & 7;
	}

	public static long alterTrigram(long seed, int n, int d) {
		long tri = (getTrigram(seed, n)+d)&7;
		long mask = ~(7L<<(n*3));
		return (seed&mask) | (tri<<(n*3));
	}
	
	public static void main(String[] args) {
		int[] nump = {2, 6};
		Random random = new Random();
		for(int numPortals : nump) {
			PortalSystem psys = create(null, numPortals);
			System.out.printf("\n%d PORTALS\n-----------------\n", numPortals);
			for(int index=0; index<numPortals; index++) {
				System.out.printf("%d/%d : ", index, numPortals);
				for(int phase=0; phase<42/numPortals; phase++) {
					int n = 0;
					int d = 0;
					switch(numPortals) {
						case 2:
							n = phase;
							d = index==0 ? +1 : -1;
							break;
						case 6:
							n = phase * 3 + (index % 3);
							d = index<3 ? +1 : -1;
							break;
					}
					
					System.out.printf("%d%s, ", n, d>0 ? "+" : "-");
					if(!psys.roundtripTestSeed(random.nextLong() & psys.regionSeedMask, index, phase))
						System.err.printf("Round trip failed: seed\n");
				}
				System.out.println();
				if(!psys.roundtripTestIndex(index))
					System.err.printf("Round trip failed: index\n");
				if(!psys.roundtripTestDir(index))
					System.err.printf("Round trip failed: dir\n");
			}
		}
	}
}
