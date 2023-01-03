package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.texture.Texture;

public class Plaza extends TileTemplate {

	public static final Color plazaColor = new Color(0xd5ceba);
	
	private static TileComponent plaza;
	
	public Plaza() {
		super(Street.streetColor);
	}

	public Plaza(Color minimapColor) {
		super(minimapColor);
	}

	@Override
	public void createComponents() {
		plaza = new TileComponent(
				FastMeshBuilder.plane(Tile.size, 1, 1, ObjectShader.vertexInfo, null),
				new Texture(plazaColor));
	}

	@Override
	public void createGeometry(Tile tile, LevelRenderer renderer, Random random) {
		plaza.addInstance(new TileObjectInfo(tile));
		renderer.terrain.addWalls(tile);
		Template.street.addHandrails(tile);
	}

}
