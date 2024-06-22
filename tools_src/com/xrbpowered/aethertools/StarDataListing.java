package com.xrbpowered.aethertools;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.world.region.RegionCache;
import com.xrbpowered.aethertown.world.region.RegionMode;
import com.xrbpowered.aethertown.world.stars.StarData;
import com.xrbpowered.aethertown.world.stars.StarData.Star;

public class StarDataListing {

	public static boolean legacyRandom = false;
	public static long useSeed = -1L;
	public static int searchCount = 1000;
	public static double magLimit = -2.0f;//-1.5f;
	public static double magScorePower = 2.5;
	public static double deScorePower = 3.5;

	private static long getSeed(RegionCache rc, long seed) {
		return rc.portals.getRegionSeed(seed);
	}

	private static double calcScore(ArrayList<Star> stars) {
		double score = 0.0;
		for(Star s : stars) {
			if(s.mag>magLimit)
				continue;
			// score += Math.pow(deScorePower, (Math.cos(Math.PI/2.0 - s.de) + 1.0) * 2.0) * Math.pow(magScorePower, -s.mag);
			double v = -s.mag / Math.sqrt(s.ra*s.ra + (s.de - Math.PI/4.0)*(s.de - Math.PI/4.0));
			if(v>score)
				score = v;
		}
		return score;
	}

	private static void printListing(long seed, ArrayList<Star> stars) {
		System.out.printf("Seed: %dL\n", seed);
		for(Star s : stars) {
			if(s.mag>magLimit)
				continue;
			System.out.printf(" - mag:%.2f, temp:%.0fK, ra:%.1fh, de:%.0f\u00b0\n",
					s.mag, s.temp, s.ra / Math.PI * 12.0, Math.toDegrees(s.de));
		}
		System.out.printf("score: %.0f\n\n", calcScore(stars));
	}

	public static void main(String[] args) {
		RegionCache.useLegacy(legacyRandom);
		RegionCache rc = new RegionCache(RegionMode.defaultMode);
		
		if(useSeed < 0L) {
			Random random = new Random();
			
			double maxScore = 0.0;
			long bestSeed = 0L;
			ArrayList<Star> bestStars = null;
			
			for(int i=0; i<searchCount; i++) {
				long seed = getSeed(rc, random.nextLong());
				if(i%50==0)
					System.out.printf("#%d : %dL\n", i, seed);
				
				ArrayList<Star> stars = StarData.generate(RegionCache.getRand(seed));
				double score = calcScore(stars);
				if(bestStars==null || score>maxScore) {
					maxScore = score;
					bestSeed = seed;
					bestStars = stars;
				}
			}
			
			printListing(bestSeed, bestStars);
		}
		else {
			long seed = getSeed(rc, useSeed);
			printListing(seed, StarData.generate(RegionCache.getRand(seed)));
		}
	}

}
