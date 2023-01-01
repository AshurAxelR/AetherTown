package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;

public class Hill extends TileTemplate {

	public class HillTile extends Tile {
		public Integer maxDelta = null;
		
		public HillTile() {
			super(Hill.this);
		}
	}
	
	public Hill() {
		super(TerrainBuilder.grassColor.color());
	}
	
	@Override
	public Tile createTile() {
		return new HillTile();
	}
	
	@Override
	public float getYAt(Tile tile, float sx, float sz) {
		return tile.level.h.gety(tile.x, tile.z, sx, sz);
	}
	
	@Override
	public int getFixedYStrength() {
		return 0;
	}

	@Override
	public int getFenceY(Tile tile, Corner c) {
		return HeightMap.tiley(tile, c);
	}
	
	@Override
	protected void updateHeightLimit(Token t) {
		HeightLimiter.updateAt(t, HeightLimiter.maxCliff, 0);
	}
	
	@Override
	protected boolean canGenerate(Token t) {
		return t.level.isInside(t.x, t.z) && t.level.fitsHeight(t.x, t.y, t.z, false);
	}
	
	@Override
	public void createComponents() {
	}
	
	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		renderer.terrain.addHillTile(TerrainBuilder.grassColor.color(), tile);
		int maxd = getMaxDelta(tile);
		if(maxd<10)
			Template.park.addTrees(tile, random);
	}
	
	public int getMaxDelta(Tile atile) {
		HillTile tile = (HillTile) atile;
		if(tile.maxDelta==null) {
			int[] yloc = tile.level.h.yloc(tile.x, tile.z);
			tile.maxDelta = MathUtils.maxDelta(yloc);
		}
		return tile.maxDelta;
	}
	
	@Override
	public boolean finalizeTile(Tile atile, Random random) {
		HillTile tile = (HillTile) atile;
		boolean res = false;
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		if(miny!=tile.basey) {
			tile.basey = miny;
			res = true;
		}
		tile.maxDelta = MathUtils.maxDelta(yloc);
		if(tile.maxDelta>1)
			return res;

		Dir adjDir = null;
		int y = 0;
		int countUp = 0;
		for(Dir d : Dir.shuffle(random)) {
			Tile adj = tile.getAdj(d);
			if(adj==null)
				continue;
			if(adj.t==Template.park || adj.t==Template.street) {
				if(Math.abs(adj.basey-tile.basey)<=1) {
					if(adjDir==null || adj.t==Template.street) {
						adjDir = d;
						y = adj.basey;
					}
				}
			}
			if(adj!=null && adj.basey>tile.basey-2) {
				countUp++;
			}
		}
		if(adjDir!=null && countUp>1) {
			Template.park.forceGenerate(new Token(tile.level, tile.x, y, tile.z, adjDir.flip()), random);
			return true;
		}
		else
			return res;
	}
	
}
