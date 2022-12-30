package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.LightTileComponent;
import com.xrbpowered.aethertown.render.tiles.LightTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.gen.LargeParkGenerator;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class Street extends TileTemplate {

	public static final Color streetColor = new Color(0xb5b5aa);
	
	public static TileComponent street;
	
	private static TileComponent lampPost;
	private static LightTileComponent lamp;
	//private static SpriteComponent sprite;
	private static TileComponent bridge, bridgeSupport;

	public Street() {
		super(streetColor);
	}
	
	@Override
	public void createComponents() {
		street = new TileComponent(
				FastMeshBuilder.plane(Tile.size, 1, 1, ObjectShader.vertexInfo, null),
				new Texture(streetColor));
		//sprite = new SpriteComponent(new Texture("checker.png"));
		lamp = new LightTileComponent(
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
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		street.addInstance(new TileObjectInfo(tile));
		if(!addBridge(tile, tile.basey, renderer))
			renderer.terrain.addWalls(tile);
		// if(tile.x%2==0 && tile.z%2==0)
		// 	sprite.addInstance(new SpriteInfo(tile).size(Tile.size));
		addLamp(tile, renderer, random, 0);
	}
	
	public boolean addBridge(Tile tile, int basey, LevelRenderer renderer) {
		int dy0 = basey-tile.basey;
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		int maxy = MathUtils.max(yloc);
		Template adjt = tile.getAdjT(tile.d);
		if((maxy<=basey-3) && (adjt==Template.street || (adjt instanceof StreetSlope))) {
			bridge.addInstance(new TileObjectInfo(tile, 0, dy0-6, 0));
			int sh = basey-6-miny;
			if(sh>0)
				bridgeSupport.addInstance(new TileObjectInfo(tile, 0, dy0-6, 0).scale(1, sh*Tile.ysize));
			renderer.terrain.addHillTile(TerrainBuilder.grassColor.color(), tile);
			return true;
		}
		else {
			return false;
		}
	}
	
	public void addLamp(Tile tile, LevelRenderer renderer, Random random, float dy) {
		if((tile.x+tile.z)%2==0)
			return;
		
		boolean hasLamp = random.nextInt(3)==0;
		if(!hasLamp) {
			for(Dir d : Dir.values()) {
				Template adjt = tile.getAdjT(d);
				if(adjt==Template.house) {
					hasLamp = random.nextInt(4)>0;
					break;
				}
			}
		}
		
		if(hasLamp) {
			for(Dir d : Dir.shuffle(random)) {
				Template adjt = tile.getAdjT(d);
				if(adjt!=Template.street && !(adjt instanceof StreetSlope) && adjt!=Template.monument) {
					float dx = d.dx*0.45f;
					float dz = d.dz*0.45f;
					lamp.addInstance(new LightTileObjectInfo(tile, dx, dy, dz));
					lampPost.addInstance(new TileObjectInfo(tile, dx, dy, dz));
					renderer.pointLights.setLight(tile, dx, dy+5.5f, dz);
					break;
				}
			}
		}
	}
	
	/**
	 * @return 0: trim not needed, 1: can't trim, 2: trimmed 
	 */
	public static int trimStreet(Tile tile, Random random) {
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
				if(adj.t==Template.hill)
					continue;
				if(adj.t!=Template.park)
					return 0;
				res = 1;
				park = adj;
			}
			if((adj.t==Template.street || (adj.t instanceof StreetSlope)) && src!=null)
				return 0;
		}
		
		if(res==1) {
			if(park!=null) {
				if(park.sub==null) {
					Template.monument.forceGenerate(Token.forTile(park), random);
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
				t = t.getAdj(dsrc);
			}
		}
		tile.level.map[tile.x][tile.z] = null;
		return 2;
	}
	
}
