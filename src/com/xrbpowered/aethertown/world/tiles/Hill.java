package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;

public class Hill extends TileTemplate {

	public Hill() {
		super(TerrainBuilder.grassColor.color());
	}
	
	@Override
	public boolean generate(Token t, Random random) {
		new Tile(this, t.d).place(t);
		updateHeightLimit(t);
		return true;
	}
	
	@Override
	public boolean isFixedY() {
		return false;
	}
	
	@Override
	protected void updateHeightLimit(Token t) {
		HeightLimiter.updateAt(t, HeightLimiter.maxCliff, 0);
	}
	
	@Override
	public void createComponents() {
	}
	
	@Override
	public void createGeometry(Tile tile, TerrainBuilder terrain, Random random) {
		terrain.addHillTile(tile);
		HeightMap h = tile.level.h;
		int maxd = HeightMap.maxDelta(h.y[tile.x][tile.z], h.y[tile.x+1][tile.z], h.y[tile.x][tile.z+1], h.y[tile.x+1][tile.z+1]);
		if(maxd<10)
			Template.park.addTrees(tile, random);
	}
	
	@Override
	public boolean finalizeTile(Tile tile, Random random) {
		HeightMap h = tile.level.h;
		int maxd = HeightMap.maxDelta(h.y[tile.x][tile.z], h.y[tile.x+1][tile.z], h.y[tile.x][tile.z+1], h.y[tile.x+1][tile.z+1]); // TODO save maxd
		if(maxd>1)
			return false;

		boolean adjPark = false;
		for(Dir d : Dir.values()) {
			Tile adj = tile.getAdj(d);
			Template adjt = (adj==null) ? null : adj.t;
			if(adjt==Template.park || adjt==Template.street) {
				if(Math.abs(adj.basey-tile.basey)<=1) {
					adjPark = true;
					break;
				}
			}
		}
		if(adjPark) {
			tile.level.map[tile.x][tile.z] = null;
			Template.park.generate(new Token(tile.level, tile.x, tile.geth(), tile.z, Dir.north), random);
			return true;
		}
		else
			return false;
	}
	
}
