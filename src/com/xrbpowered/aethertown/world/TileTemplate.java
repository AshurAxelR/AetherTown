package com.xrbpowered.aethertown.world;

import java.awt.Color;
import java.util.Random;

public abstract class TileTemplate extends Template implements Generator {

	public TileTemplate(Color minimapColor) {
		super(minimapColor);
	}

	protected void updateHeightLimit(Token t) {
		HeightLimiter.updateAt(t, HeightLimiter.maxWall, 3);
	}

	protected boolean canGenerate(Token t) {
		return t.fits() && t.isFree();
	}
	
	@Override
	public boolean generate(Token t, Random random) {
		if(canGenerate(t)) {
			new Tile(this, t.d).place(t);
			updateHeightLimit(t);
			return true;
		}
		else
			return false;
	}

}
