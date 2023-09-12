package com.xrbpowered.aethertools;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.util.Arrays;

public class FollowTerrain {

	public static final int maxAtt = 3;
	public static final boolean sortOpts = true;
	
	public class Edge {
		public final int y;
		public final int dy;
		public final int i;
		public final int di;
		public final int dir;
		public final int contSlope;

		public Edge(int y, int dy, int di, int dir) {
			this.y = y;
			this.dy = di*dy;
			this.di = di;
			this.i = di>0 ? -1 : guide.length;
			this.dir = dir;
			this.contSlope = (dy==0) ? 0 : 1;
		}

		public Edge(Edge e, int dy) {
			this.di = e.di;
			this.y = e.y+dy*di;
			this.dy = dy;
			this.i = e.i + di;
			this.dir = e.dir;
			this.contSlope = (dy==0) ? 0 : e.contSlope+1;
		}
		
		@Override
		public String toString() {
			return String.format("[%d] y=%d,dy=%+d", i, y, dy);
		}
	}
	
	public class Result {
		public final int[] y;
		public int cost;
		
		public Result(int y) {
			this.y = new int[] {y};
		}

		public Result(int sy, int[] ey) {
			this.y = new int[ey.length+1];
			this.y[0] = sy;
			for(int i=0; i<ey.length; i++)
				this.y[i+1] = ey[i];
		}

		public Result(int[] sy, int ey) {
			this.y = new int[sy.length+1];
			for(int i=0; i<sy.length; i++)
				this.y[i] = sy[i];
			this.y[sy.length] = ey;
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
	
	public int[] guide;
	public int starty, startdy, endy, enddy;
	public int guideAvg;
	
	public boolean useDir;
	public long iterations;
	
	public FollowTerrain(boolean useDir, int starty, int startdy, int endy, int enddy, int[] guide) {
		this.useDir = useDir;
		this.starty = starty;
		this.startdy = startdy;
		this.endy = endy;
		this.enddy = enddy;
		this.guide = guide;
		int sum = 0;
		for(int i=0; i<guide.length; i++)
			sum += guide[i];
		guideAvg = sum/guide.length;
	}
	
	public int calcCost(int j, int y, int dy) {
		int R = abs(y-guide[j]);
		if(abs(dy)>1)
			R *= 2;
		return R*R;
	}

	private DYOption[] getOptions(Edge e, int... dy) {
		DYOption[] opts = new DYOption[dy.length];
		for(int i=0; i<dy.length; i++) {
			DYOption opt = new DYOption();
			opt.dy = dy[i];
			opt.cost = calcCost(e.i+e.di, e.y+opt.dy*e.di, opt.dy);
			opts[i] = opt;
		}
		return opts;
	}

	private DYOption[] getOptions(Edge e) {
		if(e.dy==0)
			return useDir ? getOptions(e, 0, e.dir, e.dir*2, e.dir*4) : getOptions(e, 0, 1, -1, 2, -2, 4, -4);
		else if(e.contSlope<2 || abs(e.dy)==1)
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

	public Result compute1(Edge s, Edge e) {
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
		Result res = new Result(s.y+sel.dy);
		res.cost = minCost;
		return res;
	}
	
	public Result computeMax(Edge s, Edge e) {
		iterations++;
		// System.out.printf("%d: computeMax(%s, %s)\n", iterations, s.toString(), e.toString());
		int L = e.i - s.i - 1;
		if(L==1)
			return compute1(s, e);
		
		int alldy = abs(e.y-s.y);
		if(alldy%4!=0)
			return null; // must be multiple of 4
		
		int h = e.y>s.y ? 4 : -4;
		DYOption[] optS = alldy>0 && (s.dy==0 || s.dy==h) ? (e.contSlope<=2 ? getOptions(s, h, 0) : getOptions(s, 0, h)) : getOptions(s, 0);
		
		Result min = null;
		DYOption sel = null;
		int minCost = 0;
		for(DYOption opt : optS) {
			Edge si = new Edge(s, opt.dy);
			Result sub = compute(si, e, false);
			if(sub!=null) {
				int cost = sub.cost+opt.cost;
				if(min==null || cost<minCost) {
					sel = opt;
					min = sub;
					minCost = cost;
				}
			}
		}
		if(min==null)
			return null; // all failed
		Result res = new Result(s.y+sel.dy, min.y);
		res.cost = minCost;
		return res;
	}

	public Result compute(Edge s, Edge e, boolean preferExpandStart) {
		iterations++;
		// System.out.printf("%d: compute(%s, %s)\n", iterations, s.toString(), e.toString());
		int L = e.i - s.i - 1;
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
				Edge si = new Edge(s, opt.dy);
				Result sub = compute(si, e, false);
				if(sub!=null) {
					int cost = sub.cost+opt.cost;
					//for(int c=0; c<si.i; c++) System.out.print("|   ");
					//System.out.printf("+ opt.dy=%+d, opt.cost=%d, sub.cost=%d\n", opt.dy, opt.cost, sub.cost);
					if(min==null || cost<minCost) {
						sel = opt;
						min = sub;
						minCost = cost;
						if(att>=maxAtt)
							break;
					}
				}
				else {
					//for(int c=0; c<si.i; c++) System.out.print("|   ");
					//System.out.printf("+ opt.dy=%+d SUB NULL!\n", opt.dy);
				}
				att++;
			}
			if(min==null)
				return null; // all failed
			Result resS = new Result(s.y+sel.dy, min.y);
			resS.cost = minCost;
			return resS;
		}
		
		else {
			if(sortOpts)
				Arrays.sort(optE);
			Result min = null;
			DYOption sel = null;
			int minCost = 0;
			int att = 1;
			for(DYOption opt : optE) {
				Edge ei = new Edge(e, opt.dy);
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
			Result resE = new Result(min.y, e.y-sel.dy);
			resE.cost = minCost;
			return resE;
		}
	}
	
	public Result compute() {
		Edge s = new Edge(starty, startdy, 1, starty<guideAvg ? 1 : -1);
		Edge e = new Edge(endy, enddy, -1, endy<guideAvg ? -1 : 1);
		iterations = 0;
		Result res = compute(s, e, true);
		System.out.printf("Computed in %d iterations, cost=%d\n", iterations, res==null ? -1 : res.cost);
		return res;
	}

}
