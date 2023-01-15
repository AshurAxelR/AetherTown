package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.tiles.Bridge;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;

public class BridgePresetGenerator extends StreetPresetGenerator {

	private static final TileTemplate[][] sett = {
		{null, null, null, Street.template, null},
		{Street.template, Street.subTemplate, Street.subTemplate, Bridge.template, Street.template},
		{Street.subTemplate, StreetSlope.template4, StreetSlope.template4, Street.template, null}
	};
	
	private static final Dir[][] setd = {
		{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
		{Dir.west, Dir.west, Dir.west, Dir.west, Dir.east},
		{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
	};
	
	private static final int[][] sety = {
		{0, 0, 0, 0, 0},
		{8, 8, 8, 8, 8},
		{8, 8, 4, 0, 0},
	};
	
	private static final ExitPoint[] setent = {
		new ExitPoint(3, 3, Dir.north, 0, -1),
		new ExitPoint(1, -1, Dir.east, 8, 1),
		new ExitPoint(-1, 3, Dir.south, 0, -1),
		new ExitPoint(1, 5, Dir.west, 8, 1)
	};
	
	@Override
	public int tisize() {
		return 3;
	}
	
	@Override
	public int tjsize() {
		return 5;
	}
	
	@Override
	public TileTemplate sett(int ti, int tj) {
		return sett[ti][tj]; 
	}
	
	@Override
	public Dir setd(int ti, int tj) {
		return setd[ti][tj];
	}
	
	@Override
	public int sety(int ti, int tj) {
		return sety[ti][tj];
	}
	
	@Override
	public EntryPoint[] setent() {
		return setent;
	}
	
	@Override
	public void fillStreet(Random random) {
	}
	
}
