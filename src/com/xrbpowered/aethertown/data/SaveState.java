package com.xrbpowered.aethertown.data;

import static com.xrbpowered.aethertown.AetherTown.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.utils.AbstractConfig;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.RegionMode;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class SaveState extends AbstractConfig {

	public static final String savePath = "./save.dat";
	
	private static final String cfgName = "save.cfg";
	private static final String visitsName = "visits.dat";

	private static final String[] all = {
		cfgName, visitsName
	};
	private static final HashSet<String> required = new HashSet<>(Arrays.asList(all));
	
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
	
	public void update() {
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
	}
	
	@Override
	public SaveState load() {
		try(ZipInputStream zip = new ZipInputStream(new FileInputStream(new File(savePath)))) {
			HashSet<String> entries = new HashSet<>();
			
			ZipEntry zipEntry;
			while((zipEntry = zip.getNextEntry()) != null) {
				String name = zipEntry.getName();
				boolean res;
				
				if(cfgName.equals(name))
					res = super.load(cfgName, zip);
				else if(visitsName.equals(name))
					res = RegionVisits.load(zip);
				else
					res = false;
				
				zip.closeEntry();
				if(res)
					entries.add(name);
				if(!res && required.contains(name))
					throw new IOException();
			}
			
			for(String name : required) {
				if(!entries.contains(name)) {
					System.err.println("Missing "+name);
					throw new IOException();
				}
			}
			
			System.out.printf("%s loaded.\n", savePath);
			return this;
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
			System.err.printf("Can't load %s. Using default.\n", savePath);
			return reset();
		}
	}

	@Override
	public void save() {
		try(ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(savePath)))) {
			for(String name : all) {
				zip.putNextEntry(new ZipEntry(name));
				boolean res;
				
				if(cfgName.equals(name))
					res = super.save(cfgName, zip);
				else if(visitsName.equals(name))
					res = RegionVisits.save(zip);
				else
					res = false;
				
				zip.closeEntry();
				if(!res && required.contains(name))
					throw new IOException();
			}
			System.out.printf("%s saved.\n", savePath);
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
			System.err.printf("Can't save %s.\n", savePath);
			new File(savePath).delete();
		}
	}
	
}
