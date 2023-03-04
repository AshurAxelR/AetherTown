package com.xrbpowered.aethertown;

import com.xrbpowered.aethertown.utils.AbstractConfig;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class SaveState extends AbstractConfig {

	public long regionSeed = -1L;
	public boolean defaultStart = true;
	public int levelx = Region.sizez/2;
	public int levelz = Region.sizez/2;
	
	public float time = 0.25f;
	
	public float cameraPosX = 0f;
	public float cameraPosZ = 0f;
	public float cameraLookX = 0f;
	public float cameraLookY = 0f;
	
	public SaveState() {
		super("./save.cfg");
	}
	
	@Override
	protected Object parseValue(String name, String value, Class<?> type) {
		if(name.equals("time"))
			return WorldTime.parseTime(value);
		else
			return super.parseValue(name, value, type);
	}
	
	@Override
	protected String formatValue(String name, Object obj) {
		if(name.equals("time"))
			return WorldTime.getFormattedTime((Float) obj);
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
