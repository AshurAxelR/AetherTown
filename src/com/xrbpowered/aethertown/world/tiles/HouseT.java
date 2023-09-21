package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.render.tiles.IllumTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.FenceGenerator;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Tile.SubInfo;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureStyle;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureTileSet;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.HouseGeneratorBase;
import com.xrbpowered.gl.res.texture.Texture;

public class HouseT extends TileTemplate {

	public static final int roofHeight = 6;
	
	public static final HouseT template = new HouseT();
	
	private static TileComponent roof, roofEndLeft, roofEndRight;
	
	public class HouseTile extends Tile {
		public int groundy;
		public Vector3f[] illum;
		public boolean steps = false;

		public HouseTile() {
			super(HouseT.this);
		}
		
		@Override
		public void place(Level level, int x, int y, int z, Dir d) {
			super.place(level, x, y, z, d);
			groundy = basey;
		}
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
	public int getBlockY(Tile tile) {
		HouseGenerator house = (HouseGenerator) tile.sub.parent;
		return tile.basey + house.arch.getRoofY() + HouseT.roofHeight;
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
	public float getYOut(Tile tile, Dir d, float sout, float sx, float sz, float prevy) {
		if(((HouseTile) tile).steps && d==tile.d.flip())
			return FenceGenerator.getFenceYOut(tile.basey, sout);
		else
			return Float.NEGATIVE_INFINITY;
	}
	
	@Override
	public void createComponents() {
		ArchitectureTileSet.createComponents();
		roof = new TileComponent(
				BasicGeometry.slope(Tile.size, roofHeight*Tile.ysize, ObjectShader.vertexInfo, null),
				ChurchT.roofTexture);
		Texture upperWallColor = TexColor.get(new Color(0xd5ceba));
		roofEndLeft = new TileComponent(
				BasicGeometry.slopeSideLeft(Tile.size, roofHeight*Tile.ysize, ObjectShader.vertexInfo, null),
				upperWallColor);
		roofEndRight = new TileComponent(
				BasicGeometry.slopeSideRight(Tile.size, roofHeight*Tile.ysize, ObjectShader.vertexInfo, null),
				upperWallColor);
	}

	@Override
	public void decorateTile(Tile atile, Random random) {
		HouseTile tile = (HouseTile) atile;
		HouseGenerator house = (HouseGenerator) tile.sub.parent;
		tile.illum = new Vector3f[house.arch.floorCount];
		for(int f=0; f<tile.illum.length; f++)
			tile.illum[f] = IllumPattern.calcMod(random, house.arch.getIllum(f));
		if(tile.sub.i==0 && tile.sub.j==0 && tile.getAdj(tile.d.flip()).t==StreetSlope.template1)
			tile.steps = true;
	}
	
	@Override
	public void createGeometry(Tile atile, LevelRenderer r) {
		HouseTile tile = (HouseTile) atile;
		r.terrain.addWalls(tile);
		if(tile.steps)
			FenceGenerator.steps.addInstance(r, new TileObjectInfo(tile).rotate(tile.d.flip()));
		
		SubInfo sub = tile.sub;
		HouseGenerator house = (HouseGenerator) sub.parent;
		ArchitectureStyle arch = house.arch;
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		
		int left = -house.left+house.marginLeft;
		int right = house.right-house.marginRight;
		int front = house.marginFront;
		int back = house.fwd-house.marginBack;
		
		for(int f=0; f<arch.floorCount; f++) {
			IllumLayer illumLayer = arch.getIllumLayer(f);
			float illumTrigger = 1.75f; // TODO house illum trigger
			Vector3f illumMod = tile.illum[f];
			r.blockLighting.addLight(illumLayer, tile, tile.basey+arch.getLightY(f), illumMod, 0.35f, true);
			if(sub.j==front)
				(f==0 && sub.i==0 && sub.j==front ? arch.getDoor() : arch.getWall(f, Dir.south, tile, yloc))
						.addInstance(r, new IllumTileObjectInfo(tile, 0, arch.getFloorY(f), 0)
						.illum(illumLayer, illumTrigger).illumMod(illumMod).rotate(tile.d.flip()));
			if(sub.i==left)
				arch.getWall(f, Dir.west, tile, yloc).addInstance(r, new IllumTileObjectInfo(tile, 0, arch.getFloorY(f), 0)
						.illum(illumLayer, illumTrigger).illumMod(illumMod).rotate(tile.d.ccw()));
			if(sub.i==right)
				arch.getWall(f, Dir.east, tile, yloc).addInstance(r, new IllumTileObjectInfo(tile, 0, arch.getFloorY(f), 0)
						.illum(illumLayer, illumTrigger).illumMod(illumMod).rotate(tile.d.cw()));
			if(sub.j==back)
				arch.getWall(f, Dir.north, tile, yloc).addInstance(r, new IllumTileObjectInfo(tile, 0, arch.getFloorY(f), 0)
						.illum(illumLayer, illumTrigger).illumMod(illumMod).rotate(tile.d));
		}

		int roofy = arch.getRoofY();
		if(house.alignStraight) {
			roof.addInstance(r, new TileObjectInfo(tile, 0, roofy, 0).rotate(sub.i==left ? tile.d.ccw() : tile.d.cw()));
			if(sub.j==front)
				(sub.i==left ? roofEndLeft : roofEndRight).addInstance(r, new TileObjectInfo(tile, 0, roofy, 0).rotate(tile.d.flip()));
			if(sub.j==back)
				(sub.i==left ? roofEndRight : roofEndLeft).addInstance(r, new TileObjectInfo(tile, 0, roofy, 0));
		}
		else {
			roof.addInstance(r, new TileObjectInfo(tile, 0, roofy, 0).rotate(sub.j==0 ? tile.d.flip() : tile.d));
			if(sub.i==left)
				(sub.j==house.fwd ? roofEndLeft : roofEndRight).addInstance(r, new TileObjectInfo(tile, 0, roofy, 0).rotate(tile.d.ccw()));
			if(sub.i==right)
				(sub.j==house.fwd ? roofEndRight : roofEndLeft).addInstance(r, new TileObjectInfo(tile, 0, roofy, 0).rotate(tile.d.cw()));
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
		HouseGenerator house = (HouseGenerator) tile.sub.parent;
		if(maxGround>tile.basey+house.arch.maxGround()) {
			house.remove();
			HouseGenerator.resetHouseList(tile.level);
			return true;
		}
		return house.arch.matchGround(tile, max);
	}
	
	public static boolean allowLamp(Tile tile) {
		if(tile.t!=template || tile.sub==null)
			return true;
		HouseGenerator house = (HouseGenerator) tile.sub.parent;
		return tile.sub.i!=0 || tile.sub.j!=0 || house.arch.allowLampAtDoor();
	}
	
}
