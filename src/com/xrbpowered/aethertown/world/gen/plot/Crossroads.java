package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;

public class Crossroads extends StreetPresetGenerator {

	private static final TileTemplate[] mids = { Monument.template, Park.template, Plaza.template }; 
	private static final WRandom midsw = new WRandom(0.2, 0.7, 0.1); 
	
	private static final TileTemplate[][] sett = {
		{Street.subTemplate, Street.template, Street.subTemplate},
		{Street.template, null, Street.template},
		{Street.subTemplate, Street.template, Street.subTemplate}
	};
	
	private static final Dir[][] setd = {
		{Dir.north, Dir.east, Dir.east},
		{Dir.north, Dir.north, Dir.south},
		{Dir.west, Dir.west, Dir.south}
	};
	
	private static final ExitPoint[] setout = {
		new ExitPoint(0, 3, 1, Dir.north, 0, 0),
		new ExitPoint(1, 1, -1, Dir.east, 0, 0),
		new ExitPoint(2, -1, 1, Dir.south, 0, 0),
		new ExitPoint(3, 1, 3, Dir.west, 0, 0)
	};
	
	private static final EntryPoint[] setent = { setout[0] };
	
	public final TileTemplate mid;
	
	public Crossroads(TileTemplate mid) {
		super(15);
		this.mid = mid;
	}

	public Crossroads(Random random) {
		this(mids[midsw.next(random)]);
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
		return (ti==1 && tj==1) ? mid : sett[ti][tj]; 
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
	public ExitPoint[] setout() {
		return setout;
	}

	@Override
	protected Tile placeAt(Token t, int i, int j, Random random) {
		Tile tile = super.placeAt(t, i, j, random);
		if(tile.t==Street.subTemplate) {
			((StreetTile) tile).lamp.req = true;
		}
		return tile;
	}
	
	@Override
	public void fillStreet(Random random) {
	}
	
	public static Token centerAt(Token t) {
		return new Token(t.level, t.x-t.d.dx, t.y, t.z-t.d.dz, t.d);
	}

}
