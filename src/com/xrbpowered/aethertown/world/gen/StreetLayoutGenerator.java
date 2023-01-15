package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.gen.plot.Crossroads;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Street;

public class StreetLayoutGenerator extends TokenGenerator {

	public final int houseLimit;
	
	public StreetLayoutGenerator(int limit) {
		super(0);
		houseLimit = limit;
	}

	@Override
	public boolean generate(Token startToken, Random random) {
		clearTokens();
		/*Template.street.generate(startToken, random);
		for(Dir d : Dir.values())
			addToken(startToken.next(d, 0).setGenerator(new StreetGenerator(random, 0)));*/
		Crossroads start = new Crossroads();
		start.generate(startToken, random);
		start.collectTokens(this, random);
		return generate(random);
	}
	
	private static final WRandom nextw = new WRandom(0.5, 0.1, 0, 0.3, 1);
	private static final WRandom nextwLim = new WRandom(1.5, 0.3, 0, 0.2, 0);
	
	@Override
	protected Generator selectGenerator(Token t, Random random) {
		StreetGenerator street = new StreetGenerator(random, StreetGenerator.getPrevDy(t));
		boolean fit = (t.level.houseCount>=houseLimit) ? false : street.checkFit(t, random);
		if(fit && (street.isPerfectMatch() || tokenCount()<2 || random.nextInt(10)>0))
			return street;
		else {
			return StreetGenerator.selectSideGenerator(t.level, (t.level.houseCount>=houseLimit) ? nextwLim : nextw, random, 0);
		}
	}

	private static boolean addPointOfInterest(Level level, Tile tile, Random random) {
		Dir[] dirs = random.nextBoolean() ?
				new Dir[] {tile.d, tile.d.cw(), tile.d.ccw()} :
				new Dir[] {tile.d, tile.d.ccw(), tile.d.cw()};
		for(Dir d : dirs) {
			Token t = Token.forAdj(tile, d);
			if(new HouseGenerator().generate(t, random))
				return true;
			if(Monument.template.generate(t, random))
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
								upd |= addPointOfInterest(level, tile, random);
								break;
							default:
						}
					}
				}
		}
	}
	
	private static void reconnectStreets(Level level, Random random) {
		boolean upd = true;
		while(upd) {
			upd = false;
			for(Dir d : Dir.shuffle(random)) {
				upd |= new StreetConnector(level, d).connectAll(random);
			}
		}
		
		for(LevelConnection lc : level.info.conns) {
			if(!new StreetConnector(level, lc.d, 0).connectOut(lc, random))
				System.err.printf("Failed to connect %s[%d]\n", lc.d.name(), lc.i);
		}
	}
	
	public static void finishLayout(Level level, Random random) {
		trimStreets(level, random);
		for(PlotGenerator plot : level.plots)
			plot.fillStreet(random);
		reconnectStreets(level, random);
		level.heightLimiter.revalidate();
	}
}
