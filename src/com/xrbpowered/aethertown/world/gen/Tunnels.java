package com.xrbpowered.aethertown.world.gen;

import java.util.ArrayList;
import java.util.LinkedList;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.ScaledTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TunnelTileComponent;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TunnelTileTemplate;
import com.xrbpowered.aethertown.world.TunnelTileTemplate.TunnelTile;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Hill.HillTile;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;

public class Tunnels {

	public static final int tunnelHeight = 8;
	
	public static enum TunnelType {
		straight, junction, object, fixed
	}
	
	public static class TunnelInfo {
		public final TunnelTile below;
		
		public int rank = 1; // 0 not entrance, 1 tile tunnel, 2 tile tunnel, 3 longer tunnel
		public int depth = 0;
		public TunnelType type;
		
		public int basey;
		public int topy, maxTopY;
		public int[] y = null;
		public HillTile top = null;
		
		public TunnelInfo(TunnelTile below, TunnelType type, int basey) {
			this.type = type;
			this.below = below;
			this.basey = basey;
			this.topy = basey;
		}

		public TunnelInfo(TunnelTile below, TunnelType type) {
			this(below, type, below.basey+tunnelHeight);
		}

		public int getGroundY(Corner c) {
			return c==null || y==null ? topy : y[c.ordinal()];
		}
		
		public float getTopY(float sx, float sz) {
			return (y==null) ? Tile.ysize*topy : below.level.h.gety(below.x, below.z, sx, sz);
		}
	}

	private static TileComponent wall;
	private static TileComponent bridge, bridgeSupport;
	private static TileComponent tunnelj, tunneljSupport;
	
	public static void createComponents() {
		wall = new TunnelTileComponent(
				BasicGeometry.wall(Tile.size, Tile.ysize, ObjectShader.vertexInfo),
				TexColor.get(TerrainBuilder.wallColor));
		bridge = new TunnelTileComponent(
				ObjMeshLoader.loadObj("models/tunnel/bridge.obj", 0, 1f, ObjectShader.vertexInfo),
				TexColor.get(TerrainBuilder.wallColor));
		bridgeSupport = new TunnelTileComponent(
				ObjMeshLoader.loadObj("models/tunnel/bridge_support.obj", 0, 1f, ObjectShader.vertexInfo),
				TexColor.get(TerrainBuilder.wallColor));
		tunnelj = new TunnelTileComponent(
				ObjMeshLoader.loadObj("models/tunnel/tunnelj.obj", 0, 1f, ObjectShader.vertexInfo),
				TexColor.get(TerrainBuilder.wallColor));
		tunneljSupport = new TunnelTileComponent(
				ObjMeshLoader.loadObj("models/tunnel/tunnelj_support.obj", 0, 1f, ObjectShader.vertexInfo),
				TexColor.get(TerrainBuilder.wallColor));
	}
	
	public final Level level;
	public final ArrayList<TunnelInfo> tunnels = new ArrayList<>();
	
	public Tunnels(Level level) {
		this.level = level;
	}
	
	private void addTunnels() {
		tunnels.clear();
		for(int x=1; x<level.levelSize-1; x++)
			for(int z=1; z<level.levelSize-1; z++) {
				Tile tile = level.map[x][z];
				if(tile!=null && tile instanceof TunnelTile) {
					TunnelTile t = (TunnelTile) tile;
					if(t.tunnel!=null)
						tunnels.add(t.tunnel);
				}
			}
		
		boolean upd = true;
		while(upd) {
			level.h.calculate(true);
			upd = false;
			for(int x=1; x<level.levelSize-1; x++)
				for(int z=1; z<level.levelSize-1; z++) {
					if(level.info.isPortal() && level.isInside(x, z, 16))
						continue;
					
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
			Dir[] dirs = (tunnel.type==TunnelType.object)
					? new Dir[] { tunnel.below.d.flip() }
					: Dir.values();
			for(Dir d : dirs) {
				Tile adj = tunnel.below.getAdj(d);
				if(adj!=null && adj.t!=Hill.template && !hasTunnel(adj) && adj.basey<=tunnel.topy) {
					tunnel.rank = 1;
					break;
				}
			}
		}
		for(TunnelInfo tunnel : tunnels) {
			tunnel.depth = tunnel.rank>0 ? 0 : level.levelSize;
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
		boolean upd = true;
		while(upd) {
			upd = false;
			for(TunnelInfo tunnel : tunnels) {
				if(tunnel.depth==0) {
					if(tunnel.type==TunnelType.junction)
						tunnel.type = TunnelType.straight;
					continue;
				}
				for(Dir d : Dir.values()) {
					TunnelInfo adjTunnel = adjTunnel(tunnel.below, d);
					if(adjTunnel!=null) {
						int depth = adjTunnel.depth+1;
						if(depth<tunnel.depth) {
							tunnel.depth = depth;
							upd = true;
						}
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
				return y>=topy ? i-1 : 0;
			if(y>=topy)
				return i;
			for(Dir da : Dir.values()) {
				if(da==d.flip())
					continue;
				Tile adj = t.getAdj(da);
				if(adj==null || adj.t!=Hill.template
						&& !(i==1 && (adj.t instanceof Plaza || adj.t instanceof Park)))
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
	
	private void checkAddCornerSide(Tile tile) {
		if(tile==null || tile.t!=Hill.template)
			return;
		int topy = tile.basey;
		int groundY = tile.basey;
		Dir dt = null;
		int count = 0;
		for(Dir d : Dir.values()) {
			Tile adj = tile.getAdj(d);
			if(adj==null)
				return;
			if(hasTunnel(adj)) {
				TunnelInfo tunnel = ((TunnelTile) adj).tunnel;
				if(tunnel.rank>0) {
					dt = d.flip();
					count++;
					int y = tunnel.topy;
					if(y>topy)
						topy = y;
				}
				continue;
			}
			if(adj.t!=Hill.template) {
				dt = d.flip();
				count++;
				int y = adj.getGroundY();
				if(y<groundY)
					groundY = y;
			}
		}
		if(count>1 && groundY>=topy)
			Plaza.tunnelSideTemplate.forceGenerate(new Token(level, tile.x, topy, tile.z, dt));
	}
	
	private int calcYFromAdj(TunnelInfo tunnel, Dir d) {
		TunnelInfo adjTunnel = adjTunnel(tunnel.below, d);
		if(adjTunnel==null) {
			System.err.printf("Missing connecting tunnel [%d, %d]\n", tunnel.below.x, tunnel.below.z);
			if(tunnel.rank==0)
				tunnel.type = TunnelType.junction;
			else
				tunnel.type = TunnelType.object;
			return tunnel.topy;
		}
		int y = adjTunnel.type==TunnelType.fixed ? adjTunnel.topy : Math.max(tunnel.topy, adjTunnel.topy);
		tunnel.y[d.leftCorner().ordinal()] = y;
		tunnel.y[d.rightCorner().ordinal()] = y;
		return y;
	}
	
	private Integer topYFromAdjHill(int topy, Tile below, Dir d) {
		Tile t = below.getAdj(d);
		if(t==null)
			return null;
		else if(t.t!=Hill.template)
			return (t.basey>topy) ? t.basey : null;
		else
			return (t.t.getFenceY(t, d.leftCorner()) + t.t.getFenceY(t, d.rightCorner())) / 2;
	}

	private Integer topYFromAdjHills(int topy, Tile below, Dir[] ds) {
		int sum = 0;
		int n = 0;
		for(Dir d : ds) {
			Integer y = topYFromAdjHill(topy, below, d);
			if(y!=null) {
				sum += y;
				n++;
			}
		}
		return n==0 ? null : sum/n;
	}

	private void calcTopY() {
		boolean upd = true;
		while(upd) {
			upd = false;
			for(TunnelInfo tunnel : tunnels) {
				if(tunnel.type==TunnelType.fixed)
					continue;
				
				int topy = tunnel.basey;
				if(tunnel.rank==0) {
					Integer y = null;
					if(tunnel.type==TunnelType.straight)
						y = topYFromAdjHills(topy, tunnel.below, new Dir[] {tunnel.below.d.cw(), tunnel.below.d.ccw()});
					else if(tunnel.type==TunnelType.object)
						y = topYFromAdjHills(topy, tunnel.below, new Dir[] {tunnel.below.d, tunnel.below.d.cw(), tunnel.below.d.ccw()});
					if(y!=null && y>topy)
						topy = y;
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
				tunnel.y = new int[4];
				tunnel.maxTopY = Math.max(
					calcYFromAdj(tunnel, tunnel.below.d),
					calcYFromAdj(tunnel, tunnel.below.d.flip())
				);
			}
		}
	}
	
	private void addTop(TunnelTile tile) {
		TunnelInfo tunnel = tile.tunnel;
		HillTile top = (HillTile) Hill.template.createTile();
		top.attach(tile.level, tile.x, tunnel.topy, tile.z, tile.d);
		Hill.recalcBase(top);
		tunnel.top = top;
	}
	
	public void placeTunnels() {
		addTunnels();
		if(tunnels.isEmpty())
			return;
		
		calcRanks();
		calcTopY();
		if(adjustSides())
			calcRanks();
		
		for(TunnelInfo tunnel : tunnels) {
			checkAddCornerSide(tunnel.below.getAdj(tunnel.below.d.cw()));
			checkAddCornerSide(tunnel.below.getAdj(tunnel.below.d.ccw()));
		}
		
		level.h.calculate(true);
		Hill.recalcMaxDelta(level);
		
		for(TunnelInfo tunnel : tunnels) {
			checkTerrain(tunnel);
			checkConnection(tunnel.below);
			if(tunnel.rank==0 && tunnel.topy>tunnel.basey+1)
				addTop(tunnel.below);
			// System.err.printf("  -- tunnel@[%d, %d] : %s\n", tunnel.below.x, tunnel.below.z, tunnel.type.name());
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
				GeneratorException.raise("Broken tunnel geometry [%d, %d].%s: %d!=%d\n", x, z, c.name(), ty, hy);
		}
	}
	
	private static void checkConnection(TunnelTile tile) {
		TunnelInfo tunnel = tile.tunnel;
		if(tunnel.type==TunnelType.straight) {
			int adj = 0;
			if(adjTunnel(tile, tile.d)!=null)
				adj++;
			if(adjTunnel(tile, tile.d.flip())!=null)
				adj++;
			
			int req;
			if(tunnel.rank==0) // not entrance
				req = 2;
			else if(tunnel.rank==1) // 1 tile tunnel
				req = 0;
			else
				req = 1;
			
			if(adj!=req)
				GeneratorException.warning("Connecting tunnel count mismatch [%d, %d]: adj=%d, req=%d\n", tile.x, tile.z, adj, req);
		}
	}

	public static void createBridge(LevelRenderer r, Tile tile, int basey, int lowy, Dir d) {
		int dy = basey-tile.basey;
		int sh = basey-6-lowy;
		bridge.addInstance(r, new TileObjectInfo(tile, 0, dy-6, 0).rotate(d));
		if(sh>0)
			bridgeSupport.addInstance(r, new ScaledTileObjectInfo(tile, 0, dy-6, 0).scale(1, sh*Tile.ysize).rotate(d));
	}

	public static void createBridge(LevelRenderer r, Tile tile, int basey, int lowy) {
		createBridge(r, tile, basey, lowy, tile.d);
	}

	public static void createTunnelJunction(LevelRenderer r, Tile tile, int basey, int lowy) {
		int dy = basey-tile.basey;
		int sh = basey-6-lowy;
		tunnelj.addInstance(r, new TileObjectInfo(tile, 0, dy-6, 0));
		if(sh>0)
			tunneljSupport.addInstance(r, new ScaledTileObjectInfo(tile, 0, dy-6, 0).scale(1, sh*Tile.ysize));
	}

	public static void createHillBridge(LevelRenderer r, Tile tile, int basey) {
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		createBridge(r, tile, basey, miny);
		r.terrain.addHillTile(TerrainMaterial.hillGrass, tile);
	}
	
	public static void autoAddHillBridge(StreetTile tile, int basey) {
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		int maxy = MathUtils.max(yloc);
		if(maxy>basey-3 || basey-miny>=24)
			return;
		TileTemplate adjt = tile.getAdjT(tile.d);
		if(adjt==null || !(Street.isAnyPath(adjt) || (adjt instanceof Plaza)))
			return;
		Tile adjcw = tile.getAdj(tile.d.cw());
		if(adjcw==null || adjcw.getGroundY()>=basey)
			return;
		Tile adjccw = tile.getAdj(tile.d.ccw());
		if(adjccw==null || adjccw.getGroundY()>=basey)
			return;
		tile.bridge = true;
	}
	
	private static void createInterTunnelWall(LevelRenderer r, TunnelTile tile, Dir d) {
		int basey = tile.tunnel.basey;
		TunnelInfo adjTunnel = adjTunnel(tile, d);
		if(adjTunnel!=null) {
			if(adjTunnel.basey>basey) {
				TileObjectInfo info = new ScaledTileObjectInfo(tile).scale(1, adjTunnel.basey-basey).rotate(d);
				info.position.y = basey * Tile.ysize;
				wall.addInstance(r, info); // internal
				// r.terrain.addWall(tile.x, tile.z, d, basey, adjTunnel.basey, basey, adjTunnel.basey); // internal
			}
		}
		else if(tile.tunnel.rank>0)
			r.terrain.addWall(tile.x, tile.z, d, basey, tile.tunnel.getGroundY(d.leftCorner()), basey, tile.tunnel.getGroundY(d.rightCorner())); // entrance front, external
	}
	
	public static void createTunnel(LevelRenderer r, TunnelInfo tunnel, int lowy, boolean light) {
		TunnelTile tile = tunnel.below;
		int basey = tunnel.basey;
		int topy = tunnel.topy;
		Dir dr = tile.d.cw();
		
		if(tunnel.type!=TunnelType.junction)
			createBridge(r, tile, basey, lowy, dr);
		else
			createTunnelJunction(r, tile, basey, lowy);
		
		if(tunnel.y==null)
			r.terrain.addFlatTile(tunnel.rank>0 ? TerrainMaterial.plaza : TerrainMaterial.hillGrass, tile.x, topy, tile.z);
		else
			r.terrain.addHillTile(TerrainMaterial.hillGrass, tile.x, tile.z);
		
		if(tunnel.rank>2 || tunnel.type==TunnelType.object) {
			// entrance sides, external
			r.terrain.addWall(tile.x, tile.z, dr, topy, topy);
			r.terrain.addWall(tile.x, tile.z, dr.flip(), topy, topy);
		}
		
		switch(tunnel.type) {
			case junction:
				for(Dir d : Dir.values()) {
					createInterTunnelWall(r, tile, d);
					if(!hasTunnel(tunnel.below.getAdj(d))) {
						TileObjectInfo info = new ScaledTileObjectInfo(tile.getAdj(d)).scale(1, basey-lowy).rotate(d.flip());
						info.position.y = lowy * Tile.ysize;
						wall.addInstance(r, info); // end wall, internal
						// r.terrain.addWall(tile.x+d.dx, tile.z+d.dz, d.flip(), lowy, basey, lowy, basey);
					}
				}
				break;
			case straight:
				createInterTunnelWall(r, tile, tile.d);
				createInterTunnelWall(r, tile, tile.d.flip());
				break;
			default:
				break;
		}

		if(light && (tunnel.rank>0 || tunnel.type!=TunnelType.straight || (tile.x+tile.z)%2==0)) {
			r.pointLights.setLight(tile, 0, basey-tile.basey-2.5f, 0, 4.5f);
			r.blockLighting.addLight(IllumLayer.alwaysOn, tile, basey-3, Lamps.lampLightColor, 0.3f, false);
		}
		
		if(tunnel.top!=null)
			tunnel.top.createTrees(r);
		
		r.tunnelDepthMap.addTunnel(tunnel);
	}

	public static void createTunnel(LevelRenderer r, TunnelInfo tunnel, int lowy) {
		createTunnel(r, tunnel, lowy, true);
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
