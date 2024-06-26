package com.xrbpowered.aethertown.world;

import java.util.Random;

import com.xrbpowered.aethertown.render.TerrainChunkBuilder;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.gen.Tunnels;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelInfo;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelType;
import com.xrbpowered.aethertown.world.tiles.Hill;

public abstract class TunnelTileTemplate extends TileTemplate {

	public static class TunnelTile extends Tile {
		public TunnelInfo tunnel = null;
		
		public TunnelTile(TunnelTileTemplate t) {
			super(t);
		}
		
		public TunnelInfo addTunnel(TunnelType type) {
			tunnel = new TunnelInfo(this, type);
			return tunnel;
		}
	}

	@Override
	public Tile createTile() {
		return new TunnelTile(this);
	}
	
	public int getNoTunnelGroundY(Tile tile, Corner c) {
		return super.getGroundY(tile, c);
	}

	@Override
	public int getGroundY(Tile atile, Corner c) {
		TunnelTile tile = (TunnelTile) atile;
		if(tile.tunnel!=null)
			return tile.tunnel.getGroundY(c);
		else
			return c==null ? tile.basey : getNoTunnelGroundY(tile, c);
	}

	public float getNoTunnelYIn(Tile tile, float sx, float sz, float prevy) {
		return super.getYIn(tile, sx, sz, prevy);
	}

	@Override
	public float getYIn(Tile atile, float sx, float sz, float prevy) {
		TunnelTile tile = (TunnelTile) atile;
		if(tile.tunnel!=null && Tunnels.isAbove(prevy, tile.tunnel.basey))
			return tile.tunnel.getTopY(sx, sz);
		else
			return getNoTunnelYIn(tile, sx, sz, prevy);
	}
	
	@Override
	public int getFenceY(Tile tile, Corner c) {
		return getGroundY(tile, c);
	}

	@Override
	public int getBlockY(Tile atile) {
		TunnelTile tile = (TunnelTile) atile;
		if(tile.tunnel!=null)
			return tile.tunnel.maxTopY;
		else
			return tile.basey;
	}
	
	public int getNoTunnelLightBlockY(Tile tile) {
		return super.getLightBlockY(tile);
	}

	@Override
	public int getLightBlockY(Tile atile) {
		TunnelTile tile = (TunnelTile) atile;
		if(tile.tunnel!=null)
			return tile.tunnel.maxTopY;
		else
			return getNoTunnelLightBlockY(tile);
	}
	
	public void decorateTunnelTop(TunnelTile tile, Random random) {
		if(tile.tunnel!=null && tile.tunnel.top!=null)
			Hill.template.decorateTile(tile.tunnel.top, random);
	}
	
	public static boolean tunnelWallCondition(Tile tile, Dir d, int h) {
		Tile adj = tile.getAdj(d);
		if(adj==null)
			return false;
		if(adj.t==Hill.template) {
			int[] yloc = tile.level.h.yloc(adj.x, adj.z);
			int miny = MathUtils.min(yloc);
			int maxDelta = MathUtils.maxDelta(yloc);
			if(maxDelta>=TerrainChunkBuilder.cliffDelta && tile.basey-h*2<=miny)
				return true;
		}
		else if(adj.t.getFixedYStrength()>0) {
			return adj.basey>tile.basey+Tunnels.tunnelHeight;
		}
		return false;
	}
	
	public static boolean straightTunnelCondition(TunnelTile tile, int h) {
		return tunnelWallCondition(tile, tile.d.cw(), h) && tunnelWallCondition(tile, tile.d.ccw(), h);
	}

	public static boolean straightTunnelCondition(TunnelTile tile) {
		return straightTunnelCondition(tile, 0);
	}
	
	public abstract void maybeAddTunnel(TunnelTile tile);

}
