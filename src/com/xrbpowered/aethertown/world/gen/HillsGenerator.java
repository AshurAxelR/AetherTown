package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Token;

public class HillsGenerator extends TokenGenerator {

	public int mindy = -4;
	public int maxdy = 4;
	
	public HillsGenerator(int limit) {
		super(limit);
	}
	
	public HillsGenerator setAmp(int mindy, int maxdy) {
		this.mindy = mindy;
		this.maxdy = maxdy;
		return this;
	}
	
	@Override
	protected Generator selectGenerator(Token t, Random random) {
		return Template.hill;
	}

	@Override
	protected boolean checkToken(Token t) {
		int miny = t.level.heightLimiter.miny[t.x][t.z];
		int maxy = t.level.heightLimiter.maxy[t.x][t.z];
		if(miny<=maxy) {
			if(t.y>maxy)
				t.y = maxy;
			if(t.y<miny)
				t.y = miny;
		}
		else {
			t.y = (miny+maxy)/2;
		}
		return true;
	}
	
	@Override
	protected void spreadTokens(Token t, Random random) {
		for(Dir d : Dir.values()) {
			addToken(t.next(d, random.nextInt(maxdy-mindy+1)+mindy));
		}
	}
	
}
