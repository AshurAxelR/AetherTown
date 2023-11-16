package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TunnelTileTemplate;
import com.xrbpowered.aethertown.world.gen.Tunnels;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelType;

public class Alcove extends TunnelTileTemplate {

	public static final Alcove template = new Alcove();

	@Override
	public Tile createTile() {
		return new TunnelTile(this);
	}
	
	@Override
	public void createComponents() {
	}
	
	@Override
	public void maybeAddTunnel(TunnelTile tile) {
	}
	
	@Override
	public void decorateTile(Tile tile, Random random) {
		decorateTunnelTop((TunnelTile) tile, random);
	}

	@Override
	public void createGeometry(Tile atile, LevelRenderer r) {
		TunnelTile tile = (TunnelTile) atile;
		Tunnels.createTunnel(r, tile.tunnel, tile.basey);
		Street.street.addInstance(r, new TileObjectInfo(tile));
		Bench.bench.addInstance(r, new TileObjectInfo(tile, 0.25f, 0));
	}
	
	public static Tile convert(Tile src) {
		TunnelTile tile = (TunnelTile) template.forceGenerate(new Token(src.level, src.x, src.basey, src.z, src.d));
		tile.addTunnel(TunnelType.object);
		return tile;
	}

	public static boolean maybeConvert(Tile tile) {
		if(tunnelWallCondition(tile, tile.d, 0) &&
				tunnelWallCondition(tile, tile.d.cw(), 0) &&
				tunnelWallCondition(tile, tile.d.ccw(), 0)) {
			convert(tile);
			return true;
		}
		else {
			return false;
		}
	}

}
