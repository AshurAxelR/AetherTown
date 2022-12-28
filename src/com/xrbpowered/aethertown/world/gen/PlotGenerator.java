package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
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
	
	public Token tokenAt(int i, int j) {
		return new Token(startToken.level,
				startToken.x + j*d.dx + i*dr.dx,
				startToken.y,
				startToken.z + j*d.dz + i*dr.dz,
				startToken.d);
	}

	public boolean isFree(int left, int right, int fwd) {
		for(int j=0; j<=fwd; j++)
			for(int i=-left; i<=right; i++) {
				Token t = tokenAt(i, j);
				if(!t.fits() || !t.isFree())
					return false;
			}
		return true;
	}

	public boolean isFree() {
		return isFree(left, right, fwd);
	}

	protected abstract void placeAt(Token t, int i, int j, Random random);

	protected void registerPlot() {
		startToken.level.plots.add(this);
	}
	
	protected void place(Random random) {
		for(int j=0; j<=fwd; j++)
			for(int i=-left; i<=right; i++) {
				placeAt(tokenAt(i, j), i, j, random);
			}
		registerPlot();
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
	
	public void fillStreet(Random random) {
		Level level = startToken.level;
		Dir dl = dr.flip();
		int y = startToken.y;
		for(int i=-1; i>=-left; i--) {
			Token t = tokenAt(i, -1);
			t.d = dl;
			t.y = y;
			Tile tile = level.map[t.x][t.z];
			if(tile==null)
				Template.street.generate(t, random);
			else if(tile.t==Template.street)
				y = tile.basey;
			else
				break;
		}
		y = startToken.y;
		for(int i=1; i<=right; i++) {
			Token t = tokenAt(i, -1);
			t.d = dr;
			t.y = y;
			Tile tile = level.map[t.x][t.z];
			if(tile==null)
				Template.street.generate(t, random);
			else if(tile.t==Template.street)
				y = tile.basey;
			else
				break;
		}
	}

}
