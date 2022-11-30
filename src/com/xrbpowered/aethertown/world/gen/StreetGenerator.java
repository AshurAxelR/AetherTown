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

	private static final int margin = 20;
	private static final WRandom sidew = new WRandom(1.5, 0.2, 0.5, 1); // new WRandom(1, 0.5, 0.25, 1);
	private static final int[] dyopt = {0, -1, 1, -4, 4};
	private static final WRandom dyoptw = new WRandom(0.1, 0.4, 0.4, 0.5, 0.5);

	private int dy, absdy;
	private Integer targetHeight = null;
	private boolean perfectMatch = false;

	private Token startToken;
	private Token endToken = null;
	private Level level;
	private Dir d, dleft, dright;
	private int len;
	private int[] dylist = null;
	
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
			gen.generate(new Token(level, t.x+side.dx, t.y, t.z+side.dz, side), random);
	}
	
	public StreetGenerator(Random random, int prevdy) {
		dy = dyopt[dyoptw.next(random)];
		absdy = Math.abs(dy);
		if(dy*prevdy<0)
			dy = -dy;
	}
	
	public int getDy() {
		return dy;
	}
	
	public boolean isPerfectMatch() {
		return perfectMatch;
	}
	
	private boolean fitLength(Random random) {
		final int maxLen = (absdy>1) ? 2+random.nextInt(4) : 3+random.nextInt(5);
		final int minLen = (absdy>1) ? 2 : 3;
		final Dir[] checkDirs = {dleft, dright};
		
		Token t = startToken;
		for(len=0; len<maxLen+2; len++) { 
			if(!Level.isInside(t.x, t.z, margin)) {
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
				for(int i=1; i<=2; i++) {
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
				if(h>(len-1)) {
					if(h>(len-1)*4 || h%4!=0)
						return null;
					dy = 4*sign;
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
			if(!t.fitsHeight() || !level.fitsHeight(t.x, t.y+this.dy, t.z))
				return null;
			int dy = 0;
			if(i>0 && i<len) {
				int h = targetHeight==null ? 0 : Math.abs(targetHeight-t.y);
				if(h>=len-i-1 || random.nextInt(4)>0)
					dy = this.dy;
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
			if(!temp.generate(ts, random)) {
				System.err.println("Should not happen here!");
				return null;
			}
		}
		endToken = t;
		return tlist;
	}
	
	private void generateSides(Token[] tlist, Random random) {
		for(int i=0; i<len; i++) {
			Token ts = tlist[i];
			int h = Math.abs(dylist[i]);
			if(Math.abs(dy)<2) {
				placeSide(level, ts, dright, random, h);
				placeSide(level, ts, dleft, random, h);
			}
		}
	}
	
	public boolean checkFit(Token startToken, Random random) {
		this.startToken = startToken;
		level = startToken.level;
		d = startToken.d;
		dleft = d.ccw();
		dright = d.cw();
		
		if(!fitLength(random))
			return false;
		
		dylist = planHeight(random);
		return dylist!=null;
	}
	
	@Override
	public boolean generate(Token startToken, Random random) {
		if(dylist==null || this.startToken!=startToken) {
			if(!checkFit(startToken, random))
				return false;
		}
		Token[] tlist = generateStreet(random);
		generateSides(tlist, random);
		
		if(endToken.isFree())
			Template.street.generate(endToken, random);
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
