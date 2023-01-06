package com.xrbpowered.aethertown.world.gen;

import java.util.LinkedList;
import java.util.Random;

import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TokenProvider;

public abstract class TokenGenerator implements Generator {

	public int limit = 0;
	
	private LinkedList<Token> tokens = new LinkedList<>();
	private int countTokens = 0;

	public TokenGenerator(int limit) {
		this.limit = limit;
	}
	
	protected abstract Generator selectGenerator(Token t, Random random);

	public void clearTokens() {
		tokens.clear();
		countTokens = 0;
	}
	
	public void addToken(Token t) {
		tokens.add(t);
		countTokens++;
	}
	
	public int tokenCount() {
		return countTokens;
	}
	
	protected boolean checkToken(Token t) {
		return t.level.map[t.x][t.z]==null;
	}
	
	protected void spreadTokens(Token t, Random random) {
	}
	
	protected boolean place(Generator gen, Token t, Random random) {
		return gen.generate(t, random);
	}

	protected void flushTokens() {
	}
	
	@Override
	public boolean generate(Token startToken, Random random) {
		clearTokens();
		addToken(startToken);
		return generate(random);
	}

	public boolean generate(Random random) {
		int placed = 0;
		while(countTokens>0) {
			Token t = tokens.remove(random.nextInt(countTokens));
			countTokens--;
			if(!t.level.isInside(t.x, t.z))
				continue;
			if(!checkToken(t))
				continue;
			
			Generator gen = (t.gen==null) ? selectGenerator(t, random) : t.gen;
			if(gen==null)
				continue;
			if(place(gen, t, random)) {
				placed++;
				if(limit>0 && placed>limit)
					break;
				if(gen instanceof TokenProvider)
					((TokenProvider) gen).collectTokens(this, random);
				else
					spreadTokens(t, random);
			}
		}
		if(countTokens>0) {
			flushTokens();
		}
		return placed>0;
	}

}
