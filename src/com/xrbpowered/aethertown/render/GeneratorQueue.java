package com.xrbpowered.aethertown.render;

import java.util.LinkedList;

import com.xrbpowered.aethertown.render.LevelCache.CacheEntry;
import com.xrbpowered.aethertown.world.Level;

public class GeneratorQueue extends Thread {

	private LinkedList<CacheEntry> queue = new LinkedList<>();
	private Object lock = null;
	
	public void queueLevel(CacheEntry c) {
		queue.add(c);
		if(lock!=null)
			synchronized (lock) {
				lock.notify();
			}
	}
	
	@Override
	public void run() {
		System.out.println("Generator thread started.");
		lock = new Object();
		try {
			for(;;) {
				synchronized (lock) {
					while(queue.isEmpty())
						lock.wait();
				}
				while(!queue.isEmpty()) {
					Thread.sleep(100);
					CacheEntry c = queue.removeFirst();
					Level level = new Level(c.info);
					level.generate();
					c.level = level;
				}
			}
		}
		catch (InterruptedException e) {
			System.err.println("Generator thread closed.");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Generator thread stopped.");
		}
	}

}
