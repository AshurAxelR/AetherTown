package com.xrbpowered.aethertown.world.stars.chart;

import java.awt.Shape;
import java.util.ArrayList;

import com.xrbpowered.aethertown.world.stars.Star;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class ChartStar extends Star {

	public double r;
	public Shape shape;

	public static ArrayList<String> getStarInfo(ArrayList<ChartStar> chartStars) {
		ArrayList<String> out = new ArrayList<>();
		for(ChartStar s : chartStars) {
			String tier;
			if(s.mag<-3.6f)
				tier = "Mythical";
			else if(s.mag<-2.6f)
				tier = "Legendary";
			else if(s.mag<-1.6f)
				tier = "Epic";
			else
				continue;
			
			String season;
			int m = ((int)(s.ra/2f)+9)%12;
			if(s.de>75f)
				season = "all year (polar)";
			else if(s.de>50f)
				season = "all year";
			else if(s.de>30f)
				season = WorldTime.monthNames[(m+9)%12] + "-" + WorldTime.monthNames[(m+3)%12];
			else {
				season = "in " + WorldTime.monthNames[m];
				if(s.de<-40f)
					continue;
				else if(s.de<-30f)
					season += " (very low)";
				else if(s.de<-15f)
					season += " (low)";
			}
			
			out.add(String.format("%s star (%.2f%s) %s\n", tier, s.mag, getSpectralClass(s.temp), season));
		}
		return out;
	}
	
	public static String getSpectralClass(float t) {
		if(t>=60000f) return "WR";
		else if(t>=30000f) return "O";
		else if(t>=10000f) return "B";
		else if(t>=7500f) return "A";
		else if(t>=6000f) return "F";
		else if(t>=5200f) return "G";
		else if(t>=3700f) return "K";
		else if(t>=2300f) return "M";
		else return "S";
	}
	

}
