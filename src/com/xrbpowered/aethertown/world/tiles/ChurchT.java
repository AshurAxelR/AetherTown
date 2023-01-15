package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.env.Seasons;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.render.tiles.IllumTileObjectInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Tile.SubInfo;
import com.xrbpowered.aethertown.world.gen.plot.HouseGeneratorBase;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class ChurchT extends TileTemplate {

	public static final ChurchT template = new ChurchT();
	
	private static final Color blockLightColor = new Color(0x5e4123);
	private static final Color backLightColor = new Color(0x694c2d);
	
	private static IllumTileComponent mid, front1, front2, front3, front4, back1, back2;
	private static TileComponent midRoof, chapelRoof;
	
	public ChurchT() {
		super(HouseT.roofColor.colors[Seasons.summer]);
	}
	
	@Override
	public String getTileInfo(Tile tile) {
		return ((HouseGeneratorBase) tile.sub.parent).getInfo();
	}
	
	@Override
	public int getFenceY(Tile tile, Corner c) {
		return tile.basey+12;
	}
	
	@Override
	public int getLightBlockY(Tile tile) {
		return tile.basey+15;
	}
	
	@Override
	public void createComponents() {
		mid = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/church/church_mid.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/church/church_mid.png", false, true, false),
				new Texture("models/church/church_mid_illum.png", false, true, false));
		front1 = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/church/church_front_1.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/church/church_front_1.png", false, true, false),
				new Texture("models/church/church_front_1_illum.png", false, true, false));
		front2 = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/church/church_front_2.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/church/church_front_2.png", false, true, false),
				new Texture("models/church/church_front_2_illum.png", false, true, false));
		front3 = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/church/church_front_3.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/church/church_front_3.png", false, true, false),
				TexColor.get(Color.BLACK));
		front4 = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/church/church_front_4.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/church/church_front_4.png", false, true, false),
				new Texture("models/church/church_front_4_illum.png", false, true, false));
		back1 = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/church/church_back_1.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/church/church_front_1.png", false, true, false),
				new Texture("models/church/church_front_1_illum.png", false, true, false));
		back2 = new IllumTileComponent(
				ObjMeshLoader.loadObj("models/church/church_back_2.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/church/church_back_2.png", false, true, false),
				new Texture("models/church/church_back_2_illum.png", false, true, false));
		midRoof = new TileComponent(
				ObjMeshLoader.loadObj("models/church/church_mid_roof.obj", 0, 1f, ObjectShader.vertexInfo, null),
				HouseT.roofColor.texture());
		chapelRoof = new TileComponent(
				ObjMeshLoader.loadObj("models/church/church_chapel_roof.obj", 0, 1f, ObjectShader.vertexInfo, null),
				HouseT.roofColor.texture());
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		renderer.terrain.addWalls(tile);
		SubInfo sub = tile.sub;
		if(sub.i==0) {
			Dir dr = tile.sub.parent.dr;
			IllumTileObjectInfo info = new IllumTileObjectInfo(tile, 0.5f*dr.dx, 0, 0.5f*dr.dz);
			TileObjectInfo roofInfo = new TileObjectInfo(tile, 0.5f*dr.dx, 0, 0.5f*dr.dz);
			if(sub.j==1) {
				front1.addInstance(info);
				front2.addInstance(info);
				front3.addInstance(info);
				front4.addInstance(info);
				chapelRoof.addInstance(roofInfo);
				renderer.pointLights.setLight(tile.getAdj(tile.d.flip()), 0.5f*dr.dx, 10f, 0.5f*dr.dz, 4f);
			}
			else if(sub.j==sub.parent.fwd-2) {
				back1.addInstance(info);
				back2.addInstance(info);
			}
			else {
				mid.addInstance(info);
			}
			midRoof.addInstance(roofInfo);
		}
		if(sub.j==1)
			renderer.blockLighting.addLight(tile, tile.basey+8, Street.lampLightColor, 0.4f, false);
		else if(sub.j==sub.parent.fwd-2)
			renderer.blockLighting.addLight(tile, tile.basey+10, blockLightColor, 0.7f, true);
		else
			renderer.blockLighting.addLight(tile, tile.basey+8, backLightColor, 0.4f, true);
	}

}
