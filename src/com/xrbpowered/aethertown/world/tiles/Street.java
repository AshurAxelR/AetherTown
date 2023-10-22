package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.sprites.SpriteComponent;
import com.xrbpowered.aethertown.render.sprites.SpriteInfo;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.render.tiles.IllumTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.ScaledTileComponent;
import com.xrbpowered.aethertown.render.tiles.ScaledTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.FenceGenerator;
import com.xrbpowered.aethertown.world.GeneratorException;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.gen.FollowTerrain;
import com.xrbpowered.aethertown.world.gen.plot.LargeParkGenerator;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class Street extends TileTemplate {

	public static final Color streetColor = new Color(0xb5b5aa);
	public static final Color lampLightColor = new Color(0xfff0b4); // new Color(0xfffae5);
	
	public static final Street template = new Street();
	public static final Street subTemplate = new Street();
	
	public static TileComponent street;
	
	private static TileComponent lampPost;
	private static IllumTileComponent lamp;
	private static SpriteComponent coronaSprite;
	private static TileComponent bridge, bridgeSupport;

	public static class StreetTile extends Tile {
		public boolean lamp = false;
		public Dir lampd = null;
		public boolean bridge = false;
		public boolean forceExpand = false;
		
		public FollowTerrain debugFT = null; // FIXME remove debug
		
		public StreetTile(TileTemplate t) {
			super(t);
		}
	}

	@Override
	public Tile createTile() {
		return new StreetTile(this);
	}
	
	@Override
	public float getYIn(Tile tile, float sx, float sz, float y0) {
		if(((StreetTile)tile).bridge && Bridge.isUnder(y0, tile.basey))
			return tile.level.h.gety(tile.x, tile.z, sx, sz);
		else
			return super.getYIn(tile, sx, sz, y0);
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
				new Texture(streetColor));
		lamp = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/lamp/lamp.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/lamp/lamp.png", false, true, false),
				new Texture("models/lamp/lamp_illum.png", false, true, false));
		coronaSprite = new SpriteComponent(new Texture("models/lamp/corona.png"));
		lampPost = new TileComponent(
				ObjMeshLoader.loadObj("models/lamp/lamp_post.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0x353433));
		bridge = new TileComponent(
				ObjMeshLoader.loadObj("models/bridge/bridge.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture(TerrainBuilder.wallColor));
		bridgeSupport = new ScaledTileComponent(
				ObjMeshLoader.loadObj("models/bridge/bridge_support.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture(TerrainBuilder.wallColor));
		FenceGenerator.createComponents();
	}

	@Override
	public void decorateTile(Tile tile, Random random) {
		addLamp(tile, random);
		autoAddHillBridge((StreetTile)tile, tile.basey);
		FenceGenerator.addHandrails(tile);
	}
	
	@Override
	public boolean postDecorateTile(Tile tile, Random random) {
		return FenceGenerator.fillFenceGaps(tile);
	}
	
	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		street.addInstance(r, new TileObjectInfo(tile));
		if(((StreetTile) tile).bridge)
			createHillBridge(r, tile, tile.basey);
		else
			r.terrain.addWalls(tile);
		FenceGenerator.createFences(r, tile);
		createLamp(tile, r, 0);
	}
	
	public void createBridge(LevelRenderer r, Tile tile, int basey, int lowy) {
		int dy = basey-tile.basey;
		int sh = basey-6-lowy;
		bridge.addInstance(r, new TileObjectInfo(tile, 0, dy-6, 0));
		if(sh>0)
			bridgeSupport.addInstance(r, new ScaledTileObjectInfo(tile, 0, dy-6, 0).scale(1, sh*Tile.ysize));
	}

	public void createHillBridge(LevelRenderer r, Tile tile, int basey) {
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		createBridge(r, tile, basey, miny);
		r.terrain.addHillTile(TerrainMaterial.hillGrass, tile);
	}

	public void autoAddHillBridge(StreetTile tile, int basey) {
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
	
	public void addLamp(Tile atile, Random random) {
		StreetTile tile = (StreetTile) atile;
		if(!tile.level.isInside(tile.x, tile.z, 4) || tile.level.info.isPortal()) {
			tile.lamp = false;
			if(tile.x==1) {
				tile.lampd = Dir.north;
				tile.lamp = true;
			}
			else if(tile.x==tile.level.levelSize-2) {
				tile.lampd = Dir.south;
				tile.lamp = true;
			}
			else if(tile.z==1) {
				tile.lampd = Dir.east;
				tile.lamp = true;
			}
			else if(tile.z==tile.level.levelSize-2) {
				tile.lampd = Dir.west;
				tile.lamp = true;
			}
			return;
		}
		boolean hasLamp = tile.lamp || (tile.x+tile.z)%5==0;
		if(!hasLamp) {
			for(Dir d : Dir.values()) {
				TileTemplate adjt = tile.getAdjT(d);
				if(adjt==HouseT.template)
					hasLamp = random.nextInt(4)>0;
				else if(adjt instanceof StreetSlope && ((StreetSlope) adjt).h>1)
					hasLamp = true;
				if(hasLamp)
					break;
			}
		}
		if(hasLamp) {
			for(Dir8 d : Dir8.values()) {
				Tile adj = tile.getAdj(d.dx, d.dz);
				if(adj!=null && (adj instanceof StreetTile) && ((StreetTile) adj).lamp) {
					tile.lamp = false;
					return;
				}
			}
			for(Dir d : Dir.shuffle(random)) {
				Tile adj = tile.getAdj(d);
				TileTemplate adjt = adj.t;
				if(adjt!=null && !Street.isAnyPath(adjt) && !(adjt instanceof Plaza) && HouseT.allowLamp(adj)) {
					tile.lampd = d;
					tile.lamp = true;
					break;
				}
			}
		}
	}
	
	public void createLamp(Tile atile, LevelRenderer r, float dy) {
		StreetTile tile = (StreetTile) atile;
		Dir d = tile.lampd;
		if(!tile.lamp || d==null)
			return;
		float dx = d.dx*0.45f;
		float dz = d.dz*0.45f;
		lamp.addInstance(r, new IllumTileObjectInfo(tile, dx, dy, dz));
		lampPost.addInstance(r, new TileObjectInfo(tile, dx, dy, dz));
		coronaSprite.addInstance(r, new SpriteInfo(tile, dx, dy+5.75f, dz).size(Tile.size*0.75f));
		r.pointLights.setLight(tile, dx, dy+5.5f, dz, 4.5f);
		r.blockLighting.addLight(IllumLayer.alwaysOn, tile, tile.basey+5, lampLightColor, 0.5f, false);
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
				throw new GeneratorException("Orphan %s sub street at [%d, %d]\n", tile.sub.parent.getClass().getSimpleName(), tile.x, tile.z);
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
