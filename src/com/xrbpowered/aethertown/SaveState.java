package com.xrbpowered.aethertown;

import java.util.LinkedList;

import org.joml.Vector2i;

import com.xrbpowered.aethertown.utils.AbstractConfig;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class SaveState extends AbstractConfig {

	public long regionSeed = -1L;
	public boolean defaultStart = true;
	public int levelx = Region.sizez/2;
	public int levelz = Region.sizez/2;
	
	public int day = 0;
	public float time = 0.25f;
	
	public float cameraPosX = 0f;
	public float cameraPosZ = 0f;
	public float cameraLookX = 0f;
	public float cameraLookY = 0f;

	public LinkedList<Vector2i> visited = new LinkedList<Vector2i>();
	
	public SaveState() {
		super("./save.cfg");
	}
	
	public void listVisited(Region region) {
		visited.clear();
		for(int x=0; x<Region.sizex; x++)
			for(int z=0; z<Region.sizez; z++) {
				LevelInfo level = region.map[x][z];
				if(level!=null && level.visited)
					visited.add(new Vector2i(level.x0, level.z0));
			}
	}
	
	public void assignVisited(Region region) {
		for(Vector2i v : visited) {
			LevelInfo level = region.map[v.x][v.y];
			if(level!=null)
				level.visited = true;
		}
	}

	@Override
	protected Object parseValue(String name, String value, Class<?> type) {
		if(name.equals("time"))
			return WorldTime.parseTime(value);
		else if(name.equals("visited")) {
			visited.clear();
			if(value.isEmpty())
				return visited;
			String[] vs = value.split(";");
			for(String v : vs) {
				String[] xz = v.split(",");
				if(xz.length!=2)
					return null;
				try {
					int x = Integer.parseInt(xz[0]);
					int z = Integer.parseInt(xz[1]);
					if(!Region.isInside(x, z))
						return null;
					visited.add(new Vector2i(x, z));
				}
				catch (NumberFormatException e) {
					return null;
				}
			}
			return visited;
		}
		else
			return super.parseValue(name, value, type);
	}
	
	@Override
	protected String formatValue(String name, Object obj) {
		if(name.equals("time"))
			return WorldTime.getFormattedTime((Float) obj);
		else if(name.equals("visited")) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for(Vector2i v : visited) {
				if(!first) sb.append(";");
				first = false;
				sb.append(String.format("%d,%d", v.x, v.y));
			}
			return sb.toString();
		}
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
