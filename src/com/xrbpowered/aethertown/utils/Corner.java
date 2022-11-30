package com.xrbpowered.aethertown.utils;

public enum Corner {

	nw(-1, -1),
	ne(1, -1),
	se(1, 1),
	sw(-1, 1);
	
	public final int dx, dz;
	public final int tx, tz;
	
	private Corner(int dx, int dz) {
		this.dx = dx;
		this.dz = dz;
		this.tx = dx>0 ? 0 : -1;
		this.tz = dz>0 ? 0 : -1;
	}
	
	public Corner rotate(Dir d) {
		return values()[(ordinal()+d.ordinal())%4];
	}
	
}
