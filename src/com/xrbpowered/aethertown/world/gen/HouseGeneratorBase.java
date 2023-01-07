package com.xrbpowered.aethertown.world.gen;

import com.xrbpowered.aethertown.utils.Dir;

public abstract class HouseGeneratorBase extends PlotGenerator {

	public boolean alignStraight;
	public boolean illum;

	public int marginLeft = 0;
	public int marginRight = 0;
	public int marginFront = 0;
	public int marginBack = 0;
	
	public abstract String getInfo();

	@Override
	protected Dir alignToken(int i, int j) {
		return d;
	}
	
}
