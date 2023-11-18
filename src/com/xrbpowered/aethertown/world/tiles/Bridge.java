package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TunnelTileTemplate;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.aethertown.world.gen.Tunnels;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelInfo;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelType;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;

public class Bridge extends TunnelTileTemplate {

	public static final Bridge template = new Bridge();
	public static final int height = Tunnels.tunnelHeight;
	
	public static class BridgeTile extends StreetTile {
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
			return Tile.ysize*(tile.basey-height);
		else
			return super.getNoTunnelYIn(tile, sx, sz, prevy);
	}
	
	@Override
	public void maybeAddTunnel(TunnelTile tile) {
	}
	
	@Override
	public Tile forceGenerate(Token t) {
		BridgeTile tile = (BridgeTile) super.forceGenerate(t);
		tile.tunnel = new TunnelInfo(tile, TunnelType.fixed, tile.basey);
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
		Street.template.createBridge(r, tile, tile.basey, tile.basey-height);
		Fences.createFences(r, tile);
		
		Street.street.addInstance(r, new TileObjectInfo(tile, 0, -height, 0));
		r.pointLights.setLight(tile, 0, -height+5.5f, 0, 4.5f);
		r.blockLighting.addLight(IllumLayer.alwaysOn, tile, tile.basey-height+5, Street.lampLightColor, 0.5f, false);
	}
	
	public static boolean isUnder(float y0, int basey) {
		return y0<Tile.ysize*basey-AetherTown.pawnHeight-1.1f;
	}

}
