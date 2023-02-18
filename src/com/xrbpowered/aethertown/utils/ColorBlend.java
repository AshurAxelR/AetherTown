package com.xrbpowered.aethertown.utils;

import java.awt.Color;
import java.security.InvalidParameterException;

public class ColorBlend {

	private final float[] stops;
	private final float[] rs;
	private final float[] gs;
	private final float[] bs;
	
	public ColorBlend(Color[] colors, float[] stops) {
		if(colors.length!=stops.length)
			throw new InvalidParameterException();
		this.stops = stops;
		int len = stops.length;
		this.rs = new float[len];
		this.gs = new float[len];
		this.bs = new float[len];
		for(int i=0; i<len; i++) {
			rs[i] = colors[i].getRed()/255f;
			gs[i] = colors[i].getGreen()/255f;
			bs[i] = colors[i].getBlue()/255f;
		}
	}
	
	public Color get(float u) {
		float r = MathUtils.clamp(MathUtils.lerps(u, rs, stops));
		float g = MathUtils.clamp(MathUtils.lerps(u, gs, stops));
		float b = MathUtils.clamp(MathUtils.lerps(u, bs, stops));
		return new Color(r, g, b);
	}
	
	public static Color blend(Color c0, Color c1, float s) {
		float r = MathUtils.clamp(MathUtils.lerp(c0.getRed(), c1.getRed(), s)/255f);
		float g = MathUtils.clamp(MathUtils.lerp(c0.getGreen(), c1.getGreen(), s)/255f);
		float b = MathUtils.clamp(MathUtils.lerp(c0.getBlue(), c1.getBlue(), s)/255f);
		return new Color(r, g, b);
	}

}
