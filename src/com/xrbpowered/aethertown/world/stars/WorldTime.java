package com.xrbpowered.aethertown.world.stars;

import static com.xrbpowered.aethertown.AetherTown.settings;
import static com.xrbpowered.aethertown.utils.MathUtils.frac;

public class WorldTime {
	
	public static final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	public static final int daysInYear = 12*7;
	public static final int equinoxDay = 19;

	public static final double hour = 1.0 / (double)(24);
	public static final double minute = 1.0 / (double)(60*24);
	public static final double second = 1.0 / (double)(60*60*24);
	
	public static double yearPhase = 0f;

	public static double time = 0.0;
	
	public static void updateTime(double dt) {
		time += dt*settings.timeSpeed*second;
	}
	
	public static void shiftTimeOfYear(double dt) {
		yearPhase += dt*0.05;
	}

	public static float getTimeOfDay() {
		return getTimeOfDay(0f);
	}

	public static float getTimeOfDay(float addTime) {
		return (float) frac(time + addTime);
	}

	public static float getTimeOfYear() {
		return (float) frac(time/daysInYear + yearPhase);
	}
	
	public static int getHourOfDay() {
		return getHourOfDay(0f);
	}

	public static int getHourOfDay(float addTime) {
		return Math.round(getTimeOfDay(addTime)*60*60*24)/3600%24;
	}

	public static void setTime(double startSeason, int day, float t) {
		yearPhase = startSeason;
		time = day + t;
	}
	
	public static int getDay() {
		return (int)Math.floor(time);
	}
	
	public static int getDayOfYear(double time) {
		return ((int)Math.floor(time) + (int)Math.round(yearPhase*daysInYear) + equinoxDay + daysInYear) % daysInYear;
	}

	public static int getDayOfYear() {
		return getDayOfYear(time);
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
	
	public static String getFormattedTimestamp(double time) {
		return String.format("Day %d, %s, %s",
				(int)Math.floor(time)+1,
				getFormattedDate(getDayOfYear(time)),
				getFormattedTime((float) frac(time)));
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
			return Float.parseFloat(value);
		int h = Integer.parseInt(s[0]);
		int m = Integer.parseInt(s[1]);
		return h/24f + m/(24f*60f);
	}

	public static float parseDate(String value) {
		String[] s = value.split(" ", 3);
		if(s.length<2)
			return Float.parseFloat(value);
		int m = -1;
		for(int i=0; i<monthNames.length; i++)
			if(s[0].equalsIgnoreCase(monthNames[i])) {
				m = i;
				break;
			}
		if(m<0)
			throw new NumberFormatException();
		int d = Integer.parseInt(s[1])-1;
		int day = (m*7+d-equinoxDay+daysInYear)%daysInYear;
		return day / (float)daysInYear;
	}
	
	private static String[] romanLetters = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
	private static int[] romanValues = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	
	public static String romanNumeral(int x) {
		if(x<0)
			throw new NumberFormatException();
		StringBuilder sb = new StringBuilder();
		if(x>=4000) {
			sb.append(romanNumeral(x/1000));
			sb.append("\u2032");
			x = x%1000;
		}
		for(int i=0; i<romanValues.length; i++) {
			int v = romanValues[i];
			while(x>=v) {
				sb.append(romanLetters[i]);
				x -= v;
			}
		}
		return sb.toString();
	}
	
}
