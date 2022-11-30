package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class StreetSlope extends TileTemplate {

	public static final StreetSlope template1 = new StreetSlope(1);
	public static final StreetSlope template4 = new StreetSlope(4);

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
		else {
			return MathUtils.lerp(y0, y1, s);
		}
	}

	@Override
	public void createComponents() {
		if(h==4) {
			street = new TileComponent(
					ObjMeshLoader.loadObj("stairs4.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(Street.streetColor));
			side = new TileComponent(
					ObjMeshLoader.loadObj("stairs4side.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(TerrainBuilder.wallColor));
		}
		else {
			street = new TileComponent(
					BasicGeometry.slope(Tile.size, Tile.ysize*h, ObjectShader.vertexInfo, null),
					new Texture(Street.streetColor));
		}
	}

	@Override
	public void createGeometry(Tile tile, TerrainBuilder terrain, Random random) {
		if(h==4) {
			side.addInstance(new TileObjectInfo(tile, 0, -h, 0));
			terrain.addWalls(tile.x, tile.z, tile.basey-h);
		}
		else {
			terrain.addWall(tile.x, tile.z, tile.d.cw(), tile.basey, tile.basey-h);
			terrain.addWall(tile.x, tile.z, tile.d.ccw(), tile.basey-h, tile.basey);
		}
		street.addInstance(new TileObjectInfo(tile, 0, -h, 0));
	}
	
	public static StreetSlope getTemplate(int h) {
		switch(h) {
			case 1:
				return template1;
			case 4:
				return template4;
			default:
				return null;
		}
	}

}
