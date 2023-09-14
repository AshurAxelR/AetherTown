package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.TerrainTile;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;

public class Hill extends TileTemplate {

	public static final Hill template = new Hill();
	
	public class HillTile extends TerrainTile {
		public Integer maxDelta = null;
		public int miny;
		
		public HillTile() {
			super(Hill.this);
		}
	}
	
	@Override
	public Tile createTile() {
		return new HillTile();
	}
	
	@Override
	public float getYIn(Tile tile, float sx, float sz, float y0) {
		return tile.level.h.gety(tile.x, tile.z, sx, sz);
	}
	
	@Override
	public int getFixedYStrength() {
		return 0;
	}

	@Override
	public int getBlockY(Tile atile) {
		HillTile tile = (HillTile) atile;
		int maxDelta = getMaxDelta(tile);
		return tile.miny+maxDelta/2;
	}
	
	@Override
	public int getFenceY(Tile tile, Corner c) {
		return HeightMap.tiley(tile, c);
	}
	
	@Override
	public void updateHeightLimit(Token t) {
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
	public void decorateTile(Tile tile, Random random) {
		int maxd = getMaxDelta(tile);
		if(maxd<10)
			TerrainTile.addTrees((HillTile) tile, random);
	}
	
	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		r.terrain.addHillTile(TerrainMaterial.hillGrass, tile);
		((HillTile) tile).createTrees(r);
	}
	
	public int getMaxDelta(Tile atile) {
		HillTile tile = (HillTile) atile;
		if(tile.maxDelta==null) {
			int[] yloc = tile.level.h.yloc(tile.x, tile.z);
			tile.maxDelta = MathUtils.maxDelta(yloc);
			tile.miny = MathUtils.min(yloc);
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
		if(tile.maxDelta>1 || !tile.level.isInside(tile.x, tile.z, 2) || tile.level.info.terrain.noParks)
			return res;
		
		Dir adjDir = null;
		int y = 0;
		int countUp = 0;
		TileTemplate gen = Park.template;
		for(Dir d : Dir.shuffle(random)) {
			Tile adj = tile.getAdj(d);
			if(adj==null)
				continue;
			if(adj.t==Park.template || adj.t==Street.template || (adj.t instanceof Plaza)) {
				if(Math.abs(adj.basey-tile.basey)<=1) {
					if(adjDir==null || adj.t==Street.template) {
						adjDir = d;
						y = adj.basey;
					}
					if(adj.t==Street.template && random.nextInt(5)==0)
						gen = Bench.templatePark;
				}
			}
			if(adj!=null && adj.basey>tile.basey-2) {
				countUp++;
			}
		}
		if(adjDir!=null && countUp>1) {
			gen.forceGenerate(new Token(tile.level, tile.x, y, tile.z, adjDir.flip()));
			return true;
		}
		else
			return res;
	}
	
}
