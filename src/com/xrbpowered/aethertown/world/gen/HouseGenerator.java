package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.SubTile;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Token;

public class HouseGenerator extends PlotGenerator {

	private static final int[][] sizes = {
		{0, 1, 1}, {0, 1, 2}, {0, 1, 3}, {1, 0, 1}, {1, 0, 2}, {1, 0, 3},
		{1, 1, 1}, {1, 2, 1}, {2, 1, 1}
	};
	private static final WRandom sizesw = new WRandom(
		2, 0.75, 0.1, 2, 0.75, 0.1,
		1.5, 0.25, 0.25
	);
	
	public boolean alignStraight;
	public boolean illum;
	
	@Override
	protected boolean findSize(Random random) {
		int[] s = sizes[sizesw.next(random)];
		setSize(s[0], s[1], s[2]);
		int w = left+right;
		if(w==fwd)
			alignStraight = random.nextBoolean();
		else
			alignStraight = (fwd>w);
		return true;
	}

	@Override
	protected void placeAt(Token t, int i, int j, Random random) {
		new SubTile(this, t.d, Template.house, i, j).place(t);
		HeightLimiter.updateAt(t, HeightLimiter.maxWall, 2, 3);
	}
	
	@Override
	protected void place(Random random) {
		illum = (random.nextInt(3)>0);
		super.place(random);
	}

}
