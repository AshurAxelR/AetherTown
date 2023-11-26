package com.xrbpowered.aethertown.world.gen.plot;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.TileTemplate;

public class PresetData {

	public TileTemplate[][] sett;
	public Dir[][] setd = null;
	public int[][] sety = null;

	public PresetData(TileTemplate[][] t) {
		this.sett = t;
	}
	
	public PresetData d(Dir[][] d) {
		this.setd = d;
		return this;
	}
	
	public PresetData y(int[][] y) {
		this.sety = y;
		return this;
	}
	
}
