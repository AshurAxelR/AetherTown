package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
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
	protected boolean checkToken(Token t) {
		Tile tile = t.level.map[t.x][t.z];
		if(tile!=null) {
			if(tile.t==Street.template &&  t.y<tile.basey+4 && tile.getAdj(t.d)==null) {
				t.x = t.x + t.d.dx;
				t.z = t.z + t.d.dz;
				return t.isInside() ? checkToken(t) : false;
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
			if(t.y>maxy)
				t.y = maxy;
			if(t.y<miny)
				t.y = miny;
		}
		else {
			t.y = (miny+maxy)/2;
		}
		return true;
	}
	
	@Override
	protected void spreadTokens(Token t, Random random) {
		for(Dir d : Dir.values()) {
			addToken(t.next(d, random.nextInt(maxdy-mindy+1)+mindy));
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
