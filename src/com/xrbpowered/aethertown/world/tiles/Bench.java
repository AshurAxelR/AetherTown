package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.TerrainTile;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.aethertown.world.gen.Fences.FenceType;
import com.xrbpowered.aethertown.world.gen.Lamps;
import com.xrbpowered.aethertown.world.gen.Lamps.LampInfo;
import com.xrbpowered.aethertown.world.gen.Lamps.LampTile;
import com.xrbpowered.aethertown.world.gen.Lamps.RequestLamp;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class Bench extends Plaza implements RequestLamp {

	public static final Bench templatePlaza = new Bench(true);
	public static final Bench templatePark = new Bench(false);
	
	public static TileComponent bench;

	public class BenchTile extends TerrainTile implements LampTile {
		public final LampInfo lamp = new LampInfo();
		public boolean plaza;
		
		public BenchTile() {
			super(Bench.this);
			this.plaza = Bench.this.plaza;
		}
		
		@Override
		public LampInfo getLamp() {
			return lamp;
		}
	}
	
	public final boolean plaza;
	
	public Bench(boolean plaza) {
		this.plaza = plaza;
	}
	
	@Override
	public Tile createTile() {
		return new BenchTile();
	}
	
	@Override
	public void createComponents() {
		bench = new TileComponent(
				ObjMeshLoader.loadObj("models/bench/bench.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/bench/bench.png", false, true, false)).setCulling(false);
	}
	
	@Override
	public boolean finalizeTile(Tile tile, Random random) {
		return Alcove.maybeConvert(tile);
	}
	
	@Override
	public boolean requestLamp(Tile tile) {
		((BenchTile) tile).lamp.req = true;
		return true;
	}
	
	@Override
	public boolean postDecorateTile(Tile atile, Random random) {
		BenchTile tile = (BenchTile) atile;
		boolean res = super.postDecorateTile(tile, random);
		
		if(tile.lamp.req && tile.lamp.d==null)
			return Lamps.addLamp(tile, tile.lamp, new Dir[] {tile.d.cw(), tile.d.ccw()});
		
		if(!tile.plaza) {
			boolean convert = false;
			int countFence = 0; 
			for(Dir d : Dir.values()) {
				if(tile.getFence(d)!=FenceType.none)
					countFence++;
				Tile adj = tile.getAdj(d);
				if(adj!=null && adj.d==tile.d && adj.basey==tile.basey && adj.t instanceof Bench && ((BenchTile) adj).plaza) {
					convert = true;
					break;
				}
			}
			if(convert || countFence>2) {
				tile.plaza = true;
				res = true;
			}
			else {
				Tile street = tile.getAdj(tile.d.flip());
				if(street!=null) {
					convert = false;
					for(Dir d : Dir.values()) {
						Tile adj = street.getAdj(d);
						if(adj!=null && adj.d==d && adj.t instanceof Bench && ((BenchTile) adj).plaza) {
							convert = true;
							break;
						}
					}
					if(convert) {
						tile.plaza = true;
						res = true;
					}
				}
			}
		}
		return res;
	}

	@Override
	public void createGeometry(Tile atile, LevelRenderer r) {
		BenchTile tile = (BenchTile) atile;
		float dout, doutLamp;
		if(tile.plaza) {
			super.createGeometry(tile, r);
			dout = 0.25f;
			doutLamp = 0f;
		}
		else {
			r.terrain.addWalls(tile);
			r.terrain.addFlatTile(TerrainMaterial.park, tile);
			Fences.createFences(r, tile);
			dout = -0.25f;
			doutLamp = -0.25f;
		}
		bench.addInstance(r, new TileObjectInfo(tile, dout, 0));
		Lamps.createLamp(tile, tile.lamp, r, 0, doutLamp);
	}
}
