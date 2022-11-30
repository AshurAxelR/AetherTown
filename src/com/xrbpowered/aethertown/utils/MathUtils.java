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

}
