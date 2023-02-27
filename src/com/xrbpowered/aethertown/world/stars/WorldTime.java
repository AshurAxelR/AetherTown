package com.xrbpowered.aethertown.world.stars;

import com.xrbpowered.aethertown.render.env.Seasons;

public class WorldTime {

	public static final float dayOfYear = 0.75f; // 0f - spring equinox, 0.25f - summer solstice, 0.5f - autumn equinox, 0.75f - winter solstice
	@SuppressWarnings("unused")
	public static final int season = (dayOfYear>0.7f && dayOfYear<0.85f) ? Seasons.winter : Seasons.summer;

	public static final float timeSpeed = 20f;

	private static final float cycleTimeFactor = (float)Math.PI * 2f / (float)(60*60*24);
	
	public static float cycleTime = calcCycleTime(0.25f);
	
	public static void updateTime(float dt) {
		cycleTime += dt*timeSpeed*cycleTimeFactor;
	}
	
	private static float calcCycleTime(float t) {
		return (float)Math.PI * 2f * (dayOfYear + t - 0.5f);
	}
	
	public static void setTimeOfDay(float t) {
		cycleTime = calcCycleTime(t); 
	}
	
	public static float getTimeOfDay() {
		float t = (cycleTime / (float)Math.PI / 2f) - WorldTime.dayOfYear + 0.5f;
		if(t<0)
			t += Math.floor(t)+1;
		else
			t -= Math.floor(t);
		return t;
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

}
