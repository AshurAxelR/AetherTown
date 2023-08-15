package com.xrbpowered.aethertown.world.stars;

import static com.xrbpowered.aethertown.AetherTown.settings;
import static com.xrbpowered.aethertown.utils.MathUtils.frac;

public class WorldTime {
	
	public static final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public static final int daysInYear = 12*7;
	public static final int equinoxDay = 19;
	public static float yearPhase = 0f;

	private static final float timeFactor = 1f / (float)(60*60*24);
	
	public static float time = 0f;
	
	public static void updateTime(float dt) {
		time += dt*settings.timeSpeed*timeFactor;
	}
	
	public static void shiftTimeOfYear(float dt) {
		yearPhase += dt*0.05f;
	}

	public static float getTimeOfDay() {
		return frac(time);
	}
	
	public static float getTimeOfYear() {
		return frac(time/daysInYear + yearPhase);
	}
	
	public static void setTime(float startSeason, int day, float t) {
		yearPhase = startSeason;
		time = day + t;
	}
	
	public static int getDay() {
		return (int)Math.floor(time);
	}
	
	public static int getDayOfYear() {
		float t = frac((float)Math.floor(time)/daysInYear + yearPhase);
		return ((int)(t*daysInYear) + equinoxDay) % daysInYear;
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
