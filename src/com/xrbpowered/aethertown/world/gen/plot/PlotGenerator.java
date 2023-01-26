package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Street;

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
	
	protected Dir alignToken(int i, int j) {
		if(i==0)
			return d;
		else if(i>0)
			return dr;
		else
			return dr.flip();
	}
	
	public boolean ignoreToken(int i, int j) {
		return false;
	}
	
	public Token tokenAt(int i, int j, Dir td) {
		return new Token(startToken.level,
				startToken.x + j*d.dx + i*dr.dx,
				startToken.y,
				startToken.z + j*d.dz + i*dr.dz,
				td);
	}
	
	public Token tokenAt(int i, int j) {
		return tokenAt(i, j, alignToken(i, j));
	}

	public boolean fits() {
		for(int j=0; j<=fwd; j++)
			for(int i=-left; i<=right; i++) {
				if(ignoreToken(i, j))
					continue;
				Token t = tokenAt(i, j);
				if(!t.fits() || !t.isFree())
					return false;
			}
		return true;
	}

	protected abstract Tile placeAt(Token t, int i, int j, Random random);

	protected void registerPlot() {
		startToken.level.plots.add(this);
	}
	
	protected void place(Random random) {
		for(int j=0; j<=fwd; j++)
			for(int i=-left; i<=right; i++) {
				if(ignoreToken(i, j))
					continue;
				placeAt(tokenAt(i, j), i, j, random);
			}
		registerPlot();
	}
	
	@Override
	public boolean generate(Token startToken, Random random) {
		this.startToken = startToken;
		d = startToken.d;
		dr = startToken.d.cw();
		if(!findSize(random) || !fits())
			return false;
		place(random);
		return true;
	}
	
	public void remove() {
		Level level = startToken.level;
		for(int j=0; j<=fwd; j++)
			for(int i=-left; i<=right; i++) {
				if(ignoreToken(i, j))
					continue;
				Token t = tokenAt(i, j);
				level.map[t.x][t.z] = null;
			}
		level.plots.remove(this);
		level.heightLimiter.invalidate();
	}
	
	public void fillStreet(Random random) {
		Level level = startToken.level;
		Dir dl = dr.flip();
		int y = startToken.y;
		for(int i=-1; i>=-left; i--) {
			Token t = tokenAt(i, -1, dl);
			t.y = y;
			Tile tile = level.map[t.x][t.z];
			if(tile==null)
				Street.template.generate(t, random);
			else if(tile.t==Street.template)
				y = tile.basey;
			else
				break;
		}
		y = startToken.y;
		for(int i=1; i<=right; i++) {
			Token t = tokenAt(i, -1, dr);
			t.y = y;
			Tile tile = level.map[t.x][t.z];
			if(tile==null)
				Street.template.generate(t, random);
			else if(tile.t==Street.template)
				y = tile.basey;
			else
				break;
		}
	}

}
