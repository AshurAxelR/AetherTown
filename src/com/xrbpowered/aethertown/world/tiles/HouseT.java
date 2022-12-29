package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.env.Seasons;
import com.xrbpowered.aethertown.render.tiles.LightTileComponent;
import com.xrbpowered.aethertown.render.tiles.LightTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.SubTile;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.StreetLayoutGenerator;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public class HouseT extends Template {

	private static final Seasons roofColor = new Seasons(new Color(0x57554a), new Color(0xe0eef1));
	
	private static LightTileComponent groundWall, groundWallDoor, groundWallBlank, upperWall, upperWallBlank;
	private static TileComponent roof, roofEndLeft, roofEndRight;
	
	public HouseT() {
		super(roofColor.colors[Seasons.summer]);
	}
	
	@Override
	public String getTileInfo(Tile tile) {
		HouseGenerator house = (HouseGenerator) ((SubTile) tile).parent;
		return String.format("House %d", house.index+1);
	}
	
	@Override
	public int getFixedYStrength() {
		return 1;
	}
	
	@Override
	public int getGroundY(Tile tile) {
		if(tile.data==null)
			return tile.basey;
		else
			return (Integer)tile.data;
	}
	
	@Override
	public void createComponents() {
		StaticMesh wall = BasicGeometry.wall(Tile.size, 6*Tile.ysize, ObjectShader.vertexInfo, null);
		groundWall = new LightTileComponent(wall,
				new Texture("ground_wall.png", false, true, false),
				new Texture("ground_wall_illum.png", false, true, false));
		groundWallDoor = new LightTileComponent(wall,
				new Texture("ground_wall_door.png", false, true, false),
				new Texture("ground_wall_door_illum.png", false, true, false));
		groundWallBlank = new LightTileComponent(wall,
				new Texture("ground_wall_blank.png", false, true, false),
				TexColor.get(Color.BLACK));
		upperWall = new LightTileComponent(wall,
				new Texture("upper_wall.png", false, true, false),
				new Texture("upper_wall_illum.png", false, true, false));
		upperWallBlank = new LightTileComponent(wall,
				new Texture("upper_wall_blank.png", false, true, false),
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
		int gy = tile.getAdj(d).getGroundY();
		return (y0>=y) || (y1>=y) || (gy>=y);
	}
	
	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		renderer.terrain.addWalls(tile);
		
		int basey = tile.basey;
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		SubTile st = (SubTile) tile;
		HouseGenerator house = (HouseGenerator) st.parent;
		int left = -house.left;
		Vector3f illum = randomIllumMod(random, house.illum);
		if(st.subj==0)
			(st.subi==0 && st.subj==0 ? groundWallDoor : (isObstructed(tile, yloc, basey+2, tile.d.flip()) ? groundWallBlank : groundWall)).addInstance(new LightTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.flip()));
		if(st.subi==left)
			(isObstructed(tile, yloc, basey+2, tile.d.ccw()) ? groundWallBlank : groundWall).addInstance(new LightTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.ccw()));
		if(st.subi==house.right)
			(isObstructed(tile, yloc, basey+2, tile.d.cw()) ? groundWallBlank : groundWall).addInstance(new LightTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.cw()));
		if(st.subj==house.fwd)
			(isObstructed(tile, yloc, basey+2, tile.d) ? groundWallBlank : groundWall).addInstance(new LightTileObjectInfo(tile, 0, 0, 0).illumMod(illum));
		
		illum = randomIllumMod(random, house.illum);
		if(st.subj==0)
			(isObstructed(tile, yloc, basey+7, tile.d.flip()) ? upperWallBlank : upperWall).addInstance(new LightTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.flip()));
		if(st.subi==left)
			(isObstructed(tile, yloc, basey+7, tile.d.ccw()) ? upperWallBlank : upperWall).addInstance(new LightTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.ccw()));
		if(st.subi==house.right)
			(isObstructed(tile, yloc, basey+7, tile.d.cw()) ? upperWallBlank : upperWall).addInstance(new LightTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.cw()));
		if(st.subj==house.fwd)
			(isObstructed(tile, yloc, basey+7, tile.d) ? upperWallBlank : upperWall).addInstance(new LightTileObjectInfo(tile, 0, 6, 0).illumMod(illum));
		
		if(house.alignStraight) {
			roof.addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(st.subi==left ? tile.d.ccw() : tile.d.cw()));
			if(st.subj==0)
				(st.subi==left ? roofEndLeft : roofEndRight).addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(tile.d.flip()));
			if(st.subj==house.fwd)
				(st.subi==left ? roofEndRight : roofEndLeft).addInstance(new TileObjectInfo(tile, 0, 12, 0));
		}
		else {
			roof.addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(st.subj==0 ? tile.d.flip() : tile.d));
			if(st.subi==left)
				(st.subj==house.fwd ? roofEndLeft : roofEndRight).addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(tile.d.ccw()));
			if(st.subi==house.right)
				(st.subj==house.fwd ? roofEndRight : roofEndLeft).addInstance(new TileObjectInfo(tile, 0, 12, 0).rotate(tile.d.cw()));
		}
	}
	
	@Override
	public boolean finalizeTile(Tile tile, Random random) {
		int max = tile.basey;
		int lim = tile.basey+1000;
		int maxGround = tile.basey;
		for(Dir d : Dir.values()) {
			Tile adj = tile.getAdj(d);
			if(adj==null)
				continue;
			if(adj.t==Template.hill && Template.hill.getMaxDelta(adj)>=6) {
				int[] yloc = tile.level.h.yloc(adj.x, adj.z);
				int maxy = MathUtils.max(yloc);
				if(maxy>max) {
					max = maxy;
					if(max>lim) max = lim;
				}
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
			HouseGenerator house = (HouseGenerator) ((SubTile)tile).parent;
			house.remove();
			StreetLayoutGenerator.trimStreets(tile.level, random);
			return true;
		}
		int floors = (max-tile.basey)/6; // TODO match facade floor heights
		if(floors>0) {
			if(floors>2)
				floors = 2;
			int ground = floors*6+tile.basey;
			if(ground>getGroundY(tile)) {
				tile.data = ground;
				return true;
			}
		}
		return false;
	}
	
}
