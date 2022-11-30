package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.env.Seasons;
import com.xrbpowered.aethertown.render.tiles.LightTileComponent;
import com.xrbpowered.aethertown.render.tiles.LightTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.SubTile;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.HouseGenerator;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public class HouseT extends Template {

	private static final Seasons roofColor = new Seasons(new Color(0x57554a), new Color(0xe0eef1));
	
	private static LightTileComponent groundWall, groundWallDoor, upperWall;
	private static TileComponent roof, roofEndLeft, roofEndRight;
	
	public HouseT() {
		super(roofColor.colors[Seasons.summer]);
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
		upperWall = new LightTileComponent(wall,
				new Texture("upper_wall.png", false, true, false),
				new Texture("upper_wall_illum.png", false, true, false));
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
	
	@Override
	public void createGeometry(Tile tile, TerrainBuilder terrain, Random random) {
		terrain.addWalls(tile);
		
		SubTile st = (SubTile) tile;
		HouseGenerator house = (HouseGenerator) st.parent;
		int left = -house.left;
		Vector3f illum = randomIllumMod(random, house.illum);
		if(st.subj==0)
			(st.subi==0 && st.subj==0 ? groundWallDoor : groundWall).addInstance(new LightTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.flip()));
		if(st.subi==left)
			groundWall.addInstance(new LightTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.ccw()));
		if(st.subi==house.right)
			groundWall.addInstance(new LightTileObjectInfo(tile, 0, 0, 0).illumMod(illum).rotate(tile.d.cw()));
		if(st.subj==house.fwd)
			groundWall.addInstance(new LightTileObjectInfo(tile, 0, 0, 0).illumMod(illum));
		
		illum = randomIllumMod(random, house.illum);
		if(st.subj==0)
			upperWall.addInstance(new LightTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.flip()));
		if(st.subi==left)
			upperWall.addInstance(new LightTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.ccw()));
		if(st.subi==house.right)
			upperWall.addInstance(new LightTileObjectInfo(tile, 0, 6, 0).illumMod(illum).rotate(tile.d.cw()));
		if(st.subj==house.fwd)
			upperWall.addInstance(new LightTileObjectInfo(tile, 0, 6, 0).illumMod(illum));
		
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
	
}
