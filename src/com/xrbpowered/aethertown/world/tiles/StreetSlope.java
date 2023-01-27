package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class StreetSlope extends TileTemplate {

	public static final StreetSlope template1 = new StreetSlope(1);
	public static final StreetSlope template2 = new StreetSlope(2);
	public static final StreetSlope template4 = new StreetSlope(4);

	public final int h;
	private TileComponent street, side, handrailL, handrailR;
	
	public StreetSlope(int h) {
		super(Street.streetColor);
		this.h = h;
	}
	
	@Override
	public Tile createTile() {
		return new StreetTile(this);
	}
	
	@Override
	public float getYAt(Tile tile, float sx, float sz, float prevy) {
		if(((StreetTile)tile).bridge && Bridge.isUnder(prevy, tile.basey-h))
			return tile.level.h.gety(tile.x, tile.z, sx, sz);

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
	public int getFenceY(Tile tile, Corner c) {
		c = c.rotate(tile.d); // FIXME wtf, Corner.rotate not working?
		if(tile.d==Dir.north || tile.d==Dir.south)  
			return (c==Corner.ne || c==Corner.nw) ? tile.basey-h : tile.basey;
		else
			return (c==Corner.ne || c==Corner.nw) ? tile.basey : tile.basey-h;
	}

	@Override
	public boolean canExpandFill(Tile tile) {
		return false;
	}
	
	@Override
	public void createComponents() {
		Texture handrailTex = new Texture("models/fences/handrail.png", false, true, false);
		if(h==4) {
			street = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs4.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(Street.streetColor));
			side = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs4side.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(TerrainBuilder.wallColor));
			handrailL = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s4l.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
			handrailR = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s4r.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
		}
		else if(h==2) {
			street = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs2.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(Street.streetColor));
			side = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs2side.obj", 0, 1f, ObjectShader.vertexInfo, null),
					new Texture(TerrainBuilder.wallColor));
			handrailL = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s2l.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
			handrailR = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s2r.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
		}
		else {
			street = new TileComponent(
					BasicGeometry.slope(Tile.size, Tile.ysize*h, ObjectShader.vertexInfo, null),
					new Texture(Street.streetColor));
			handrailL = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s1l.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
			handrailR = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s1r.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
		}
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer r, Random random) {
		if(h>1) {
			side.addInstance(r, new TileObjectInfo(tile, 0, -h, 0));
			if(!Street.template.addAutoHillBridge((StreetTile)tile, tile.basey-h, r))
				r.terrain.addWalls(tile.x, tile.z, tile.basey-h);
			// FIXME when needed slope handrails 
			Dir dl = tile.d.ccw();
			Dir dr = tile.d.cw();
			if(Street.needsHandrail(tile, dl)) {
				handrailL.addInstance(r, new TileObjectInfo(tile).rotate(dl));
				Street.addHandrailPoles(r, tile, dl, -h, 0);
				Street.handrailPole.addInstance(r, new TileObjectInfo(tile, 0.5f*dl.dx+(0.5f-0.1875f*h)*tile.d.dx, 0, 0.5f*dl.dz+(0.5f-0.1875f*h)*tile.d.dz));
			}
			if(Street.needsHandrail(tile, dr)) {
				handrailR.addInstance(r, new TileObjectInfo(tile).rotate(dr));
				Street.addHandrailPoles(r, tile, dr, 0, -h);
				Street.handrailPole.addInstance(r, new TileObjectInfo(tile, 0.5f*dr.dx+(0.5f-0.1875f*h)*tile.d.dx, 0, 0.5f*dr.dz+(0.5f-0.1875f*h)*tile.d.dz));
			}
		}
		else {
			Dir dl = tile.d.ccw();
			Dir dr = tile.d.cw();
			if(Street.template.addAutoHillBridge((StreetTile)tile, tile.basey-h, r)) {
				r.terrain.addWall(tile.x, tile.z, dr, tile.basey-h, tile.basey, tile.basey-h, tile.basey-h);
				r.terrain.addWall(tile.x, tile.z, dl, tile.basey-h, tile.basey-h, tile.basey-h, tile.basey);
			}
			else {
				r.terrain.addWall(tile.x, tile.z, dr, tile.basey, tile.basey-h);
				r.terrain.addWall(tile.x, tile.z, dl, tile.basey-h, tile.basey);
			}
			if(Street.needsHandrail(tile, dl)) {
				handrailL.addInstance(r, new TileObjectInfo(tile).rotate(dl));
				Street.addHandrailPoles(r, tile, dl, -1, 0);
			}
			if(Street.needsHandrail(tile, dr)) {
				handrailR.addInstance(r, new TileObjectInfo(tile).rotate(dr));
				Street.addHandrailPoles(r, tile, dr, 0, -1);
			}
			Street.template.addLamp(tile, r, random, -0.5f);
		}
		street.addInstance(r, new TileObjectInfo(tile, 0, -h, 0));
	}
	
	public static TileTemplate getTemplate(int h) {
		switch(h) {
			case 0:
				return Street.template;
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
