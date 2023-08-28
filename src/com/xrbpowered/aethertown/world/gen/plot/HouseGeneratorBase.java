package com.xrbpowered.aethertown.world.gen.plot;

import com.xrbpowered.aethertown.utils.Dir;

public abstract class HouseGeneratorBase extends PlotGenerator {

	public boolean alignStraight;

	public int marginLeft = 0;
	public int marginRight = 0;
	public int marginFront = 0;
	public int marginBack = 0;
	
	public abstract String getInfo();

	public int getFootprint() {
		int left = -this.left+marginLeft;
		int right = this.right-marginRight;
		int front = marginFront;
		int back = this.fwd-marginBack;
		return (right-left+1)*(back-front+1);
	}

	@Override
	protected Dir alignToken(int i, int j) {
		return d;
	}
	
}
