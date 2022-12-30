package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.TokenProvider;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;

public class StreetGenerator implements Generator, TokenProvider {

	public static final int streetGap = 1;
	private static final int streetMargin = 40; // 96; //20;

	private int dy, absdy;
	private Integer targetHeight = null;
	private boolean perfectMatch = false;

	private Token startToken;
	private Token endToken = null;
	private Level level;
	private Dir d, dleft, dright;
	private int len;
	private int[] dylist = null;
	
	private int margin = streetMargin;
	public boolean generateSides = true;
	
	private static final WRandom sidew = new WRandom(1.5, 0.2, 0.5, 1);

	public static Generator selectSideGenerator(Random random, int h) {
		switch(sidew.next(random)) {
			case 1:
				return Template.park;
			case 2:
				return (h==0) ? new LargeParkGenerator() : null;
			case 3:
				return new HouseGenerator();
			default:
				return null;
		}
	}
	
	public static void placeSide(Level level, Token t, Dir side, Random random, int h) {
		Generator gen = selectSideGenerator(random, h);
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
		dy = 0;
		absdy = 0;
	}
	
	public StreetGenerator(Random random, int prevdy) {
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
				if(tile.t==Template.street) {
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
						if(tile.t==Template.street) {
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
			if(targetHeight==null && (!t.fitsHeight() || !level.fitsHeight(t.x, t.y+this.dy, t.z))) {
				return null;
			}
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
			
			TileTemplate temp = (dylist[i]==0) ? Template.street : slope;
			temp.forceGenerate(ts, random);
		}
		endToken = t;
		return tlist;
	}
	
	private void generateSides(Token[] tlist, Random random) {
		if(!generateSides || Math.abs(dy)>=2)
			return;
		for(int i=0; i<len; i++) {
			Token ts = tlist[i];
			int h = Math.abs(dylist[i]);
			placeSide(level, ts, dright, random, h);
			placeSide(level, ts, dleft, random, h);
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

	@Override
	public boolean generate(Token startToken, Random random) {
		if(dylist==null || this.startToken!=startToken) {
			if(!checkFit(startToken, random, null, null))
				return false;
		}
		Token[] tlist = generateStreet(random);
		generateSides(tlist, random);
		
		if(endToken.isFree())
			Template.street.forceGenerate(endToken, random);
		return true;
	}
	
	@Override
	public void collectTokens(TokenGenerator out, Random random) {
		out.addToken(endToken.next(endToken.d.cw(), 0).setContext(this));
		out.addToken(endToken.next(endToken.d.ccw(), 0).setContext(this));
		if(random.nextInt(3)==0)
			out.addToken(endToken.next(endToken.d, 0).setContext(this));
	}

}
