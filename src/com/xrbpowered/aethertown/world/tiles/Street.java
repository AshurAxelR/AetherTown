package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.render.tiles.IllumTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.gen.plot.LargeParkGenerator;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class Street extends TileTemplate {

	public static final Color streetColor = new Color(0xb5b5aa);
	public static final Color lampLightColor = new Color(0xfff0b4); // new Color(0xfffae5);
	
	public static final Street template = new Street();
	public static final Street subTemplate = new Street();
	
	public static TileComponent street, handrailPole;
	
	private static TileComponent lampPost;
	private static IllumTileComponent lamp;
	//private static SpriteComponent sprite;
	private static TileComponent bridge, bridgeSupport;
	private static TileComponent handrail;

	public static class StreetTile extends Tile {
		public boolean lamp = false;
		public boolean bridge = false;
		
		public StreetTile(TileTemplate t) {
			super(t);
		}
	}
	
	public Street() {
		super(streetColor);
	}

	@Override
	public Tile createTile() {
		return new StreetTile(this);
	}
	
	@Override
	public float getYAt(Tile tile, float sx, float sz, float y0) {
		if(((StreetTile)tile).bridge && Bridge.isUnder(y0, tile.basey))
			return tile.level.h.gety(tile.x, tile.z, sx, sz);
		else
			return super.getYAt(tile, sx, sz, y0);
	}
	
	@Override
	public void createComponents() {
		street = new TileComponent(
				FastMeshBuilder.plane(Tile.size, 1, 1, ObjectShader.vertexInfo, null),
				new Texture(streetColor));
		//sprite = new SpriteComponent(new Texture("checker.png"));
		lamp = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/lamp/lamp.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/lamp/lamp.png", false, true, false),
				new Texture("models/lamp/lamp_illum.png", false, true, false));
		lampPost = new TileComponent(
				ObjMeshLoader.loadObj("models/lamp/lamp_post.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0x353433));
		bridge = new TileComponent(
				ObjMeshLoader.loadObj("models/bridge/bridge.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture(TerrainBuilder.wallColor));
		bridgeSupport = new TileComponent(
				ObjMeshLoader.loadObj("models/bridge/bridge_support.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture(TerrainBuilder.wallColor));
		handrail = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/handrail.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/fences/handrail.png", false, true, false));
		handrailPole = new TileComponent(
				ObjMeshLoader.loadObj("models/fences/handrail_pole.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(0xd5ceba));
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		street.addInstance(new TileObjectInfo(tile));
		if(!addAutoHillBridge((StreetTile)tile, tile.basey, renderer))
			renderer.terrain.addWalls(tile);
		addHandrails(tile);
		// if(tile.x%2==0 && tile.z%2==0)
		// 	sprite.addInstance(new SpriteInfo(tile).size(Tile.size));
		addLamp(tile, renderer, random, 0);
	}
	
	public static boolean needsHandrail(Tile tile, Dir d, int dy0, int dy1) {
		Corner c0 = d.leftCorner();
		Corner c1 = d.rightCorner();
		Tile adj = tile.getAdj(d);
		if(adj==null)
			return false;
		if(adj.t==Hill.template && tile.basey>=adj.basey+4)
			return true;
		return tile.t.getFenceY(tile, c0)>adj.t.getFenceY(adj, c0.flipOver(d))+dy0 ||
				tile.t.getFenceY(tile, c1)>adj.t.getFenceY(adj, c1.flipOver(d))+dy1;
	}

	public static boolean needsHandrail(Tile tile, Dir d) {
		return needsHandrail(tile, d, 0, 0);
	}

	public static void addHandrailPoles(Tile tile, Dir d, int dy0, int dy1) {
		Corner c0 = d.leftCorner();
		handrailPole.addInstance(new TileObjectInfo(tile, 0.5f*c0.dx, dy0, 0.5f*c0.dz));
		Corner c1 = d.rightCorner();
		handrailPole.addInstance(new TileObjectInfo(tile, 0.5f*c1.dx, dy1, 0.5f*c1.dz));
	}
	
	public void addHandrail(Tile tile, Dir d) {
		if(needsHandrail(tile, d)) {
			handrail.addInstance(new TileObjectInfo(tile).rotate(d));
			addHandrailPoles(tile, d, 0, 0);
		}
	}
	
	public void addHandrails(Tile tile) {
		Dir dsrc = tile.d.flip();
		for(Dir d : Dir.values()) {
			if(d!=dsrc)
				addHandrail(tile, d);
			else {
				Tile src = tile.getAdj(d);
				if(src!=null && needsHandrail(tile, d, 1, 1)) {
					handrail.addInstance(new TileObjectInfo(tile).rotate(d));
					addHandrailPoles(tile, d, 0, 0);
				}
				else {
					// TODO add entry stairs in place of handrails
				}
			}
		}
	}
	
	public void addBridge(Tile tile, int basey, int lowy) {
		int dy = basey-tile.basey;
		int sh = basey-6-lowy;
		bridge.addInstance(new TileObjectInfo(tile, 0, dy-6, 0));
		if(sh>0)
			bridgeSupport.addInstance(new TileObjectInfo(tile, 0, dy-6, 0).scale(1, sh*Tile.ysize));
	}
	
	public boolean addAutoHillBridge(StreetTile tile, int basey, LevelRenderer renderer) {
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		int maxy = MathUtils.max(yloc);
		if(maxy>basey-3 || basey-miny>=24)
			return false;
		TileTemplate adjt = tile.getAdjT(tile.d);
		if(adjt==null || !(Street.isAnyStreet(adjt) || (adjt instanceof Plaza)))
			return false;
		Tile adjcw = tile.getAdj(tile.d.cw());
		if(adjcw==null || adjcw.getGroundY()>=basey)
			return false;
		Tile adjccw = tile.getAdj(tile.d.ccw());
		if(adjccw==null || adjccw.getGroundY()>=basey)
			return false;
		tile.bridge = true;
		addBridge(tile, basey, miny);
		renderer.terrain.addHillTile(TerrainBuilder.grassColor.color(), tile);
		return true;
	}
	
	public void addLamp(Tile atile, LevelRenderer renderer, Random random, float dy) {
		StreetTile tile = (StreetTile) atile;
		if(!tile.level.isInside(tile.x, tile.z, 5)) {
			tile.lamp = false;
			return;
		}
		
		boolean hasLamp = tile.lamp || random.nextInt(4)==0;
		if(!hasLamp) {
			for(Dir d : Dir.values()) {
				TileTemplate adjt = tile.getAdjT(d);
				if(adjt==HouseT.template) {
					hasLamp = random.nextInt(4)>0;
					break;
				}
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
				TileTemplate adjt = tile.getAdjT(d);
				if(adjt!=null && !Street.isAnyPath(adjt) && !(adjt instanceof Plaza)) {
					float dx = d.dx*0.45f;
					float dz = d.dz*0.45f;
					lamp.addInstance(new IllumTileObjectInfo(tile, dx, dy, dz));
					lampPost.addInstance(new TileObjectInfo(tile, dx, dy, dz));
					renderer.pointLights.setLight(tile, dx, dy+5.5f, dz, 4.5f);
					renderer.blockLighting.addLight(tile, tile.basey+5, lampLightColor, 0.5f, false);
					tile.lamp = true;
					break;
				}
			}
		}
	}
	
	@Override
	public boolean canExpandFill(Tile tile) {
		TileTemplate adjtL = tile.getAdjT(tile.d.cw());
		TileTemplate adjtR = tile.getAdjT(tile.d.ccw());
		return (adjtR!=null && isAnyStreet(adjtR) || adjtL!=null && isAnyStreet(adjtL));
	}
	
	/**
	 * @return 0: trim not needed, 1: can't trim, 2: trimmed
	 */
	public static int trimStreet(Tile tile, Random random) {
		if(tile.x==0 || tile.x==tile.level.levelSize-1 || tile.z==0 || tile.z==tile.level.levelSize-1)
			return 0;
		
		Dir dsrc = tile.d.flip();
		Tile src = tile.getAdj(dsrc);
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
				if(adj.t!=Park.template)
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
					Monument.template.forceGenerate(Token.forTile(park), random);
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
				System.err.printf("Orphan %s sub street at [%d, %d]\n", tile.sub.parent.getClass().getSimpleName(), tile.x, tile.z);
		}
		if((tile.x==tile.level.getStartX() && tile.z==tile.level.getStartZ()))
			return 1;
		
		if(src!=null && (src.t instanceof StreetSlope)) {
			int dy = ((StreetSlope)src.t).h;
			Dir align = src.d; 
			if(dy==1) {
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
