package com.xrbpowered.aethertown;

import java.util.LinkedList;

import com.xrbpowered.aethertown.utils.AbstractConfig;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.RegionMode;
import com.xrbpowered.aethertown.world.region.UniversalLevelInfo;
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

	// FIXME bookmarks and visited are not compatible with inter-region travel
	public LinkedList<UniversalLevelInfo> bookmarks = new LinkedList<UniversalLevelInfo>();
	public LinkedList<UniversalLevelInfo> visited = new LinkedList<UniversalLevelInfo>();
	
	public SaveState() {
		super("./save.cfg");
	}
	
	public void listVisited(Region region) {
		visited.clear();
		for(int x=0; x<region.sizex; x++)
			for(int z=0; z<region.sizez; z++) {
				LevelInfo level = region.map[x][z];
				if(level!=null && level.visited)
					visited.add(new UniversalLevelInfo(level));
			}
	}
	
	public void assignVisited(Region region) {
		for(UniversalLevelInfo v : visited) {
			if(v.regionSeed!=region.seed || !region.isInside(v.x, v.z))
				continue;
			LevelInfo level = region.map[v.x][v.z];
			if(level!=null)
				level.visited = true;
		}
	}
	
	private static Object parseLevelList(String value, LinkedList<UniversalLevelInfo> list, boolean allowNulls) {
		list.clear();
		if(value.isEmpty())
			return list;
		String[] vs = value.split(";");
		for(String v : vs) {
			if(allowNulls && v.isEmpty()) {
				list.add(null);
				continue;
			}
			UniversalLevelInfo level = UniversalLevelInfo.parseValue(v);
			if(level==null)
				return null;
			list.add(level);
		}
		return list;
	}

	private static String formatLevelList(LinkedList<UniversalLevelInfo> list) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(UniversalLevelInfo v : list) {
			if(!first) sb.append(";");
			first = false;
			if(v!=null)
				sb.append(v.format());
		}
		return sb.toString();
	}

	@Override
	protected Object parseValue(String name, String value, Class<?> type) {
		if(name.equals("time"))
			return WorldTime.parseTime(value);
		else if(name.equals("bookmarks"))
			return parseLevelList(value, bookmarks, true);
		else if(name.equals("visited"))
			return parseLevelList(value, visited, false);
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
			return formatLevelList(bookmarks);
		else if(name.equals("visited"))
			return formatLevelList(visited);
		else if(name.equals("regionMode"))
			return ((RegionMode) obj).formatValue();
		else
			return super.formatValue(name, obj);
	}
	
	public LevelInfo getLevel(Region region) {
		return defaultStart ? region.startLevel : region.getLevel(levelx, levelz);
	}

}
