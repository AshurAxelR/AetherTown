package com.xrbpowered.aethertown.world.gen;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.TerrainChunkBuilder;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;

public abstract class Tunnels {

	public static class TunnelInfo {
		public final StreetTile below;
		public int basey;
		public boolean lone = true;
		public boolean entrance = true;
		public boolean junction = false;
		public int[] y = new int[4];
		
		public TunnelInfo(StreetTile below, int h) {
			this.below = below;
			this.basey = below.basey+h;
		}

		public TunnelInfo(StreetTile below) {
			this(below, 8);
		}
		
		public int getGroundY(Corner c) {
			return c==null ? basey : y[c.ordinal()];
		}
	}

	public static boolean isAdjTunnel(Tile tile, Dir d) {
		Tile adj = tile.getAdj(d);
		return adj!=null && adj instanceof StreetTile && ((StreetTile) adj).tunnel!=null;
	}
	
	public static boolean tunnelWallCondition(Tile tile, Dir d, int h) {
		Tile adj = tile.getAdj(d);
		if(adj!=null && adj.t==Hill.template) {
			int[] yloc = tile.level.h.yloc(adj.x, adj.z);
			int miny = MathUtils.min(yloc);
			int maxDelta = MathUtils.maxDelta(yloc);
			if(maxDelta>TerrainChunkBuilder.cliffDelta && tile.basey-h<=miny)
				return true;
		}
		return false;
	}
	
	private static TunnelInfo placeTunnel(StreetTile tile) {
		if(tile.tunnel!=null)
			return null;
		
		if(tile.t instanceof StreetSlope)
			return ((StreetSlope) tile.t).checkTunnel(tile);
		else if(tile.t==Street.template || tile.t==Street.subTemplate)
			return Street.template.checkTunnel(tile);
		else
			return null;
	}
	
	public static void placeTunnels(Level level, Random random) {
		ArrayList<TunnelInfo> tunnels = new ArrayList<>();

		boolean upd = true;
		while(upd) {
			level.h.calculate(true);
			upd = false;
			for(int x=0; x<level.levelSize; x++)
				for(int z=0; z<level.levelSize; z++) {
					Tile tile = level.map[x][z];
					if(tile!=null && tile instanceof StreetTile) {
						TunnelInfo tunnel = placeTunnel((StreetTile) tile);
						if(tunnel!=null) {
							tunnels.add(tunnel);
							upd = true;
						}
					}
				}
		}
		
		for(TunnelInfo tunnel : tunnels) {
			tunnel.entrance = false;
			tunnel.lone = true;
			for(Dir d : Dir.values()) {
				if(!isAdjTunnel(tunnel.below, d)) {
					Tile adj = tunnel.below.getAdj(d);
					if(adj!=null && adj.t!=Hill.template) {
						tunnel.entrance = true;
					}
				}
				else {
					tunnel.lone = false;
				}
			}
		}
		
		upd = true;
		while(upd) {
			upd = false;
			for(TunnelInfo tunnel : tunnels) {
				if(tunnel.lone || !tunnel.junction && !tunnel.entrance)
					continue;
				for(Dir d : Dir.values()) {
					if(isAdjTunnel(tunnel.below, d)) {
						TunnelInfo adjTunnel = ((StreetTile) tunnel.below.getAdj(d)).tunnel;
						if((tunnel.junction || tunnel.entrance) && tunnel.basey<adjTunnel.basey) {
							tunnel.basey = adjTunnel.basey;
							upd = true;
						}
					}
				}
			}
		}
		for(TunnelInfo tunnel : tunnels) {
			if(tunnel.junction || tunnel.entrance || tunnel.lone) {
				for(int i=0; i<4; i++)
					tunnel.y[i] = tunnel.basey;
			}
			else {
				Dir d = tunnel.below.d;
				TunnelInfo adjTunnel = ((StreetTile) tunnel.below.getAdj(d)).tunnel;
				if(adjTunnel==null)
					throw new GeneratorException("Missing connecting tunnel");
				int y = Math.max(tunnel.basey, adjTunnel.basey);
				tunnel.y[d.leftCorner().ordinal()] = y;
				tunnel.y[d.rightCorner().ordinal()] = y;
				
				d = d.flip();
				adjTunnel = ((StreetTile) tunnel.below.getAdj(d)).tunnel;
				if(adjTunnel==null)
					throw new GeneratorException("Missing connecting tunnel");
				y = Math.max(tunnel.basey, adjTunnel.basey);
				tunnel.y[d.leftCorner().ordinal()] = y;
				tunnel.y[d.rightCorner().ordinal()] = y;
			}
		}
		
		for(TunnelInfo tunnel : tunnels) {
			/*if(tunnel.lone) {
				tunnel.below.tunnel = null;
			}
			else {*/
				System.out.printf("  -- tunnel@[%d, %d]\n", tunnel.below.x, tunnel.below.z); // FIXME remove printf
				for(Dir d : Dir.values()) {
					Tile adj = tunnel.below.getAdj(d);
					if(adj!=null && adj.t==Hill.template) {
						if(tunnel.lone) { // TODO same for size 2 tunnels
							Tile t = adj;
							int[] yloc = level.h.yloc(t.x, t.z);
							int i = 1;
							while(t!=null && t.t==Hill.template && MathUtils.min(yloc)<tunnel.basey) {
								// FIXME check all adj hills; if not, remove the tunnel
								Plaza.tunnelSideTemplate.forceGenerate(new Token(level, t.x, tunnel.basey, t.z, d));
								i++;
								if(i>2)
									break;
								t = t.getAdj(d);
								yloc = level.h.yloc(t.x, t.z);
							}
						}
						else if(adj.basey<tunnel.basey) {
							adj.basey = tunnel.basey;
						}
					}
				}
			// }
		}
		
		level.h.calculate(true);
		
		for(TunnelInfo tunnel : tunnels) {
			if(!tunnel.lone && !finalizeCheckTerrain(tunnel))
				throw new GeneratorException("Broken tunnel geometry");
		}
	}
	
	public static boolean finalizeCheckTerrain(TunnelInfo tunnel) {
		if(tunnel.entrance)
			return true;
		int x = tunnel.below.x;
		int z = tunnel.below.z;
		for(Corner c : Corner.values()) {
			if(tunnel.y[c.ordinal()]!=tunnel.below.level.h.y[x+c.tx+1][z+c.tz+1]) {
				System.err.printf("tunnel[%d, %d] y[%s] %d!=%d\n", x, z, c.name(), tunnel.y[c.ordinal()], tunnel.below.level.h.y[x+c.tx+1][z+c.tz+1]);
				return false;
			}
		}
		return true;
	}

	public static void createTunnel(LevelRenderer r, Tile tile, TunnelInfo tunnel, int lowy) {
		int basey = tunnel.basey;
		Dir dr = tile.d.cw();
		if(!tunnel.junction)
			Street.template.createBridge(r, tile, basey, lowy, dr);
		
		if(tunnel.entrance || tunnel.junction)
			r.terrain.addFlatTile(tunnel.entrance ? TerrainMaterial.plaza : TerrainMaterial.hillGrass, tile.x, basey, tile.z);
		else
			r.terrain.addHillTile(TerrainMaterial.hillGrass, tile.x, tile.z);
		
		if(tunnel.entrance) {
			r.terrain.addWall(tile.x, tile.z, dr, basey, basey);
			r.terrain.addWall(tile.x, tile.z, dr.flip(), basey, basey);
		}
		if(tunnel.junction) {
			for(Dir d : Dir.values()) {
				if(!isAdjTunnel(tunnel.below, d))
					r.terrain.addWall(tile.x+d.dx, tile.z+d.dz, d.flip(), lowy, basey, lowy, basey);
			}
		}
		else {
			Dir d = tunnel.below.d;
			r.terrain.addWall(tile.x, tile.z, d, basey, tunnel.y[d.leftCorner().ordinal()], basey, tunnel.y[d.rightCorner().ordinal()]);
			d = d.flip();
			r.terrain.addWall(tile.x, tile.z, d, basey, tunnel.y[d.leftCorner().ordinal()], basey, tunnel.y[d.rightCorner().ordinal()]);
		}
	}
	
	public static boolean isAbove(float y0, int basey) {
		return y0>Tile.ysize*(basey-1);
	}

}
