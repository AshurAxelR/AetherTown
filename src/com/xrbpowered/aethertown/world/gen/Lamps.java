package com.xrbpowered.aethertown.world.gen;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.sprites.SpriteComponent;
import com.xrbpowered.aethertown.render.sprites.SpriteInfo;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.render.tiles.IllumTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.tiles.Alcove;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class Lamps {

	public static final Color lampLightColor = new Color(0xfff0b4); // new Color(0xfffae5);
	public static final Color lampPostColor = new Color(0x353433);

	public static class LampInfo {
		public boolean req = false;
		public Dir d = null;
	}

	public interface LampTile {
		public LampInfo getLamp();
	}
	
	public interface RequestLamp {
		public boolean requestLamp(Tile tile);
	}

	private static TileComponent lampPost;
	private static IllumTileComponent lantern;
	private static SpriteComponent coronaSprite;

	public static void createComponents() {
		lantern = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/lamp/lamp.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/lamp/lamp.png", false, true, false),
				new Texture("models/lamp/lamp_illum.png", false, true, false));
		coronaSprite = new SpriteComponent(new Texture("models/lamp/corona.png"));
		lampPost = new TileComponent(
				ObjMeshLoader.loadObj("models/lamp/lamp_post.obj", 0, 1f, ObjectShader.vertexInfo, null),
				TexColor.get(lampPostColor));
	}
	
	public static boolean hasAdjLamp(Tile tile) {
		for(Dir8 d : Dir8.values()) {
			Tile adj = tile.getAdj(d.dx, d.dz);
			if(adj!=null && (adj instanceof LampTile) && ((LampTile) adj).getLamp().req)
				return true;
		}
		return false;
	}
	
	public static boolean addLamp(Tile tile, LampInfo lamp, Dir[] dirs) {
		if(hasAdjLamp(tile)) {
			lamp.req = false;
			return true;
		}
		lamp.d = null;
		for(Dir d : dirs) {
			Tile adj = tile.getAdj(d);
			if(adj==null || !HouseT.allowLamp(adj) || adj.t==Alcove.template || Tunnels.hasTunnel(adj))
				continue;
			if((Street.isAnyPath(adj.t) || adj.t instanceof Plaza) &&
					(Math.abs(tile.getGroundY(d.leftCorner())-adj.getGroundY(d.flip().rightCorner()))<2 ||
					Math.abs(tile.getGroundY(d.rightCorner())-adj.getGroundY(d.flip().leftCorner()))<2))
				continue;
			lamp.d = d;
			lamp.req = true;
			break;
		}
		if(lamp.req && lamp.d==null) {
			lamp.req = false;
			return false;
		}
		else
			return true;
	}

	public static boolean addLamp(Tile tile, LampInfo lamp, Random random) {
		return addLamp(tile, lamp, Dir.shuffle(random));
	}

	private static void requestAdjLamp(Tile tile, Random random) {
		for(Dir d : Dir.shuffle(random)) {
			Tile adj = tile.getAdj(d);
			if(adj!=null && adj.t instanceof RequestLamp) {
				if(((RequestLamp) adj.t).requestLamp(adj))
					return;
			}
		}
	}
	
	public static void addStreetLamp(Tile tile, LampInfo lamp, Random random) {
		if(!tile.level.isInside(tile.x, tile.z, 4) || tile.level.info.isPortal()) {
			lamp.req = false;
			if(tile.x==1) {
				lamp.d = Dir.north;
				lamp.req = true;
			}
			else if(tile.x==tile.level.levelSize-2) {
				lamp.d = Dir.south;
				lamp.req = true;
			}
			else if(tile.z==1) {
				lamp.d = Dir.east;
				lamp.req = true;
			}
			else if(tile.z==tile.level.levelSize-2) {
				lamp.d = Dir.west;
				lamp.req = true;
			}
			return;
		}
		boolean hasLamp = lamp.req || (tile.x+tile.z)%5==0;
		if(!hasLamp) {
			for(Dir d : Dir.values()) {
				TileTemplate adjt = tile.getAdjT(d);
				if(adjt==HouseT.template)
					hasLamp = random.nextInt(4)>0;
				else if(adjt instanceof StreetSlope && ((StreetSlope) adjt).h>1)
					hasLamp = true;
				if(hasLamp)
					break;
			}
		}
		if(hasLamp && !addLamp(tile, lamp, random))
			requestAdjLamp(tile, random);
	}
	
	public static void createLamp(Tile tile, LampInfo lamp, LevelRenderer r, float dy, float dout) {
		Dir d = lamp.d;
		if(!lamp.req || d==null)
			return;
		float dx = d.dx*0.45f;
		if(d.dx==0)
			dx += tile.d.dx*dout;
		float dz = d.dz*0.45f;
		if(d.dz==0)
			dz += tile.d.dz*dout;
		lantern.addInstance(r, new IllumTileObjectInfo(tile, dx, dy, dz));
		lampPost.addInstance(r, new TileObjectInfo(tile, dx, dy, dz));
		coronaSprite.addInstance(r, new SpriteInfo(tile, dx, dy+5.75f, dz).size(Tile.size*0.75f));
		r.pointLights.setLight(tile, dx, dy+5.5f, dz, 4.5f);
		r.blockLighting.addLight(IllumLayer.alwaysOn, tile, tile.basey+5, lampLightColor, 0.5f, false);
	}
	
	public static void createLamp(Tile tile, LampInfo lamp, LevelRenderer r, float dy) {
		createLamp(tile, lamp, r, dy, 0f);
	}


}
