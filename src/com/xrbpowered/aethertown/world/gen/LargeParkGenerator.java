package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Token;

public class LargeParkGenerator extends PlotGenerator {

	@Override
	protected boolean findSize(Random random) {
		setSize(1, 1, 2);
		return true;
	}

	@Override
	protected void placeAt(Token t, int i, int j, Random random) {
		Template.park.generate(t, random);
	}

	@Override
	public void fillStreet(Random random) {
	}
	
}
