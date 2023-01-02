package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TokenProvider;

public class Crossroads extends PlotGenerator implements TokenProvider {

	private static final Dir[][] alignMap = {
		{Dir.north, Dir.east, Dir.east},
		{Dir.north, Dir.north, Dir.south},
		{Dir.west, Dir.west, Dir.south}
	};
	
	public int type = 1;

	@Override
	protected boolean findSize(Random random) {
		setSize(1, 1, 2);
		return true;
	}
	
	@Override
	protected Dir alignToken(int i, int j) {
		return d.apply(alignMap[2-j][i+1]);
	}

	@Override
	protected void placeAt(Token t, int i, int j, Random random) {
		TileTemplate temp;
		if(i==0 && j==1) {
			if(type==0)
				temp = Template.park;
			else
				temp = Template.monument;
		}
		else
			temp = Template.street;
		temp.forceGenerate(t, random).makeSub(this, i, j);
	}

	@Override
	public void fillStreet(Random random) {
	}
	
	@Override
	public void collectTokens(TokenGenerator out, Random random) {
		out.addToken(tokenAt(0, 3, Dir.north).setGenerator(new StreetGenerator(random, 0)));
		out.addToken(tokenAt(2, 1, Dir.east).setGenerator(new StreetGenerator(random, 0)));
		out.addToken(tokenAt(-2, 1, Dir.west).setGenerator(new StreetGenerator(random, 0)));
		out.addToken(tokenAt(0, -1, Dir.south).setGenerator(new StreetGenerator(random, 0)));
	}

}
