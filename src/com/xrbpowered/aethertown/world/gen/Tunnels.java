package com.xrbpowered.aethertown.world.gen;

import java.util.ArrayList;
import java.util.LinkedList;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TunnelTileTemplate;
import com.xrbpowered.aethertown.world.TunnelTileTemplate.TunnelTile;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;

public class Tunnels {

	public static final int tunnelHeight = 8;
	
	public static enum TunnelType {
		straight, junction, object
	}
	
	public static class TunnelInfo {
		public final TunnelTile below;
		
		public int basey;
		public int topy, maxTopY;
		public int[] y = null;
		
		public int rank = 1; // 0 not entrance, 1 tile tunnel, 2 tile tunnel, 3 longer tunnel
		public TunnelType type;
		
		public TunnelInfo(TunnelTile below, TunnelType type) {
			this.type = type;
			this.below = below;
			this.basey = below.basey+tunnelHeight;
			this.topy = basey;
		}
		
		public int getGroundY(Corner c) {
			return c==null || y==null ? topy : y[c.ordinal()];
		}
	}

	public final Level level;
	public final ArrayList<TunnelInfo> tunnels = new ArrayList<>();
	
	public Tunnels(Level level) {
		this.level = level;
	}
	
	private void addTunnels() {
		tunnels.clear();
		boolean upd = true;
		while(upd) {
			level.h.calculate(true);
			upd = false;
			for(int x=1; x<level.levelSize-1; x++)
				for(int z=1; z<level.levelSize-1; z++) {
					Tile tile = level.map[x][z];
					if(tile!=null && tile instanceof TunnelTile) {
						TunnelTile t = (TunnelTile) tile;
						if(t.tunnel==null) {
							((TunnelTileTemplate) tile.t).maybeAddTunnel(t);
							if(t.tunnel!=null) {
								tunnels.add(t.tunnel);
								upd = true;
							}
						}
					}
				}
		}
	}
	
	private void calcRanks() {
		for(TunnelInfo tunnel : tunnels) {
			tunnel.rank = 0;
			for(Dir d : Dir.values()) {
				Tile adj = tunnel.below.getAdj(d);
				if(adj!=null && adj.t!=Hill.template && !hasTunnel(adj)) {
					tunnel.rank = 1;
					break;
				}
			}
		}
		for(TunnelInfo tunnel : tunnels) {
			if(tunnel.rank>0) {
				for(Dir d : Dir.values()) {
					Tile adj = tunnel.below.getAdj(d);
					if(hasTunnel(adj)) {
						TunnelInfo adjTunnel = ((TunnelTile) adj).tunnel;
						if(adjTunnel.rank>0)
							tunnel.rank = 2;
						else if(tunnel.rank==1)
							tunnel.rank = 3;
					}
				}
			}
		}
	}
	
	private int testAddSide(Tile t, Dir d, int topy, int maxSides) {
		int i;
		for(i=1; i<=maxSides; i++) {
			t = t.getAdj(d);
			if(t==null)
				return 0;
			int y = Math.min(t.t.getFenceY(t, d.leftCorner()), t.t.getFenceY(t, d.rightCorner()));
			if(t.t!=Hill.template)
				return y>=topy ? i : 0;
			// int miny = MathUtils.min(level.h.yloc(t.x, t.z));
			// System.out.printf("* %s[%d] y=%d, miny=%d, topy=%d\n", d.name(), i, y, miny, topy);
			if(y>=topy)
				return i;
			for(Dir da : Dir.values()) {
				if(da==d.flip())
					continue;
				Tile adj = t.getAdj(da);
				if(adj==null || adj.t!=Hill.template
						&& !(i==1 && (adj.t instanceof Plaza || adj.t==Park.template)))
					return 0;
			}
		}
		return maxSides+1;
	}

	private void placeSide(Tile t, Dir d, int len, int topy, int maxSides) {
		for(int i=1; i<=len; i++) {
			t = t.getAdj(d);
			Plaza.tunnelSideTemplate.forceGenerate(new Token(level, t.x, topy, t.z, d));
			if(i==maxSides) {
				Tile adj = t.getAdj(d);
				if(adj.t==Hill.template && adj.basey<topy)
					adj.basey = topy;
				break;
			}
		}
	}

	private boolean adjustSides() {
		LinkedList<TunnelInfo> removed = new LinkedList<>();
		
		for(TunnelInfo tunnel : tunnels) {
			if((tunnel.rank==1 || tunnel.rank==2) && tunnel.type==TunnelType.straight) {
				// System.out.printf("[%d, %d] adjustSides rank=%d\n", tunnel.below.x, tunnel.below.z, tunnel.rank);
				int maxSides = tunnel.rank==2 ? 1 : 2;
				int ir = testAddSide(tunnel.below, tunnel.below.d.cw(), tunnel.topy, maxSides);
				int il = testAddSide(tunnel.below, tunnel.below.d.ccw(), tunnel.topy, maxSides);
				if(ir==0 || il==0) {
					tunnel.below.tunnel = null;
					removed.add(tunnel);
				}
				else {
					placeSide(tunnel.below, tunnel.below.d.cw(), ir, tunnel.topy, maxSides);
					placeSide(tunnel.below, tunnel.below.d.ccw(), il, tunnel.topy, maxSides);
				}
			}
			else {
				for(Dir d : Dir.values()) {
					Tile adj = tunnel.below.getAdj(d);
					if(adj!=null && adj.t==Hill.template && adj.basey<tunnel.topy)
						adj.basey = tunnel.topy;
				}
			}
		}
		
		if(removed.isEmpty())
			return false;
		else {
			tunnels.removeAll(removed);
			return true;
		}
	}
	
	private int calcYFromAdj(TunnelInfo tunnel, Dir d) {
		TunnelInfo adjTunnel = adjTunnel(tunnel.below, d);
		if(adjTunnel==null) {
			System.err.println("Missing connecting tunnel");
			return tunnel.topy;
		}
		int y = Math.max(tunnel.topy, adjTunnel.topy);
		tunnel.y[d.leftCorner().ordinal()] = y;
		tunnel.y[d.rightCorner().ordinal()] = y;
		return y;
	}
	
	private void calcTopY() {
		boolean upd = true;
		while(upd) {
			upd = false;
			for(TunnelInfo tunnel : tunnels) {
				int topy = tunnel.basey;
				if(tunnel.type==TunnelType.straight) {
					// TODO from adj hills
				}
				if(tunnel.rank>1 || tunnel.type!=TunnelType.straight) {
					for(Dir d : Dir.values()) {
						TunnelInfo adjTunnel = adjTunnel(tunnel.below, d);
						if(adjTunnel!=null && topy<adjTunnel.topy)
							topy = adjTunnel.topy;
					}
				}
				if(topy!=tunnel.topy) {
					tunnel.topy = topy;
					upd = true;
				}
			}
		}

		for(TunnelInfo tunnel : tunnels) {
			tunnel.maxTopY = tunnel.topy;
			if(tunnel.type==TunnelType.straight && tunnel.rank==0) {
				tunnel.topy = tunnel.basey;
				tunnel.y = new int[4];
				tunnel.maxTopY = Math.max(
					calcYFromAdj(tunnel, tunnel.below.d),
					calcYFromAdj(tunnel, tunnel.below.d.flip())
				);
			}
		}
	}
	
	public void placeTunnels() {
		addTunnels();
		if(tunnels.isEmpty())
			return;
		
		calcRanks();
		calcTopY();
		if(adjustSides())
			calcRanks(); // FIXME recalc top y?
		
		level.h.calculate(true);
		Hill.recalcMaxDelta(level);
		
		for(TunnelInfo tunnel : tunnels) {
			checkTerrain(tunnel);
			System.out.printf("  -- tunnel@[%d, %d]\n", tunnel.below.x, tunnel.below.z); // FIXME remove printf
		}
	}
	
	private static void checkTerrain(TunnelInfo tunnel) {
		if(tunnel.rank>0)
			return;
		int x = tunnel.below.x;
		int z = tunnel.below.z;
		for(Corner c : Corner.values()) {
			int ty = tunnel.getGroundY(c);
			int hy = tunnel.below.level.h.y[x+c.tx+1][z+c.tz+1];
			if(ty!=hy)
				throw new GeneratorException("Broken tunnel geometry [%d, %d].%s: %d!=%d\n", x, z, c.name(), ty, hy);
		}
	}

	public static void createTunnel(LevelRenderer r, TunnelInfo tunnel, int lowy) {
		TunnelTile tile = tunnel.below;
		int basey = tunnel.basey;
		int topy = tunnel.topy;
		Dir dr = tile.d.cw();
		
		if(tunnel.type!=TunnelType.junction)
			Street.template.createBridge(r, tile, basey, lowy, dr);
		
		if(tunnel.y==null)
			r.terrain.addFlatTile(tunnel.rank>0 ? TerrainMaterial.plaza : TerrainMaterial.hillGrass, tile.x, topy, tile.z);
		else
			r.terrain.addHillTile(TerrainMaterial.hillGrass, tile.x, tile.z);
		
		if(tunnel.rank>2) {
			r.terrain.addWall(tile.x, tile.z, dr, topy, topy);
			r.terrain.addWall(tile.x, tile.z, dr.flip(), topy, topy);
		}
		
		switch(tunnel.type) {
			case junction:
				for(Dir d : Dir.values()) {
					if(!hasTunnel(tunnel.below.getAdj(d)))
						r.terrain.addWall(tile.x+d.dx, tile.z+d.dz, d.flip(), lowy, basey, lowy, basey);
				}
				break;
			case object:
				// TODO tunnel object geometry
				break;
			default:
				Dir d = tile.d;
				r.terrain.addWall(tile.x, tile.z, d, basey, tunnel.getGroundY(d.leftCorner()), basey, tunnel.getGroundY(d.rightCorner()));
				d = d.flip();
				r.terrain.addWall(tile.x, tile.z, d, basey, tunnel.getGroundY(d.leftCorner()), basey, tunnel.getGroundY(d.rightCorner()));
		}

		if(tunnel.rank>0 || tunnel.type!=TunnelType.straight || (tile.x+tile.z)%2==0) {
			r.pointLights.setLight(tile, 0, basey-tile.basey-2.5f, 0, 4.5f);
			r.blockLighting.addLight(IllumLayer.alwaysOn, tile, basey-3, Street.lampLightColor, 0.3f, false);
		}
	}
	
	public static boolean isAbove(float y0, int basey) {
		return y0>Tile.ysize*(basey-1);
	}
	
	public static boolean hasTunnel(Tile tile) {
		return tile!=null && tile instanceof TunnelTile && ((TunnelTile) tile).tunnel!=null;
	}

	public static TunnelInfo adjTunnel(Tile tile, Dir d) {
		Tile adj = tile.getAdj(d);
		if(adj!=null && adj instanceof TunnelTile)
			return ((TunnelTile) adj).tunnel;
		else
			return null;
	}


}
