package com.xrbpowered.aethertown.world.tiles;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.sprites.SpriteComponent;
import com.xrbpowered.aethertown.render.sprites.SpriteInfo;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.texture.Texture;

public class Street extends TileTemplate {

	public static final Color streetColor = new Color(0xb5b5aa);
	
	private static TileComponent street;
	private static SpriteComponent sprite;

	public Street() {
		super(streetColor);
	}
	
	@Override
	public void createComponents() {
		street = new TileComponent(
				FastMeshBuilder.plane(Tile.size, 1, 1, ObjectShader.vertexInfo, null),
				new Texture(streetColor));
		sprite = new SpriteComponent(new Texture("checker.png"));
	}

	@Override
	public void createGeometry(Tile tile, TerrainBuilder terrain, Random random) {
		terrain.addWalls(tile);
		street.addInstance(new TileObjectInfo(tile));
		// if(tile.x%2==0 && tile.z%2==0)
		// 	sprite.addInstance(new SpriteInfo(tile).size(Tile.size));
	}
	
	@Override
	public boolean finalizeTile(Tile tile, Random random) {
		if(random.nextInt(4)==0)
			return false;
		
		boolean adjSlope = false;
		boolean adjHouse = false;
		int countStreet = 0;
		for(Dir d : Dir.values()) { // FIXME orphan parks after street trimming
			Template adjt = tile.getAdjT(d);
			// if(adjt==null)
			//	return false;
			if(adjt==Template.street)
				countStreet++;
			else if(adjt==StreetSlope.template1 || adjt==StreetSlope.template4)
				adjSlope = true;
			else if(adjt==Template.house)
				adjHouse = true;
		}
		if(countStreet<2 && !adjHouse) {
			if(!adjSlope) {
				tile.level.map[tile.x][tile.z] = null;
				return true;
			}
		}
		return false;
	}
	
}
