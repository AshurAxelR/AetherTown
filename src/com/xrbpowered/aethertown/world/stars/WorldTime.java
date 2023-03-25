package com.xrbpowered.aethertown.world.stars;

import static com.xrbpowered.aethertown.AetherTown.settings;

import com.xrbpowered.aethertown.render.env.Seasons;

public class WorldTime {

	private static final float cycleTimeFactor = (float)Math.PI * 2f / (float)(60*60*24);
	
	public static int day1 = 0;
	public static float cycleTime = calcCycleTime(0.25f);
	
	public static void updateTime(float dt) {
		cycleTime += dt*settings.timeSpeed*cycleTimeFactor;
	}
	
	private static float calcCycleTime(float t) {
		return (float)Math.PI * 2f * (settings.dayOfYear + t - 0.5f);
	}
	
	private static float fromCycleTime() {
		return (cycleTime / (float)Math.PI / 2f) - settings.dayOfYear + 0.5f;
	}
	
	public static void setTimeOfDay(float t) {
		cycleTime = calcCycleTime(t); 
	}
	
	public static float getTimeOfDay() {
		float t = fromCycleTime();
		return t - (float)Math.floor(t);
	}
	
	public static int getDay() {
		return (int)Math.floor(fromCycleTime()) + day1;
	}
	
	public static String getFormattedTime(float t) {
		int s = Math.round(t*60*60*24);
		int m = (s/60)%60;
		int h = (s/3600);
		return String.format("%02d:%02d", h, m);
	}
	
	public static String getFormattedTime() {
		return getFormattedTime(getTimeOfDay());
	}
	
	public static float parseTime(String value) {
		String[] s = value.split(":", 3);
		if(s.length<2)
			throw new NumberFormatException();
		int h = Integer.parseInt(s[0]);
		int m = Integer.parseInt(s[1]);
		return h/24f + m/(24f*60f);
	}
	
	public static int season() {
		return (settings.dayOfYear>0.7f && settings.dayOfYear<0.85f) ? Seasons.winter : Seasons.summer;
	}

}
