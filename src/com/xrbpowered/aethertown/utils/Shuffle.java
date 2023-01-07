package com.xrbpowered.aethertown.utils;

import java.util.LinkedList;
import java.util.Random;

public class Shuffle {

	public final int size;
	
	private LinkedList<Integer> indices = new LinkedList<>();
	private int rem;
	
	public Shuffle(int size) {
		this.size = size;
		refill();
	}
	
	private void refill() {
		for(int i=0; i<size; i++)
			indices.add(i);
		rem = size;
	}
	
	public int next(Random random) {
		if(rem==0)
			refill();
		int n = indices.remove(random.nextInt(rem));
		rem--;
		return n;
	}

}
