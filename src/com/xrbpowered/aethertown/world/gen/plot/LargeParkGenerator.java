package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;

public class LargeParkGenerator extends PlotGenerator {

	private static final WRandom typew = new WRandom(3.5, 1, 0.5, 1);
	private static final WRandom typeUpw = new WRandom(0, 1, 0.5, 0.5);
	
	public int type = 0;
	private WRandom w;
	
	public LargeParkGenerator(boolean pointOfInterest) {
		w = pointOfInterest ? typeUpw : typew;
	}
	
	@Override
	protected boolean findSize(Random random) {
		setSize(1, 1, 2);
		return true;
	}

	@Override
	protected Tile placeAt(Token t, int i, int j, Random random) {
		TileTemplate temp;
		if(i==0 && j<2) {
			if(type==0)
				temp = Park.template;
			else if(j==0)
				temp = Street.template;
			else if(type==3)
				temp = Park.template;
			else
				temp = Monument.template;
		}
		else if(type<2)
			temp = Park.template;
		else
			temp = Plaza.template;
		return temp.forceGenerate(t, random).makeSub(this, i, j);
	}
	
	@Override
	public boolean generate(Token startToken, Random random) {
		type = w.next(random);
		return super.generate(startToken, random);
	}

	@Override
	public void fillStreet(Random random) {
	}
	
	public void promote(Random random) {
		if(type==0) {
			remove();
			type = typeUpw.next(random);
			place(random);
		}
	}
	
}
