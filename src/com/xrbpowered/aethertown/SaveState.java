package com.xrbpowered.aethertown;

import java.util.LinkedList;

import org.joml.Vector2i;

import com.xrbpowered.aethertown.utils.AbstractConfig;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.RegionMode;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class SaveState extends AbstractConfig {

	public RegionMode regionMode = AetherTown.settings.regionMode;
	public long regionSeed = AetherTown.settings.regionSeed;
	public boolean defaultStart = true;
	public int levelx = 0;
	public int levelz = 0;
	
	public int day = 0;
	public float startSeason = AetherTown.settings.startSeason;
	public float time = AetherTown.settings.startTime;
	
	public float cameraPosX = 0f;
	public float cameraPosZ = 0f;
	public float cameraLookX = 0f;
	public float cameraLookY = 0f;

	public LinkedList<Vector2i> bookmarks = new LinkedList<Vector2i>();
	public LinkedList<Vector2i> visited = new LinkedList<Vector2i>();
	
	public SaveState() {
		super("./save.cfg");
	}
	
	public void listVisited(Region region) {
		visited.clear();
		for(int x=0; x<region.sizex; x++)
			for(int z=0; z<region.sizez; z++) {
				LevelInfo level = region.map[x][z];
				if(level!=null && level.visited)
					visited.add(new Vector2i(level.x0, level.z0));
			}
	}
	
	public void assignVisited(Region region) {
		for(Vector2i v : visited) {
			if(!region.isInside(v.x, v.y))
				continue;
			LevelInfo level = region.map[v.x][v.y];
			if(level!=null)
				level.visited = true;
		}
	}
	
	private static Object parsePointList(String value, LinkedList<Vector2i> list, boolean allowNulls) {
		list.clear();
		if(value.isEmpty())
			return list;
		String[] vs = value.split(";");
		for(String v : vs) {
			if(allowNulls && v.isEmpty()) {
				list.add(null);
				continue;
			}
			String[] xz = v.split(",");
			if(xz.length!=2)
				return null;
			try {
				int x = Integer.parseInt(xz[0]);
				int z = Integer.parseInt(xz[1]);
				list.add(new Vector2i(x, z));
			}
			catch (NumberFormatException e) {
				return null;
			}
		}
		return list;
	}

	private static String formatPointList(LinkedList<Vector2i> list) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(Vector2i v : list) {
			if(!first) sb.append(";");
			first = false;
			if(v!=null)
				sb.append(String.format("%d,%d", v.x, v.y));
		}
		return sb.toString();
	}

	@Override
	protected Object parseValue(String name, String value, Class<?> type) {
		if(name.equals("time"))
			return WorldTime.parseTime(value);
		else if(name.equals("bookmarks"))
			return parsePointList(value, bookmarks, true);
		else if(name.equals("visited"))
			return parsePointList(value, visited, false);
		else if(name.equals("regionMode"))
			return RegionMode.parseValue(value);
		else
			return super.parseValue(name, value, type);
	}
	
	@Override
	protected String formatValue(String name, Object obj) {
		if(name.equals("time"))
			return WorldTime.getFormattedTime((Float) obj);
		else if(name.equals("bookmarks"))
			return formatPointList(bookmarks);
		else if(name.equals("visited"))
			return formatPointList(visited);
		else if(name.equals("regionMode"))
			return ((RegionMode) obj).formatValue();
		else
			return super.formatValue(name, obj);
	}
	
	public long getRegionSeed() {
		return regionSeed>=0L ? regionSeed : System.currentTimeMillis();
	}
	
	public LevelInfo getLevel(Region region) {
		return defaultStart ? region.startLevel : region.getLevel(levelx, levelz);
	}

}
