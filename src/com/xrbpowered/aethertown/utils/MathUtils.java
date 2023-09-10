package com.xrbpowered.aethertown.utils;

public class MathUtils {

	public static float lerp(float x0, float x1, float s) {
		return x0*(1f-s) + x1*s;
	}
	
	public static float lerps(float u, float[] xs, float[] stops) {
		if(u<stops[0])
			return xs[0];
		float x0 = xs[0];
		float u0 = stops[0];
		for(int i=1; i<stops.length; i++) {
			float x1 = xs[i];
			float u1 = stops[i];
			if(u<u1 && u1>u0) {
				float s = (u-u0)/(u1-u0);
				return lerp(x0, x1, s);
			}
			x0 = x1;
			u0 = u1;
		}
		return xs[xs.length-1];
	}
	
	public static float clamp(float x, float min, float max) {
		if(x<min)
			return min;
		else if(x>max)
			return max;
		else
			return x;
	}
	
	public static float clamp(float x) {
		return clamp(x, 0f, 1f);
	}

	public static double clamp(double x, double min, double max) {
		if(x<min)
			return min;
		else if(x>max)
			return max;
		else
			return x;
	}
	
	public static double clamp(double x) {
		return clamp(x, 0f, 1f);
	}

	public static int mdist(int x1, int z1, int x2, int z2) {
		// Manhattan distance
		return Math.abs(x1-x2) + Math.abs(z1-z2);
	}
	
	public static int min(int... x) {
		int minx = x[0];
		for(int i=1; i<x.length; i++) {
			if(x[i]<minx)
				minx = x[i];
		}
		return minx;
	}

	public static int max(int... x) {
		int maxx = x[0];
		for(int i=1; i<x.length; i++) {
			if(x[i]>maxx)
				maxx = x[i];
		}
		return maxx;
	}
	
	public static int maxDelta(int... x) {
		return max(x) - min(x);
	}
	
	public static float frac(float x) {
		return x - (float)Math.floor(x);
	}

	public static double frac(double x) {
		return x - Math.floor(x);
	}

}
