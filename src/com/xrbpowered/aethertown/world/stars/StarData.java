package com.xrbpowered.aethertown.world.stars;

import java.util.ArrayList;

import com.xrbpowered.aethertown.utils.Rand;

public class StarData {

	public static final float axialTilt = (float)Math.toRadians(23.45); // Earth: 23.45
	public static final float latitude = (float)Math.toRadians(45); // 0 - north pole, 180 - south pole
	public static final boolean debugSun = false;

	public static final int numStars = 100000;
	public static final int improveNumStars = 42000;
	public static final double improvedLambda = 1.115;

	public static class Star {
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
		
		private int select(Rand random) {
			if(max<=0.0)
				return 0;
			double x = random.nextDouble() * max;
			for(int i=0;; i++) {
				if(x<w[i])
					return i;
				x -= w[i];
			}
		}
		
		public double next(Rand random) {
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

	public static double nextImprovedMag(Rand rand, boolean first) {
		double mag;
		if(first) {
			do {
				mag = -1.5 - Math.log(1.0 - rand.nextDouble()) / (-improvedLambda*9.5);
			} while(mag < -4.0);
		}
		else {
			do {
				mag = 8.0 - Math.log(1.0 - rand.nextDouble()) / (-improvedLambda);
			} while(mag < -4.0);
		}
		return mag;
	}
	
	public static Star sun = null;
	
	public static void updateSun(float timeOfYear) {
		sun.ra = timeOfYear*Math.PI*2f;
		sun.de = axialTilt*Math.sin(sun.ra);
	}
	
	public static ArrayList<Star> generate(Rand random, boolean improved) {
		ArrayList<Star> stars = new ArrayList<>();
		int num = improved ? improveNumStars : numStars;
		for(int i=0; i<num; i++) {
			Star star = new Star();
			star.ra = random.nextDouble()*2.0*Math.PI;
			star.de = Math.asin(2.0*random.nextDouble()-1.0);
			star.mag = (float)(improved ? nextImprovedMag(random, i==0) : magRand.next(random));
			star.temp = (float) Math.pow(10, tempRand.next(random));
			if(star.mag<8f)
				stars.add(star);
		}

		sun = new Star(); // sun
		sun.mag = -10;
		sun.temp = 5500;

		if(debugSun)
			stars.add(sun);
		return stars;
	}
	
}
