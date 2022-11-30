package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.env.Seasons;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.texture.Texture;

public class Park extends TileTemplate {

	private static final Seasons grassColor = new Seasons(new Color(0x70a545), new Color(0xf4fcfd));

	private static final float treeRadius = 0.6f*Tile.size;
	private static final float trunkRadius = 0.065f*Tile.size;
	private static final float bushRadius = 0.3f*Tile.size;
	
	public static TileComponent grass, tree, trunk, bush;

	public Park() {
		super(grassColor.color());
	}
	
	@Override
	public void createComponents() {
		grass = new TileComponent(
				FastMeshBuilder.plane(Tile.size, 1, 1, ObjectShader.vertexInfo, null),
				grassColor.texture());
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
	public void createGeometry(Tile tile, TerrainBuilder terrain, Random random) {
		terrain.addWalls(tile);
		grass.addInstance(new TileObjectInfo(tile));
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
	
}
