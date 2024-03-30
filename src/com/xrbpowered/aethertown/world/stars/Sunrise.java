package com.xrbpowered.aethertown.world.stars;

import static com.xrbpowered.aethertown.world.stars.StarData.*;
import static com.xrbpowered.aethertown.world.stars.WorldTime.*;
import static java.lang.Math.*; 

public class Sunrise {
	
	private Sunrise() {
	}

	public static Double calcw(int dayOfYear, int sunAngle) {
		double d = axialTilt * sin((daysInYear-equinoxDay+dayOfYear)*2.0*PI/daysInYear);
		double phi = latitude;
		double cos = (sin(toRadians(sunAngle)) - sin(d)*sin(phi))/cos(d)/cos(phi);
		if(cos<-1.0 || cos>1.0)
			return null;
		else
			return acos(cos);
	}

	public static Double calcw(int dayOfYear) {
		return calcw(dayOfYear, 0);
	}
	
	public static Float toTimeOfDay(Double w, boolean am) {
		if(w==null)
			return null;
		else
			return (float)(1.0 + (am ? -1.0 : 1.0) * w/PI)/2f;
	}
	
	public static Float sunrise(int dayOfYear) {
		return toTimeOfDay(calcw(dayOfYear), true);
	}
	
	public static Float sunset(int dayOfYear) {
		return toTimeOfDay(calcw(dayOfYear), false);
	}

}
