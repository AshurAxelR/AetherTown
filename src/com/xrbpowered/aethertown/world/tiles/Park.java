package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.render.env.SeasonalTexture;
import com.xrbpowered.aethertown.render.tiles.ScaledTileComponent;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.TerrainTile;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.gl.res.texture.Texture;

public class Park extends TileTemplate {

	public static final Park template = new Park();
	
	public static TileComponent tree, pine, trunk, bush;

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
		Texture treeTexture = new SeasonalTexture(new int[] {14, 25, 35, 48, 56, 65, 77},
				new Color[] {
					new Color(0x81bf45),
					new Color(0x4b7a00),
					new Color(0x496d00),
					new Color(0x516a0c),
					new Color(0xdebd4a),
					new Color(0xd3a848),
					new Color(0xe0eef1)
				});
		Texture pineTexture = new SeasonalTexture(new int[] {10, 25, 45, 65, 77},
				new Color[] {
					new Color(0x426a18),
					new Color(0x395e13),
					new Color(0x485c12),
					new Color(0x3a5c23),
					new Color(0xcee1ed)
				});
		Texture bushTexture = new SeasonalTexture(new int[] {14, 22, 30, 50, 70, 77},
				new Color[] {
					new Color(0x74ad45),
					new Color(0x56862d),
					new Color(0x497522),
					new Color(0x597c25),
					new Color(0xaf793d),
					new Color(0xe9f2f4)
				});
		
		tree = new ScaledTileComponent(
				BasicGeometry.sphere(1f, 8, -1, ObjectShader.vertexInfo),
				treeTexture);
		pine = new ScaledTileComponent(
				BasicGeometry.doubleCone(1f, 8, 0, 1, 0.2f, ObjectShader.vertexInfo),
				pineTexture);
		trunk = new ScaledTileComponent(
				BasicGeometry.cylinder(0.26f, 4, 1f, -1, ObjectShader.vertexInfo),
				new Texture(new Color(0x615746)));
		bush = new ScaledTileComponent(
				BasicGeometry.sphere(1f, 8, -0.5f, ObjectShader.vertexInfo),
				bushTexture);
	}

	@Override
	public void decorateTile(Tile tile, Random random) {
		TerrainTile.addTrees((ParkTile) tile, random);
		if(!isFlex(tile))
			Fences.addFences(tile);
	}
	
	@Override
	public boolean postDecorateTile(Tile tile, Random random) {
		if(!isFlex(tile))
			return Fences.fillFenceGaps(tile);
		else
			return false;
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
		Fences.createFences(r, tile);
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
		if(!tile.flex) {
			for(Dir d : Dir.values()) {
				Tile adj = tile.getAdj(d);
				if(adj!=null && adj.t instanceof StreetSlope && ((StreetSlope) adj.t).h==1
						&& Math.abs(adj.basey-tile.basey)<=1 && Math.abs(adj.basey-miny)<=1) {
					tile.basey = adj.basey;
					tile.flex = true;
					return true;
				}
			}
			tile.flex = (miny==tile.basey-1);
		}
		return false;
	}
	
}
