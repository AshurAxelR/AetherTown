package com.xrbpowered.aethertown.world.gen;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.gen.plot.Crossroads;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.LargeParkGenerator;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;
import com.xrbpowered.aethertown.world.gen.plot.StreetPresetGenerator;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.tiles.Bench;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Street;

public class StreetLayoutGenerator extends TokenGenerator {

	public final int houseLimit;
	
	public Token startToken;
	
	public StreetLayoutGenerator(int limit) {
		super(0);
		houseLimit = limit;
	}
	
	@Override
	public boolean generate(Token startToken, Random random) {
		LevelInfo info = startToken.level.info;
		if(info.terrain.noParks) {
			this.startToken = startToken;
			Street.template.forceGenerate(startToken);
			if(info.isPortal())
				new PortalZoneGenerator().generate(startToken, random);
			return true;
		}
		clearTokens();
		this.startToken = Crossroads.centerAt(startToken);
		Crossroads start = new Crossroads(Monument.template);
		start.ignoreHeightLimit = true;
		if(!start.generate(this.startToken, random))
			GeneratorException.raise("StreetLayoutGenerator: initial placement failed");
		start.collectTokens(this, random);
		return generate(random);
	}
	
	private static final WRandom nextw = new WRandom(0.2, 0.1, 0, 0.3, 1, 0.3);
	private static final WRandom nextwLim = new WRandom(1, 0.3, 0, 0.2, 0, 0.5);
	
	@Override
	protected Generator selectGenerator(Token t, Random random) {
		if(t.level.houseCount<houseLimit) {
			StreetGenerator street = new StreetGenerator(random, StreetGenerator.getPrevDy(t));
			if(street.checkFit(t, random) && (street.isPerfectMatch() || tokenCount()<2 || random.nextInt(10)>0))
				return street;
		}
		return StreetGenerator.selectSideGenerator(t.level, (t.level.houseCount>=houseLimit) ? nextwLim : nextw, random, 0);
	}

	public static boolean addPointOfInterest(Token t, Random random) {
		if(t.level.info.settlement.maxHouses>0 && new HouseGenerator().generate(t, random))
			return true;
		else if(new LargeParkGenerator(true, random).generate(t, random))
			return true;
		else if(Bench.templatePlaza.generate(t, random))
			return true;
		else
			return false;
	}

	public static boolean addPointOfInterest(Tile tile, Random random) {
		Dir[] dirs = random.nextBoolean() ?
				new Dir[] {tile.d, tile.d.cw(), tile.d.ccw()} :
				new Dir[] {tile.d, tile.d.ccw(), tile.d.cw()};
		for(Dir d : dirs) {
			Token t = Token.forAdj(tile, d);
			if(addPointOfInterest(t, random))
				return true;
		}
		return false;
	}
	
	public static void trimStreets(Level level, Random random) {
		boolean upd = true;
		while(upd) {
			upd = false;
			for(int x=0; x<level.levelSize; x++)
				for(int z=0; z<level.levelSize; z++) {
					Tile tile = level.map[x][z];
					if(tile!=null && tile.t==Street.template) {
						switch(Street.trimStreet(tile, random)) {
							case 2:
								upd = true;
								break;
							case 1:
								upd |= addPointOfInterest(tile, random);
								break;
							default:
						}
					}
				}
			
			LinkedList<StreetPresetGenerator> splots = new LinkedList<>();
			for(PlotGenerator plot : level.plots) {
				if(plot instanceof StreetPresetGenerator)
					splots.add((StreetPresetGenerator) plot);
			}
			
			for(StreetPresetGenerator sp : splots) {
				upd |= sp.trimStreet(random);
			}
		}
	}
	
	private static void connectOut(Level level, Random random, boolean multi, StreetLayoutGenerator sides) {
		for(LevelConnection lc : level.info.conns) {
			if(!new StreetConnector(level, lc.d, 0, sides).connectOut(lc, random, multi))
				GeneratorException.raise("Failed to connect %s[%d]\n", lc.d.name(), lc.i);
		}
	}
	
	private static void reconnectStreets(Level level, Random random, boolean loop, StreetLayoutGenerator sides) {
		if(level.info.settlement.maxHouses==0)
			return;
		boolean upd = true;
		while(upd && loop) {
			upd = false;
			for(Dir d : Dir.shuffle(random)) {
				upd |= new StreetConnector(level, d, sides).connectAll(random);
			}
		}
	}
	
	public static void finishLayout(Level level, Random random) {
		StreetLayoutGenerator sides = new StreetLayoutGenerator(0);
		if(level.info.terrain.noParks) {
			connectOut(level, random, false, sides);
			trimStreets(level, random);
		}
		else {
			reconnectStreets(level, random, false, sides);
			connectOut(level, random, true, sides);
			trimStreets(level, random);
			for(PlotGenerator plot : level.plots)
				plot.fillStreet(random);
			reconnectStreets(level, random, true, sides);
		}
		level.heightLimiter.revalidate();
		sides.generate(random);
	}
	
	public static void followTerrain(Level level) {
		if(level.info.isPortal())
			return;
		ArrayList<FollowTerrain> fts = FollowTerrain.findStreets(level);
		for(FollowTerrain ft : fts)
			ft.apply(ft.compute(), level);
		FollowTerrain.levelCorners(level);
	}
}
