package com.xrbpowered.aethertown.utils;

public class MathUtils {

	public static float lerp(float x0, float x1, float s) {
		return x0*(1f-s) + x1*s;
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

}
