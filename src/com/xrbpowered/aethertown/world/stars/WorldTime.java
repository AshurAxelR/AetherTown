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
	
	public static int getHourOfDay() {
		return Math.round(getTimeOfDay()*60*60*24)/3600%24;
	}
	
	public static void setTime(float startSeason, int day, float t) {
		yearPhase = startSeason;
		time = day + t;
	}
	
	public static int getDay() {
		return (int)Math.floor(time);
	}
	
	public static int getDayOfYear() {
		return ((int)((float)Math.floor(time) + yearPhase*daysInYear) + equinoxDay + daysInYear) % daysInYear;
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

	public static String romanNumeral(int x) {
		if(x>=4000 || x<0)
			throw new NumberFormatException();
		if(x>=1000)
			return "M"+romanNumeral(x-1000);
		if(x>=900)
			return "CM"+romanNumeral(x-900);
		if(x>=500)
			return "D"+romanNumeral(x-500);
		if(x>=400)
			return "CD"+romanNumeral(x-400);
		if(x>=100)
			return "C"+romanNumeral(x-100);
		if(x>=90)
			return "XC"+romanNumeral(x-90);
		if(x>=50)
			return "L"+romanNumeral(x-50);
		if(x>=40)
			return "XL"+romanNumeral(x-40);
		if(x>=10)
			return "X"+romanNumeral(x-10);
		if(x>=9)
			return "IX"+romanNumeral(x-9);
		if(x>=5)
			return "V"+romanNumeral(x-5);
		if(x>=4)
			return "IV"+romanNumeral(x-4);
		if(x>=1)
			return "I"+romanNumeral(x-1);
		return "";
	}
	
}
