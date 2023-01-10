package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.env.Seasons;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.render.tiles.IllumTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Tile.SubInfo;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.gen.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.HouseGeneratorBase;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public class HouseT extends TileTemplate {

	public static final Seasons roofColor = new Seasons(new Color(0x57554a), new Color(0xe0eef1));
	
	public static final HouseT template = new HouseT();
	
	private static IllumTileComponent groundWall, groundWallDoor, groundWallBlank, upperWall, upperWallBlank;
	private static TileComponent roof, roofEndLeft, roofEndRight;
	
	public class HouseTile extends Tile {
		public int groundy;

		public HouseTile() {
			super(HouseT.this);
		}
		
		@Override
		public void place(Level level, int x, int y, int z, Dir d) {
			super.place(level, x, y, z, d);
			groundy = basey;
		}
	}
	
	public HouseT() {
		super(roofColor.colors[Seasons.summer]);
	}
	
	@Override
	public Tile createTile() {
		return new HouseTile();
	}
	
	@Override
	public String getTileInfo(Tile tile) {
		return ((HouseGeneratorBase) tile.sub.parent).getInfo();
	}
	
	@Override
	public int getFixedYStrength() {
		return 1;
	}
	
	@Override
	public int getGroundY(Tile tile) {
		return ((HouseTile) tile).groundy;
	}
	
	@Override
	public int getFenceY(Tile tile, Corner c) {
		return tile.basey+11;
	}
	
	@Override
	public int getLightBlockY(Tile tile) {
		return tile.basey+15;
	}
	
	@Override
	public void createComponents() {
		StaticMesh wall = BasicGeometry.wall(Tile.size, 6*Tile.ysize, ObjectShader.vertexInfo, null);
		groundWall = new IllumTileComponent(wall,
				new Texture("models/house/ground_wall.png", false, true, false),
				new Texture("models/house/ground_wall_illum.png", false, true, false));
		groundWallDoor = new IllumTileComponent(wall,
				new Texture("models/house/ground_wall_door.png", false, true, false),
				new Texture("models/house/ground_wall_door_illum.png", false, true, false));
		groundWallBlank = new IllumTileComponent(wall,
				new Texture("models/house/ground_wall_blank.png", false, true, false),
				TexColor.get(Color.BLACK));
		upperWall = new IllumTileComponent(wall,
				new Texture("models/house/upper_wall.png", false, true, false),
				new Texture("models/house/upper_wall_illum.png", false, true, false));
		upperWallBlank = new IllumTileComponent(wall,
				new Texture("models/house/upper_wall_blank.png", false, true, false),
				TexColor.get(Color.BLACK));
		roof = new TileComponent(
				BasicGeometry.slope(Tile.size, 8*Tile.ysize, ObjectShader.vertexInfo, null),
				roofColor.texture());
		Texture upperWallColor = new Texture(new Color(0xd5ceba));
		roofEndLeft = new TileComponent(
				BasicGeometry.slopeSideLeft(Tile.size, 8*Tile.ysize, ObjectShader.vertexInfo, null),
				upperWallColor);
		roofEndRight = new TileComponent(
				BasicGeometry.slopeSideRight(Tile.size, 8*Tile.ysize, ObjectShader.vertexInfo, null),
				upperWallColor);
	}

	private Vector3f randomIllumMod(Random random, boolean houseIllum) {
		if(!houseIllum)
			return null;
		float r = random.nextFloat()*0.5f+0.5f;
		float g = r*(random.nextFloat()*0.2f+0.8f);
		float b = g*(random.nextFloat()*0.2f+0.8f);
		boolean on = random.nextInt(3)==0;
		return new Vector3f(r, g, b).mul(on ? 1f : 0.04f);
	}
	
	private static boolean isObstructed(Tile tile, int[] yloc, int y, Dir d) {
		int y0 = yloc[d.leftCorner().ordinal()];
		int y1 = yloc[d.rightCorner().ordinal()];
		int gy = y;
		Tile adj = tile.getAdj(d);
		if(adj!=null)
			gy = adj.getGroundY();
		return (y0>=y) || (y1>=y) || (gy>=y);
	}
	
	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		renderer.terrain.addWalls(tile);
		
		int basey = tile.basey;
		SubInfo sub = tile.sub;
		HouseGeneratorBase house = (HouseGeneratorBase) sub.parent;
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		
		int left = -house.left+house.marginLeft;
		int right = house.right-house.marginRight;
		int front = house.marginFront;
		int back = house.fwd-house.marginBack;
		
		Vector3f illum = randomIllumMod(random, house.illum);
		renderer.blockLighting.addLight(tile, tile.basey+4, illum, 0.35f, true);
		if(sub.j==front)
			(sub.i==0 && sub.j==front ? groundWallDoor : (isObstructed(tile, yloc, basey+2, tile.d.flip()) ? groundWallBlank : groundWall)).addInstance(new IllumTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.flip()));
		if(sub.i==left)
			(isObstructed(tile, yloc, basey+2, tile.d.ccw()) ? groundWallBlank : groundWall).addInstance(new IllumTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.ccw()));
		if(sub.i==right)
			(isObstructed(tile, yloc, basey+2, tile.d.cw()) ? groundWallBlank : groundWall).addInstance(new IllumTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.cw()));
		if(sub.j==back)
			(isObstructed(tile, yloc, basey+2, tile.d) ? groundWallBlank : groundWall).addInstance(new IllumTileObjectInfo(tile, 0, 0, 0).illumMod(illum));
		
		illum = randomIllumMod(random, house.illum);
		renderer.blockLighting.addLight(tile, tile.basey+10, illum, 0.35f, true);
		if(sub.j==front)
			(isObstructed(tile, yloc, basey+7, tile.d.flip()) ? upperWallBlank : upperWall).addInstance(new IllumTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.flip()));
		if(sub.i==left)
			(isObstructed(tile, yloc, basey+7, tile.d.ccw()) ? upperWallBlank : upperWall).addInstance(new IllumTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.ccw()));
		if(sub.i==right)
			(isObstructed(tile, yloc, basey+7, tile.d.cw()) ? upperWallBlank : upperWall).addInstance(new IllumTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.cw()));
		if(sub.j==back)
			(isObstructed(tile, yloc, basey+7, tile.d) ? upperWallBlank : upperWall).addInstance(new IllumTileObjectInfo(tile, 0, 6, 0).illumMod(illum));
		
		if(house.alignStraight) {
			roof.addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(sub.i==left ? tile.d.ccw() : tile.d.cw()));
			if(sub.j==front)
				(sub.i==left ? roofEndLeft : roofEndRight).addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(tile.d.flip()));
			if(sub.j==back)
				(sub.i==left ? roofEndRight : roofEndLeft).addInstance(new TileObjectInfo(tile, 0, 12, 0));
		}
		else {
			roof.addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(sub.j==0 ? tile.d.flip() : tile.d));
			if(sub.i==left)
				(sub.j==house.fwd ? roofEndLeft : roofEndRight).addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(tile.d.ccw()));
			if(sub.i==right)
				(sub.j==house.fwd ? roofEndRight : roofEndLeft).addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(tile.d.cw()));
		}
	}
	
	@Override
	public boolean finalizeTile(Tile atile, Random random) {
		HouseTile tile = (HouseTile) atile;
		int max = tile.basey;
		int lim = tile.basey+1000;
		int maxGround = tile.basey;
		for(Dir d : Dir.values()) {
			Tile adj = tile.getAdj(d);
			if(adj==null)
				continue;
			if(adj.t==Hill.template && Hill.template.getMaxDelta(adj)>=6) {
				int[] yloc = tile.level.h.yloc(adj.x, adj.z);
				int maxy = MathUtils.max(yloc);
				if(maxy>max) {
					max = maxy;
					if(max>lim) max = lim;
				}
				if(maxy>maxGround)
					maxGround = maxy;
			}
			if(adj.t.getFixedYStrength()>1) {
				int y = adj.getGroundY();
				if(y<lim) {
					lim = y;
					if(max>lim) max = lim;
				}
				if(y>maxGround)
					maxGround = y;
			}
		}
		if(maxGround>tile.basey+12) {
			HouseGenerator house = (HouseGenerator) tile.sub.parent;
			house.remove();
			return true;
		}
		int floors = (max-tile.basey)/6; // TODO match facade floor heights
		if(floors>0) {
			if(floors>2)
				floors = 2;
			int ground = floors*6+tile.basey;
			if(ground>getGroundY(tile)) {
				tile.groundy = ground;
				return true;
			}
		}
		return false;
	}
	
}
