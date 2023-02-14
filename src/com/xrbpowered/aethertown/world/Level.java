package com.xrbpowered.aethertown.world;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.gen.HillsGenerator;
import com.xrbpowered.aethertown.world.gen.StreetGenerator;
import com.xrbpowered.aethertown.world.gen.StreetLayoutGenerator;
import com.xrbpowered.aethertown.world.gen.plot.ChurchGenerator;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;
import com.xrbpowered.aethertown.world.region.HouseAssignment;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelNames;

public class Level {

	private static final int maxRefillAttempts = 10;
	private static final int maxGeneratorAttempts = 3;
	
	public final LevelInfo info;
	public final int levelSize;
	
	public Tile[][] map;
	public HeightMap h;
	
	public ArrayList<ChurchGenerator> churches = null;
	public ArrayList<HouseGenerator> houses = null;
	public int houseCount = 0;
	public String name;

	// available only during generation
	public HeightGuide heightGuide = null;
	public HeightLimiter heightLimiter = null;
	public ArrayList<PlotGenerator> plots = null;

	public Level(LevelInfo info) {
		this.info = info;
		this.levelSize = info.getLevelSize();
	}
	
	@Override
	public int hashCode() {
		return info.hashCode();
	}
	
	public int getStartX() {
		return levelSize/2;
	}

	public int getStartZ() {
		return levelSize/2;
	}

	private void resetGenerator() {
		map = new Tile[levelSize][levelSize];
		h = new HeightMap(this);
		houses = null;
		churches = new ArrayList<>();
		houseCount = 0;
		heightGuide = new HeightGuide(info).generate();
		heightLimiter = new HeightLimiter(this);
		plots = new ArrayList<>();
		StreetGenerator.defaultStreetMargin = info.settlement.getStreetMargin(levelSize);
	}
	
	private void releaseGenerator() {
		heightLimiter = null;
		// heightGuide = null;
		plots = null;
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
		System.out.printf("Generating... %dL\n", info.seed);
		Random random = new Random(info.seed);
		for(int att = 0; att<maxGeneratorAttempts; att++) {
			if(att>0)
				System.out.printf("Retrying...\nAttempt #%d\n", att+1);
			try {
				generate(random);
				System.gc();
				System.out.println("Done.");
				return;
			}
			catch (GeneratorException e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("Generator attempts limit reached");
	}

	private void generate(Random random) {
		resetGenerator();
		
		Token startToken = new Token(this, getStartX(), info.terrain.starty, getStartZ(), Dir.north);
		if(info.settlement.maxHouses>0 || !info.conns.isEmpty()) {
			new StreetLayoutGenerator(info.settlement.maxHouses).generate(startToken, random);
			if(houseCount<info.settlement.minHouses)
				throw new GeneratorException("Settlement is too small");
			StreetLayoutGenerator.finishLayout(this, random);
		}
		else {
			new HillsGenerator(20).generate(startToken, random);
		}
		
		HillsGenerator.expand(this, random, 5, 15, -2, 2);
		HillsGenerator.expand(this, random, 5, 25, -2, 4);
		HillsGenerator.expand(this, random, 1, 0, -8, 2);
		// HillsGenerator.expand(this, random, 1, 0, -4, 2);
		
		int att = 0;
		for(;; att++) {
			heightLimiter.revalidate();
			if(!HillsGenerator.expand(this, random, 0, 0, -2, 2) && att>0) {
				System.err.println("Failed to get refill tokens on att "+att);
				break;
			}
			if(finalizeTiles(random))
				break;
			if(att>=maxRefillAttempts-1) {
				System.err.println("Refill attempts limit reached");
				break;
			}
			StreetLayoutGenerator.trimStreets(this, random); // in case of removed plots
		}
		System.out.printf("Completed %d refill cycles\n", att+1);
		if(!checkNulls())
			throw new GeneratorException("Level incomplete");
		
		houses = HouseGenerator.listHouses(this, random);
		houseCount = houses.size();
		name = LevelNames.next(random, info.settlement);
		HouseAssignment.assignHouses(this, random);
		
		decorate(random);
		
		releaseGenerator();
	}

	private boolean checkNulls() {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				if(map[x][z]==null) {
					System.err.printf("null tile at [%d, %d]\n", x, z);
					return false;
				}
			}
		return true;
	}
	
	private void decorate(Random random) {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				Tile tile = map[x][z];
				tile.t.decorateTile(tile, random);
			}
	}

	public void createGeometry(LevelRenderer renderer) {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++) {
				Tile tile = map[x][z];
				tile.t.createGeometry(tile, renderer);
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
		return isInside(levelSize, x, z, 0);
	}
	
	public boolean isInside(int x, int z, int margin) {
		return isInside(levelSize, x, z, margin);
	}
	
	public static boolean isInside(int levelSize, int x, int z, int margin) {
		return x>=margin && x<levelSize-margin && z>=margin && z<levelSize-margin;
	}

	public static int edgeDist(int levelSize, int x) {
		return x<levelSize/2 ? x : levelSize-1-x;
	}

	public static int edgeDist(int levelSize, int x, int z) {
		return Math.min(edgeDist(levelSize, x), edgeDist(levelSize, z));
	}

}
