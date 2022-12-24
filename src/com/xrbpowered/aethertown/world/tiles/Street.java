package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.tiles.LightTileComponent;
import com.xrbpowered.aethertown.render.tiles.LightTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class Street extends TileTemplate {

	public static final Color streetColor = new Color(0xb5b5aa);
	
	private static TileComponent street, lampPost;
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
				ObjMeshLoader.loadObj("lamp.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("lamp.png", false, true, false),
				new Texture("lamp_illum.png", false, true, false));
		lampPost = new TileComponent(
				ObjMeshLoader.loadObj("lamp_post.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture(new Color(0x353433)));
		bridge = new TileComponent(
				ObjMeshLoader.loadObj("bridge.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture(TerrainBuilder.wallColor));
		bridgeSupport = new TileComponent(
				ObjMeshLoader.loadObj("bridge_support.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture(TerrainBuilder.wallColor));
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		street.addInstance(new TileObjectInfo(tile));
		if(!addBridge(tile, tile.basey, renderer))
			renderer.terrain.addWalls(tile);
		// if(tile.x%2==0 && tile.z%2==0)
		// 	sprite.addInstance(new SpriteInfo(tile).size(Tile.size));
		addLamp(tile, random, 0);
	}
	
	public boolean addBridge(Tile tile, int basey, LevelRenderer renderer) {
		int dy0 = basey-tile.basey;
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		int maxy = MathUtils.max(yloc);
		Template adjt = tile.getAdjT(tile.d);
		if((maxy<=basey-3) && (adjt==Template.street || adjt==StreetSlope.template1 || adjt==StreetSlope.template4)) {
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
	
	public void addLamp(Tile tile, Random random, float dy) {
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
			Dir d = Dir.random(random);
			for(int i=0; i<4; i++) {
				Template adjt = tile.getAdjT(d);
				if(adjt!=Template.street && adjt!=StreetSlope.template1 && adjt!=StreetSlope.template4) {
					float dx = d.dx*0.45f;
					float dz = d.dz*0.45f;
					lamp.addInstance(new LightTileObjectInfo(tile, dx, dy, dz));
					lampPost.addInstance(new TileObjectInfo(tile, dx, dy, dz));
					break;
				}
				d = d.cw();
			}
		}
	}
	
	@Override
	public boolean finalizeTile(Tile tile, Random random) {
		if(random.nextInt(4)==0)
			return false;
		
		boolean adjSlope = false;
		boolean adjHouse = false;
		int countStreet = 0;
		for(Dir d : Dir.values()) { // FIXME orphan parks after street trimming
			Template adjt = tile.getAdjT(d);
			// if(adjt==null)
			//	return false;
			if(adjt==Template.street)
				countStreet++;
			else if(adjt==StreetSlope.template1 || adjt==StreetSlope.template4)
				adjSlope = true;
			else if(adjt==Template.house)
				adjHouse = true;
		}
		if(countStreet<2 && !adjHouse) {
			if(!adjSlope) {
				tile.level.map[tile.x][tile.z] = null;
				return true;
			}
		}
		return false;
	}
	
}
