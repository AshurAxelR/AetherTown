package com.xrbpowered.aethertown.world.stars;

import java.util.ArrayList;
import java.util.Random;

public class RandomStarData {

	public static final float axialTilt = (float)Math.toRadians(23.45);
	public static final float dayOfYear = 0.75f;

	public static final int numStars = 100000;
	public static final boolean debugSun = false;
	
	public static class StarData {
		public double ra, de;
		public float mag, temp;
	}
	
	public static final CompGauss magRand = new CompGauss(
		-3, 15,
		new double[][] {
			{0.005, 0.995}, {8.4, 8.4}, {1.87*2.0, 1.87}
		});

	public static final CompGauss tempRand = new CompGauss(
		3, 5,
		new double[][] {
			{0.2, 0.5, 0.55, 0.6}, {3.44, 3.54, 3.72, 3.86}, {0.03, 0.035, 0.05, 0.19}
		});

	public static class CompGauss {
		
		public final double limLow, limHigh;
		private final double[] w;
		private final double max;
		public final double[] mean;
		public final double[] sigma;
		
		public CompGauss(double limLow, double limHigh, double[][] s) {
			this.limLow = limLow;
			this.limHigh = limHigh;
			this.w = s[0];
			this.mean = s[1];
			this.sigma = s[2];
			double sum= 0;
			for(int i=0; i<w.length; i++)
				sum += w[i];
			this.max = sum;
		}
		
		private int select(Random random) {
			if(max<=0.0)
				return 0;
			double x = random.nextDouble() * max;
			for(int i=0;; i++) {
				if(x<w[i])
					return i;
				x -= w[i];
			}
		}
		
		public double next(Random random) {
			int s = select(random);
			double x = random.nextGaussian()*sigma[s]+mean[s];
			while(x<limLow || x>limHigh) {
				if(x<limLow)
					x = 2.0*limLow-x;
				if(x>limHigh)
					x = 2.0*limHigh-x;
			}
			return x;
		}
	}

	public static StarData sun = null;
	
	public static ArrayList<StarData> generate(Random random) {
		ArrayList<StarData> stars = new ArrayList<>();
		for(int i=0; i<numStars; i++) {
			StarData star = new StarData();
			star.ra = random.nextDouble()*2.0*Math.PI;
			star.de = Math.asin(2.0*random.nextDouble()-1.0);
			star.mag = (float) magRand.next(random);
			star.temp = (float) Math.pow(10, tempRand.next(random));
			if(star.mag<8f)
				stars.add(star);
		}

		sun = new StarData(); // sun
		sun.ra = dayOfYear*Math.PI*2f;
		sun.de = axialTilt*Math.sin(sun.ra);
		sun.mag = -10;
		sun.temp = 5500;

		if(debugSun)
			stars.add(sun);
		return stars;
	}
	
}
