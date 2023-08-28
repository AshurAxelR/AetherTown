package com.xrbpowered.aethertown.world.gen.plot;

import java.awt.Color;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.env.SeasonalTexture;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.tiles.HouseT.HouseTile;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public abstract class ArchitectureTileSet {

	public IllumTileComponent[] doors;
	public final IllumTileComponent[] walls;
	public final IllumTileComponent[] blanks;
	
	public ArchitectureTileSet(int maxFloors) {
		this.walls = new IllumTileComponent[maxFloors];
		this.blanks = new IllumTileComponent[maxFloors];
	}
	
	public boolean isEnd(Dir d, HouseTile tile) {
		boolean alignStraight = ((HouseGenerator) tile.sub.parent).alignStraight;
		return (d.dx!=0)^alignStraight;
	}
	
	public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
		return (blank ? blanks : walls)[floor<walls.length ? floor : walls.length-1];
	}

	public abstract void createSetComponents();

	public static StaticMesh wall;
	
	public static ArchitectureTileSet baseSet = new ArchitectureTileSet(3) {
		public IllumTileComponent topEnd;
		
		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			if(!blank && floor==2 && isEnd(d, tile))
				return topEnd;
			else
				return super.getWall(floor, d, tile, blank);
		}
		
		@Override
		public void createSetComponents() {
			doors = new IllumTileComponent[1];
			doors[0] = new IllumTileComponent(wall,
					new Texture("models/house/ground_wall_door.png", false, true, false),
					new Texture("models/house/ground_wall_door_illum.png", false, true, false));
			walls[0] = new IllumTileComponent(wall,
					new Texture("models/house/ground_wall.png", false, true, false),
					new Texture("models/house/ground_wall_illum.png", false, true, false));
			walls[1] = new IllumTileComponent(wall,
					new Texture("models/house/upper_wall.png", false, true, false),
					new Texture("models/house/upper_wall_illum.png", false, true, false));
			walls[2] = new IllumTileComponent(wall,
					new SeasonalTexture(new int[] {10, 77},
							new Texture("models/house/top_wall.png", false, true, false),
							new Texture("models/house/top_wall_winter.png", false, true, false)
					),
					new Texture("models/house/ground_wall_illum.png", false, true, false));
			blanks[0] = new IllumTileComponent(wall,
					new Texture("models/house/ground_wall_blank.png", false, true, false),
					TexColor.get(Color.BLACK));
			blanks[1] = new IllumTileComponent(wall,
					new Texture("models/house/upper_wall_blank.png", false, true, false),
					TexColor.get(Color.BLACK));
			blanks[2] = new IllumTileComponent(wall,
					new Texture("models/house/top_wall_blank.png", false, true, false),
					TexColor.get(Color.BLACK));
			topEnd = new IllumTileComponent(wall,
					new Texture("models/house/top_wall_end.png", false, true, false),
					new Texture("models/house/ground_wall_illum.png", false, true, false));
		}
	};
	
	public static ArchitectureTileSet shopSet = new ArchitectureTileSet(1) {
		@Override
		public void createSetComponents() {
			doors = new IllumTileComponent[2];
			doors[0] = new IllumTileComponent(wall,
					new Texture("models/house/shop/door.png", false, true, false),
					new Texture("models/house/shop/door_illum.png", false, true, false));
			doors[1] = new IllumTileComponent(wall,
					new Texture("models/house/shop/ddoor.png", false, true, false),
					new Texture("models/house/shop/ddoor_illum.png", false, true, false));
			walls[0] = new IllumTileComponent(wall,
					new Texture("models/house/shop/ground.png", false, true, false),
					new Texture("models/house/shop/ground_illum.png", false, true, false));
			blanks[0] = new IllumTileComponent(wall,
					new Texture("models/house/shop/ground_blank.png", false, true, false),
					TexColor.get(Color.BLACK));
		}
	};
	
	public static ArchitectureTileSet officeSet = baseSet; // TODO office windows set
	
	public static void createComponents() {
		wall = BasicGeometry.wall(Tile.size, 6*Tile.ysize, ObjectShader.vertexInfo, null);
		baseSet.createSetComponents();
		shopSet.createSetComponents();
	}

	
}
