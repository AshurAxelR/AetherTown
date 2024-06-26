package com.xrbpowered.aethertown.world.region;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Rand;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.region.paths.LinearRegionPaths;
import com.xrbpowered.aethertown.world.region.paths.PortalRegionPaths;

public abstract class RegionMode {

	public final int sizex, sizez;
	
	protected RegionMode(int sizex, int sizez) {
		this.sizex = sizex;
		this.sizez = sizez;
	}
	
	public abstract void coreGenerate(Region region, Rand random);
	public abstract String formatValue();
	
	public int getNumPortals() {
		return 0;
	}
	
	private static int intParam(String[] vs, int i, int def) {
		return vs.length>i ? Integer.parseInt(vs[i]) : def;
	}

	private static LevelSettlementType settlementParam(String[] vs, int i, LevelSettlementType def) {
		return vs.length>i ? LevelSettlementType.valueOf(vs[i]) : def;
	}
	
	private static LevelTerrainModel terrainParam(String[] vs, int i, LevelTerrainModel def) {
		try {
			return vs.length>i ? (LevelTerrainModel) LevelTerrainModel.class.getField(vs[i]).get(null) : def;
		} catch (Exception e) {
			return def;
		}
	}

	public static RegionMode parseValue(String value) {
		String[] vs = value.split(",\\s*", 10);
		switch(vs[0]) {
			case "linear":
				return new Linear(intParam(vs, 1, 64));
			case "oneLevel":
				return new OneLevel(intParam(vs, 1, 2), settlementParam(vs, 2, LevelSettlementType.village));
			case "fewLevels":
				return new FewLevels(settlementParam(vs, 1, LevelSettlementType.village), terrainParam(vs, 2, LevelTerrainModel.hill));
			case "smallPeak":
				return new SmallPeak(settlementParam(vs, 1, LevelSettlementType.village), settlementParam(vs, 2, LevelSettlementType.outpost));
			case "twoPortals":
				return twoPortals;
			default:
				System.err.printf("Unknown region mode: %s\n", vs[0]);
				return defaultMode;
		}
	}
	
	public static class Linear extends RegionMode {
		public Linear(int addWidth) {
			super(addWidth+128, 128);
		}
		@Override
		public int getNumPortals() {
			return 6;
		}
		@Override
		public void coreGenerate(Region region, Rand random) {
			new LinearRegionPaths(region, random).generatePaths();
			for(int index=0; index<getNumPortals(); index++) {
				Dir d = region.cache.portals.getPortalDir(index);
				if(!new PortalRegionPaths(region, d.flip()).scanAndPlace(index, 56, random))
					GeneratorException.raise("Can't connect portal %d (%s)", index, d.name());
			}
		}
		@Override
		public String formatValue() {
			return String.format("linear,%d", sizex-128);
		}
	}

	public static class OneLevel extends RegionMode {
		public final int size;
		public final LevelSettlementType settlement;
		public OneLevel(int size, LevelSettlementType settlement) {
			super(16, 16);
			this.size = size;
			this.settlement = settlement;
		}
		@Override
		public void coreGenerate(Region region, Rand random) {
			LevelInfo level = new LevelInfo(region, sizex/2, sizez/2, size, random.nextLong());
			level.setTerrain(LevelTerrainModel.hill);
			level.setSettlement(settlement);
			level.place();
			region.startLevel = level;
		}
		@Override
		public String formatValue() {
			return String.format("oneLevel,%d,%s", size, settlement.name());
		}
	}
	
	public static class FewLevels extends RegionMode {
		public final LevelSettlementType settlement;
		public final LevelTerrainModel terrain;
		public FewLevels(LevelSettlementType settlement, LevelTerrainModel terrain) {
			super(16, 16);
			this.settlement = settlement;
			this.terrain = terrain;
		}
		private LevelInfo makeLevel(Region region, int x, int z, Rand random) {
			LevelInfo level = new LevelInfo(region, x, z, 2, random.nextLong());
			level.setTerrain(terrain);
			level.setSettlement(settlement);
			level.place();
			return level;
		}
		@Override
		public void coreGenerate(Region region, Rand random) {
			int x0 = sizex/2;
			int z0 = sizez/2;
			region.startLevel = makeLevel(region, x0, z0, random);
			for(Dir d : Dir.values()) {
				makeLevel(region, x0+d.dx*2, z0+d.dz*2, random);
				region.connectLevels(x0+(1+d.dx)/2, z0+(1+d.dz)/2, d);
			}
		}
		@Override
		public String formatValue() {
			return String.format("fewLevels,%s,%s", settlement.name(), terrain.name);
		}
	}
	
	public static class SmallPeak extends RegionMode {
		public final LevelSettlementType midSettlement, sideSettlement;
		public SmallPeak(LevelSettlementType midSettlement, LevelSettlementType sideSettlement) {
			super(16, 16);
			this.midSettlement = midSettlement;
			this.sideSettlement = sideSettlement;
		}
		@Override
		public void coreGenerate(Region region, Rand random) {
			int x0 = sizex/2;
			int z0 = sizez/2;
			LevelInfo level = new LevelInfo(region, x0, z0, 1, random.nextLong());
			level.setTerrain(LevelTerrainModel.peak);
			level.setSettlement(midSettlement);
			level.place();
			region.startLevel = level;
			for(Dir d : Dir.values()) {
				level = new LevelInfo(region, x0+d.dx, z0+d.dz, 1, random.nextLong());
				level.setTerrain(LevelTerrainModel.hill);
				level.setSettlement(sideSettlement);
				level.place();
				region.connectLevels(x0, z0, d);
			}
		}
		@Override
		public String formatValue() {
			return String.format("smallPeak,%s,%s", midSettlement.name(), sideSettlement.name());
		}
	}
	
	public static RegionMode twoPortals = new RegionMode(16, 16) {
		@Override
		public int getNumPortals() {
			return 2;
		}
		@Override
		public void coreGenerate(Region region, Rand random) {
			int x0 = sizex/2;
			int z0 = sizez/2;
			Dir[] dirs = new Dir[] {region.cache.portals.getPortalDir(0), region.cache.portals.getPortalDir(1)};
			int index = 0;
			for(Dir d : dirs) {
				region.generatePortal(index, x0+2*d.flip().dx, z0+2*d.flip().dz, d, random);
				index++;
			}
			LevelInfo level = new LevelInfo(region, x0, z0, 1, random.nextLong());
			level.setTerrain(LevelTerrainModel.low);
			level.setSettlement(LevelSettlementType.inn);
			level.place();
			region.startLevel = level;
			for(Dir d : dirs)
				region.connectLevels(x0, z0, d.flip());
		}
		@Override
		public String formatValue() {
			return "twoPortals";
		}
	};
	
	public static RegionMode defaultMode = new OneLevel(2, LevelSettlementType.village);

}
