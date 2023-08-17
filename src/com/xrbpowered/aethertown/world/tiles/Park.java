package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.render.env.SeasonalTexture;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.FenceGenerator;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.TerrainTile;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.texture.Texture;

public class Park extends TileTemplate {

	public static final float treeRadius = 0.6f*Tile.size;
	public static final float trunkRadius = 0.065f*Tile.size;
	public static final float bushRadius = 0.3f*Tile.size;
	
	public static final Park template = new Park();
	
	public static TileComponent tree, trunk, bush;

	public class ParkTile extends TerrainTile {
		public boolean flex = false;
		
		public ParkTile() {
			super(Park.this);
		}
	}
	
	@Override
	public Tile createTile() {
		return new ParkTile();
	}
	
	private static boolean isFlex(Tile tile) {
		return ((ParkTile) tile).flex;
	}
	
	@Override
	public float getYIn(Tile tile, float sx, float sz, float y0) {
		if(isFlex(tile))
			return tile.level.h.gety(tile.x, tile.z, sx, sz);
		else
			return super.getYIn(tile, sx, sz, y0);
	}
	
	@Override
	public int getFenceY(Tile tile, Corner c) {
		if(isFlex(tile))
			return HeightMap.tiley(tile, c);
		else
			return super.getFenceY(tile, c);
	}
	
	@Override
	public void createComponents() {
		Texture treeTexture = new SeasonalTexture(new int[] {14, 77},
				new Color[] {
					new Color(0x496d00),
					new Color(0xe0eef1)
				});
		Texture bushTexture = new SeasonalTexture(new int[] {14, 77},
				new Color[] {
					new Color(0x497522),
					new Color(0xe9f2f4)
				});
		
		tree = new TileComponent(
				BasicGeometry.sphere(treeRadius, 8, -1, ObjectShader.vertexInfo),
				treeTexture);
		trunk = new TileComponent(
				BasicGeometry.cylinder(trunkRadius, 4, 1f, -1, ObjectShader.vertexInfo),
				new Texture(new Color(0x665545)));
		bush = new TileComponent(
				BasicGeometry.sphere(bushRadius, 8, -0.5f, ObjectShader.vertexInfo),
				bushTexture);
	}

	@Override
	public void decorateTile(Tile tile, Random random) {
		TerrainTile.addTrees((ParkTile) tile, random);
		if(!isFlex(tile))
			FenceGenerator.addHandrails(tile);
		// TODO fill handrail gaps (for hills too)
	}
	
	@Override
	public void createGeometry(Tile tile, LevelRenderer r) {
		if(isFlex(tile)) {
			r.terrain.addHillTile(TerrainMaterial.park, tile);
		}
		else {
			r.terrain.addWalls(tile);
			r.terrain.addFlatTile(TerrainMaterial.park, tile);
		}
		((ParkTile) tile).createTrees(r);
		FenceGenerator.createFences(r, tile);
	}
	
	@Override
	public boolean finalizeTile(Tile atile, Random random) {
		ParkTile tile = (ParkTile) atile;
		boolean remove = true;
		for(Dir d : Dir.values()) {
			if(d==tile.d.flip())
				continue;
			Tile adj = tile.getAdj(d);
			if(adj!=null && adj.basey>tile.basey-2) {
				remove = false;
				break;
			}
		}
		if(remove) {
			tile.level.map[tile.x][tile.z] = null;
			tile.level.heightLimiter.invalidate();
			return true;
		}
		
		int[] yloc = tile.level.h.yloc(tile.x, tile.z);
		int miny = MathUtils.min(yloc);
		if(miny>tile.basey) {
			tile.basey = miny;
			return true;
		}
		tile.flex = (miny==tile.basey-1);
		return false;
	}
	
}
