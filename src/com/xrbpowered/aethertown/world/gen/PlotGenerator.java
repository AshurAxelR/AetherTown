package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Token;

public abstract class PlotGenerator implements Generator {

	public int left, right, fwd;
	
	public Token startToken;
	public Dir d, dr;
	
	public void setSize(int left, int right, int fwd) {
		this.left = left;
		this.right = right;
		this.fwd = fwd;
	}
	
	protected abstract boolean findSize(Random random);
	
	protected Token tokenAt(int i, int j) {
		return new Token(startToken.level,
				startToken.x + j*d.dx + i*dr.dx,
				startToken.y,
				startToken.z + j*d.dz + i*dr.dz,
				startToken.d);
	}
	
	protected boolean isFree() {
		for(int j=0; j<=fwd; j++)
			for(int i=-left; i<=right; i++) {
				Token t = tokenAt(i, j);
				if(!t.fits() || !t.isFree())
					return false;
			}
		return true;
	}

	protected abstract void placeAt(Token t, int i, int j, Random random);

	protected void place(Random random) {
		for(int j=0; j<=fwd; j++)
			for(int i=-left; i<=right; i++) {
				placeAt(tokenAt(i, j), i, j, random);
			}
	}
	
	@Override
	public boolean generate(Token startToken, Random random) {
		this.startToken = startToken;
		d = startToken.d;
		dr = startToken.d.cw();
		if(!findSize(random) || !isFree())
			return false;
		place(random);
		return true;
	}

}
