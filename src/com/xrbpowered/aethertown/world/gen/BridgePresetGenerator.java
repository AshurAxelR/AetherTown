package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TokenProvider;
import com.xrbpowered.aethertown.world.tiles.Bridge;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;

public class BridgePresetGenerator extends PlotGenerator implements TokenProvider {

	private static final TileTemplate[][] sett = {
		{null, null, null, Street.template, null},
		{Street.template, Street.template, Street.template, Bridge.template, Street.template},
		{Street.subTemplate, StreetSlope.template4, StreetSlope.template4, Street.template, null}
	};
	
	private static final Dir[][] setd = {
		{Dir.west, Dir.west, Dir.west, Dir.north, Dir.east},
		{Dir.west, Dir.west, Dir.west, Dir.west, Dir.east},
		{Dir.south, Dir.east, Dir.east, Dir.south, Dir.east}
	};
	
	private static final int[][] sety = {
		{0, 0, 0, 0, 0},
		{8, 8, 8, 8, 8},
		{8, 8, 4, 0, 0},
	};
	
	private Dir align = Dir.north;
	private int basey = 0;

	private void setAlign(Dir align) {
		this.align = align;
		switch(align) {
			case north:
				left = 3;
				right = 1;
				fwd = 2;
				basey = 0;
				break;
			case east:
				left = 1;
				right = 1;
				fwd = 4;
				basey = 8;
				break;
			case south:
				left = 1;
				right = 3;
				fwd = 2;
				basey = 0;
				break;
			case west:
				left = 1;
				right = 1;
				fwd = 4;
				basey = 8;
				break;
		}
	}
	
	private int tj(int i, int j) {
		switch(align) {
			case north: return left+i;
			case east: return j;
			case south: return right-i;
			case west: return fwd-j;
			default: return 0;
		}
	}

	private int ti(int i, int j) {
		switch(align) {
			case north: return fwd-j;
			case east: return left+i;
			case south: return j;
			case west: return right-i;
			default: return 0;
		}
	}
	
	private boolean inset(int ti, int tj) {
		return (ti>=0 && ti<3 && tj>=0 && tj<5);
	}

	@Override
	protected Dir alignToken(int i, int j) {
		int ti = ti(i, j);
		int tj = tj(i, j);
		if(inset(ti, tj)) {
			return setd[ti][tj].apply(d).unapply(align);
		}
		else
			return d;
	}
	
	@Override
	public boolean ignoreToken(int i, int j) {
		return sett[ti(i, j)][tj(i, j)]==null;
	}
	
	@Override
	public Token tokenAt(int i, int j, Dir td) {
		Token t = super.tokenAt(i, j, td);
		int ti = ti(i, j);
		int tj = tj(i, j);
		if(inset(ti, tj))
			t.offsY(sety[ti(i, j)][tj(i, j)]-basey);
		return t;
	}
	
	@Override
	protected boolean findSize(Random random) {
		for(Dir d : Dir.shuffle(random)) {
			setAlign(d);
			if(fits())
				return true;
		}
		return false;
	}

	@Override
	protected void placeAt(Token t, int i, int j, Random random) {
		TileTemplate temp = sett[ti(i, j)][tj(i, j)];
		if(temp!=null)
			temp.forceGenerate(t, random).makeSub(this, i, j);
	}
	
	@Override
	public void fillStreet(Random random) {
	}

	@Override
	public void collectTokens(TokenGenerator out, Random random) {
		switch(align) {
			case north:
				out.addToken(tokenAt(0, 3, Dir.north).setGenerator(new StreetGenerator(random, -1)));
				out.addToken(tokenAt(2, 1, Dir.east).offsY(8).setGenerator(new StreetGenerator(random, 0)));
				out.addToken(tokenAt(-4, 1, Dir.west).offsY(8).setGenerator(new StreetGenerator(random, 0)));
				out.addToken(tokenAt(0, -1, Dir.south).setGenerator(new StreetGenerator(random, -1)));
				return;
			case east:
				out.addToken(tokenAt(0, 5, Dir.north).setGenerator(new StreetGenerator(random, 0)));
				out.addToken(tokenAt(2, 3, Dir.east).offsY(-8).setGenerator(new StreetGenerator(random, -1)));
				out.addToken(tokenAt(-2, 3, Dir.west).offsY(-8).setGenerator(new StreetGenerator(random, -1)));
				out.addToken(tokenAt(0, -1, Dir.south).setGenerator(new StreetGenerator(random, 0)));
				return;
			case south:
				out.addToken(tokenAt(0, 3, Dir.north).setGenerator(new StreetGenerator(random, -1)));
				out.addToken(tokenAt(4, 1, Dir.east).offsY(8).setGenerator(new StreetGenerator(random, 0)));
				out.addToken(tokenAt(-2, 1, Dir.west).offsY(8).setGenerator(new StreetGenerator(random, 0)));
				out.addToken(tokenAt(0, -1, Dir.south).setGenerator(new StreetGenerator(random, -1)));
				return;
			case west:
				out.addToken(tokenAt(0, 5, Dir.north).setGenerator(new StreetGenerator(random, 0)));
				out.addToken(tokenAt(2, 1, Dir.east).offsY(-8).setGenerator(new StreetGenerator(random, -1)));
				out.addToken(tokenAt(-2, 1, Dir.west).offsY(-8).setGenerator(new StreetGenerator(random, -1)));
				out.addToken(tokenAt(0, -1, Dir.south).setGenerator(new StreetGenerator(random, 0)));
				return;
		}
	}
	
}
