package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Level;
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

	private static int poiIndex = 6;
	
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
		new PresetData(new TileTemplate[][] { // large park with table
			{Park.template, Park.template, Park.template},
			{Park.template, Bench.templateParkTable, Park.template},
			{Park.template, Park.templateLawn, Park.template},
		}),
		new PresetData(new TileTemplate[][] { // large lawn with table
			{Park.templateLawn, Park.templateLawn, Park.templateLawn},
			{Park.templateLawn, Bench.templateParkTable, Park.templateLawn},
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
			{Plaza.template, Plaza.template, Plaza.template},
			{Plaza.templateLamp, Fountain.template, Plaza.templateLamp},
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
		}),
		new PresetData(new TileTemplate[][] { // large park with table poi
			{Park.template, Park.template, Park.template},
			{Park.template, Bench.templatePlazaTable, Park.template},
			{Park.template, Street.template, Park.template},
		})
	};
	
	private static final PresetData xmasPreset = new PresetData(new TileTemplate[][] {
		{Plaza.templateLamp, Plaza.templateLamp, Plaza.templateLamp},
		{Plaza.template, Park.templateXmas, Plaza.template},
		{Plaza.templateLamp, Plaza.template, Plaza.templateLamp},
	});
	
	private static final EntryPoint[] setent = { new EntryPoint(3, 1) };
	
	private static final WRandom typew = new WRandom(
		0.4, 0.8, 0.2, 0.5, 0.8, 0.3,
		0.3, 0.7, 0.2, 0.1, 0.9, 0.4, 0.1, 0.3
	);
	private static final WRandom typeUpw = new WRandom(
		0, 0, 0, 0, 0, 0,
		0.2, 0.6, 0.2, 0.1, 0.5, 0.7, 0.2, 0.5
	);
	
	protected PresetData preset;
	protected boolean poi;
	
	public LargeParkGenerator(Level level, boolean pointOfInterest, Random random) {
		if(!level.hasXmasTree && level.info.settlement.maxHouses>10) {
			preset = xmasPreset;
			poi = true;
		}
		else {
			int i =(pointOfInterest ? typeUpw : typew).next(random);
			preset = presets[i];
			poi = (i>=poiIndex);
		}
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
	
	@Override
	protected void registerPlot() {
		super.registerPlot();
		if(preset==xmasPreset)
			startToken.level.hasXmasTree = true;
	}
	
	public void promote(Random random) {
		if(!poi) {
			remove();
			preset = presets[typeUpw.next(random)];
			poi = true;
			place(random);
		}
	}
	
}
