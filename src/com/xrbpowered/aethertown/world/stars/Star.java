package com.xrbpowered.aethertown.world.stars;

import com.xrbpowered.aethertown.world.stars.chart.ChartStar;
import com.xrbpowered.aethertown.world.stars.chart.Projection;

public class Star implements Comparable<Star> {
	
	public double ra; // right ascention (render: radians, chart: 0h .. 23h)
	public double de; // declination (render: radians, chart: -90 .. +90)
	public float mag, temp;
	
	@Override
	public int compareTo(Star o) {
		return Double.compare(this.mag, o.mag);
	}
	
	public ChartStar toChart() {
		ChartStar s = new ChartStar();
		s.ra = ra * 12.0 / Math.PI;
		s.de = de * 180.0 / Math.PI;
		s.r = this.calcR();
		s.mag = mag;
		s.temp = temp;
		return s;
	}
	
	public double calcR() {
		double rf = Math.pow((7.0 - mag) / 3.4, 2.0/3.0);
		return (Projection.circleScale * rf * rf) / Math.sqrt(Math.PI);
	}
}