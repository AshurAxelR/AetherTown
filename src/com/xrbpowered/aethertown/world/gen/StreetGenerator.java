package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TokenProvider;
import com.xrbpowered.aethertown.world.gen.plot.BridgePresetGenerator;
import com.xrbpowered.aethertown.world.gen.plot.Crossroads;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.LargeParkGenerator;
import com.xrbpowered.aethertown.world.tiles.Bench;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;

public class StreetGenerator implements Generator, TokenProvider {

	public static final int streetGap = 1;

	public static int defaultStreetMargin = 20;

	private int dy, absdy;
	private Integer targetHeight = null;
	private boolean perfectMatch = false;

	public Token startToken;
	private Token endToken = null;
	private Level level;
	private Dir d, dleft, dright;
	private int len;
	public int[] dylist = null;
	
	private int margin = defaultStreetMargin;
	public boolean generateSides = true;
	public boolean ignoreHeightLimiter = false;
	
	private static final WRandom sidew = new WRandom(1.2, 0.2, 0.2, 0.1, 1, 0.3);

	public static Generator selectSideGenerator(Level level, WRandom w, Random random, int h) {
		switch(w.next(random)) {
			case 1:
				return Park.template;
			case 2:
				return (h==0) ? new LargeParkGenerator(false) : null;
			case 3:
				return (h==0) ? new LargeParkGenerator(true) : null;
			case 4:
				if(level.info.settlement.maxHouses>0)
					return HouseGenerator.select(level, h);
				else
					return null;
			case 5:
				return (h==0) ? Bench.templatePark : null;
			default:
				return null;
		}
	}

	public static void placeSide(Level level, Token t, Dir side, WRandom w, Random random, int h) {
		if(h>1)
			return;
		Generator gen = selectSideGenerator(level, w, random, h);
		if(gen!=null)
			gen.generate(t.next(side, 0), random);
	}
	
	public static int getPrevDy(Token t) {
		if(t.context instanceof StreetGenerator)
			return ((StreetGenerator) t.context).getDy();
		else
			return 0;
	}
	
	private static final int[] dyopt = {0, -1, 1, -2, 2, -4, 4};
	private static final WRandom dyoptw = new WRandom(0.1, 0.4, 0.4, 0.3, 0.3, 0.2, 0.2);
	
	public StreetGenerator() {
		margin = defaultStreetMargin;
		dy = 0;
		absdy = 0;
	}
	
	public StreetGenerator(Random random, int prevdy) {
		margin = defaultStreetMargin;
		dy = dyopt[dyoptw.next(random)];
		absdy = Math.abs(dy);
		if(dy*prevdy<0)
			dy = -dy;
	}
	
	public StreetGenerator setMargin(int margin) {
		this.margin = margin;
		return this;
	}
	
	public StreetGenerator setGenerateSides(boolean generateSides) {
		this.generateSides = generateSides;
		return this;
	}
	
	public int getDy() {
		return dy;
	}
	
	public boolean isPerfectMatch() {
		return perfectMatch;
	}
	
	public Token getEndToken() {
		return endToken;
	}
	
	private boolean fitLength(Random random, Integer targetLength) {
		int maxLen = (absdy>1) ? 2+random.nextInt(4) : 3+random.nextInt(5);
		int minLen = (absdy>1) ? streetGap : 3;
		int checkLen = maxLen+2;
		if(targetLength!=null) {
			maxLen = targetLength+1;
			minLen = targetLength;
			checkLen = maxLen;
		}
		final Dir[] checkDirs = {dleft, dright};
		
		Token t = startToken;
		for(len=0; len<checkLen; len++) { 
			if(!level.isInside(t.x, t.z, margin)) {
				len--;
				if(len>=maxLen)
					len = maxLen-1;
				return len>=minLen;
			}
			
			Tile tile = level.map[t.x][t.z];
			if(tile!=null) {
				if(tile.t==Street.template) {
					if(len>=maxLen) {
						len -= 4;
						return len>=minLen;
					}
					else {
						targetHeight = tile.basey;
						return len>0;
					}
				}
				else if(tile.t instanceof StreetSlope) {
					len -= 4;
					return len>=minLen;
				}
				else {
					len--;
					if(len>=maxLen)
						len = maxLen-1;
					return len>=minLen;
				}
			}
			
			for(Dir cd : checkDirs) {
				boolean free = true;
				for(int i=1; i<=streetGap; i++) {
					tile = Tile.getAdj(level, t.x, t.z, i*cd.dx, i*cd.dz);
					if(tile!=null) {
						if(tile.t==Street.template) {
							if(len>=maxLen || !free) {
								len -= 4;
								return len>=minLen;
							}
							else {
								targetHeight = tile.basey;
								return len>minLen;
							}
						}
						else {
							free = false;
						}
					}
				}
			}
			
			t = t.next(d, 0);
		}
		if(len>=maxLen)
			len = maxLen-1;
		return true;
	}
	
	private static final int[] planOpt = {4, 2};

	private int[] planHeight(Random random) {
		int maxContSlope = absdy>1 ? 2 : 3;
		if(targetHeight!=null) {
			maxContSlope = len;
			if(targetHeight==startToken.y) {
				dy = 0;
			}
			else {
				int sign = (targetHeight>startToken.y) ? 1 : -1;
				int h = Math.abs(targetHeight-startToken.y);
				if(h>len) {
					dy = 0;
					for(int dh : planOpt) {
						if(!(h>len*dh || h%dh!=0)) {
							dy = dh*sign;
							break;
						}
					}
					if(dy==0) {
						return null;
					}
				}
				else {
					dy = sign;
				}
			}
		}
		absdy = Math.abs(dy);
		
		int[] dylist = new int[len];
		int contSlope = 0;
		Token t = startToken;
		for(int i=0; i<=len; i++) {
			if(!ignoreHeightLimiter && targetHeight==null && (!t.fitsHeight() || !level.fitsHeight(t.x, t.y+this.dy, t.z)))
				return null;
			if(!ignoreHeightLimiter && targetHeight!=null && !level.overlapsHeight(t.x, t.y, t.z, HeightLimiter.maxWall))
				return null;
			int dy = 0;
			if(i<len) {
				if(targetHeight==null) {
					if(random.nextInt(3)>0)
						dy = this.dy;
				}
				else if(t.y!=targetHeight) {
					float dh = Math.abs(targetHeight-t.y)/(float)(len-1-i);
					if(absdy<=1 && dh-random.nextFloat()*0.25f>0.5f || dh>0.67f*absdy)
						dy = this.dy;
				}
			}
			if(dy!=0) {
				contSlope++;
				if(contSlope>maxContSlope)
					dy = 0;
			}
			if(dy==0)
				contSlope = 0;
			if(i<len) {
				dylist[i] = dy;
				t = t.next(d, dy);
			}
		}
		if(targetHeight!=null) {
			if(t.y!=targetHeight)
				return null;
			else
				perfectMatch = true;
		}
		return dylist;
	}
	
	private Token[] generateStreet(Random random) {
		TileTemplate slope = StreetSlope.getTemplate(absdy);
		Token t = startToken;
		Token[] tlist = new Token[len];
		for(int i=0; i<len; i++) {
			Token ts = t;
			if(dylist[i]>0)
				ts = new Token(level, ts.x, ts.y+dylist[i], ts.z, d.flip());
			tlist[i] = ts;
			t = t.next(d, dylist[i]);
			
			TileTemplate temp = (dylist[i]==0) ? Street.template : slope;
			temp.forceGenerate(ts);
			//StreetTile tile = (StreetTile) temp.forceGenerate(ts);
			//if(absdy>1 && dylist[i]==0)
			//	tile.lamp = true;
		}
		endToken = t;
		return tlist;
	}
	
	public void generateSide(Dir dside, Token[] tlist, WRandom w, Random random) {
		//if(Math.abs(dy)>=2)
		//	return;
		for(int i=0; i<len; i++) {
			Token ts = tlist[i];
			int h = Math.abs(dylist[i]);
			placeSide(level, ts, dside, w, random, h);
		}
	}
	
	public boolean checkFit(Token startToken, Random random, Integer targetLength, Integer targetHeight) {
		this.startToken = startToken;
		level = startToken.level;
		d = startToken.d;
		dleft = d.ccw();
		dright = d.cw();
		this.targetHeight = targetHeight;
		
		if(!fitLength(random, targetLength))
			return false;
		if(targetLength!=null && len!=targetLength)
			return false;
		
		dylist = planHeight(random);
		return dylist!=null;
	}

	public boolean checkFit(Token startToken, Random random) {
		return checkFit(startToken, random, null, null);
	}

	public Token[] finish(Random random) {
		Token[] tlist = generateStreet(random);
		if(generateSides && absdy<2) {
			generateSide(dright, tlist, sidew, random);
			generateSide(dleft, tlist, sidew, random);
		}
		if(endToken.isFree()) {
			StreetTile tile = (StreetTile) Street.template.forceGenerate(endToken);
			tile.lamp = true;
		}
		return tlist;
	}

	@Override
	public boolean generate(Token startToken, Random random) {
		if(dylist==null || this.startToken!=startToken) {
			if(!checkFit(startToken, random, null, null))
				return false;
		}
		finish(random);
		return true;
	}
	
	private static final WRandom wcross = new WRandom(0.2, 0.4, 0.4);
	
	@Override
	public void collectTokens(TokenGenerator out, Random random) {
		if(dylist[len-1]==0 && (len<2 || dylist[len-2]==0)) {
			switch(wcross.next(random)) {
				case 0:
					out.addToken(endToken.next(endToken.d, 0).setGenerator(new Crossroads()));
					return;
				case 1:
					out.addToken(endToken.next(endToken.d, 0).setGenerator(new BridgePresetGenerator()));
					return;
				default:
			}
		}
		out.addToken(endToken.next(endToken.d.cw(), 0).setContext(this));
		out.addToken(endToken.next(endToken.d.ccw(), 0).setContext(this));
		if(random.nextInt(3)==0)
			out.addToken(endToken.next(endToken.d, 0).setContext(this));
	}

}
