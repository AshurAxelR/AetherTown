package com.xrbpowered.aethertown.world;

import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;

public abstract class TileTemplate implements Generator {

	public Tile createTile() {
		return new Tile(this);
	}

	public String getTileInfo(Tile tile) {
		return "";
	}
	
	public int getFixedYStrength() {
		return 2;
	}
	
	public int getGroundY(Tile tile) {
		return tile.basey;
	}

	public int getFenceY(Tile tile, Corner c) {
		return tile.basey;
	}
	
	public int getLightBlockY(Tile tile) {
		return tile.basey;
	}
	
	public float getYOut(Tile tile, Dir d, float sout, float sx, float sz, float prevy) {
		switch(tile.getFence(d)) {
			case stepsOut:
				return FenceGenerator.getFenceYOut(tile.basey, sout);
			default:
				return Float.NEGATIVE_INFINITY;
		}
	}
	
	public static float sForXZ(float sx, float sz, Dir d) {
		sx *= d.dx;
		if(sx<0) sx += 1;
		sz *= d.dz;
		if(sz<0) sz += 1;
		return sx+sz;
	}
	
	protected float getYFromAdj(Tile tile, float sx, float sz, float prevy) {
		float max = Float.NEGATIVE_INFINITY;
		for(Dir d : Dir.values()) {
			Tile adj = tile.getAdj(d.flip());
			if(adj!=null) {
				float sout = sForXZ(sx, sz, d);
				max = Math.max(max, adj.t.getYOut(adj, d, sout, sx-d.dx, sz-d.dz, prevy));
			}
		}
		return max;
	}

	public float getYIn(Tile tile, float sx, float sz, float prevy) {
		return Tile.ysize*tile.basey;
	}

	public final float getYAt(Tile tile, float sx, float sz, float prevy) {
		return Math.max(getYIn(tile, sx, sz, prevy), getYFromAdj(tile, sx, sz, prevy));
	}

	public boolean finalizeTile(Tile tile, Random random) {
		return false;
	}
	
	public void decorateTile(Tile tile, Random random) {
	}

	public boolean postDecorateTile(Tile tile, Random random) {
		return false;
	}

	public abstract void createComponents();
	public abstract void createGeometry(Tile tile, LevelRenderer renderer);
	
	public void updateHeightLimit(Token t) {
		HeightLimiter.updateAt(t, HeightLimiter.maxWall, 3);
	}

	protected boolean canGenerate(Token t) {
		return t.fits() && t.isFree();
	}
	
	@Override
	public boolean generate(Token t, Random random) {
		if(canGenerate(t)) {
			forceGenerate(t, random);
			return true;
		}
		else
			return false;
	}

	public Tile forceGenerate(Token t, Random random) {
		Tile tile = createTile();
		tile.place(t);
		updateHeightLimit(t);
		return tile;
	}
	
	public boolean canExpandFill(Tile tile) {
		return true;
	}
	
	public boolean noSkipExpandFill(Tile tile) {
		return false;
	}

}
