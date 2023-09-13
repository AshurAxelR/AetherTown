package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;

public abstract class PresetPlotGenerator extends PlotGenerator {

	public static class EntryPoint {
		public final int ti, tj;
		public final Dir align;
		public final int basey;
		
		public EntryPoint(int ti, int tj, Dir d, int basey) {
			this.ti = ti;
			this.tj = tj;
			this.align = d;
			this.basey = basey;
		}
	}

	protected EntryPoint entry = null;

	public abstract int tisize();
	public abstract int tjsize();
	public abstract TileTemplate sett(int ti, int tj);
	public abstract Dir setd(int ti, int tj);
	
	public int sety(int ti, int tj) {
		return 0;
	}
	
	public abstract EntryPoint[] setent();
	
	public void setEntry(EntryPoint e) {
		this.entry = e;
		switch(e.align) {
			case north:
				left = e.tj;
				right = tjsize()-e.tj-1;
				fwd = tisize()-1;
				break;
			case east:
				left = e.ti;
				right = tisize()-e.ti-1;
				fwd = tjsize()-1;
				break;
			case south:
				left = tjsize()-e.tj-1;
				right = e.tj;
				fwd = tisize()-1;
				break;
			case west:
				left = tisize()-e.ti-1;
				right = e.ti;
				fwd = tjsize()-1;
				break;
		}
	}
	
	public int ti(int i, int j) {
		switch(entry.align) {
			case north: return fwd-j;
			case east: return left+i;
			case south: return j;
			case west: return right-i;
			default: return 0;
		}
	}
	
	public int tj(int i, int j) {
		switch(entry.align) {
			case north: return left+i;
			case east: return j;
			case south: return right-i;
			case west: return fwd-j;
			default: return 0;
		}
	}
	
	public int calci(int ti, int tj) {
		switch(entry.align) {
			case north: return tj-left;
			case east: return ti-left;
			case south: return right-tj;
			case west: return right-ti;
			default: return 0;
		}
	}

	public int calcj(int ti, int tj) {
		switch(entry.align) {
			case north: return fwd-ti;
			case east: return tj;
			case south: return ti;
			case west: return fwd-tj;
			default: return 0;
		}
	}

	private boolean inset(int ti, int tj) {
		return (ti>=0 && ti<tisize() && tj>=0 && tj<tjsize());
	}

	@Override
	protected Dir alignToken(int i, int j) {
		int ti = ti(i, j);
		int tj = tj(i, j);
		if(inset(ti, tj)) {
			return setd(ti, tj).apply(d).unapply(entry.align);
		}
		else
			return d;
	}
	
	@Override
	public boolean ignoreToken(int i, int j) {
		return sett(ti(i, j), tj(i, j))==null;
	}
	
	@Override
	public Token tokenAt(int i, int j, Dir td) {
		Token t = super.tokenAt(i, j, td);
		int ti = ti(i, j);
		int tj = tj(i, j);
		if(inset(ti, tj))
			t.offsY(sety(ti, tj)-entry.basey);
		return t;
	}

	@Override
	protected boolean findSize(Random random) {
		EntryPoint[] entries = setent();
		if(entries.length==1) {
			setEntry(entries[0]);
			return true;
		}
		else {
			Shuffle sh = new Shuffle(entries.length);
			for(int i=0; i<entries.length; i++) {
				EntryPoint e = entries[sh.next(random)];
				setEntry(e);
				if(fits())
					return true;
			}
			return false;
		}
	}

	@Override
	protected Tile placeAt(Token t, int i, int j, Random random) {
		TileTemplate temp = sett(ti(i, j), tj(i, j));
		return temp.forceGenerate(t).makeSub(this, i, j);
	}

}
