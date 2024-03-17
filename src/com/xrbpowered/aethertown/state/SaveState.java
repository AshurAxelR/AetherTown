package com.xrbpowered.aethertown.state;

import static com.xrbpowered.aethertown.AetherTown.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;

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

	public SaveState() {
	}
	
	@Override
	public SaveState reset() {
		return new SaveState();
	}
	
	@Override
	protected Object parseValue(String name, String value, Class<?> type) {
		if(name.equals("time"))
			return WorldTime.parseTime(value);
		else if(name.equals("regionMode"))
			return RegionMode.parseValue(value);
		else
			return super.parseValue(name, value, type);
	}
	
	@Override
	protected String formatValue(String name, Object obj) {
		if(name.equals("time"))
			return WorldTime.getFormattedTime((Float) obj);
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
		startSeason = (float) WorldTime.yearPhase;
		day = WorldTime.getDay();
		time = WorldTime.getTimeOfDay();
		player.toSave(this);
		return this;
	}
	
	private static final String cfgName = "save.cfg";
	private static final String visitsName = "visits.dat";
	private static final String cooldownsName = "cooldowns.dat";
	private static final String bookmarksName = "bookmarks.dat";

	private static final Collection<String> all = Arrays.asList(
		cfgName, visitsName, cooldownsName, bookmarksName
	);
	private static final Collection<String> required = Arrays.asList(
		cfgName, visitsName, cooldownsName
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
		else if(cooldownsName.equals(name))
			return GlobalCooldowns.load(in);
		else if(bookmarksName.equals(name))
			return AetherTown.settings.allowBookmaks && Bookmarks.load(in);
		else
			return false;
	}
	
	@Override
	public boolean saveDataEntry(String name, OutputStream out) {
		if(cfgName.equals(name))
			return super.save(cfgName, out);
		else if(visitsName.equals(name))
			return RegionVisits.save(out);
		else if(cooldownsName.equals(name))
			return GlobalCooldowns.save(out);
		else if(bookmarksName.equals(name))
			return AetherTown.settings.allowBookmaks && !Bookmarks.isEmpty() && Bookmarks.save(out);
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
