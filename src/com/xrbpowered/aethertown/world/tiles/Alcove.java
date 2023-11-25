package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TunnelTileTemplate;
import com.xrbpowered.aethertown.world.gen.Lamps;
import com.xrbpowered.aethertown.world.gen.Tunnels;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelType;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class Alcove extends TunnelTileTemplate {

	public static final Alcove template = new Alcove();

	private static TileComponent spring;

	@Override
	public Tile createTile() {
		return new TunnelTile(this);
	}
	
	@Override
	public void createComponents() {
		spring = new TileComponent(
				ObjMeshLoader.loadObj("models/tunnel/poi_spring.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/palette.png", false, true, false));
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
		Tunnels.createTunnel(r, tile.tunnel, tile.basey, false);
		//Street.street.addInstance(r, new TileObjectInfo(tile));
		//Bench.bench.addInstance(r, new TileObjectInfo(tile, 0.25f, 0));
		spring.addInstance(r, new TileObjectInfo(tile));
		r.pointLights.setLight(tile, -0.25f*tile.d.dx, tile.tunnel.basey-tile.basey-2.5f, -0.25f*tile.d.dz, 4.5f);
		r.blockLighting.addLight(IllumLayer.alwaysOn, tile, tile.tunnel.basey-3, Lamps.lampLightColor, 0.3f, false);
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
