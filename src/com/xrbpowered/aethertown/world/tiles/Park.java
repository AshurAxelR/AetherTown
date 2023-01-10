package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.env.Seasons;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.texture.Texture;

public class Park extends TileTemplate {

	private static final Seasons grassColor = new Seasons(new Color(0x70a545), new Color(0xf4fcfd));

	private static final float treeRadius = 0.6f*Tile.size;
	private static final float trunkRadius = 0.065f*Tile.size;
	private static final float bushRadius = 0.3f*Tile.size;
	
	public static final Park template = new Park();
	
	public static TileComponent tree, trunk, bush;

	public class ParkTile extends Tile {
		public boolean flex = false;
		
		public ParkTile() {
			super(Park.this);
		}
	}
	
	public Park() {
		super(new Color(0xddeebb));
	}
	
	@Override
	public Tile createTile() {
		return new ParkTile();
	}
	
	private static boolean isFlex(Tile tile) {
		return ((ParkTile) tile).flex;
	}
	
	@Override
	public float getYAt(Tile tile, float sx, float sz) {
		if(isFlex(tile))
			return tile.level.h.gety(tile.x, tile.z, sx, sz);
		else
			return super.getYAt(tile, sx, sz);
	}
	
	@Override
	public int getFenceY(Tile tile, Corner c) {
		if(isFlex(tile))
			return HeightMap.tiley(tile, c);
		else
			return super.getFenceY(tile, c);
	}
	
	@Override
	public void createComponents() {
		tree = new TileComponent(
				BasicGeometry.sphere(treeRadius, 8, -1, ObjectShader.vertexInfo),
				new Seasons(new Color(0x496d00), new Color(0xe0eef1)).texture());
		trunk = new TileComponent(
				BasicGeometry.cylinder(trunkRadius, 4, 1f, -1, ObjectShader.vertexInfo),
				new Texture(new Color(0x665545)));
		bush = new TileComponent(
				BasicGeometry.sphere(bushRadius, 8, -0.5f, ObjectShader.vertexInfo),
				new Seasons(new Color(0x497522), new Color(0xe9f2f4)).texture());
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		if(isFlex(tile)) {
			renderer.terrain.addHillTile(grassColor.color(), tile);
		}
		else {
			renderer.terrain.addWalls(tile);
			renderer.terrain.addFlatTile(grassColor.color(), tile);
			Street.template.addHandrails(tile);
		}
		addTrees(tile, random);
	}
	
	public void addTrees(Tile tile, Random random) {
		if(tile.basey<=-120)
			return;
		float x = tile.x*Tile.size;
		float z = tile.z*Tile.size;
		if(random.nextInt(4)==0 && random.nextInt(60)>-tile.basey) {
			float px = random.nextFloat()*0.5f + 0.25f;
			float pz = random.nextFloat()*0.5f + 0.25f;
			float tx = Tile.size*(px-0.5f);
			float tz = Tile.size*(pz-0.5f);
			float ty = (0.3f+random.nextFloat()*0.4f)*Tile.size;
			float sy = 0.9f+random.nextFloat()*0.4f;
			float y0 = tile.level.gety(tile.x, tile.z, px, pz);
			tree.addInstance(new TileObjectInfo(x+tx, y0+ty, z+tz)
					.scale(0.8f+random.nextFloat()*0.4f, sy));
			trunk.addInstance(new TileObjectInfo(x+tx, y0, z+tz).scale(1f, 0.2f*Tile.size+ty));
		}
		int numBushes = random.nextInt(7) - 3;
		for(int i=0; i<numBushes; i++) {
			if(random.nextInt(120)<-tile.basey)
				continue;
			float px = random.nextFloat();
			float pz = random.nextFloat();
			float tx = Tile.size*(px - 0.5f);
			float tz = Tile.size*(pz - 0.5f);
			float sy = 0.6f+random.nextFloat();
			float s = sy+random.nextFloat()*0.4f;
			float r = bushRadius*s;
			
			if(tx<-Tile.size/2f+r && tile.getAdjT(-1, 0)!=tile.t)
				continue;
			if(tx>Tile.size/2f-r && tile.getAdjT(+1, 0)!=tile.t)
				continue;
			if(tz<-Tile.size/2f+r && tile.getAdjT(0, -1)!=tile.t)
				continue;
			if(tz>Tile.size/2f-r && tile.getAdjT(0, +1)!=tile.t)
				continue;
			
			float y0 = tile.level.gety(tile.x, tile.z, px, pz);
			bush.addInstance(new TileObjectInfo(x+tx, y0, z+tz).scale(s, sy));
		}
	}
	
	@Override
	public boolean finalizeTile(Tile atile, Random random) {
		ParkTile tile = (ParkTile) atile;
		boolean remove = true;
		for(Dir d : Dir.values()) {
			if(d==tile.d.flip())
				continue;
			Tile adj = tile.getAdj(d);
			if(adj!=null && adj.basey>tile.basey-2) {
				remove = false;
				break;
			}
		}
		if(remove) {
			tile.level.map[tile.x][tile.z] = null;
			tile.level.heightLimiter.invalidate();
			return true;
		}
		
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		if(miny>tile.basey) {
			tile.basey = miny;
			return true;
		}
		tile.flex = (miny==tile.basey-1);
		return false;
	}
	
}
