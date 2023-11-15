package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TunnelTileTemplate;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;

public class Bridge extends TunnelTileTemplate {

	public static final Bridge template = new Bridge();
	
	public static class BridgeTile extends StreetTile {
		public TileTemplate under;
		public int h = 8;
		
		public BridgeTile() {
			super(template);
		}
	}

	@Override
	public Tile createTile() {
		return new BridgeTile();
	}
	
	public float getNoTunnelYIn(Tile tile, float sx, float sz, float prevy) {
		if(isUnder(prevy, tile.basey))
			return Tile.ysize*(tile.basey-((BridgeTile) tile).h);
		else
			return super.getNoTunnelYIn(tile, sx, sz, prevy);
	}
	
	@Override
	public void maybeAddTunnel(TunnelTile tile) {
	}
	
	protected boolean canGenerate(Token t) {
		return t.level.isInside(t.x, t.z); // TODO check under
	}
	
	public Tile forceGenerate(Token t) {
		Tile under = t.level.map[t.x][t.z];
		BridgeTile tile = (BridgeTile) createTile();
		if(under==null) {
			tile.under = Street.template;
			tile.h = 8;
		}
		else if(t.y < under.basey) {
			tile.under = Street.template;
			tile.h = under.basey-t.y; 
		}
		else {
			tile.under = under.t;
			tile.h = t.y-under.basey;
		}
		tile.place(t);
		return tile;
	}
	
	@Override
	public void createComponents() {
	}

	@Override
	public void decorateTile(Tile tile, Random random) {
		Fences.addFences(tile);
	}
	
	@Override
	public void createGeometry(Tile atile, LevelRenderer r) {
		BridgeTile tile = (BridgeTile) atile;
		Street.street.addInstance(r, new TileObjectInfo(tile));
		Street.template.createBridge(r, tile, tile.basey, tile.basey-tile.h);
		Fences.createFences(r, tile);
		
		if(tile.under==Street.template) {
			// FIXME under bridge create geometry via TileTemplate.createGeometry()
			Street.street.addInstance(r, new TileObjectInfo(tile, 0, -tile.h, 0));
			r.pointLights.setLight(tile, 0, -tile.h+5.5f, 0, 4.5f);
			r.blockLighting.addLight(IllumLayer.alwaysOn, tile, tile.basey-tile.h+5, Street.lampLightColor, 0.5f, false);
		}
		else {
			GeneratorException.raise("Not supported yet.");
		}
	}
	
	public static boolean isUnder(float y0, int basey) {
		return y0<Tile.ysize*basey-AetherTown.pawnHeight-1.1f;
	}

}
