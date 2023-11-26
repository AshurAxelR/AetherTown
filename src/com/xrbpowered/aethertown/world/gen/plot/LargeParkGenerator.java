package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.tiles.Bench;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;

public class LargeParkGenerator extends PresetPlotGenerator {

	private static final Dir[][] setd = {
		{Dir.west, Dir.north, Dir.east},
		{Dir.west, Dir.north, Dir.east},
		{Dir.west, Dir.north, Dir.east}
	};

	private static final PresetData[] presets = {
		new PresetData(new TileTemplate[][] { // large park
			{Park.template, Park.template, Park.template},
			{Park.template, Park.template, Park.template},
			{Park.template, Park.template, Park.template},
		}),
		new PresetData(new TileTemplate[][] { // monument park
			{Park.template, Park.template, Park.template},
			{Park.template, Monument.template, Park.template},
			{Park.template, Street.template, Park.template},
		}),
		new PresetData(new TileTemplate[][] { // monument plaza
			{Plaza.template, Plaza.template, Plaza.template},
			{Plaza.template, Monument.template, Plaza.template},
			{Plaza.template, Street.template, Plaza.template},
		}),
		new PresetData(new TileTemplate[][] { // large plaza
			{Plaza.template, Bench.templatePlaza, Plaza.template},
			{Bench.templatePlaza, Park.template, Bench.templatePlaza},
			{Plaza.template, Street.template, Plaza.template},
		})
	};
	
	private static final EntryPoint[] setent = { new EntryPoint(3, 1) };
	
	private static final WRandom typew = new WRandom(3.5, 1, 0.5, 1);
	private static final WRandom typeUpw = new WRandom(0, 1, 0.5, 0.5);
	
	protected PresetData preset;
	
	public LargeParkGenerator(boolean pointOfInterest, Random random) {
		preset = presets[(pointOfInterest ? typeUpw : typew).next(random)];
	}
	
	@Override
	public int tisize() {
		return 3;
	}

	@Override
	public int tjsize() {
		return 3;
	}

	@Override
	public TileTemplate sett(int ti, int tj) {
		return preset.sett[ti][tj];
	}

	@Override
	public Dir setd(int ti, int tj) {
		return setd[ti][tj];
	}

	@Override
	public EntryPoint[] setent() {
		return setent;
	}

	@Override
	public void fillStreet(Random random) {
	}
	
	public void promote(Random random) {
		if(preset==presets[0]) {
			remove();
			preset = presets[typeUpw.next(random)];
			place(random);
		}
	}
	
}
