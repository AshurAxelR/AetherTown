package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Street;

public class StreetLayoutGenerator extends TokenGenerator {

	public StreetLayoutGenerator(int limit) {
		super(limit);
	}

	@Override
	public boolean generate(Token startToken, Random random) {
		clearTokens();
		Template.street.generate(startToken, random);
		for(Dir d : Dir.values())
			addToken(startToken.next(d, 0).setGenerator(new StreetGenerator(random, 0)));
		return generate(random);
	}
	
	@Override
	protected Generator selectGenerator(Token t, Random random) {
		StreetGenerator street = new StreetGenerator(random, StreetGenerator.getPrevDy(t));
		boolean fit = street.checkFit(t, random);
		if(fit && (street.isPerfectMatch() || tokenCount()<2 || random.nextInt(10)>0))
			return street;
		else
			return StreetGenerator.selectSideGenerator(random, 0);
	}

	private static boolean addPointOfInterest(Level level, Tile tile, Random random) {
		Dir[] dirs = random.nextBoolean() ?
				new Dir[] {tile.d, tile.d.cw(), tile.d.ccw()} :
				new Dir[] {tile.d, tile.d.ccw(), tile.d.cw()};
		for(Dir d : dirs) {
			Token t = Token.forAdj(tile, d);
			if(Template.monument.generate(t, random))
				return true;
		}
		return false;
	}
	
	public static void trimStreets(Level level, Random random) {
		boolean upd = true;
		while(upd) {
			// TODO recalc height limiter
			upd = false;
			for(int x=0; x<level.levelSize; x++)
				for(int z=0; z<level.levelSize; z++) {
					Tile tile = level.map[x][z];
					if(tile!=null && tile.t==Template.street) {
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
	}
	
	public static void finishLayout(Level level, Random random) {
		trimStreets(level, random);
		for(PlotGenerator plot : level.plots)
			plot.fillStreet(random);
		reconnectStreets(level, random);
	}
}
