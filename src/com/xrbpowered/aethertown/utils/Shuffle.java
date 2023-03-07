package com.xrbpowered.aethertown.utils;

import java.util.LinkedList;
import java.util.Random;

public class Shuffle {

	public final int size;
	
	private LinkedList<Integer> indices = new LinkedList<>();
	private int rem;
	
	public Shuffle(int size) {
		this.size = size;
		reset();
	}
	
	public void reset() {
		indices.clear();
		for(int i=0; i<size; i++)
			indices.add(i);
		rem = size;
	}
	
	public int next(Random random) {
		if(rem==0)
			reset();
		int n = indices.remove(random.nextInt(rem));
		rem--;
		return n;
	}

	public static class List<T> extends Shuffle {
		public final T[] items;
		
		@SafeVarargs
		public List(T... items) {
			super(items.length);
			this.items = items;
		}
		
		public T nextItem(Random random) {
			return items[super.next(random)];
		}
	}
	
}
