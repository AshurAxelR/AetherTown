package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class StreetSlope extends TileTemplate {

	private static final StreetSlope template1 = new StreetSlope(1);
	private static final StreetSlope template2 = new StreetSlope(2);
	private static final StreetSlope template4 = new StreetSlope(4);

	public final int h;
	private TileComponent street, side;
	
	public StreetSlope(int h) {
		super(Street.streetColor);
		this.h = h;
	}
	
	@Override
	public float gety(Tile tile, float sx, float sz) {
		float y0 = Tile.ysize*tile.basey;
		float y1 = Tile.ysize*(tile.basey-h);
		sx *= tile.d.dx;
		if(sx<0) sx += 1;
		sz *= tile.d.dz;
		if(sz<0) sz += 1;
		float s = sx+sz;
		if(h==4) {
			s = MathUtils.clamp((s-0.25f)/0.75f);
			return MathUtils.lerp(y0, y1, s);
		}
		else if(h==2) {
			s = MathUtils.clamp((s-0.5f)/0.5f);
			return MathUtils.lerp(y0, y1, s);
		}
		else {
			return MathUtils.lerp(y0, y1, s);
		}
	}

	@Override
	public void createComponents() {
		if(h==4) {
			street = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs4.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(Street.streetColor));
			side = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs4side.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(TerrainBuilder.wallColor));
		}
		else if(h==2) {
			street = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs2.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(Street.streetColor));
			side = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs2side.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(TerrainBuilder.wallColor));
		}
		else {
			street = new TileComponent(
					BasicGeometry.slope(Tile.size, Tile.ysize*h, ObjectShader.vertexInfo, null),
					new Texture(Street.streetColor));
		}
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		if(h>1) {
			side.addInstance(new TileObjectInfo(tile, 0, -h, 0));
			if(!Template.street.addBridge(tile, tile.basey-h, renderer))
				renderer.terrain.addWalls(tile.x, tile.z, tile.basey-h);
		}
		else {
			if(Template.street.addBridge(tile, tile.basey-h, renderer)) {
				renderer.terrain.addWall(tile.x, tile.z, tile.d.cw(), tile.basey-h, tile.basey, tile.basey-h, tile.basey-h);
				renderer.terrain.addWall(tile.x, tile.z, tile.d.ccw(), tile.basey-h, tile.basey-h, tile.basey-h, tile.basey);
			}
			else {
				renderer.terrain.addWall(tile.x, tile.z, tile.d.cw(), tile.basey, tile.basey-h);
				renderer.terrain.addWall(tile.x, tile.z, tile.d.ccw(), tile.basey-h, tile.basey);
			}
			Template.street.addLamp(tile, renderer, random, -0.5f);
		}
		street.addInstance(new TileObjectInfo(tile, 0, -h, 0));
	}
	
	public static TileTemplate getTemplate(int h) {
		switch(h) {
			case 0:
				return Template.street;
			case 1:
				return template1;
			case 2:
				return template2;
			case 4:
				return template4;
			default:
				throw new RuntimeException("No template for slope "+h);
		}
	}

}
