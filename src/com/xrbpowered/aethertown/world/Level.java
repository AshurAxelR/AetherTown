package com.xrbpowered.aethertown.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.gen.HillsGenerator;
import com.xrbpowered.aethertown.world.gen.StreetLayoutGenerator;

public class Level {

	public final int levelSize;
	
	private Random random;
	public Tile[][] map;
	public HeightLimiter heightLimiter = null;
	public HeightMap h;
	
	public Level(int size) {
		this.levelSize = size;
		this.map = new Tile[levelSize][levelSize];
		this.h = new HeightMap(this);
	}
	
	public int getStartX() {
		return levelSize/2;
	}

	public int getStartZ() {
		return levelSize/2;
	}

	private void expandTokens(HillsGenerator gen, Random random, int skip) {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				if(map[x][z]!=null)
					continue;
				boolean hasAdj = false;
				Dir d = Dir.random(random);
				int y = 0;
				for(int di=0; di<4; di++) {
					Tile adj = getAdj(x, z, d);
					if(adj!=null) {
						hasAdj = true;
						y = adj.basey;
						break;
					}
					d = d.cw();
				}
				if(hasAdj && random.nextInt(skip+1)==0)
					gen.addToken(new Token(this, x, y, z, Dir.north));
			}
	}
	
	private boolean finalizeTiles(Random random) {
		boolean upd = true;
		boolean refill = false;
		while(upd) {
			h.calculate(true);
			upd = false;
			for(int x=0; x<levelSize; x++)
				for(int z=0; z<levelSize; z++) {
					Tile tile = map[x][z];
					if(tile!=null)
						upd |= tile.t.finalizeTile(tile, random);
					else
						refill = true;
				}
		}
		return !refill;
	}
	
	public void generate(Random random) {
		this.random = random;
		heightLimiter = new HeightLimiter(this);
		new StreetLayoutGenerator(0).generate(new Token(this, getStartX(), 20, getStartZ(), Dir.north), random);
		
		HillsGenerator hillsGen = new HillsGenerator(0).setAmp(-2, 2);
		expandTokens(hillsGen, random, 10);
		hillsGen.limit = hillsGen.tokenCount()*30;
		hillsGen.generate(random);

		hillsGen = new HillsGenerator(0).setAmp(-2, 4);
		expandTokens(hillsGen, random, 10);
		hillsGen.limit = hillsGen.tokenCount()*50;
		hillsGen.generate(random);

		int att = 0;
		int maxAtt = 50;
		for(; att<maxAtt; att++) {
			hillsGen = new HillsGenerator(0).setAmp(-8, 2);
			expandTokens(hillsGen, random, 0);
			if(hillsGen.tokenCount()>0) {
				hillsGen.generate(random);
			}
			else if(att>0) {
				System.err.println("Failed to get refill tokens on att "+att);
				break;
			}
			if(finalizeTiles(random))
				break;
		}
		if(att>=maxAtt)
			System.err.println("Refill attempts limit reached");
		
		heightLimiter = null;
	}
	
	public void createGeometry(LevelRenderer renderer) {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				Tile tile = map[x][z];
				if(tile!=null)
					tile.t.createGeometry(tile, renderer, random);
				else {
					System.err.printf("null tile at [%d, %d]\n", x, z);
				}
			}
	}
	
	public void drawMinimap(Graphics2D g, int tileSize) {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				Tile tile = map[x][z];
				Color c = (tile==null) ? Color.WHITE : tile.t.minimapColor;
				g.setColor(c);
				g.fillRect(x*tileSize, z*tileSize, tileSize, tileSize);
			}
	}
	
	public float gety(int x, int z, float sx, float sz) {
		if(!isInside(x, z))
			return 0;
		Tile tile = map[x][z];
		if(tile!=null)
			return tile.t.gety(tile, sx, sz);
		else
			return h.gety(x, z, sx, sz);
	}
	
	public float gety(float x, float z) {
		int tx = (int)((x+Tile.size/2)/Tile.size);
		int tz = (int)((z+Tile.size/2)/Tile.size);
		float sx = (x+Tile.size/2 - tx*Tile.size)/Tile.size;
		float sz = (z+Tile.size/2 - tz*Tile.size)/Tile.size;
		return gety(tx, tz, sx, sz);
	}
	
	public Tile getAdj(int tx, int tz, Dir d) {
		int x = tx + d.dx;
		int z = tz + d.dz;
		return isInside(x, z) ? map[x][z] : null; 
	}
	
	public boolean fits(int x, int y, int z) {
		return isInside(x, z) && fitsHeight(x, y, z);
	}
	
	public boolean fitsHeight(int x, int y, int z, boolean strict) {
		int miny = heightLimiter.miny[x][z];
		int maxy = heightLimiter.maxy[x][z];
		if(miny<=maxy)
			return y>=miny && y<=maxy;
		else if(!strict)
			return y>=maxy && y<=miny;
		else
			return false;
	}

	public boolean fitsHeight(int x, int y, int z) {
		return fitsHeight(x, y, z, true);
	}

	public boolean isInside(int x, int z) {
		return x>=0 && x<levelSize && z>=0 && z<levelSize;
	}
	
	public boolean isInside(int x, int z, int margin) {
		return x>=margin && x<levelSize-margin && z>=margin && z<levelSize-margin;
	}
	
}
