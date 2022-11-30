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
	
	@Override
	public boolean generate(Token t, Random random) {
		if(t.fits() && t.isFree()) {
			new Tile(this, t.d).place(t);
			updateHeightLimit(t);
			return true;
		}
		else
			return false;
	}

}
