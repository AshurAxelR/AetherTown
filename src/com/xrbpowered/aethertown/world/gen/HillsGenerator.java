package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Street;

public class HillsGenerator extends TokenGenerator {

	private static final int edgeBlend = 4;
	
	public int mindy = -4;
	public int maxdy = 4;

	public HillsGenerator() {
		super(0);
	}

	public HillsGenerator(int limit) {
		super(limit);
	}
	
	public HillsGenerator setAmp(int mindy, int maxdy) {
		if(maxdy<mindy)
			throw new IllegalArgumentException();
		this.mindy = mindy;
		this.maxdy = maxdy;
		return this;
	}
	
	@Override
	protected Generator selectGenerator(Token t, Random random) {
		return Hill.template;
	}

	@Override
	protected boolean checkToken(Token t, Random random) {
		Tile tile = t.level.map[t.x][t.z];
		if(tile!=null) {
			if(tile.t==Street.template &&  t.y<tile.basey+4 && tile.getAdj(t.d)==null) {
				t.x = t.x + t.d.dx;
				t.z = t.z + t.d.dz;
				return t.isInside() ? checkToken(t, random) : false;
			}
			else
				return false;
		}

		int edge = t.edgeDist();
		if(edge<edgeBlend) {
			t.y = Math.round(MathUtils.lerp(t.level.heightGuide.gety(t.x, t.z), t.y, edge/(float)edgeBlend));
		}
		
		int miny = t.level.heightLimiter.miny[t.x][t.z];
		int maxy = t.level.heightLimiter.maxy[t.x][t.z];
		if(miny<=maxy) {
			if(t.y>maxy) {
				int lowy = Math.max(miny, maxy - (maxdy-mindy));
				t.y = random.nextInt(maxy-lowy+1) + lowy;
			}
			if(t.y<miny) {
				int hiy = Math.min(maxy, miny + (maxdy-mindy));
				t.y = random.nextInt(hiy-miny+1) + miny;
			}
		}
		else {
			t.y = random.nextInt(miny-maxy+1) + maxy; //(miny+maxy)/2;
		}
		return true;
	}
	
	@Override
	protected void spreadTokens(Token t, Random random) {
		for(Dir d : Dir.values()) {
			addToken(t.next(d, random.nextInt(maxdy-mindy+1)+mindy));
		}
	}
	
	private static Token hillToken(Level level, Random random, int margin, float slope) {
		int x = random.nextInt(level.levelSize-margin*2+1)+margin; 
		int z = random.nextInt(level.levelSize-margin*2+1)+margin;
		if(level.map[x][z]!=null)
			return null;
		
		boolean free = true;
		for(int j=1; j<4; j++)
			for(Dir8 d : Dir8.values()) {
				if(level.map[x+j*d.dx][z+j*d.dz]!=null) {
					free = false;
					break;
				}
			}
		if(!free)
			return null;
		
		int tx = x;
		int tz = z;
		int mid = level.levelSize/2;
		int dist = 0;
		int ty = 0;
		for(;;) {
			if(tx!=mid && random.nextBoolean())
				tx += (tx>mid) ? -1 : 1;
			else
				tz += (tz>mid) ? -1 : 1;
			dist++;
			Tile tile = level.map[tx][tz];
			if(tile!=null) {
				ty = tile.basey;
				break;
			}
		}
		
		int miny = level.heightLimiter.miny[x][z];
		int maxy = level.heightLimiter.maxy[x][z];
		int midy = (miny+maxy)/2;
		int y = (int)(ty + dist*slope);
		if(y>midy)
			y = midy + random.nextInt(maxy-midy+1);
		return new Token(level, x, y, z, Dir.random(random));
	}

	public static void makeHills(Level level, Random random, int count, int limit, float slope) {
		for(int i=0; i<count*level.info.size*level.info.size; i++) {
			HillsGenerator hillsGen = new HillsGenerator(limit).setAmp(-2, 2);
			Token t = hillToken(level, random, 8, slope);
			if(t!=null)
				hillsGen.generate(t, random);
		}
	}

	private static void expandTokens(Level level, HillsGenerator gen, Random random, int skip) {
		for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				if(level.map[x][z]!=null)
					continue;
				Dir adjDir = null;
				boolean expand = false;
				boolean noSkip = false;
				int y = 0;
				for(Dir d : Dir.shuffle(random)) {
					Tile adj = level.getAdj(x, z, d);
					if(adj!=null) {
						adjDir = d;
						expand = adj.t.canExpandFill(adj) || random.nextInt(8)==0;
						noSkip |= adj.t.noSkipExpandFill(adj);
						y = adj.basey;
						if(expand)
							break;
					}
				}
				if(adjDir!=null && (skip==0 || expand) && (noSkip || random.nextInt(skip+1)==0))
					gen.addToken(new Token(level, x, y, z, adjDir));
			}
	}

	public static boolean expand(Level level, Random random, int skip, int limit, int mindy, int maxdy) {
		HillsGenerator hillsGen = new HillsGenerator().setAmp(mindy, maxdy);
		expandTokens(level, hillsGen, random, skip);
		if(hillsGen.tokenCount()>0) {
			hillsGen.limit = hillsGen.tokenCount()*limit;
			hillsGen.generate(random);
			return true;
		}
		else {
			return false;
		}
	}
	
}
