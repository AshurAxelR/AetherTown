package com.xrbpowered.aethertown.world;

import com.xrbpowered.aethertown.utils.Dir;

public class Token {
	
	public final Level level;
	public int x, y, z;
	public Dir d;
	public Generator gen = null;
	public Object context = null;
	
	public Token(Level level, int x, int y, int z, Dir d) {
		this.level = level;
		this.x = x;
		this.y = y;
		this.z = z;
		this.d = d;
	}
	
	public Token setGenerator(Generator gen) {
		this.gen = gen;
		return this;
	}
	
	public Token setContext(Object ctx) {
		this.context = ctx;
		return this;
	}
	
	public Token next(Dir d, int dy) {
		return new Token(level, x+d.dx, y+dy, z+d.dz, d);
	}
	
	public Token offsY(int dy) {
		y += dy;
		return this;
	}
	
	public boolean fits() {
		return level.fits(x, y, z);
	}

	public boolean fitsHeight() {
		return level.fitsHeight(x, y, z);
	}
	
	public boolean isInside() {
		return level.isInside(x, z);
	}

	public boolean isFree() {
		return level.map[x][z]==null;
	}

	public Tile tile() {
		return level.map[x][z];
	}
	
	public static Token forTile(Tile tile) {
		return new Token(tile.level, tile.x, tile.basey, tile.z, tile.d);
	}
	
	public static Token forAdj(Tile tile, Dir d) {
		return new Token(tile.level, tile.x+d.dx, tile.basey, tile.z+d.dz, d);
	}
}