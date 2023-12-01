package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.tiles.Bench;
import com.xrbpowered.aethertown.world.tiles.Fountain;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Pavillion;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;

public class LargeParkGenerator extends PresetPlotGenerator {

	private static final Dir[][] setd = {
		{Dir.west, Dir.north, Dir.east},
		{Dir.west, Dir.north, Dir.east},
		{Dir.west, Dir.north, Dir.east}
	};

	private static int poiIndex = 5;
	
	private static final PresetData[] presets = {
		new PresetData(new TileTemplate[][] { // large park, random
			{Park.template, Park.template, Park.template},
			{Park.template, Park.template, Park.template},
			{Park.template, Park.template, Park.template},
		}),
		new PresetData(new TileTemplate[][] { // large lawn
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
		}),
		new PresetData(new TileTemplate[][] { // large lawn front
			{Park.template, Park.template, Park.template},
			{Park.template, Park.template, Park.template},
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
		}),
		new PresetData(new TileTemplate[][] { // large lawn back
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
			{Park.template, Park.template, Park.template},
		}),
		new PresetData(new TileTemplate[][] { // large lawn with tree
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
			{Park.templateLawn, Park.template, Park.templateLawn},
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
		}),
		
		new PresetData(new TileTemplate[][] { // monument park
			{Park.template, Park.template, Park.template},
			{Park.template, Monument.template, Park.template},
			{Park.templateLawn, Street.template, Park.templateLawn},
		}),
		new PresetData(new TileTemplate[][] { // fountain park
			{Park.template, Park.template, Park.template},
			{Park.template, Fountain.template, Park.template},
			{Park.template, Street.template, Park.template},
		}),
		new PresetData(new TileTemplate[][] { // monument plaza
			{Plaza.template, Plaza.template, Plaza.template},
			{Plaza.template, Monument.template, Plaza.template},
			{Plaza.template, Street.template, Plaza.template},
		}),
		new PresetData(new TileTemplate[][] { // fountain plaza
			{Plaza.template, Bench.templatePlaza, Plaza.template},
			{Bench.templatePlazaLampR, Fountain.template, Bench.templatePlazaLampL},
			{Plaza.template, Street.template, Plaza.template},
		}),
		new PresetData(new TileTemplate[][] { // large plaza
			{Plaza.template, Bench.templatePlaza, Plaza.template},
			{Bench.templatePlazaLampR, Park.template, Bench.templatePlazaLampL},
			{Plaza.template, Street.template, Plaza.template},
		}),
		new PresetData(new TileTemplate[][] { // pavillion park
			{Park.template, Park.templateLawn, Park.template},
			{Park.templateLawn, Pavillion.template, Park.templateLawn},
			{Park.template, Street.template, Park.template},
		}),
		new PresetData(new TileTemplate[][] { // pavillion plaza
			{Plaza.template, Plaza.template, Plaza.template},
			{Plaza.template, Pavillion.template, Plaza.template},
			{Plaza.template, Street.template, Plaza.template},
		})
	};
	
	private static final EntryPoint[] setent = { new EntryPoint(3, 1) };
	
	private static final WRandom typew = new WRandom(
		0.5, 1.0, 0.2, 0.5, 0.3,
		0.5, 0.8, 0.1, 0.2, 0.9, 0.4, 0.1
	);
	private static final WRandom typeUpw = new WRandom(
		0, 0, 0, 0, 0,
		0.5, 0.7, 0.1, 0.2, 0.5, 0.8, 0.2
	);
	
	protected int presetIndex;
	
	public LargeParkGenerator(boolean pointOfInterest, Random random) {
		presetIndex =(pointOfInterest ? typeUpw : typew).next(random);
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
		return presets[presetIndex].sett[ti][tj];
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
		if(presetIndex<poiIndex) {
			remove();
			presetIndex = typeUpw.next(random);
			place(random);
		}
	}
	
}
