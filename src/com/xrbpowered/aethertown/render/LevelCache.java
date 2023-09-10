package com.xrbpowered.aethertown.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.env.SkyBuffer;
import com.xrbpowered.aethertown.render.tiles.TileRenderer;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.scene.CameraActor;

public class LevelCache {

	public static final int storageLimit = 64;
	
	public class CacheEntry {
		public final LevelInfo info;
		
		public Level level = null;
		public LevelRenderer renderer = null;
		public long lastRenderTime = -1L;
		
		public CacheEntry(LevelInfo info) {
			this.info = info;
		}
	}
	
	public final GeneratorQueue generatorQueue;
	
	private HashMap<LevelInfo, CacheEntry> infoMap = new HashMap<>();
	private ArrayList<CacheEntry> list = new ArrayList<>();
	private CacheEntry active = null;
	
	private int storage = 0;
	public int renderedLevels = 0;
	
	private boolean missingRenderers = false;
	
	public LevelCache() {
		generatorQueue = new GeneratorQueue();
		generatorQueue.start();
	}
	
	public Level activeLevel() {
		return active.level;
	}
	
	public boolean isMissingRenderers() {
		return missingRenderers;
	}
	
	public LevelRenderer activeLevelRenderer() {
		return active.renderer;
	}
	
	public LevelInfo add(LevelInfo info, boolean markVisited) {
		if(!infoMap.containsKey(info)) {
			CacheEntry c = new CacheEntry(info);
			generatorQueue.queueLevel(c);
			infoMap.put(info, c);
			list.add(c);
			missingRenderers = true;
			
			storage += info.size*info.size;
			while(storage > storageLimit)
				expel(getLRU());
		}
		else {
			info = infoMap.get(info).info;
		}
		if(markVisited)
			info.visited = true;
		return info;
	}
	
	public void addAllAdj(LevelInfo info, boolean markVisited, boolean followPortals) {
		// prioritise origin:
		add(info, markVisited);
		if(followPortals && info.isPortal())
			addAllAdj(info.region.cache.portals.otherLevel, markVisited, false);
		
		for(int x=info.x0-1; x<info.x0+info.size+1; x++)
			for(int z=info.z0-1; z<info.z0+info.size+1; z++) {
				if(x==info.x0 && z==info.z0)
					continue;
				LevelInfo level = add(info.region.getLevel(x, z), markVisited);
				if(followPortals && level.isPortal())
					add(info.region.cache.portals.otherLevel, markVisited);
			}
	}

	public void addAllAdj(LevelInfo info, boolean markVisited) {
		addAllAdj(info, markVisited, true);
	}

	public LevelInfo getLRU() {
		CacheEntry lru = null;
		for(CacheEntry c : list) {
			if(c.lastRenderTime<0L)
				continue;
			if(lru==null || c.lastRenderTime<lru.lastRenderTime)
				lru = c;
		}
		return lru.info;
	}
	
	public void expel(LevelInfo info) {
		CacheEntry c = infoMap.get(info);
		if(c!=null) {
			infoMap.remove(info);
			list.remove(c);
			storage -= info.size*info.size;
		}
	}

	public int getStoredBlocks() {
		return storage;
	}
	
	public Level getLevel(LevelInfo info) {
		return infoMap.get(info).level;
	}
	
	public Level setActive(LevelInfo info, boolean waitGenerator) {
		active = infoMap.get(info);
		for(CacheEntry c : list)
			updateLevelOffset(c);
		
		if(waitGenerator && active.level==null) {
			System.err.println("... Level cache waiting for the generator thread");
			try {
				while(active.level==null)
					Thread.sleep(10);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.err.println("... Level cache ready");
		}
		
		return active.level;
	}
	
	public Iterable<Level> list() {
		return new Iterable<Level>() {
			final Iterator<CacheEntry> i = list.iterator();
			@Override
			public Iterator<Level> iterator() {
				return new Iterator<Level>() {
					@Override
					public boolean hasNext() {
						return i.hasNext();
					}
					@Override
					public Level next() {
						CacheEntry e = i.next();
						return e.level;
					}
				};
			}
		};
	}
	
	public HashSet<Region> regionsInUse() {
		HashSet<Region> set = new HashSet<>();
		for (CacheEntry c : list)
			set.add(c.info.region);
		return set;
	}
	
	public Level findHover(Region region, float x, float z) {
		for(CacheEntry c : list) {
			if(c.level==null || c.renderer==null || c.info.region!=region) // FIXME call findHover again on renderer change
				continue;
			if(Level.hoverInside(c.level.levelSize, x - c.renderer.levelOffset.x, z - c.renderer.levelOffset.y))
				return c.level;
		}
		return null;
	}

	public void createRenderers(SkyBuffer sky, TileRenderer tiles) {
		missingRenderers = false;
		for(CacheEntry c : list) {
			if(c.renderer!=null)
				continue;
			if(c.level==null) {
				missingRenderers = true;
				continue;
			}
			c.renderer = new LevelRenderer(c.level, sky, tiles);
			System.out.println("Building geometry...");
			c.renderer.createLevelGeometry();
			System.out.println("Done.");
			updateLevelOffset(c);
		}
	}
	
	public static void adjustCameraPosition(LevelInfo prev, LevelInfo next, Vector3f pos) {
		float dx = (prev.x0 - next.x0)*LevelInfo.baseSize*Tile.size;
		float dz = (prev.z0 - next.z0)*LevelInfo.baseSize*Tile.size;
		pos.x += dx;
		pos.z += dz;
	}

	private void updateLevelOffset(CacheEntry c) {
		if(c.renderer==null)
			return;
		int dx = c.info.x0 - active.info.x0;
		int dz = c.info.z0 - active.info.z0;
		c.renderer.levelOffset.set(dx*LevelInfo.baseSize*Tile.size, dz*LevelInfo.baseSize*Tile.size);
	}

	private ArrayList<LevelRenderer> renderQueue = new ArrayList<>();
	
	public void renderAll(RenderTarget target, CameraActor.Perspective camera, Region region) {
		long time = System.currentTimeMillis();
		renderedLevels = 0;
		renderQueue.clear();
		for(CacheEntry c : list) {
			if(c.renderer==null || c.info.region!=region)
				continue;
			if(c.renderer.levelDist(camera.position.x, camera.position.z) > camera.getFar()) {
				if(c.lastRenderTime<0L)
					c.lastRenderTime = 0L;
				continue;
			}
			c.lastRenderTime = time;
			renderedLevels++;
			renderQueue.add(c.renderer);
		}
		for(int renderPass : LevelRenderer.renderPassList)
			for(LevelRenderer renderer : renderQueue)
				renderer.render(target, renderPass);
	}
	
}
