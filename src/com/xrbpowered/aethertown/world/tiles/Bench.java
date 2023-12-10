package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.render.env.SeasonalTexture;
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

	public enum BenchType {
		bench(new Dir[] {Dir.east, Dir.west}),
		table(new Dir[] {Dir.north}),
		none(new Dir[] {Dir.north});
		
		public final Dir[] lampDirs;
		
		private BenchType(Dir[] lampDirs) {
			this.lampDirs = lampDirs;
		}
	}
	
	public static final Bench templatePark = new Bench(BenchType.bench, false);
	public static final Bench templatePlaza = new Bench(BenchType.bench, true);
	public static final Bench templatePlazaLampL = new Bench(BenchType.bench, true, new Dir[] {Dir.west, Dir.east});
	public static final Bench templatePlazaLampR = new Bench(BenchType.bench, true, null);
	public static final Bench templateParkTable = new Bench(BenchType.table, false);
	public static final Bench templatePlazaTable = new Bench(BenchType.table, true, null);
	
	public static Texture benchTexture;
	private static TileComponent bench, parkTable;

	public class BenchTile extends TerrainTile implements LampTile {
		public final LampInfo lamp = new LampInfo();
		public boolean plaza;
		
		public BenchTile() {
			super(Bench.this);
			this.plaza = Bench.this.plaza;
			if(lampReq)
				this.lamp.req = true;
		}
		
		@Override
		public LampInfo getLamp() {
			return lamp;
		}
	}
	
	public final boolean plaza;
	public final boolean lampReq;

	private final BenchType type;
	private final Dir[] lampDirs;

	public Bench(BenchType type, boolean plaza) {
		this.type = type;
		this.plaza = plaza;
		this.lampReq = false;
		this.lampDirs = type.lampDirs;
	}

	public Bench(BenchType type, boolean plaza, Dir[] lampReq) {
		this.type = type;
		this.plaza = plaza;
		this.lampReq = true;
		this.lampDirs = (lampReq==null) ? type.lampDirs : lampReq;
	}
	
	@Override
	public Tile createTile() {
		return new BenchTile();
	}
	
	@Override
	public void createComponents() {
		benchTexture = new SeasonalTexture(new int[] {10, 77},
				new Texture("models/bench/bench.png", true, true, false),
				new Texture("models/bench/bench_winter.png", true, true, false)); 
		bench = new TileComponent(ObjMeshLoader.loadObj("models/bench/bench.obj", 0, 1f, ObjectShader.vertexInfo, null), benchTexture);
		parkTable = new TileComponent(ObjMeshLoader.loadObj("models/bench/park_table.obj", 0, 1f, ObjectShader.vertexInfo, null), benchTexture);
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
			return Lamps.addLamp(tile, tile.lamp, lampDirs, true);
		
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
		if(tile.plaza) {
			super.createGeometry(tile, r);
		}
		else {
			r.terrain.addWalls(tile);
			r.terrain.addFlatTile(TerrainMaterial.park, tile);
			Fences.createFences(r, tile);
		}
		switch(type) {
			case bench: {
				float dout = tile.plaza ? 0.25f : -0.25f;
				bench.addInstance(r, new TileObjectInfo(tile, dout, 0));
				Lamps.createLamp(tile, tile.lamp, r, 0, dout);
				break;
			}
			case table: {
				parkTable.addInstance(r, new TileObjectInfo(tile));
				Lamps.createLamp(tile, tile.lamp, r, 0);
				break;
			}
			case none:
				Lamps.createLamp(tile, tile.lamp, r, 0);
				break;
		}
	}
}
