package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Bridge;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;

public class BridgePresetGenerator extends StreetPresetGenerator {

	private static final PresetData fullPreset = new PresetData(new TileTemplate[][] {
		{null, null, null, Street.template, null},
		{Street.template, Street.subTemplate, Street.subTemplate, Bridge.template, Street.template},
		{Street.subTemplate, StreetSlope.template4, StreetSlope.template4, Street.template, null}
	}).d(new Dir[][] {
		{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
		{Dir.west, Dir.west, Dir.west, Dir.west, Dir.east},
		{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
	}).y(new int[][] {
		{0, 0, 0, 0, 0},
		{8, 8, 8, 8, 8},
		{8, 8, 4, 0, 0},
	});
	
	private static final PresetData noBridgePreset = new PresetData(new TileTemplate[][] {
		{null, null, null, null, null},
		{Street.template, Street.subTemplate, Street.subTemplate, Street.subTemplate, Street.template},
		{Street.subTemplate, StreetSlope.template4, StreetSlope.template4, Street.template, null}
	}).d(new Dir[][] {
		{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
		{Dir.west, Dir.west, Dir.west, Dir.west, Dir.east},
		{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
	}).y(new int[][] {
		{0, 0, 0, 0, 0},
		{8, 8, 8, 8, 8},
		{8, 8, 4, 0, 0},
	});

	private static final PresetData[] presets = {
		null, null, null,
		new PresetData(new TileTemplate[][] { // 3
			{null, null, null, null, null},
			{Street.template, StreetSlope.template4, StreetSlope.template4, Street.subTemplate, null},
			{null, null, null, Street.template, null}
		}).d(new Dir[][] {
			{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
			{Dir.west, Dir.east, Dir.east, Dir.west, Dir.east},
			{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
		}).y(new int[][] {
			{0, 0, 0, 0, 0},
			{8, 8, 4, 0, 0},
			{0, 0, 0, 0, 0},
		}),
		null,
		new PresetData(new TileTemplate[][] { // 5
			{null, null, null, Street.template, null},
			{null, null, null, Street.subTemplate, null},
			{null, null, null, Street.template, null}
		}).d(new Dir[][] {
			{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
			{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
			{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
		}).y(new int[][] {
			{0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0},
		}),
		new PresetData(new TileTemplate[][] { // 6
			{null, null, null, Street.template, null},
			{Street.template, StreetSlope.template4, StreetSlope.template4, Street.subTemplate, null},
			{null, null, null, null, null}
		}).d(new Dir[][] {
			{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
			{Dir.west, Dir.east, Dir.east, Dir.west, Dir.east},
			{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
		}).y(new int[][] {
			{0, 0, 0, 0, 0},
			{8, 8, 4, 0, 0},
			{0, 0, 0, 0, 0},
		}),
		new PresetData(new TileTemplate[][] { // 7
			{null, null, null, Street.template, null},
			{Street.template, StreetSlope.template4, StreetSlope.template4, Street.subTemplate, null},
			{null, null, null, Street.template, null}
		}).d(new Dir[][] {
			{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
			{Dir.west, Dir.east, Dir.east, Dir.west, Dir.east},
			{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
		}).y(new int[][] {
			{0, 0, 0, 0, 0},
			{8, 8, 4, 0, 0},
			{0, 0, 0, 0, 0},
		}),
		null,
		noBridgePreset, // 9
		new PresetData(new TileTemplate[][] { // 10
			{null, null, null, null, null},
			{Street.template, Street.subTemplate, Street.subTemplate, Street.subTemplate, Street.template},
			{null, null, null, null, null}
		}).d(new Dir[][] {
			{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
			{Dir.west, Dir.west, Dir.west, Dir.west, Dir.east},
			{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
		}).y(new int[][] {
			{0, 0, 0, 0, 0},
			{8, 8, 8, 8, 8},
			{0, 0, 0, 0, 0},
		}),
		noBridgePreset, // 11
		fullPreset, // 12
		fullPreset, // 13
		fullPreset, // 14
		fullPreset // 15
	};
	
	private static final ExitPoint[] setent = {
		new ExitPoint(0, 3, 3, Dir.north, 0, -1),
		new ExitPoint(1, 1, -1, Dir.east, 8, 1),
		new ExitPoint(2, -1, 3, Dir.south, 0, -1).crit(),
		new ExitPoint(3, 1, 5, Dir.west, 8, 1).crit()
	};
	
	protected PresetData preset;

	public BridgePresetGenerator() {
		super(15);
	}

	@Override
	protected StreetPresetGenerator setMask(int mask) {
		super.setMask(mask);
		this.preset = presets[mask];
		return this;
	}
	
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
		return preset.sett[ti][tj]; 
	}
	
	@Override
	public Dir setd(int ti, int tj) {
		return preset.setd[ti][tj];
	}
	
	@Override
	public int sety(int ti, int tj) {
		return preset.sety[ti][tj];
	}
	
	@Override
	public EntryPoint[] setent() {
		return setent;
	}
	
	@Override
	public void fillStreet(Random random) {
	}
	
	@Override
	protected Tile placeAt(Token t, int i, int j, Random random) {
		Tile tile = super.placeAt(t, i, j, random);
		int ti = ti(i, j);
		int tj = tj(i, j);
		if(((ti+tj)%2==1) && (tile.t==Street.subTemplate || tile.t==Street.template)) {
			((StreetTile) tile).lamp = true;
		}
		return tile;
	}
	
	@Override
	protected boolean removeOrPromote(int conn, int mask, Token[] tokens, Random random) {
		if(conn>1) {
			PresetData p = presets[mask];
			if(p!=null && p!=preset) {
				remove();
				setMask(mask);
				place(random);
				return true;
			}
		}
		return super.removeOrPromote(conn, mask, tokens, random);
	}
	
}
