package com.xrbpowered.aethertown.world;

import java.util.Random;

import com.xrbpowered.aethertown.world.gen.TokenGenerator;

public interface TokenProvider {

	public void collectTokens(TokenGenerator out, Random random);
	
}
