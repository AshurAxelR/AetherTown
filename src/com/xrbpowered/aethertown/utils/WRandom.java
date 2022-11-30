package com.xrbpowered.aethertown.utils;

import java.util.Random;

public class WRandom {

	public final double[] w;
	public final double max;
	
	public WRandom(double... w) {
		this.w = w;
		double sum= 0;
		for(int i=0; i<w.length; i++)
			sum += w[i];
		this.max = sum;
	}

	public int next(Random random) {
		if(max<=0.0)
			return 0;
		double x = random.nextDouble() * max;
		for(int i=0;; i++) {
			if(x<w[i])
				return i;
			x -= w[i];
		}
	}

}
