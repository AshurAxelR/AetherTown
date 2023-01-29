package com.xrbpowered.aethertown.world;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;

import static com.xrbpowered.aethertown.world.tiles.Park.*;

public class TerrainTile extends Tile {

	public static class Tree {
		public float px, pz, ty, s, sy;
		
		public Tree generate(TerrainTile tile, Random random) {
			px = random.nextFloat()*0.5f + 0.25f;
			pz = random.nextFloat()*0.5f + 0.25f;
			ty = (0.3f+random.nextFloat()*0.4f)*Tile.size;
			sy = 0.9f+random.nextFloat()*0.4f;
			s = 0.8f+random.nextFloat()*0.4f;
			return this;
		}
		
		public void addInstance(TerrainTile tile, LevelRenderer r) {
			float x = tile.x*Tile.size;
			float z = tile.z*Tile.size;
			float tx = Tile.size*(px-0.5f);
			float tz = Tile.size*(pz-0.5f);
			float y0 = tile.level.gety(tile.x, tile.z, px, pz);
			tree.addInstance(r, new TileObjectInfo(x+tx, y0+ty, z+tz).scale(s, sy));
			trunk.addInstance(r, new TileObjectInfo(x+tx, y0, z+tz).scale(1f, 0.2f*Tile.size+ty));
		}
	}

	public static class Bush {
		public float px, pz, s, sy;
		
		public Bush generate(TerrainTile tile, Random random) {
			px = random.nextFloat();
			pz = random.nextFloat();
			sy = 0.6f+random.nextFloat();
			s = sy+random.nextFloat()*0.4f;
			
			float r = bushRadius*s;
			float tx = Tile.size*(px - 0.5f);
			float tz = Tile.size*(pz - 0.5f);
			if(tx<-Tile.size/2f+r && tile.getAdjT(-1, 0)!=tile.t)
				return null;
			if(tx>Tile.size/2f-r && tile.getAdjT(+1, 0)!=tile.t)
				return null;
			if(tz<-Tile.size/2f+r && tile.getAdjT(0, -1)!=tile.t)
				return null;
			if(tz>Tile.size/2f-r && tile.getAdjT(0, +1)!=tile.t)
				return null;
			return this;
		}
		
		public void addInstance(TerrainTile tile, LevelRenderer r) {
			float x = tile.x*Tile.size;
			float z = tile.z*Tile.size;
			float tx = Tile.size*(px - 0.5f);
			float tz = Tile.size*(pz - 0.5f);
			float y0 = tile.level.gety(tile.x, tile.z, px, pz);
			bush.addInstance(r, new TileObjectInfo(x+tx, y0, z+tz).scale(s, sy));
		}
	}
	
	public ArrayList<Tree> trees = new ArrayList<>();
	public ArrayList<Bush> bushes = new ArrayList<>();
	
	protected TerrainTile(TileTemplate t) {
		super(t);
	}
	
	public void createTrees(LevelRenderer r) {
		for(Tree tree : trees)
			tree.addInstance(this, r);
		for(Bush bush : bushes)
			bush.addInstance(this, r);
	}
	
	public static void addTrees(TerrainTile tile, Random random) {
		if(tile.basey<=-120)
			return;
		if(random.nextInt(4)==0 && random.nextInt(60)>-tile.basey) {
			Tree tree = new Tree().generate(tile, random);
			if(tree!=null)
				tile.trees.add(tree);
		}
		int numBushes = random.nextInt(7) - 3;
		for(int i=0; i<numBushes; i++) {
			if(random.nextInt(120)<-tile.basey)
			 	continue;
			Bush bush = new Bush().generate(tile, random);
			if(bush!=null)
				tile.bushes.add(bush);
		}
	}
	
}
