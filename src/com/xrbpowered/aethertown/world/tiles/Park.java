package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.env.SeasonalTexture;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.render.tiles.ScaledTileComponent;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.TerrainTile;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public class Park extends TileTemplate {

	private static final boolean useBranchTexture = true;
	
	private enum ParkType {
		common, lawn, xmas
	}
	
	public static final Park template = new Park(ParkType.common);
	public static final Park templateLawn = new Park(ParkType.lawn);
	public static final Park templateXmas = new Park(ParkType.xmas);
	
	public static TileComponent tree, cherryTree, pine, xmasTree, trunk, bush;

	public class ParkTile extends TerrainTile {
		public boolean flex = false;
		
		public ParkTile() {
			super(Park.this);
		}
	}
	
	public final ParkType type;
	
	public Park(ParkType type) {
		this.type = type;
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
		SeasonalTexture treeTexture = new SeasonalTexture(new int[] {14, 25, 35, 48, 56, 65, 77},
				new Color[] {
					new Color(0x81bf45),
					new Color(0x4b7a00),
					new Color(0x496d00),
					new Color(0x516a0c),
					new Color(0xdebd4a),
					new Color(0xd3a848),
					new Color(0xe0eef1)
				});
		SeasonalTexture cherryTexture = new SeasonalTexture(new int[] {14, 35, 45, 54, 58, 65, 77},
				new Color[] {
					new Color(0xe9c5ce),
					new Color(0x426905),
					new Color(0x4b600b),
					new Color(0x64670f),
					new Color(0xcf9731),
					new Color(0xb25c32),
					new Color(0xe0eef1)
				});
		SeasonalTexture bushTexture = new SeasonalTexture(new int[] {14, 22, 30, 50, 70, 77},
				new Color[] {
					new Color(0x74ad45),
					new Color(0x56862d),
					new Color(0x497522),
					new Color(0x597c25),
					new Color(0xaf793d),
					new Color(0xe9f2f4)
				});
		SeasonalTexture pineTexture = new SeasonalTexture(new int[] {10, 25, 45, 65, 77},
				new Color[] {
					new Color(0x426a18),
					new Color(0x395e13),
					new Color(0x485c12),
					new Color(0x3a5c23),
					new Color(0xcee1ed)
				});
		
		StaticMesh treeMesh = BasicGeometry.sphere(1f, 8, -1, ObjectShader.vertexInfo); 

		if(useBranchTexture) {
			Texture branches = new Texture("models/trees/branches.png", true, true, false);
			Texture branchesSnow = new Texture("models/trees/branches_snow.png", true, true, false);
			
			treeTexture.replace(70, 21, branches);
			treeTexture.replace(77, 10, branchesSnow);
			cherryTexture.replace(72, 16, branches);
			cherryTexture.replace(77, 10, branchesSnow);
			bushTexture.replace(72, 18, branches);
			bushTexture.replace(77, 10, branchesSnow);
			
			tree = new ScaledTileComponent(treeMesh, treeTexture).setCulling(false);
			cherryTree = new ScaledTileComponent(treeMesh, cherryTexture).setCulling(false);
			bush = new ScaledTileComponent(
					BasicGeometry.sphere(1f, 8, -0.5f, ObjectShader.vertexInfo),
					bushTexture).setCulling(false);
		}
		else {
			tree = new ScaledTileComponent(treeMesh, treeTexture);
			cherryTree = new ScaledTileComponent(treeMesh, cherryTexture);
			bush = new ScaledTileComponent(
					BasicGeometry.sphere(1f, 8, -0.5f, ObjectShader.vertexInfo),
					bushTexture);
		}
		
		trunk = new ScaledTileComponent(
				BasicGeometry.cylinder(0.26f, 4, 1f, -1, ObjectShader.vertexInfo),
				new Texture(new Color(0x615746)));
		
		pine = new ScaledTileComponent(BasicGeometry.doubleCone(1f, 8, 0, 1, 0.2f, ObjectShader.vertexInfo), pineTexture);
		xmasTree = new IllumTileComponent(
				BasicGeometry.doubleCone(1.6f, 8, 0.3f, 8f, 0.5f, ObjectShader.vertexInfo),
				pineTexture.copy().replace(77, 1, new Texture("models/xmas/xmas_tree.png", true, true, false)),
				new SeasonalTexture(new int[] {1, 77}, TexColor.get(Color.BLACK), new Texture("models/xmas/xmas_tree_illum.png", true, true, false)));
	}

	@Override
	public void decorateTile(Tile tile, Random random) {
		if(type==ParkType.common)
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
		
		if(type==ParkType.xmas) {
			TileObjectInfo info = new TileObjectInfo(tile);
			xmasTree.addInstance(r, info);
			trunk.addInstance(r, info);
		}
	}
	
	@Override
	public boolean finalizeTile(Tile atile, Random random) {
		if(Alcove.maybeConvert(atile)) {
			for(;;) {
				atile = atile.getAdj(atile.d.flip());
				if(atile!=null && atile.t instanceof Park && ((Park) atile.t).type!=ParkType.lawn)
					templateLawn.forceGenerate(Token.forTile(atile));
				else
					return true;
			}
		}
		
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
					tile.flex = true;
					return true;
				}
			}
			tile.flex = (miny==tile.basey-1);
		}
		else {
			int maxy = MathUtils.max(yloc);
			if(maxy>miny+2) {
				tile.flex = false;
				return true;
			}
		}
		return false;
	}
	
}
