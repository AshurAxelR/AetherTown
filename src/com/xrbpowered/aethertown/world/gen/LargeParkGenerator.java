package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;

public class LargeParkGenerator extends PlotGenerator {

	private static final WRandom typew = new WRandom(3.5, 1, 0.5, 1);
	private static final WRandom typeUpw = new WRandom(0, 1, 0.5, 0.5);
	
	public int type = 0;
	
	@Override
	protected boolean findSize(Random random) {
		setSize(1, 1, 2);
		return true;
	}

	@Override
	protected void placeAt(Token t, int i, int j, Random random) {
		TileTemplate temp;
		if(i==0 && j<2) {
			if(type==0)
				temp = Template.park;
			else if(j==0)
				temp = Template.street;
			else if(type==3)
				temp = Template.park;
			else
				temp = Template.monument;
		}
		else if(type<2)
			temp = Template.park;
		else
			temp = Template.plaza;
		temp.forceGenerate(t, random).makeSub(this, i, j);
	}
	
	@Override
	public boolean generate(Token startToken, Random random) {
		type = typew.next(random);
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
