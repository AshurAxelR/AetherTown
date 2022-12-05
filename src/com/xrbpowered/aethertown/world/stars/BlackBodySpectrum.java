package com.xrbpowered.aethertown.world.stars;

import java.awt.Color;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.utils.MathUtils;

public class BlackBodySpectrum {

	// http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/

	public static double getRed(double t) {
		t *= 0.01;
		if(t<66.0)
			return 1.0;
		else {
			double r = 329.698727446 * Math.pow(t - 60.0, -0.1332047592);
			return MathUtils.clamp(r/255.0);
		}
	}
	
	public static double getGreen(double t) {
		t *= 0.01;
		if(t<66.0) {
			double g = 99.4708025861 * Math.log(t)  - 161.1195681661;
			return MathUtils.clamp(g/255.0);
		}
		else {
			double g = 288.1221695283 * Math.pow(t - 60.0, -0.0755148492);
			return MathUtils.clamp(g/255.0);
		}
	}
	
	public static double getBlue(double t) {
		t *= 0.01;
		if(t>=66.0)
			return 1.0;
		else if(t<=19.0)
			return 0.0;
		else {
			double b = 138.5177312231 * Math.log(t - 10.0)  - 305.0447927307;
			return MathUtils.clamp(b/255.0);
		}
	}
	
	public static Color getColor(double t) {
		return new Color((float)getRed(t), (float)getGreen(t), (float)getBlue(t)); 
	}
	
	public static Vector3f getColor(double t, Vector3f out) {
		if(out==null)
			out = new Vector3f();
		out.set((float)getRed(t), (float)getGreen(t), (float)getBlue(t));
		return out;
	}
	
}
