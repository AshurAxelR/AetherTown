package com.xrbpowered.aethertown.world.region;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;

public abstract class RegionMode {

	public final int sizex, sizez;
	
	protected RegionMode(int sizex, int sizez) {
		this.sizex = sizex;
		this.sizez = sizez;
	}
	
	public abstract void coreGenerate(Region region, Random random);
	public abstract String formatValue();
	
	private static int intParam(String[] vs, int i, int def) {
		return vs.length>i ? Integer.parseInt(vs[i]) : def;
	}

	private static LevelSettlementType settlementParam(String[] vs, int i, LevelSettlementType def) {
		return vs.length>i ? LevelSettlementType.valueOf(vs[i]) : def;
	}

	public static RegionMode parseValue(String value) {
		String[] vs = value.split(",\\s*", 10);
		switch(vs[0]) {
			case "linear":
				return linear;
			case "oneLevel":
				return new OneLevel(intParam(vs, 1, 2), settlementParam(vs, 2, LevelSettlementType.village));
			case "smallPeak":
				return new SmallPeak(settlementParam(vs, 1, LevelSettlementType.village), settlementParam(vs, 2, LevelSettlementType.outpost));
			default:
				System.err.printf("Unknown region mode: %s\n", vs[0]);
				return defaultMode;
		}
	}
	
	public static RegionMode linear = new RegionMode(512, 128) {
		@Override
		public void coreGenerate(Region region, Random random) {
			new RegionPaths(region, random).generatePaths();
		}
		@Override
		public String formatValue() {
			return "linear";
		}
	};

	public static class OneLevel extends RegionMode {
		public final int size;
		public final LevelSettlementType settlement;
		public OneLevel(int size, LevelSettlementType settlement) {
			super(16, 16);
			this.size = size;
			this.settlement = settlement;
		}
		@Override
		public void coreGenerate(Region region, Random random) {
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
	
	public static class SmallPeak extends RegionMode {
		public final LevelSettlementType midSettlement, sideSettlement;
		public SmallPeak(LevelSettlementType midSettlement, LevelSettlementType sideSettlement) {
			super(16, 16);
			this.midSettlement = midSettlement;
			this.sideSettlement = sideSettlement;
		}
		@Override
		public void coreGenerate(Region region, Random random) {
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
	
	public static RegionMode defaultMode = new OneLevel(2, LevelSettlementType.village);

}
