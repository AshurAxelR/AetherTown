package com.xrbpowered.aethertown.world;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.gen.HillsGenerator;
import com.xrbpowered.aethertown.world.gen.StreetGenerator;
import com.xrbpowered.aethertown.world.gen.StreetLayoutGenerator;
import com.xrbpowered.aethertown.world.gen.Tunnels;
import com.xrbpowered.aethertown.world.gen.WalkingDistance;
import com.xrbpowered.aethertown.world.gen.plot.ChurchGenerator;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;
import com.xrbpowered.aethertown.world.region.HouseAssignment;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.tiles.HouseT;

public class Level {

	private static final int maxRefillAttempts = 10;
	private static final int maxGeneratorAttempts = 10;
	
	public final LevelInfo info;
	public final int levelSize;
	
	public Tile[][] map;
	public HeightMap h;
	
	public ArrayList<ChurchGenerator> churches = null;
	public ArrayList<HouseGenerator> houses = null;
	public int houseCount = 0;

	// available only during generation
	public HeightGuide heightGuide = null;
	public HeightLimiter heightLimiter = null;
	public ArrayList<PlotGenerator> plots = null;
	public WalkingDistance walkingDist = null;

	private int startx, startz;
	
	public Level(LevelInfo info) {
		this.info = info;
		this.levelSize = info.getLevelSize();
	}
	
	@Override
	public int hashCode() {
		return info.hashCode();
	}
	
	public int getStartX() {
		return startx;
	}

	public int getStartZ() {
		return startz;
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
		walkingDist = new WalkingDistance(this);
		StreetGenerator.defaultStreetMargin = info.settlement.getStreetMargin(levelSize);
	}
	
	private void releaseGenerator() {
		heightLimiter = null;
		heightGuide = null;
		plots = null;
		walkingDist = null;
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
		System.out.printf("Generating... *%04dL:[%d, %d] %dL\n", info.region.seed%10000L, info.x0, info.z0, info.seed);
		Random random = new Random(info.seed); // FIXME Same seed generates different levels regardless of HeightGuide. Why?
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
				System.err.printf("Generation failed: %s\n", e.getMessage());
			}
		}
		throw new RuntimeException("Generator attempts limit reached");
	}

	private void checkHouseCount() {
		if(houseCount<info.settlement.minHouses)
			GeneratorException.raise("Settlement is too small: %d vs %d min for %s",
					houseCount, info.settlement.minHouses, info.settlement.title);
	}
	
	private void generate(Random random) {
		resetGenerator();
		
		startx = levelSize/2;
		startz = levelSize/2;
		Token startToken = new Token(this, startx, info.terrain.starty, startz, Dir.north);
		boolean genStreets = false;
		if(info.settlement.maxHouses>0 || !info.conns.isEmpty()) {
			genStreets = true;
			StreetLayoutGenerator gen = new StreetLayoutGenerator(info.settlement.maxHouses);
			gen.generate(startToken, random);
			startx = gen.startToken.x;
			startz = gen.startToken.z;
			checkHouseCount();
			StreetLayoutGenerator.finishLayout(this, random);
		}
		else {
			info.terrain.startTerrain(startToken, random);
		}

		info.terrain.fillTerrain(this, random);
		
		for(int att=0;; att++) {
			heightLimiter.revalidate();
			if(!HillsGenerator.expand(this, random, 0, 0, -2, 2) && att>0) {
				System.err.println("Failed to get refill tokens on att "+att);
				break;
			}
			
			if(genStreets) {
				genStreets = false;
				h.calculate(true);
				StreetLayoutGenerator.followTerrain(this);
				heightLimiter.revalidate();
			}
			
			if(houses==null) {
				houses = HouseGenerator.listHouses(this, random);
				houseCount = houses.size();
				checkHouseCount();
				HouseAssignment.assignHouses(this, random);
			}
			
			if(finalizeTiles(random))
				break;
			if(att>=maxRefillAttempts-1) {
				System.err.println("Refill attempts limit reached");
				break;
			}
			StreetLayoutGenerator.trimStreets(this, random); // in case of removed plots
		}

		new Tunnels(this).placeTunnels();
		
		if(houses==null || !checkNulls())
			GeneratorException.raise("Level incomplete");

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
				if(tile.t==HouseT.template)
					continue;
				for(Corner c : Corner.values()) {
					int fy = tile.t.getFenceY(tile, c);
					int h = HeightMap.tiley(tile, c);
					if(fy<h)
						GeneratorException.raise("Negative wall: [%d, %d] (%s) %d<%d\n", x, z, c.name(), fy, h);
				}
			}
		boolean upd = true;
		while(upd) {
			upd = false;
			for(int x=0; x<levelSize; x++)
				for(int z=0; z<levelSize; z++) {
					Tile tile = map[x][z];
					upd |= tile.t.postDecorateTile(tile, random);
				}
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
			return tile.t.getYAt(tile, sx, sz, tile.basey+100); // ensure top
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
	
	public static boolean hoverInside(int levelSize, float x, float z) {
		return (x+Tile.size/2)>=0f && (x+Tile.size/2)<=levelSize*Tile.size &&
				(z+Tile.size/2)>=0f && (z+Tile.size/2)<=levelSize*Tile.size;
	}
	
	public static int hover(float cam) {
		return (int)((cam+Tile.size/2)/Tile.size);
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
