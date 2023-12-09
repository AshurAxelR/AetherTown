package com.xrbpowered.aethertown.world.gen;

import static com.xrbpowered.aethertown.world.gen.StreetGenerator.streetGap;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;

public class StreetConnector {

	private static final int reconnectMargin = 3;
	private static final int zMargin = 5;
	private static final int sidesMargin = 4;
	private static final float ascFactor = 2.67f;
	
	private static int lenForH(int h) {
		return (int)Math.ceil(h/ascFactor);
	}
	
	private class ConnPoint {
		public int i, j;
		public int x, z, basey;
		
		public ConnPoint(int i, int j, int x, int z, int basey) {
			this.i = i;
			this.j = j;
			this.x = x;
			this.z = z;
			this.basey = basey;
		}

		public ConnPoint(int i, int j, int basey) {
			this(i, j, calcx(i, j), calcz(i, j), basey);
		}

		public ConnPoint moveOut(int dj, int dy) {
			return new ConnPoint(i, j-dj, x + dj*dout.dx, z + dj*dout.dz, basey+dy);
		}

		public ConnPoint moveIn(int dj, int dy) {
			return new ConnPoint(i, j+dj, x + dj*din.dx, z + dj*din.dz, basey+dy);
		}

		public int mdist(ConnPoint conn) {
			return MathUtils.mdist(x, z, conn.x, conn.z);
		}
		
		public int mdisty(ConnPoint conn) {
			return MathUtils.mdist(x, z, conn.x, conn.z) + lenForH(Math.abs(basey-conn.basey));
		}
		
		@Override
		public String toString() {
			return String.format("[%d,%d]:%d", x, z, basey);
		}
	}
	
	private class ConnPiece {
		public final Dir d;
		public int x, z;
		public int len, yS, yD;
		
		public Token startToken;
		public StreetGenerator street;
		
		public ConnPiece(Dir d) {
			this.d = d;
		}
		
		public ConnPiece connect(ConnPoint connS, ConnPoint connD) {
			this.x = connS.x+d.dx;
			this.z = connS.z+d.dz;
			this.len = connS.mdist(connD)-1;
			this.yS = connS.basey;
			this.yD = connD.basey;
			return this;
		}
		
		public ConnPiece setStart(ConnPoint conn) {
			this.x = conn.x+d.dx;
			this.z = conn.z+d.dz;
			this.yS = conn.basey;
			return this;
		}

		public ConnPiece setStart(ConnPiece piece) {
			this.x = piece.x + piece.len*piece.d.dx + d.dx;
			this.z = piece.z + piece.len*piece.d.dz + d.dz;
			this.yS = piece.yD;
			return this;
		}

		public ConnPiece setLen(int len) {
			this.len = len;
			return this;
		}
		
		public ConnPiece setTargetY(int y) {
			this.yD = y;
			return this;
		}
		
		public boolean check(Random random) {
			if(len<1)
				return false;
			street = new StreetGenerator().setMargin(margin).setGenerateSides(false);
			street.ignoreHeightLimiter = true; // FIXME remove ignoreHeightLimiter
			startToken = new Token(level, x, yS, z, d);
			return street.checkFit(startToken, random, len, yD);
		}
		
		public void generate(Random random) {
			street.finish(random);
			Token end = street.getEndToken();
			if(end!=null) {
				Tile t = end.tile();
				if(t!=null && t.t==Street.template) {
					if(sides!=null && level.isInside(end.x, end.z, sidesMargin))
						sides.addAllAdj(end);
					((StreetTile) t).forceExpand = true;
				}
			}
		}
	}
	
	public final Level level;
	public final StreetLayoutGenerator sides;
	public final int levelSize;
	public final int margin;
	public final Dir din, dout, dnext;
	
	private int startx, startz;
	private int[] wopen;
	private ArrayList<ConnPoint> connPoints = new ArrayList<>();
	
	public StreetConnector(Level level, Dir d, int margin, StreetLayoutGenerator sides) {
		this.level = level;
		this.sides = sides;
		this.levelSize = level.levelSize;
		this.margin = margin;
		this.din = d.flip();
		this.dout = d;
		this.dnext = d.cw();
		this.startx = d.rightCorner().dx<0 ? 0 : levelSize-1;
		this.startz = d.rightCorner().dz<0 ? 0 : levelSize-1;
		this.wopen = new int[levelSize];
	}

	public StreetConnector(Level level, Dir d, StreetLayoutGenerator sides) {
		this(level, d, reconnectMargin, sides);
	}

	public static boolean isAnyPath(Tile tile) {
		return tile==null ? false : Street.isAnyPath(tile.t);
	}

	public static boolean isAnyPath(Level level, int x, int z) {
		if(!level.isInside(x, z))
			return false;
		else
			return isAnyPath(level.map[x][z]);
	}

	private boolean isAnyPath(int x, int z) {
		return isAnyPath(level, x, z);
	}

	private int calcx(int i, int j) {
		return startx + i*dnext.dx + j*din.dx;
	}

	private int calcz(int i, int j) {
		return startz + i*dnext.dz + j*din.dz;
	}

	private void scanOpen(int margin) {
		connPoints.clear();
		Dir dl = dnext.flip();
		Dir dr = dnext;
		for(int i=0, xi=startx, zi=startz; i<levelSize; i++, xi+=dnext.dx, zi+=dnext.dz) {
			int j=0;
			jloop: for(int xj=xi, zj=zi; j<=levelSize/2; j++, xj+=din.dx, zj+=din.dz) {
				Tile tile = level.map[xj][zj];
				if(tile!=null) {
					if(tile.t==Street.template && i>=margin && i<levelSize-margin &&
						(tile.sub==null || tile.sub.parent.canConnect(tile, dout))) { 
							connPoints.add(new ConnPoint(i, j, tile.x, tile.z, tile.basey));
					}
					if(isAnyPath(tile))
						j -= streetGap;
					break;
				}
				for(int k=1; k<=streetGap; k++) {
					if(isAnyPath(xj+k*dl.dx, zj+k*dl.dz) || isAnyPath(xj+k*dr.dx, zj+k*dr.dz)) {
						break jloop;
					}
				}
			}
			j--;
			wopen[i] = j;
		}
	}

	private boolean makeConnection(ConnPiece[] pieces, Random random) {
		if(pieces==null)
			return false;
		for(ConnPiece p : pieces) {
			if(p!=null)
				p.generate(random);
		}
		return true;
	}

	private int calcOutJ(ConnPoint connS, ConnPoint connD, int max, int addj) {
		int outj = max;
		int i0 = Math.min(connS.i, connD.i);
		int i1 = Math.max(connS.i, connD.i);
		for(int i=i0; i<=i1; i++) {
			if(wopen[i]<outj)
				outj = wopen[i];
		}
		outj -= addj;
		return outj;
	}
	
	private ConnPiece[] prepareUConnection(ConnPoint connS, ConnPoint connD, int addj, Random random) {
		int outj = calcOutJ(connS, connD, Math.min(connS.j, connD.j)-1, addj);
		if(outj<=margin)
			return null;
		
		int lenS = connS.j - outj - 1;
		int lenOut = connD.i - connS.i - 1;
		int lenD = connD.j - outj - 1;
		int len = lenS + lenOut + lenD;
		if(len>=20)
			return null;
		if(len>11 && (lenD>(lenS+lenOut)*2 || lenS>(lenD+lenOut)*2))
			return null;
		
		int hsign = (connS.basey>connD.basey) ? -1 : 1;
		int hdiff = Math.abs(connS.basey - connD.basey);
		int dy = 1;
		if(hdiff>len/2) {
			
			int hmod = hdiff % 4;
			if(hmod>0) {
				int djS, djD;
				int dj0 = hmod/2;
				int dj1 = hmod - dj0;
				if((connS.j<connD.j) ^ (dj0<dj1)) {
					djS = dj0;
					djD = dj1;
				}
				else {
					djS = dj1;
					djD = dj0;
				}
				ConnPoint connAdjS = djS==0 ? connS : connS.moveOut(djS+1, djS*hsign);
				ConnPoint connAdjD = djD==0 ? connD : connD.moveOut(djD+1, -djD*hsign);
				
				addj = addj-Math.min(djS, djD)-1;
				if(addj<0) addj = 0;
				ConnPiece[] pieces = prepareUConnection(connAdjS, connAdjD, addj, random);
				if(pieces==null)
					return null;
				
				ConnPiece pieceAdjS = (connS!=connAdjS) ? new ConnPiece(dout).connect(connS, connAdjS) : null;
				ConnPiece pieceAdjD = (connD!=connAdjD) ? new ConnPiece(din).connect(connAdjD, connD) : null;
				if(pieceAdjS!=null && !pieceAdjS.check(random) || pieceAdjD!=null && !pieceAdjD.check(random))
					return null;
				
				return new ConnPiece[] {pieceAdjS, pieces[0], pieces[1], pieces[2], pieceAdjD};
			}
			
			dy = 4;
			int lenH = lenForH(hdiff);
			if(lenH>=30)
				return null;
			if(lenH>len) {
				int add = (lenH-len+1)/2;
				lenS += add;
				lenD += add;
				len += add*2;
				outj -= add;
				if(outj<=margin)
					return null;
			}
		}
		if(lenS-lenOut>7 || lenD-lenOut>7)
			return null;
		
		int hS = (int)Math.ceil(hdiff*(lenS/(float)len)/dy)*dy;
		int hD = (int)Math.ceil(hdiff*((lenS+lenOut)/(float)len)/dy)*dy;
		
		ConnPiece pieceS = new ConnPiece(dout).setLen(lenS).setStart(connS).setTargetY(connS.basey+hS*hsign);
		if(!pieceS.check(random))
			return null;
		ConnPiece pieceOut = new ConnPiece(dnext).setLen(lenOut).setStart(pieceS).setTargetY(connS.basey+hD*hsign);
		if(!pieceOut.check(random))
			return prepareUConnection(connS, connD, addj+1, random);
		ConnPiece pieceD = new ConnPiece(din).setLen(lenD).setStart(pieceOut).setTargetY(connD.basey);
		if(!pieceD.check(random))
			return null;
		
		return new ConnPiece[] {pieceS, pieceOut, pieceD};
	}

	private boolean makeUConnection(ConnPoint connS, ConnPoint connD, Random random) {
		return makeConnection(prepareUConnection(connS, connD, 0, random), random);
	}
	
	private ConnPiece[] prepareZConnection(ConnPoint connS, ConnPoint connD, int addj, Random random) {
		int outj = calcOutJ(connS, connD, connS.j-1, addj);
		if(outj<=zMargin+addj)
			return null;
		outj -= random.nextInt(outj-zMargin-addj)/2;
		
		int lenS = connS.j - outj - 1;
		int lenOut = Math.abs(connD.i - connS.i) - 1;
		int lenD = outj - connD.j - 1;
		int len = lenS + lenOut + lenD;
		
		int hsign = (connS.basey>connD.basey) ? -1 : 1;
		int hdiff = Math.abs(connS.basey - connD.basey);
		int dy = 1;
		if(hdiff>len/2) {
			
			int hmod = hdiff % 4;
			if(hmod>0) {
				int djS, djD;
				int dj0 = hmod/2;
				int dj1 = hmod - dj0;
				if((connS.j<connD.j) ^ (dj0<dj1)) {
					djS = dj0;
					djD = dj1;
				}
				else {
					djS = dj1;
					djD = dj0;
				}
				ConnPoint connAdjS = djS==0 ? connS : connS.moveOut(djS+1, djS*hsign);
				ConnPoint connAdjD = djD==0 ? connD : connD.moveIn(djD+1, -djD*hsign);
				
				addj = addj-Math.min(djS, djD)-1;
				if(addj<0) addj = 0;
				ConnPiece[] pieces = prepareZConnection(connAdjS, connAdjD, addj, random);
				if(pieces==null)
					return null;
				
				ConnPiece pieceAdjS = (connS!=connAdjS) ? new ConnPiece(dout).connect(connS, connAdjS) : null;
				ConnPiece pieceAdjD = (connD!=connAdjD) ? new ConnPiece(dout).connect(connAdjD, connD) : null;
				if(pieceAdjS!=null && !pieceAdjS.check(random) || pieceAdjD!=null && !pieceAdjD.check(random))
					return null;
				
				ConnPiece[] allPieces = new ConnPiece[pieces.length+2];
				allPieces[0] = pieceAdjS;
				for(int i=0; i<pieces.length; i++)
					allPieces[i+1] = pieces[i];
				allPieces[allPieces.length-1] = pieceAdjD;
				return allPieces;
			}
			
			dy = 4;
			int lenH = lenForH(hdiff);
			if(lenH>=len)
				return null;
		}
		
		int hS = (int)Math.ceil(hdiff*(lenS/(float)len)/dy)*dy;
		int hD = (int)Math.ceil(hdiff*((lenS+lenOut)/(float)len)/dy)*dy;
		
		ConnPiece pieceS = new ConnPiece(dout).setLen(lenS).setStart(connS).setTargetY(connS.basey+hS*hsign);
		if(!pieceS.check(random))
			return null;
		Dir d = connD.i > connS.i ? dnext : dnext.flip();
		if(lenOut>0) {
			ConnPiece pieceOut = new ConnPiece(d).setLen(lenOut).setStart(pieceS).setTargetY(connS.basey+hD*hsign);
			if(!pieceOut.check(random))
				return prepareUConnection(connS, connD, addj+1, random);
			ConnPiece pieceD = new ConnPiece(dout).setLen(lenD).setStart(pieceOut).setTargetY(connD.basey);
			if(!pieceD.check(random))
				return null;
			return new ConnPiece[] {pieceS, pieceOut, pieceD};
		}
		else {
			ConnPiece pieceD = new ConnPiece(dout).setLen(lenD).setStart(pieceS).setTargetY(connD.basey);
			if(!pieceD.check(random))
				return null;
			return new ConnPiece[] {pieceS, pieceD};
		}
	}
	
	private boolean makeZConnection(ConnPoint connS, ConnPoint connD, Random random) {
		return makeConnection(prepareZConnection(connS, connD, 0, random), random);
	}
	
	private boolean makeMultiZConnection(ConnPoint connS, ConnPoint connD, Random random) {
		int hsign = (connS.basey>connD.basey) ? -1 : 1;
		int hdiff = Math.abs(connS.basey - connD.basey);
		int hmod = hdiff % 4;
		
		int tj = 10 + random.nextInt(10);
		if(hmod>0)
			tj += 6;
		int j = connS.j-tj;
		
		if(j > connD.j-10) {
			for(int att=0; att<20;) {
				int i = (connS.i + connD.i)/2 + random.nextInt(15) - 7;
				if(i>=zMargin && i<levelSize-zMargin &&
						i!=connS.i-1 && i!=connS.i+1 &&
						i!=connD.i-1 && i!=connD.i+1) {
					int distS = MathUtils.mdist(i, connS.i, j, connS.j) - 3;
					int distD = MathUtils.mdist(i, connD.i, j, connD.j) - 3;
					int ahdiff = (hdiff - hmod) / 4;
					int dy = (ahdiff * distS / (distS+distD))*4 + hmod;
					if(dy<=distS/2 && (hdiff-dy)<=distD/2)
						 dy += (random.nextInt(5)-2)*4;
					ConnPoint conn = new ConnPoint(i, j, connS.basey + dy * hsign);
					
					ConnPiece[] pieces = prepareZConnection(connS, conn, 0, random);
					if(pieces!=null && makeMultiZConnection(conn, connD, random)) {
						makeConnection(pieces, random);
						return true;
					}
					else {
						att++;
					}
				}
			}
			return false;
		}
		else {
			return makeZConnection(connS, connD, random);
		}
	}
	
	public boolean connectAll(Random random) {
		scanOpen(margin);
		boolean upd = false;
		int iblock = 0;
		for(int p=1; p<connPoints.size()-1; p++) {
			ConnPoint conn = connPoints.get(p);
			if(conn.i<iblock+streetGap)
				continue;
			ConnPoint connL = connPoints.get(p-1);
			ConnPoint connR = connPoints.get(p+1);
			int mdistL = conn.mdisty(connL);
			int mdistR = conn.mdisty(connR);
			if(mdistL<3 && mdistR<3)
				continue;
			int maxWdist = MathUtils.max(mdistL, mdistR, 12);
			level.walkingDist.calculate(conn.x, conn.z, maxWdist);
			int wdistL = level.walkingDist.map[connL.x][connL.z]; 
			int wdistR = level.walkingDist.map[connR.x][connR.z]; 
			if(connL.i>=iblock+streetGap && wdistL>10 && wdistL>mdistL && conn.mdist(connL)<20 && wdistL>=wdistR) {
				if(makeUConnection(connL, conn, random)) {
					iblock = conn.i;
					upd = true;
				}
			}
			else if(wdistR>10 && wdistR>mdistR && conn.mdist(connR)<20 && wdistR>wdistL) {
				if(makeUConnection(conn, connR, random)) {
					iblock = connR.i;
					upd = true;
				}
			}
		}
		return upd;
	}
	
	public boolean connectOut(LevelConnection lc, Random random, boolean multi) {
		ConnPoint connOut = new ConnPoint(lc.getLevelI(), 0, lc.getY());
		scanOpen(zMargin);
		boolean res = false;
		while(!res) {
			ConnPoint connS = null;
			int minCost = level.levelSize*30;
			for(int p=0; p<connPoints.size(); p++) {
				ConnPoint conn = connPoints.get(p);
				int dj = Math.abs(conn.j - connOut.j);
				if(dj<3)
					continue;
				int cost = conn.mdisty(connOut); // * Math.abs(conn.i - connOut.i) / dj;
				if(conn.i==connOut.i+1 || conn.i==connOut.i-1)
					continue;
				if(cost<minCost) {
					connS = conn;
					minCost = cost;
				}
			}
			if(connS==null) {
				if(multi)
					return connectOut(lc, random, false);
				else
					return false;
			}
			if(multi)
				res = makeMultiZConnection(connS, connOut, random);
			else
				res = makeZConnection(connS, connOut, random);
			if(!res)
				connPoints.remove(connS);
		}
		return true;
	}

}
