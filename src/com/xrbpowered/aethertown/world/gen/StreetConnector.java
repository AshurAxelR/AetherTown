package com.xrbpowered.aethertown.world.gen;

import static com.xrbpowered.aethertown.world.gen.StreetGenerator.streetGap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.tiles.Street;

public class StreetConnector {

	private static final int reconnectMargin = 3;
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
			return Math.abs(x-conn.x) + Math.abs(z-conn.z);
		}
		
		public int mdisty(ConnPoint conn) {
			return Math.abs(x-conn.x) + Math.abs(z-conn.z) + lenForH(Math.abs(basey-conn.basey));
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
			street = new StreetGenerator().setMargin(margin).setGenerateSides(false);
			street.ignoreHeightLimiter = true; // FIXME remove ignoreHeightLimiter
			startToken = new Token(level, x, yS, z, d);
			return street.checkFit(startToken, random, len, yD);
		}
		
		public void generate(Random random) {
			street.finish(random);
		}
	}
	
	private class DistToken {
		public int x, z, dist;
		public DistToken(int x, int z, int dist) {
			this.x = x;
			this.z = z;
			this.dist = dist;
		}
	}
	
	public void calcWDist(int x0, int z0, int[][] wmap, int maxDist) {
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++)
				wmap[x][z] = maxDist+1;
		LinkedList<DistToken> tokens = new LinkedList<>();
		tokens.add(new DistToken(x0, z0, 0));
		while(!tokens.isEmpty()) {
			DistToken t = tokens.removeFirst();
			if(!isAnyPath(t.x, t.z))
				continue;
			if(t.dist>=wmap[t.x][t.z])
				continue;
			wmap[t.x][t.z] = t.dist;
			for(Dir d : Dir.values()) {
				tokens.add(new DistToken(t.x+d.dx, t.z+d.dz, t.dist+1));
			}
		}
	}
	
	public final Level level;
	public final int levelSize;
	public final int margin;
	public final Dir din, dout, dnext;
	
	private int startx, startz;
	private int[] wopen;
	private ArrayList<ConnPoint> connPoints = new ArrayList<>();
	
	public StreetConnector(Level level, Dir d, int margin) {
		this.level = level;
		this.levelSize = level.levelSize;
		this.margin = margin;
		this.din = d.flip();
		this.dout = d;
		this.dnext = d.cw();
		this.startx = d.rightCorner().dx<0 ? 0 : levelSize-1;
		this.startz = d.rightCorner().dz<0 ? 0 : levelSize-1;
		this.wopen = new int[levelSize];
	}

	public StreetConnector(Level level, Dir d) {
		this(level, d, reconnectMargin);
	}

	private static boolean isAnyPath(Tile tile) {
		return tile==null ? false : Street.isAnyPath(tile.t);
	}

	private boolean isAnyPath(int x, int z) {
		if(!level.isInside(x, z))
			return false;
		else
			return isAnyPath(level.map[x][z]);
	}

	private int calcx(int i, int j) {
		return startx + i*dnext.dx + j*din.dx;
	}

	private int calcz(int i, int j) {
		return startz + i*dnext.dz + j*din.dz;
	}

	private void scanOpen() {
		Dir dl = dnext.flip();
		Dir dr = dnext;
		for(int i=0, xi=startx, zi=startz; i<levelSize; i++, xi+=dnext.dx, zi+=dnext.dz) {
			int j=0;
			jloop: for(int xj=xi, zj=zi; j<levelSize; j++, xj+=din.dx, zj+=din.dz) {
				Tile tile = level.map[xj][zj];
				if(tile!=null) {
					if(tile.t==Street.template && i>=margin && i<levelSize-margin && j<level.levelSize/2 &&
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
		ConnPiece[] pieces = prepareUConnection(connS, connD, 0, random);
		if(pieces==null)
			return false;
		for(ConnPiece p : pieces) {
			if(p!=null)
				p.generate(random);
		}
		return true;
	}
	
	private ConnPiece[] prepareZConnection(ConnPoint connS, ConnPoint connD, int addj, Random random) {
		int outj = calcOutJ(connS, connD, connS.j-1, addj);
		if(outj<=margin+addj) // Z vs U
			return null;
		outj -= random.nextInt(outj-margin-addj)/2; // Z vs U
		
		int lenS = connS.j - outj - 1;
		int lenOut = Math.abs(connD.i - connS.i) - 1; // Z vs U
		int lenD = outj - connD.j - 1; // Z vs U
		int len = lenS + lenOut + lenD;
		// Z vs U
		
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
				ConnPoint connAdjD = djD==0 ? connD : connD.moveIn(djD+1, -djD*hsign); // Z vs U
				
				addj = addj-Math.min(djS, djD)-1;
				if(addj<0) addj = 0;
				ConnPiece[] pieces = prepareUConnection(connAdjS, connAdjD, addj, random);
				if(pieces==null)
					return null;
				
				ConnPiece pieceAdjS = (connS!=connAdjS) ? new ConnPiece(dout).connect(connS, connAdjS) : null;
				ConnPiece pieceAdjD = (connD!=connAdjD) ? new ConnPiece(dout).connect(connAdjD, connD) : null;
				if(pieceAdjS!=null && !pieceAdjS.check(random) || pieceAdjD!=null && !pieceAdjD.check(random))
					return null;
				
				return new ConnPiece[] {pieceAdjS, pieces[0], pieces[1], pieces[2], pieceAdjD};
			}
			
			dy = 4;
			int lenH = lenForH(hdiff);
			if(lenH>=len) // Z vs U
				return null;
		}
		// Z vs U
		
		int hS = (int)Math.ceil(hdiff*(lenS/(float)len)/dy)*dy;
		int hD = (int)Math.ceil(hdiff*((lenS+lenOut)/(float)len)/dy)*dy;
		
		ConnPiece pieceS = new ConnPiece(dout).setLen(lenS).setStart(connS).setTargetY(connS.basey+hS*hsign);
		if(!pieceS.check(random))
			return null;
		Dir d = connD.i > connS.i ? dnext : dnext.flip(); // Z vs U
		ConnPiece pieceOut = new ConnPiece(d).setLen(lenOut).setStart(pieceS).setTargetY(connS.basey+hD*hsign);
		if(!pieceOut.check(random))
			return prepareUConnection(connS, connD, addj+1, random);
		ConnPiece pieceD = new ConnPiece(dout).setLen(lenD).setStart(pieceOut).setTargetY(connD.basey);
		if(!pieceD.check(random))
			return null;
		
		return new ConnPiece[] {pieceS, pieceOut, pieceD};
	}
	
	private boolean makeZConnection(ConnPoint connS, ConnPoint connD, Random random) {
		ConnPiece[] pieces = prepareZConnection(connS, connD, 0, random);
		if(pieces==null)
			return false;
		for(ConnPiece p : pieces) {
			if(p!=null)
				p.generate(random);
		}
		return true;
	}
	
	public boolean connectAll(Random random) {
		scanOpen();
		boolean upd = false;
		int iblock = 0;
		int[][] wdistMap = new int[levelSize][levelSize];
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
			calcWDist(conn.x, conn.z, wdistMap, maxWdist);
			int wdistL = wdistMap[connL.x][connL.z]; 
			int wdistR = wdistMap[connR.x][connR.z]; 
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
	
	public boolean connectOut(LevelConnection lc, Random random) {
		ConnPoint connOut = new ConnPoint(lc.getLevelI(), 0, lc.getY());
		scanOpen();
		boolean res = false;
		while(!res) {
			ConnPoint connS = null;
			int minDist = level.levelSize*3;
			for(int p=1; p<connPoints.size()-1; p++) {
				ConnPoint conn = connPoints.get(p);
				int mdist = conn.mdisty(connOut);
				if(Math.abs(conn.i - connOut.i)<2)
					continue;
				if(mdist<minDist) {
					connS = conn;
					minDist = mdist;
				}
			}
			if(connS==null)
				return false;
			res = makeZConnection(connS, connOut, random);
			if(!res)
				connPoints.remove(connS);
		}
		return true;
	}

}
