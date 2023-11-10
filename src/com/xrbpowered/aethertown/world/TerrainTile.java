package com.xrbpowered.aethertown.world;

import static com.xrbpowered.aethertown.world.tiles.Park.*;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.tiles.ScaledTileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;

public class TerrainTile extends Tile {

	public static final float treeRadius = 2.4f;
	public static final float pineRadius = 1.4f;
	public static final float bushRadius = 1.2f;
	
	public static class Tree {
		public float px, pz, ty, s, sy;
		
		public Tree generate(TerrainTile tile, Random random) {
			px = random.nextFloat()*0.5f + 0.25f;
			pz = random.nextFloat()*0.5f + 0.25f;
			ty = 1.4f+random.nextFloat()*1.8f;
			s = 0.8f+random.nextFloat()*0.5f;
			sy = s*(0.9f+random.nextFloat()*0.4f);
			return this;
		}
		
		public void addInstance(TerrainTile tile, LevelRenderer r) {
			float x = tile.x*size;
			float z = tile.z*size;
			float tx = size*(px-0.5f);
			float tz = size*(pz-0.5f);
			float y0 = tile.level.gety(tile.x, tile.z, px, pz);
			tree.addInstance(r, new ScaledTileObjectInfo(x+tx, y0+ty, z+tz).scale(treeRadius*s, treeRadius*sy));
			trunk.addInstance(r, new ScaledTileObjectInfo(x+tx, y0-0.2f, z+tz).scale(s*1f, 0.8f+ty));
		}
	}

	public static class ForestTree extends Tree {
		public int n;
		
		public ForestTree(int n) {
			this.n = n;
		}
		
		public Tree generate(TerrainTile tile, Random random) {
			px = random.nextFloat()*0.8f + 0.1f;
			pz = random.nextFloat()*0.8f + 0.1f;
			ty = 1.6f+0.3f*n+random.nextFloat()*(2f+0.5f*n);
			s = 0.8f-0.1f*n+random.nextFloat()*0.5f;
			sy = s*(1.1f+random.nextFloat()*0.8f);
			return this;
		}
	}
	
	public static class PineTree extends Tree {
		public Tree generate(TerrainTile tile, Random random) {
			px = random.nextFloat()*0.4f + 0.3f;
			pz = random.nextFloat()*0.4f + 0.3f;
			ty = 0.3f+random.nextFloat()*0.7f;
			s = 0.8f+random.nextFloat()*1.0f;
			sy = s*(2.8f+random.nextFloat()*2.2f);
			return this;
		}
		
		public void addInstance(TerrainTile tile, LevelRenderer r) {
			float x = tile.x*size;
			float z = tile.z*size;
			float tx = size*(px-0.5f);
			float tz = size*(pz-0.5f);
			float y0 = tile.level.gety(tile.x, tile.z, px, pz);
			pine.addInstance(r, new ScaledTileObjectInfo(x+tx, y0+ty, z+tz).scale(pineRadius*s, pineRadius*sy));
			trunk.addInstance(r, new ScaledTileObjectInfo(x+tx, y0-0.2f, z+tz).scale(s*0.8f, 0.3f*pineRadius*sy+ty));
		}
	}

	public static class ForestPineTree extends PineTree {
		public int n;
		
		public ForestPineTree(int n) {
			this.n = n;
		}
		
		public Tree generate(TerrainTile tile, Random random) {
			px = random.nextFloat()*0.8f + 0.1f;
			pz = random.nextFloat()*0.8f + 0.1f;
			ty = 1.3f+0.8f*(n-1)+random.nextFloat()*(1.8f+0.5f*n);
			s = 1f-0.05f*n+random.nextFloat()*0.6f;
			sy = s*(3f+random.nextFloat()*1.6f);
			return this;
		}
	}


	public static class TallTree extends Tree {
		public float[] a = new float[3];
		public float[] bh = new float[3];
		public float[] bs = new float[3];
		public float[] bd = new float[3];
		
		@Override
		public Tree generate(TerrainTile tile, Random random) {
			super.generate(tile, random);
			ty = 5f+random.nextFloat()*2f;
			a[0] = random.nextFloat();
			a[1] = a[0]+0.3f+0.1f*random.nextFloat();
			a[2] = a[1]+0.3f+0.1f*random.nextFloat();
			for(int i=0; i<a.length; i++) {
				bh[i] = random.nextFloat()*0.05f;
				bs[i] = 0.8f+random.nextFloat()*0.25f;
				bd[i] = random.nextFloat()*0.1f;
			}
			return this;
		}
		
		private void addBranch(LevelRenderer r, float x, float y, float z, float s, float a, float dist) {
			tree.addInstance(r, new ScaledTileObjectInfo(x+dist*4f*(float)Math.cos(a*Math.PI*2.0), y, z-dist*4f*(float)Math.sin(a*Math.PI*2.0)).scale(treeRadius*s, treeRadius*s));
		}
		
		@Override
		public void addInstance(TerrainTile tile, LevelRenderer r) {
			float x = tile.x*size;
			float z = tile.z*size;
			float tx = size*(px-0.5f);
			float tz = size*(pz-0.5f);
			float y0 = tile.level.gety(tile.x, tile.z, px, pz);
			
			tree.addInstance(r, new ScaledTileObjectInfo(x+tx, y0+ty, z+tz).scale(s*treeRadius*1.1f, s*treeRadius*1.1f));
			addBranch(r, x+tx, y0+ty*0.45f+bh[0], z+tz, s*bs[0], a[0], 0.3f+bd[0]);
			addBranch(r, x+tx, y0+ty*0.5f+bh[1], z+tz, s*bs[1], a[1], 0.4f+bd[1]);
			addBranch(r, x+tx, y0+ty*0.65f+bh[2], z+tz, s*bs[2], a[2], 0.5f+bd[2]);
			trunk.addInstance(r, new ScaledTileObjectInfo(x+tx, y0-0.2f, z+tz).scale(s*1.1f, 0.8f+ty));
		}
	}
	
	public static class Bush {
		public float px, pz, s, sy;
		
		public Bush generate(TerrainTile tile, Random random) {
			px = random.nextFloat();
			pz = random.nextFloat();
			sy = 0.6f+random.nextFloat();
			s = sy+random.nextFloat()*0.3f;
			
			float r = bushRadius*s*0.75f;
			float tx = size*(px - 0.5f);
			float tz = size*(pz - 0.5f);
			if(tx<-size/2f+r && tile.getAdjT(-1, 0)!=tile.t)
				return null;
			if(tx>size/2f-r && tile.getAdjT(+1, 0)!=tile.t)
				return null;
			if(tz<-size/2f+r && tile.getAdjT(0, -1)!=tile.t)
				return null;
			if(tz>size/2f-r && tile.getAdjT(0, +1)!=tile.t)
				return null;
			return this;
		}
		
		public void addInstance(TerrainTile tile, LevelRenderer r) {
			float x = tile.x*size;
			float z = tile.z*size;
			float tx = size*(px - 0.5f);
			float tz = size*(pz - 0.5f);
			float y0 = tile.level.gety(tile.x, tile.z, px, pz);
			bush.addInstance(r, new ScaledTileObjectInfo(x+tx, y0, z+tz).scale(bushRadius*s, bushRadius*sy));
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
	
	private static Tree randomTree(Random random, boolean forest, int numTrees, int blockh, boolean wilderness) {
		if(forest) {
			if(wilderness && random.nextFloat() < 0.75f)
				return new ForestPineTree(numTrees);
			else
				return new ForestTree(numTrees);
		}
		if(wilderness && random.nextFloat() < 0.85f)
			return new PineTree();
		if(!wilderness && blockh<3 && random.nextFloat() < 0.2f)
			return new PineTree();
		if(random.nextFloat() < 0.3f)
			return new TallTree();
		return new Tree();
	}
	
	public static void addTrees(TerrainTile tile, Random random) {
		if(tile.basey<=-120)
			return;
		boolean isHill = (tile.t==Hill.template);
		
		boolean adjStreet = false;
		boolean adjPark = false;
		int adjTrees = 0;
		boolean wilderness = false;
		wilderness = isHill;
		for(Dir d : Dir.values()) {
			Tile adj = tile.getAdj(d);
			if(adj==null || (adj instanceof TerrainTile && !((TerrainTile) adj).trees.isEmpty()))
				adjTrees++;
			if(adj!=null && adj.t!=Hill.template) {
				wilderness = false;
				if(Street.isAnyPath(adj.t) || (adj.t instanceof Plaza))
					adjStreet = true;
				if(adj.t==Park.template)
					adjPark = true;
			}
		}
		
		boolean hasPine = false;
		if(random.nextFloat()<0.7f && random.nextInt(100)>-tile.basey) {
			boolean forest = isHill && random.nextFloat()<0.3f*adjTrees;
			int blockh = tile.getAdjBlockY()-tile.basey;
			if(blockh<6 || forest && blockh<9) {
				int numTrees = !forest ? 1 : random.nextInt(3)+1;
				for(int i=0; i<numTrees; i++) {
					Tree tree = randomTree(random, forest, numTrees, blockh, wilderness).generate(tile, random);
					if(tree!=null) {
						if(tree instanceof PineTree) {
							hasPine = true;
							i++;
						}
						tile.trees.add(tree);
					}
				}
			}
		}
		
		int numBushes = random.nextInt(6) - 2;
		if(hasPine)
			numBushes -= 2;
		else if(adjStreet) {
			numBushes += random.nextInt(4);
			if(isHill && (adjTrees>0 || !tile.trees.isEmpty()))
				numBushes += 2;
		}
		else if(isHill && adjPark) {
			numBushes += random.nextInt(4);
		}
		numBushes = Math.min(numBushes, 4-tile.trees.size());
		
		for(int i=0; i<numBushes; i++) {
			if(wilderness && random.nextInt(120)<-tile.basey)
			 	continue;
			Bush bush = new Bush().generate(tile, random);
			if(bush!=null)
				tile.bushes.add(bush);
		}
	}
	
}
