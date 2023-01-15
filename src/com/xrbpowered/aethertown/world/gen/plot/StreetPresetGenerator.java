package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TokenProvider;
import com.xrbpowered.aethertown.world.gen.StreetGenerator;
import com.xrbpowered.aethertown.world.gen.TokenGenerator;

public abstract class StreetPresetGenerator extends PresetPlotGenerator implements TokenProvider {

	public static class ExitPoint extends PresetPlotGenerator.EntryPoint {
		public final int streetDy;
		
		public ExitPoint(int ti, int tj, Dir d, int basey, int dy) {
			super(ti, tj, d, basey);
			this.streetDy = dy;
		}
	}
	
	public ExitPoint[] setout() {
		return (ExitPoint[]) setent();
	}

	protected Token createExitToken(ExitPoint e) {
		int i = calci(e.ti, e.tj);
		int j = calcj(e.ti, e.tj);
		Dir td = e.align.flip().unapply(entry.align);
		int dy = e.basey-entry.basey;
		return tokenAt(i, j, td).offsY(dy);
	}

	@Override
	public void collectTokens(TokenGenerator out, Random random) {
		ExitPoint[] outs = setout();
		if(outs==null)
			return;

		for(ExitPoint e : outs) {
			Token t = createExitToken(e);
			t.setGenerator(new StreetGenerator(random, e.streetDy));
			out.addToken(t);
		}
	}
}
