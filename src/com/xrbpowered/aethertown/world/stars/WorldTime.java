package com.xrbpowered.aethertown.world.stars;

import static com.xrbpowered.aethertown.AetherTown.settings;

public class WorldTime {
	
	public static final int daysInYear = 12*7;
	public static final int equinoxDay = 19;

	public static final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	
	private static final float dayFactor = 1f / (float)(60*60*24);
	private static final float cycleTimeFactor = (float)Math.PI * 2f * dayFactor;
	
	public static int day1 = 0;
	public static float cycleTime = calcCycleTime(0.25f);
	public static float timeOfYear = 0.25f; // 0f - spring equinox, 0.25f - summer solstice, 0.5f - autumn equinox, 0.75f - winter solstice
	
	public static void updateTime(float dt) {
		cycleTime += dt*settings.timeSpeed*cycleTimeFactor;
		timeOfYear += dt*settings.timeSpeed * dayFactor / daysInYear;
		clampTimeOfYear();
	}
	
	private static void clampTimeOfYear() {
		if(timeOfYear<0f)
			timeOfYear += 1f;
		if(timeOfYear>1f)
			timeOfYear -= 1f;
	}
	
	public static void shiftTimeOfYear(float dt) {
		timeOfYear += dt*0.05f;
		clampTimeOfYear();
		cycleTime += dt*0.05f * (float)Math.PI*2f;
	}
	
	private static float calcCycleTime(float t) {
		return (float)Math.PI * 2f * (timeOfYear + t - 0.5f);
	}
	
	private static float fromCycleTime() {
		return (cycleTime / (float)Math.PI / 2f) - timeOfYear + 0.5f;
	}
	
	public static void setTimeOfDay(float t) {
		// FIXME date break
		cycleTime = calcCycleTime(t); 
	}
	
	public static void setDayOfYear(int day) {
		timeOfYear = ((day - equinoxDay + daysInYear) % daysInYear) / (float) daysInYear;
	}
	
	public static float getTimeOfDay() {
		float t = fromCycleTime();
		return t - (float)Math.floor(t);
	}
	
	public static int getDay() {
		return (int)Math.floor(fromCycleTime()) + day1;
	}
	
	public static int getDayOfYear() {
		return ((int)(timeOfYear*daysInYear) + equinoxDay) % daysInYear;
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
	
	public static String getFormattedDate(int day) {
		int m = day/7;
		int d = day%7;
		return String.format("%s %d", monthNames[m], d+1);
	}

	public static String getFormattedDate() {
		return getFormattedDate(getDayOfYear());
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
		// FIXME re-create season textures
		return settings.season; // (timeOfYear>0.7f && timeOfYear<0.85f) ? Seasons.winter : Seasons.summer;
	}

}
