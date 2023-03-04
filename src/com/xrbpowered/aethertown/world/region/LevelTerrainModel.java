package com.xrbpowered.aethertown.world.region;

import java.util.Random;

import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.gen.HillsGenerator;

public class LevelTerrainModel {

	public static final LevelTerrainModel bottom = new LevelTerrainModel(-100, -90, -90, -80);
	public static final LevelTerrainModel low = new LevelTerrainModel(-100, -50, -30, 0);
	public static final LevelTerrainModel hill = new LevelTerrainModel(-80, -20, 20, 80) {
		@Override
		public void fillTerrain(Level level, Random random) {
			HillsGenerator.expand(level, random, 5, 15, -2, 2);
			HillsGenerator.expand(level, random, 5, 15, -2, 4);
			HillsGenerator.expand(level, random, 1, 0, -8, 2);
		}
	};

	public static LevelTerrainModel nullTerrain = bottom;
	
	public final int maxy;
	public final int starty, conny;
	public final int edgey;
	
	private LevelTerrainModel(int edgey, int conny, int starty, int maxy) {
		this.maxy = maxy;
		this.starty = starty;
		this.conny = conny;
		this.edgey = edgey;
	}
	
	public void startTerrain(Token startToken, Random random) {
		new HillsGenerator(20).setAmp(-2, 2).generate(startToken, random);
	}
	
	public void fillTerrain(Level level, Random random) {
		HillsGenerator.expand(level, random, 5, 15, -2, 2);
		HillsGenerator.expand(level, random, 1, 0, -6, 2);
	}
	
	private static final WRandom w = new WRandom(0.3, 0.7);
	private static final WRandom wpark = new WRandom(0.5, 0.5);
	private static final LevelTerrainModel[] list = {low, hill};
	public static LevelTerrainModel random(LevelInfo level, Random random) {
		if(level.settlement.getStreetMargin(level.getLevelSize(), true)<24)
			return low;
		else if(level.settlement==LevelSettlementType.none || level.settlement==LevelSettlementType.inn)
			return list[wpark.next(random)];
		else
			return list[w.next(random)];
	}
	
}
