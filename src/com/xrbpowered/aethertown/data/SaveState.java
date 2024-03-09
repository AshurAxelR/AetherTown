package com.xrbpowered.aethertown.data;

import static com.xrbpowered.aethertown.AetherTown.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.utils.AbstractConfig;
import com.xrbpowered.aethertown.utils.ZipBuilder;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.RegionMode;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class SaveState extends AbstractConfig implements ZipBuilder.DataPack {

	public static final String savePath = "./save.dat";
	
	public RegionMode regionMode = settings.regionMode;
	public long regionSeed = AetherTown.settings.regionSeed;
	public int levelx = 0;
	public int levelz = 0;
	
	public int day = 0;
	public float startSeason = settings.startSeason;
	public float time = settings.startTime;
	
	public boolean defaultStart = true;
	public float cameraPosX = 0f;
	public float cameraPosZ = 0f;
	public float cameraLookX = 0f;
	public float cameraLookY = 0f;

	public LinkedList<LevelRef> bookmarks = new LinkedList<LevelRef>();
	
	public SaveState() {
	}
	
	@Override
	public SaveState reset() {
		return new SaveState();
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
	
	public SaveState update() {
		regionMode = regionCache.mode;
		regionSeed = region.seed;
		levelx = levelInfo.x0;
		levelz = levelInfo.z0;
		startSeason = WorldTime.yearPhase;
		day = WorldTime.getDay();
		time = WorldTime.getTimeOfDay();
		defaultStart = false;
		cameraPosX = player.cameraPosition.x;
		cameraPosZ = player.cameraPosition.z;
		cameraLookX = player.cameraRotation.x;
		cameraLookY = player.cameraRotation.y;
		// aether.uiBookmarks.saveBookmarks(this); // FIXME save bookmarks
		return this;
	}
	
	private static final String cfgName = "save.cfg";
	private static final String visitsName = "visits.dat";

	private static final Collection<String> all = Arrays.asList(
		cfgName, visitsName
	);
	private static final Collection<String> required = Arrays.asList(
		cfgName, visitsName
	);

	@Override
	public Collection<String> listDataEntries() {
		return all;
	}
	
	@Override
	public Collection<String> listRequiredDataEntries() {
		return required;
	}
	
	@Override
	public boolean loadDataEntry(String name, InputStream in) {
		if(cfgName.equals(name))
			return super.load(cfgName, in);
		else if(visitsName.equals(name))
			return RegionVisits.load(in);
		else
			return false;
	}
	
	@Override
	public boolean saveDataEntry(String name, OutputStream out) {
		if(cfgName.equals(name))
			return super.save(cfgName, out);
		else if(visitsName.equals(name))
			return RegionVisits.save(out);
		else
			return false;
	}
	
	@Override
	public SaveState load() {
		if(ZipBuilder.load(savePath, this))
			return this;
		else
			return reset();
	}
	
	@Override
	public void save() {
		ZipBuilder.save(savePath, this);
	}
	
}
