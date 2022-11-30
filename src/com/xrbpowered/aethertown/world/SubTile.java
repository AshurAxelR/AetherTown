package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.utils.Dir;

public class SubTile extends Tile {

	public int subi, subj;
	public Generator parent;
	
	public SubTile(Generator parent, Dir d, Template t, int subi, int subj) {
		super(t, d);
		this.parent = parent;
		this.subi = subi;
		this.subj = subj;
	}

}
