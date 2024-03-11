package com.xrbpowered.aethertown.world.tiles;

import java.util.LinkedList;
import java.util.Random;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.actions.ViewMapAction;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.render.tiles.IllumTileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.gen.Lamps;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class NavBox extends Bench {

	public static final NavBox template = new NavBox();

	private static IllumTileComponent box;

	public class NavBoxTile extends BenchTile {
		public LevelConnection pointer = null;
		public NavBoxTile() {
			super();
		}
	}

	public NavBox() {
		super(BenchType.noLamp, false, null);
	}
	
	@Override
	public Tile createTile() {
		return new NavBoxTile();
	}
	
	@Override
	public String getTileInfo(Tile tile) {
		LevelConnection pointer = ((NavBoxTile) tile).pointer;
		if(pointer!=null) {
			LevelInfo target = pointer.getNavTarget();
			float dist = pointer.getNavTargetDist() * (LevelInfo.baseSize/2) * Tile.size / 1000f;
			String name;
			if(target==tile.level.info)
				name = "loop back";
			else {
				name = "to " + (target.isPortal() ? target.portal.getName() : target.name);
				if(target.conns.size()<2)
					name += " (end)";
			}
			return String.format("%s \u2013 %.1f km %s %s", tile.level.info.name, dist, pointer.d.name(), name);
		}
		else
			return tile.level.info.name;
	}
	
	@Override
	public TileAction getTileAction(Tile tile) {
		return ViewMapAction.action;
	}
	
	@Override
	public void createComponents() {
		box = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/bench/navbox.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/bench/navbox.png", false, true, false),
				new Texture("models/bench/navbox_illum.png", false, true, false));
	}
	
	@Override
	public boolean finalizeTile(Tile tile, Random random) {
		return false;
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		super.createGeometry(tile, r);
		float dx = -tile.d.dx*0.25f;
		float dz = -tile.d.dz*0.25f;
		box.addInstance(r, new IllumTileObjectInfo(tile, dx, 0f, dz));
		r.pointLights.setLight(tile, dx, 3f, dz, 2.5f);
		r.blockLighting.addLight(IllumLayer.alwaysOn, tile, tile.basey+3, Lamps.lampLightColor, 0.2f, false);
	}
	
	private static int countAdjTiles(Level level, int x0, int z0) {
		int count = 0;
		for(Dir d : Dir.values()) {
			int x = x0 + d.dx;
			int z = z0 + d.dz;
			if(level.isInside(x, z) && level.map[x][z]!=null)
				count++;
		}
		return count;
	}
	
	private static class PathToken {
		public int x, z;
		public Dir d;
		public PathToken(int x, int z, Dir d) {
			this.x = x;
			this.z = z;
			this.d = d;
		}
	}
	
	private static void findPlacement(Level level, LevelConnection lc, Random random) {
		LinkedList<PathToken> path = new LinkedList<>();
		
		PathToken t = new PathToken(lc.getLevelX(), lc.getLevelZ(), null);
		for(;;) {
			int countStreets = 0;
			Dir nextd = null;
			for(Dir d : Dir.values()) {
				if(d==t.d)
					continue;
				int x = t.x + d.dx;
				int z = t.z + d.dz;
				Tile adj = level.isInside(x, z) ? level.map[x][z]	 : null;
				if(adj!=null && adj.sub==null && Street.isAnyStreet(adj.t)) {
					countStreets++;
					nextd = d;
				}
				else if(adj!=null && adj.sub!=null) {
					countStreets = 4;
					break;
				}
			}
			if(countStreets==1) {
				t = new PathToken(t.x + nextd.dx, t.z + nextd.dz, nextd.flip());
				path.add(t);
			}
			else {
				if(!path.isEmpty())
					path.removeLast();
				break;
			}
		}
		
		while(!path.isEmpty()) {
			t = path.removeLast();
			Tile street = level.map[t.x][t.z];
			if(street.t instanceof StreetSlope && ((StreetSlope) street.t).h>1)
				continue;
			
			for(Dir d : Dir.shuffle(random)) {
				int x = t.x + d.dx;
				int z = t.z + d.dz;
				if(level.isInside(x, z) && level.map[x][z]==null && countAdjTiles(level, x, z)==1) {
					NavBoxTile tile = (NavBoxTile) NavBox.template.forceGenerate(new Token(level, x, street.basey, z, d));
					tile.pointer = lc;
					return;
				}
			}
		}
	}
	
	public static void createNavBoxes(Level level, Random random) {
		for(LevelConnection lc : level.info.conns)
			findPlacement(level, lc, random);
	}
	
}
