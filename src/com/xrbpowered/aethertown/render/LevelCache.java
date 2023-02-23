package com.xrbpowered.aethertown.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.env.SkyBuffer;
import com.xrbpowered.aethertown.render.tiles.TileRenderer;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.gl.res.buffer.RenderTarget;

public class LevelCache {

	private class CacheEntry {
		public Level level = null;
		public LevelRenderer renderer = null;
	}
	
	private HashMap<LevelInfo, CacheEntry> infoMap = new HashMap<>();
	private ArrayList<CacheEntry> list = new ArrayList<>();
	private CacheEntry active = null;
	
	public Level activeLevel() {
		return active.level;
	}
	
	public LevelRenderer activeLevelRenderer() {
		return active.renderer;
	}
	
	public void add(LevelInfo info) {
		if(!infoMap.containsKey(info)) {
			Level level = new Level(info);
			level.generate();
			CacheEntry c = new CacheEntry();
			c.level = level;
			infoMap.put(info, c);
			list.add(c);
		}
	}
	
	public void addAll(List<LevelInfo> list) {
		for(LevelInfo info : list)
			add(info);
	}
	
	public Level getLevel(LevelInfo info) {
		return infoMap.get(info).level;
	}
	
	public Level setActive(LevelInfo info) {
		active = infoMap.get(info);
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
	
	public Level findHover(float x, float z) {
		for (CacheEntry c : list) {
			if (Level.hoverInside(c.level.levelSize, x - c.renderer.levelOffset.x, z - c.renderer.levelOffset.y))
				return c.level;
		}
		return null;
	}

	public void createRenderers(SkyBuffer sky, TileRenderer tiles) {
		for(CacheEntry c : list) {
			c.renderer = new LevelRenderer(c.level, sky, tiles);
			System.out.println("Building geometry...");
			c.renderer.createLevelGeometry();
			System.out.println("Done.");
		}
	}
	
	public static void adjustCameraPosition(LevelInfo prev, LevelInfo next, Vector3f pos) {
		float dx = (prev.x0 - next.x0)*LevelInfo.baseSize*Tile.size;
		float dz = (prev.z0 - next.z0)*LevelInfo.baseSize*Tile.size;
		pos.x += dx;
		pos.z += dz;
	}
	
	public void updateLevelOffsets() {
		int x0 = active.level.info.x0;
		int z0 = active.level.info.z0;
		for(CacheEntry c : list) {
			int dx = c.level.info.x0 - x0;
			int dz = c.level.info.z0 - z0;
			c.renderer.levelOffset.set(dx*LevelInfo.baseSize*Tile.size, dz*LevelInfo.baseSize*Tile.size);
		}
	}
	
	public void renderAll(RenderTarget target) {
		for(CacheEntry c : list) {
			c.renderer.render(target);
		}
	}
	
}
