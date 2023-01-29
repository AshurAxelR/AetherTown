package com.xrbpowered.aethertown.world;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.gen.HillsGenerator;
import com.xrbpowered.aethertown.world.gen.StreetLayoutGenerator;
import com.xrbpowered.aethertown.world.gen.plot.ChurchGenerator;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;
import com.xrbpowered.aethertown.world.region.HouseAssignment;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelNames;

public class Level {

	public final LevelInfo info;
	public final int levelSize;
	
	private Random random; // TODO remove: after decorate() refactoring
	public Tile[][] map;
	public HeightMap h;
	
	public ArrayList<ChurchGenerator> churches = null;
	public ArrayList<HouseGenerator> houses = null;
	public int houseCount = 0;
	public String name;

	// available only during generation
	public HeightLimiter heightLimiter = null;
	public ArrayList<PlotGenerator> plots = null;

	public Level(LevelInfo info) {
		this.info = info;
		this.levelSize = info.getLevelSize();
		this.map = new Tile[levelSize][levelSize];
		this.h = new HeightMap(this);
	}
	
	public int getStartX() {
		return levelSize/2;
	}

	public int getStartZ() {
		return levelSize/2;
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

	public void generate() {
		generate(new Random(info.seed));
	}

	protected void generate(Random random) {
		this.random = random;
		heightLimiter = new HeightLimiter(this);
		plots = new ArrayList<>();
		houseCount = 0;
		churches = new ArrayList<>();
		new StreetLayoutGenerator(60).generate(new Token(this, getStartX(), 20, getStartZ(), Dir.north), random);
		StreetLayoutGenerator.finishLayout(this, random);
		
		HillsGenerator.expand(this, random, 5, 15, -2, 2);
		 HillsGenerator.expand(this, random, 5, 25, -2, 4);
		HillsGenerator.expand(this, random, 0, 0, -8, 2);
		
		int att = 0;
		int maxAtt = 10;
		for(; att<maxAtt; att++) {
			heightLimiter.revalidate();
			if(!HillsGenerator.expand(this, random, 0, 0, -2, 2) && att>0) {
				System.err.println("Failed to get refill tokens on att "+att);
				break;
			}
			if(finalizeTiles(random))
				break;
			StreetLayoutGenerator.trimStreets(this, random); // in case of removed plots
		}
		System.out.printf("Completed %d refill cycles\n", att+1);
		if(att>=maxAtt)
			System.err.println("Refill attempts limit reached");
		
		plots = null;
		houses = HouseGenerator.listHouses(this, random);
		houseCount = houses.size();
		name = LevelNames.next(random, houseCount);
		HouseAssignment.assignHouses(this, random);
		heightLimiter = null;
		
		// TODO decorate (vs createGeometry)
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
				Color c = (tile==null) ? null : tile.t.minimapColor;
				if(c!=null) {
					g.setColor(c);
					g.fillRect(x*tileSize, z*tileSize, tileSize, tileSize);
				}
			}
	}
	
	public float gety(int x, int z, float sx, float sz, float y0) {
		if(!isInside(x, z))
			return 0;
		Tile tile = map[x][z];
		if(tile!=null)
			return tile.t.getYAt(tile, sx, sz, y0);
		else
			return h.gety(x, z, sx, sz);
	}

	public float gety(int x, int z, float sx, float sz) {
		if(!isInside(x, z))
			return 0;
		Tile tile = map[x][z];
		if(tile!=null)
			return tile.t.getYAt(tile, sx, sz, tile.basey);
		else
			return h.gety(x, z, sx, sz);
	}

	public float gety(float x, float y0, float z) {
		int tx = (int)((x+Tile.size/2)/Tile.size);
		int tz = (int)((z+Tile.size/2)/Tile.size);
		float sx = (x+Tile.size/2 - tx*Tile.size)/Tile.size;
		float sz = (z+Tile.size/2 - tz*Tile.size)/Tile.size;
		return gety(tx, tz, sx, sz, y0);
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
	
	public boolean overlapsHeight(int x, int y, int z, int dy) {
		int miny = heightLimiter.miny[x][z];
		int maxy = heightLimiter.maxy[x][z];
		if(miny<=maxy)
			return y+dy>=miny && y-dy<=maxy;
		else
			return y+dy>=maxy && y-dy<=miny;
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
