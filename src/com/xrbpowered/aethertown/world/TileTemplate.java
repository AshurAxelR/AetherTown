package com.xrbpowered.aethertown.world;

import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.utils.Corner;

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
	
	public float getYAt(Tile tile, float sx, float sz, float y0) {
		return Tile.ysize*tile.basey;
	}

	public boolean finalizeTile(Tile tile, Random random) {
		return false;
	}
	
	public void decorateTile(Tile tile, Random random) {
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
