package com.xrbpowered.aethertown.world.gen;

import static java.lang.Math.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;

public class FollowTerrain {

	private static final int maxAtt = 3;
	private static final boolean sortOpts = true;
	private static final boolean useYDir = true;

	private static final boolean storeDebugInfo = false;
	
	public class Result {
		public final int x;
		public final int length;
		public int cost;
		
		public final Result s;
		public final int my;
		public final Result e;
		
		public Result(int x, int y) {
			this.x = x;
			this.length = 1;
			this.s = null;
			this.my = y;
			this.e = null;
		}

		public Result(int sy, Result e) {
			this.x = e.x-1;
			this.length =e.length+1;
			this.s = null;
			this.my = sy;
			this.e = e;
		}

		public Result(Result s, int ey) {
			this.x = s.x;
			this.length = s.length+1;
			this.s = s;
			this.my = ey;
			this.e = null;
		}
		
		public Result(Result s, int my, Result e) {
			this.x = s.x;
			this.length = s.length+e.length+2;
			this.s = s;
			this.my = my;
			this.e = e;
		}
		
		public int gety(int i) {
			if(s!=null && i<s.length)
				return s.gety(i);
			if(e!=null) {
				i = i-(e.x-x);
				if(i>=0)
					return e.gety(i);
			}
			return my;
		}
		
		public String toString() {
			if(s==null && e==null)
				return String.format("[%d]", my);
			else if(s==null)
				return String.format("[%d, %s]", my, e.toString());
			else if(e==null)
				return String.format("[%s, %d]", s.toString(), my);
			else
				return String.format("[%s, %d, %s]", s.toString(), my, e.toString());
		}
	}
	
	private class Head {
		public final int y;
		public final int dy;
		public final int x;
		public final int dx;
		public final int ydir;
		public final int contSlope;

		public Head(int y, int dy, int dx, int ydir) {
			this.y = y;
			this.dy = dy;
			this.dx = dx;
			this.x = dx>0 ? startx : endx;
			this.ydir = ydir;
			this.contSlope = (dy==0) ? 0 : 1;
		}

		public Head(Head e, int dy) {
			this.dx = e.dx;
			this.y = e.y+dy*dx;
			this.dy = dy;
			this.x = e.x + dx;
			this.ydir = e.ydir;
			this.contSlope = (dy==0) ? 0 : e.contSlope+1;
		}
	}
	
	private class DYOption implements Comparable<DYOption> {
		public int dy;
		public int cost;
		
		@Override
		public int compareTo(DYOption o) {
			int res = Integer.compare(cost, o.cost);
			if(res==0)
				res = Integer.compare(abs(dy), abs(o.dy));
			return res;
		}
	}
	
	public final int[] guide;
	public final int startx, starty, startdy;
	public final int endx, endy, enddy;
	public final int length;
	public final int guideAvg;
	
	public long iterations;
	
	public int anchorX, anchorZ;
	public Dir streetDir;
	
	public FollowTerrain(int startx, int starty, int startdy, int endx, int endy, int enddy, int[] guide) {
		this.startx = startx;
		this.starty = starty;
		this.startdy = startdy;
		this.endx = endx;
		this.endy = endy;
		this.enddy = enddy;
		this.guide = guide;
		this.length = endx-startx-1;
		guideAvg = guideAvg(startx+1, endx-1);
	}

	public FollowTerrain(int starty, int startdy, int endy, int enddy, int[] guide) {
		this(-1, starty, startdy, guide.length, endy, enddy, guide);
	}

	public FollowTerrain setAnchor(int x, int z, Dir d) {
		this.anchorX = x;
		this.anchorZ = z;
		this.streetDir = d;
		return this;
	}
	
	private int guideAvg(int sx, int ex) {
		int sum = 0;
		for(int i=sx; i<=ex; i++)
			sum += guide[i];
		return sum/(ex-sx+1);
	}
	
	private static int cost(int g, int y, int dy) {
		int h = abs(dy);
		if(h==0 && y>g+7)
			return y-g-5;
		int ymin = min(y, y+dy);
		int ymax = max(y, y+dy);
		int R = g>ymax+1 ? g-ymax-1 : g<ymin ? ymin-g : 0;
		if(h>1) {
			R = R*2+1;
			h = 6;
		}
		return R*R + h;
	}

	private int calcCost(int x, int y, int dy) {
		return cost(guide[x], y, dy);
	}

	private DYOption[] getOptions(Head e, int... dy) {
		DYOption[] opts = new DYOption[dy.length];
		for(int i=0; i<dy.length; i++) {
			DYOption opt = new DYOption();
			opt.dy = dy[i];
			opt.cost = calcCost(e.x+e.dx, e.y+opt.dy*e.dx, opt.dy);
			opts[i] = opt;
		}
		return opts;
	}

	private DYOption[] getOptions(Head e) {
		if(e.dy==0)
			return useYDir ? getOptions(e, 0, e.ydir, e.ydir*2, e.ydir*4) : getOptions(e, 0, 1, -1, 2, -2, 4, -4);
		else if(e.contSlope<3 || abs(e.dy)==1)
			return getOptions(e, 0, e.dy);
		else
			return getOptions(e, 0);
	}
	
	private int sumCost(DYOption[] opts) {
		int sum = 0;
		for(DYOption opt : opts)
			sum += opt.cost;
		return sum;
	}

	public Result compute1(Head s, Head e) {
		DYOption[] optS = getOptions(s);
		DYOption[] optE = getOptions(e);
		DYOption sel = null;
		int minCost = 0;
		for(DYOption os : optS)
			for(DYOption oe : optE) {
				if(s.y+os.dy==e.y-oe.dy && (os.dy==oe.dy || os.dy==0 || oe.dy==0)) {
					int cost = max(os.cost, oe.cost);
					if(sel==null || cost<minCost) {
						sel = os;
						minCost = cost;
					}
				}
			}
		if(sel==null)
			return null; // common dy not found
		Result res = new Result(s.x+1, s.y+sel.dy);
		res.cost = minCost;
		return res;
	}
	
	public Result computeMax(Head s, Head e) {
		iterations++;
		int L = e.x - s.x - 1;
		if(L==1)
			return compute1(s, e);
		
		int alldy = abs(e.y-s.y);
		if(alldy%4!=0)
			return null; // must be multiple of 4
		
		int dy = e.y>s.y ? 4 : -4;
		DYOption[] optS = alldy>0 && (s.dy==0 || s.dy==dy) ? (e.contSlope<=3 ? getOptions(s, dy, 0) : getOptions(s, 0, dy)) : getOptions(s, 0);
		
		Result min = null;
		DYOption sel = null;
		int minCost = 0;
		for(DYOption opt : optS) {
			Head si = new Head(s, opt.dy);
			Result sub = compute(si, e, false);
			if(sub!=null) {
				int cost = sub.cost+opt.cost;
				if(min==null || cost<minCost) {
					sel = opt;
					min = sub;
					minCost = cost;
					break; // maxAtt=1 for computeMax: use first solution
				}
			}
		}
		if(min==null)
			return null; // all failed
		Result res = new Result(s.y+sel.dy, min);
		res.cost = minCost;
		return res;
	}

	public Result compute(Head s, Head e, boolean preferExpandStart) {
		iterations++;
		int L = e.x - s.x - 1;
		if(L==1)
			return compute1(s, e);
		
		int alldy = abs(e.y-s.y);
		if(alldy>L*4)
			return null; // FAIL
		if(alldy>L*8/3)
			return computeMax(s, e);
		
		DYOption[] optS = getOptions(s);
		int sumS = sumCost(optS);
		DYOption[] optE = getOptions(e);
		int sumE = sumCost(optE);
		
		if(sumS>sumE || sumS==sumE && preferExpandStart) {
			// expand start
			if(sortOpts)
				Arrays.sort(optS);
			Result min = null;
			DYOption sel = null;
			int minCost = 0;
			int att = 1;
			for(DYOption opt : optS) {
				Head si = new Head(s, opt.dy);
				Result sub = compute(si, e, false);
				if(sub!=null) {
					int cost = sub.cost+opt.cost;
					if(min==null || cost<minCost) {
						sel = opt;
						min = sub;
						minCost = cost;
						if(att>=maxAtt)
							break;
					}
				}
				att++;
			}
			if(min==null)
				return null; // all failed
			Result resS = new Result(s.y+sel.dy, min);
			resS.cost = minCost;
			return resS;
		}
		
		else {
			// expand end
			if(sortOpts)
				Arrays.sort(optE);
			Result min = null;
			DYOption sel = null;
			int minCost = 0;
			int att = 1;
			for(DYOption opt : optE) {
				Head ei = new Head(e, opt.dy);
				Result sub = compute(s, ei, true);
				if(sub!=null) {
					int cost = sub.cost+opt.cost;
					if(min==null || cost<minCost) {
						sel = opt;
						min = sub;
						minCost = cost;
						if(att>=maxAtt)
							break;
					}
				}
				att++;
			}
			if(min==null)
				return null; // all failed
			Result resE = new Result(min, e.y-sel.dy);
			resE.cost = minCost;
			return resE;
		}
	}
	
	private Result computeWhole() {
		Head s = new Head(starty, startdy, 1, starty<guideAvg ? 1 : -1);
		Head e = new Head(endy, enddy, -1, endy<guideAvg ? -1 : 1);
		return compute(s, e, true);
	}

	private Result computeSplit(int mx, int my) {
		FollowTerrain fs = new FollowTerrain(startx, starty, startdy, mx, my, 0, guide);
		FollowTerrain fe = new FollowTerrain(mx+1, my, 0, endx, endy, enddy, guide);
		Result rs = fs.compute();
		Result re = fe.compute();
		iterations += fs.iterations + fe.iterations;
		if(rs==null || re==null)
			return null;
		Result res = new Result(rs, my, re);
		res.cost = rs.cost + re.cost + calcCost(mx, my, 0) + calcCost(mx+1, my, 0);
		return res;
	}
	
	private static Result bestOf(Result... rs) {
		Result best = null;
		for(Result r : rs) {
			if(r!=null && (best==null || r.cost<best.cost))
				best = r;
		}
		return best;
	}
	
	public Result compute() {
		iterations = 0;
		if(length>12) {
			int mx = (startx+endx)/2;
			return bestOf(
				computeSplit(mx, guideAvg),
				computeSplit(mx, guideAvg(mx-1, mx+2))
			);
		}
		else {
			return computeWhole();
		}
	}
	
	public void apply(Result res, Level level) {
		if(res==null)
			return;
		Dir d = streetDir;
		Token t = new Token(level, anchorX+d.dx, starty, anchorZ+d.dz, d);
		for(int i=0; i<=res.length; i++) {
			Token ts = t;
			int dy = (i<res.length ? res.gety(i) : endy) - t.y;
			if(dy>0)
				ts = new Token(level, ts.x, ts.y+dy, ts.z, d.flip());
			t = t.next(d, dy);
			
			TileTemplate temp = StreetSlope.getTemplate(abs(dy));
			StreetTile st = (StreetTile) temp.forceGenerate(ts);
			if(storeDebugInfo)
				st.debugFT = this;
		}
	}
	
	public void print(PrintStream out) {
		out.printf("\n[%d, %d]--[%d, %d] %s(%d)\n", 
				anchorX, anchorZ,
				anchorX+(length+2)*streetDir.dx, anchorZ+(length+2)*streetDir.dz,
				streetDir.name(), length);
		out.printf("sy=%d, sdy=%d, ey=%d, edy=%d;\n", starty, startdy, endy, enddy);
		out.printf("g = {%s};\n", String.join(", ", new Iterable<String>() {
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					private int i = 0;
					@Override
					public boolean hasNext() {
						return i<guide.length;
					}
					@Override
					public String next() {
						return Integer.toString(guide[i++]);
					}
				};
			}
		}));
	}
	
	private static FollowTerrain create(Level level, int x, int z, int dist, Dir d) {
		int xs = x-(dist+1)*d.dx;
		int zs = z-(dist+1)*d.dz;
		
		Tile tileS = level.map[xs][zs];
		int starty = tileS.basey;
		int startdy = 0;
		if(tileS.t instanceof StreetSlope) {
			startdy = -((StreetSlope) tileS.t).h;
			if(tileS.d!=d)
				startdy = -startdy;
			else
				starty += startdy;
		}
		
		Tile tileE = level.map[x][z];
		int endy = tileE.basey;
		int enddy = 0;
		if(tileE.t instanceof StreetSlope) {
			enddy = -((StreetSlope) tileE.t).h;
			if(tileE.d!=d) {
				enddy = -enddy;
				endy -= enddy;
			}
		}
		
		int g[] = new int[dist-1];
		int xi = xs;
		int zi = zs;
		int dx0, dz0, dx1, dz1;
		if(d==Dir.east) {
			dx0 = 1;
			dz0 = -1;
			dx1 = 1;
			dz1 = 2;
		}
		else {
			dx0 = -1;
			dz0 = 1;
			dx1 = 2;
			dz1 = 1;
		}
		for(int i=0; i<g.length; i++) {
			xi += d.dx;
			zi += d.dz;
			int g0 = level.h.y[xi+dx0][zi+dz0];
			int g1 = level.h.y[xi+dx1][zi+dz1];
			g[i] = (g0+g1)/2;
		}
		
		FollowTerrain ft = new FollowTerrain(starty, startdy, endy, enddy, g);
		ft.setAnchor(xs, zs, d);
		return ft;
	}
	
	private static boolean isStreet(Level level, Tile tile, Dir d) {
		if(tile==null || !level.isInside(tile.x, tile.z, 2))
			return false;
		if(tile.t!=Street.template && !(tile.t instanceof StreetSlope) || tile.sub!=null)
			return false;
		if(tile.d!=d && tile.d!=d.flip())
			return false;
		
		Tile adjS = tile.getAdj(d.flip());
		if(adjS==null || !Street.isAnyStreet(adjS.t))
			return false;
		Tile adjE = tile.getAdj(d);
		if(adjE==null || !Street.isAnyStreet(adjE.t))
			return false;
		Tile adjL = tile.getAdj(d.ccw());
		if(adjL==null || Street.isAnyStreet(adjL.t) || adjL.t!=Hill.template && adjL.d==d.ccw())
			return false;
		Tile adjR = tile.getAdj(d.cw());
		if(adjR==null || Street.isAnyStreet(adjR.t) || adjR.t!=Hill.template && adjR.d==d.cw())
			return false;
		
		return true;
	}

	public static ArrayList<FollowTerrain> findStreets(Level level) {
		ArrayList<FollowTerrain> fts = new ArrayList<>();
		int[] xd = new int[level.levelSize];
		for(int x=0; x<level.levelSize; x++) {
			int zd = 0;
			for(int z=0; z<level.levelSize; z++) {
				Tile tile = level.map[x][z];
				if(isStreet(level, tile, Dir.east))
					xd[z]++;
				else {
					if(xd[z]>1)
						fts.add(create(level, x, z, xd[z], Dir.east));
					xd[z] = 0;
				}
				if(isStreet(level, tile, Dir.south))
					zd++;
				else {
					if(zd>1)
						fts.add(create(level, x, z, zd, Dir.south));
					zd = 0;
				}
			}
		}
		return fts;
	}

}
