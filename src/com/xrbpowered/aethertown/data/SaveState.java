package com.xrbpowered.aethertown.data;

import java.util.LinkedList;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.utils.AbstractConfig;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.RegionMode;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class SaveState extends AbstractConfig {

	public static final String savePath = "./save/";
	
	public long uid = System.currentTimeMillis(); 
	
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

	public LinkedList<LevelRef> bookmarks = new LinkedList<LevelRef>();
	
	public SaveState() {
		super(savePath+"save.cfg");
	}
	
	private static Object parseLevelList(String value, LinkedList<LevelRef> list, boolean allowNulls) {
		list.clear();
		if(value.isEmpty())
			return list;
		String[] vs = value.split(";");
		for(String v : vs) {
			if(allowNulls && v.isEmpty()) {
				list.add(null);
				continue;
			}
			LevelRef level = LevelRef.parseValue(v);
			if(level==null)
				return null;
			list.add(level);
		}
		return list;
	}

	private static String formatLevelList(LinkedList<LevelRef> list) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(LevelRef v : list) {
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
		else if(name.equals("regionMode"))
			return ((RegionMode) obj).formatValue();
		else
			return super.formatValue(name, obj);
	}
	
	public LevelInfo getLevel(Region region) {
		return defaultStart ? region.startLevel : region.getLevel(levelx, levelz);
	}
	
	@Override
	public SaveState load() {
		super.load();
		RegionVisits.load(uid);
		return this;
	}

	@Override
	public void save() {
		super.save();
		RegionVisits.save(uid);
	}
	
}
