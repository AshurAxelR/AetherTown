package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TunnelTileTemplate;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.aethertown.world.gen.Lamps;
import com.xrbpowered.aethertown.world.gen.Lamps.LampInfo;
import com.xrbpowered.aethertown.world.gen.Lamps.LampTile;
import com.xrbpowered.aethertown.world.gen.Tunnels;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelType;
import com.xrbpowered.aethertown.world.gen.plot.LargeParkGenerator;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;

public class Street extends TunnelTileTemplate {

	public static final Color streetColor = new Color(0xb5b5aa);
	
	public static final Street template = new Street();
	public static final Street subTemplate = new Street();
	
	public static TileComponent street;
	
	public static class StreetTile extends TunnelTile implements LampTile {
		public final LampInfo lamp = new LampInfo();
		public boolean bridge = false;
		public boolean forceExpand = false;
		
		public StreetTile(TunnelTileTemplate t) {
			super(t);
		}
		
		@Override
		public LampInfo getLamp() {
			return lamp;
		}
	}

	@Override
	public Tile createTile() {
		return new StreetTile(this);
	}
	
	@Override
	public float getNoTunnelYIn(Tile atile, float sx, float sz, float prevy) {
		StreetTile tile = (StreetTile) atile;
		if(tile.bridge && Bridge.isUnder(prevy, tile.basey))
			return tile.level.h.gety(tile.x, tile.z, sx, sz);
		else
			return super.getNoTunnelYIn(tile, sx, sz, prevy);
	}
	
	@Override
	public void updateHeightLimit(Token t) {
		HeightLimiter.updateAt(t, HeightLimiter.maxBridge, HeightLimiter.maxWall, 3);
	}
	
	@Override
	public boolean canExpandFill(Tile tile) {
		TileTemplate adjtL = tile.getAdjT(tile.d.cw());
		TileTemplate adjtR = tile.getAdjT(tile.d.ccw());
		return (adjtR!=null && isAnyStreet(adjtR) || adjtL!=null && isAnyStreet(adjtL));
	}
	
	@Override
	public boolean noSkipExpandFill(Tile tile) {
		return ((StreetTile) tile).forceExpand;
	}
	
	@Override
	public void createComponents() {
		street = new TileComponent(
				FastMeshBuilder.plane(Tile.size, 1, 1, ObjectShader.vertexInfo, null),
				TexColor.get(streetColor));
		Lamps.createComponents();
		Fences.createComponents();
		Tunnels.createComponents();
	}
	
	@Override
	public void maybeAddTunnel(TunnelTile tile) {
		if(straightTunnelCondition(tile)) {
			tile.addTunnel(TunnelType.straight);
			return;
		}
		
		int countAdjTunnels = 0;
		for(Dir d : Dir.values()) {
			boolean tun = Tunnels.hasTunnel(tile.getAdj(d));
			if(tun)
				countAdjTunnels++;
			if(!tun && !tunnelWallCondition(tile, d, 0))
				return;
		}
		if(countAdjTunnels<2)
			return;
		
		tile.addTunnel(TunnelType.junction);
	}
	
	@Override
	public void decorateTile(Tile atile, Random random) {
		StreetTile tile = (StreetTile) atile;
		if(tile.tunnel!=null) {
			decorateTunnelTop(tile, random);
			return;
		}

		Lamps.addStreetLamp(tile, tile.lamp, random);
		Tunnels.autoAddHillBridge(tile, tile.basey);
		Fences.addFences(tile);
	}
	
	@Override
	public boolean postDecorateTile(Tile tile, Random random) {
		return Fences.fillFenceGaps(tile);
	}
	
	@Override
	public void createGeometry(Tile atile, LevelRenderer r) {
		StreetTile tile = (StreetTile) atile;
		street.addInstance(r, new TileObjectInfo(tile));
		if(tile.bridge)
			Tunnels.createHillBridge(r, tile, tile.basey);
		else
			r.terrain.addWalls(tile);

		if(tile.tunnel!=null) {
			Tunnels.createTunnel(r, tile.tunnel, tile.basey);
		}
		else {
			Fences.createFences(r, tile);
		}
		Lamps.createLamp(tile, tile.lamp, r, 0);
	}
	
	/**
	 * @return 0: trim not needed, 1: can't trim, 2: trimmed
	 */
	public static int trimStreet(Tile tile, Random random) {
		if(tile.x==0 || tile.x==tile.level.levelSize-1 || tile.z==0 || tile.z==tile.level.levelSize-1)
			return 0;
		
		Dir dsrc = tile.d.flip();
		Tile src = tile.getAdj(dsrc);
		if(src==null) {
			for(Dir d : Dir.values()) {
				Tile adj = tile.getAdj(d);
				if(adj!=null && Street.isAnyStreet(adj.t) && (adj.d==d.flip() || Math.abs(tile.basey-adj.basey)<=1)) {
					src = adj;
					dsrc = d;
					break;
				}
			}
		}
		int res = 2;
		Tile park = null;
		
		for(Dir d : Dir.shuffle(random)) {
			if(d==dsrc)
				continue;
			Tile adj = tile.getAdj(d);
			if(adj==null)
				continue;
			if(adj.d==d) {
				if(adj.t==Hill.template)
					continue;
				if(adj.t!=Park.template && adj.t!=Bench.templatePark)
					return 0;
				res = 1;
				park = adj;
			}
			if(Street.isAnyStreet(adj.t) && (adj.d==d.flip() || Math.abs(tile.basey-adj.basey)<=1) && src!=null)
				return 0;
		}
		
		if(res==1) {
			if(park!=null) {
				if(park.sub==null) {
					Bench.templatePlaza.forceGenerate(Token.forTile(park));
					return 0;
				}
				else if(park.sub.parent instanceof LargeParkGenerator) {
					((LargeParkGenerator) park.sub.parent).promote(random);
					return 0;
				}
				else
					return 0;
			}
			else
				return 1;
		}
		else if(res<2) {
			return res;
		}
		
		if(tile.sub!=null) {
			Token t = tile.sub.parent.tokenAt(0, 0);
			Tile root = t.level.map[t.x][t.z];
			if(root!=null && root.sub.parent==tile.sub.parent)
				return 0;
			else
				GeneratorException.raise("Orphan %s sub street at [%d, %d]\n", tile.sub.parent.getClass().getSimpleName(), tile.x, tile.z);
		}
		if((tile.x==tile.level.getStartX() && tile.z==tile.level.getStartZ()))
			return 1;
		
		if(src!=null && (src.t instanceof StreetSlope)) {
			Dir align = src.d; 
			if(((StreetSlope)src.t).h==1) {
				Tile t = src;
				final Dir[] dcheck = { align.cw(), align.ccw() };
				while(t!=null && t.d==align && (t.t instanceof StreetSlope)) {
					for(Dir d : dcheck) {
						Tile adj = t.getAdj(d);
						if(adj!=null && adj.d==d)
							return 1;
					}
					t = t.getAdj(dsrc);
				}
			}
			Tile t = src;
			while(t!=null && t.d==align && (t.t instanceof StreetSlope)) {
				tile.level.map[t.x][t.z] = null;
				tile.level.heightLimiter.invalidate();
				t = t.getAdj(dsrc);
			}
		}
		tile.level.map[tile.x][tile.z] = null;
		tile.level.heightLimiter.invalidate();
		return 2;
	}
	

	public static boolean isAnyStreet(TileTemplate t) {
		return t==Street.template || (t instanceof StreetSlope) || t==Bridge.template;
	}
	
	public static boolean isAnyPath(TileTemplate t) {
		return t==Street.template || t==Street.subTemplate || (t instanceof StreetSlope) || t==Bridge.template;
	}

}
