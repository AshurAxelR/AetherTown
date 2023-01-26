package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TokenProvider;
import com.xrbpowered.aethertown.world.gen.StreetGenerator;
import com.xrbpowered.aethertown.world.gen.StreetLayoutGenerator;
import com.xrbpowered.aethertown.world.gen.TokenGenerator;
import com.xrbpowered.aethertown.world.tiles.Street;

public abstract class StreetPresetGenerator extends PresetPlotGenerator implements TokenProvider {

	public static class ExitPoint extends PresetPlotGenerator.EntryPoint {
		public final int index;
		public final int streetDy;
		public boolean crit = false;
		
		public ExitPoint(int index, int ti, int tj, Dir d, int basey, int dy) {
			super(ti, tj, d, basey);
			this.index = index;
			this.streetDy = dy;
		}
		
		public ExitPoint crit() {
			this.crit = true;
			return this;
		}
	}
	
	protected int mask;
	
	public StreetPresetGenerator(int mask) {
		setMask(mask);
	}
	
	protected StreetPresetGenerator setMask(int mask) {
		this.mask = mask;
		return this;
	}
	
	public ExitPoint[] setout() {
		return (ExitPoint[]) setent();
	}
	
	protected Token createExitToken(ExitPoint e) {
		if(((1<<e.index) & mask)==0)
			return null;
		int i = calci(e.ti, e.tj);
		int j = calcj(e.ti, e.tj);
		Dir td = e.align.flip().apply(d).unapply(entry.align);
		int dy = e.basey-entry.basey;
		return tokenAt(i, j, td).offsY(dy);
	}

	@Override
	public boolean fits() {
		if(!super.fits())
			return false;
		for(ExitPoint out : setout()) {
			Token t = createExitToken(out);
			if(t==null)
				continue;
			if(!t.isInside())
				return false;
			Tile tile = t.level.map[t.x][t.z];
			if(tile!=null) {
				if(tile.t==Street.template || tile.t==Street.subTemplate) {
					if(tile.basey!=t.y)
						return false;
				}
				else
					return false;
			}
		}
		return true;
	}

	@Override
	public void collectTokens(TokenGenerator out, Random random) {
		ExitPoint[] outs = setout();
		for(ExitPoint e : outs) {
			Token t = createExitToken(e);
			if(t==null)
				continue;
			t.setGenerator(new StreetGenerator(random, e.streetDy));
			out.addToken(t);
		}
	}
	
	protected boolean removeOrPromote(int conn, int mask, Token[] tokens, Random random) {
		if(conn<=1) {
			remove();
			return true;
		}
		else {
			boolean upd = false;
			for(Token t : tokens) {
				if(t!=null) {
					upd |= StreetLayoutGenerator.addPointOfInterest(t, random);
				}
			}
			return upd;
		}
	}
	
	public boolean trimStreet(Random random) {
		ExitPoint[] outs = setout();
		Token[] tokens = new Token[outs.length];
		int conn = 0;
		int mask = 0;
		for(int i=0; i<outs.length; i++) {
			ExitPoint out = outs[i];
			Token t = createExitToken(out);
			if(t==null)
				continue;
			tokens[i] = null;
			if(t.isInside()) {
				Tile tile = t.tile();
				if(tile!=null && (tile.d==t.d || Street.isAnyPath(tile.t))) {
					conn++;
					mask |= (1<<i);
				}
				else if(tile==null && out.crit)
					tokens[i] = t;
			}
		}
		if(mask==this.mask)
			return false;
		else
			return removeOrPromote(conn, mask, tokens, random);
	}
}
