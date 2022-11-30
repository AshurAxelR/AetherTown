package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Token;

public class StreetLayoutGenerator extends TokenGenerator {

	public StreetLayoutGenerator(int limit) {
		super(limit);
	}

	@Override
	public boolean generate(Token startToken, Random random) {
		clearTokens();
		Template.street.generate(startToken, random);
		for(Dir d : Dir.values())
			addToken(startToken.next(d, 0).setGenerator(new StreetGenerator(random, 0)));
		return generate(random);
	}
	
	@Override
	protected Generator selectGenerator(Token t, Random random) {
		int prevdy = 0;
		if(t.context instanceof StreetGenerator)
			prevdy = ((StreetGenerator) t.context).getDy();
		StreetGenerator street = new StreetGenerator(random, prevdy);
		boolean fit = street.checkFit(t, random);
		if(fit && (street.isPerfectMatch() || tokenCount()<2 || random.nextInt(10)>0))
			return street;
		else
			return StreetGenerator.selectSideGenerator(random, 0);
	}

}
