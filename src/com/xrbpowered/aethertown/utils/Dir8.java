package com.xrbpowered.aethertown.utils;

public enum Dir8 {

	n(0, -1),
	ne(1, -1),
	e(1, 0),
	se(1, 1),
	s(0, 1),
	sw(-1, 1),
	w(-1, 0),
	nw(-1, -1);
	
	public final int dx, dz;
	public final float len;
	
	private Dir8(int dx, int dz) {
		this.dx = dx;
		this.dz = dz;
		this.len = (float)Math.sqrt(dx*dx+dz*dz);
	}

}
