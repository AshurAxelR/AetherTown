package com.xrbpowered.aethertown.world.gen.plot.houses;

import java.awt.Color;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.env.SeasonalTexture;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.tiles.HouseT.HouseTile;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public abstract class ArchitectureTileSet {

	public enum DoorInfo {
		residential("ground_wall_door"),
		office("office/door"),
		restaurantRed("office/restaurant_red", false),
		restaurantBlack("office/restaurant_black", false),
		officeDouble("office/ddoor", false),
		shop("shop/door"),
		localShop("shop/local"),
		fastFood("shop/fastfood"),
		coffeeShop("shop/coffee"),
		shopDouble("shop/ddoor", false),
		supermarket("shop/supermarket", false);
		
		private final String texturePath;
		public final boolean allowLamp;
		public IllumTileComponent door;
		
		private DoorInfo(String path, boolean allowLamp) {
			this.texturePath = path;
			this.allowLamp = allowLamp;
		}
		
		private DoorInfo(String path) {
			this(path, true);
		}
		
		public void createComponent() {
			door = new IllumTileComponent(wall,
					new Texture(String.format("models/house/%s.png", texturePath), false, true, false),
					new Texture(String.format("models/house/%s_illum.png", texturePath), false, true, false));
		}
	}
	
	public final IllumTileComponent[] walls;
	public final IllumTileComponent[] blanks;
	
	public ArchitectureTileSet(int maxFloors) {
		this.walls = new IllumTileComponent[maxFloors];
		this.blanks = new IllumTileComponent[maxFloors];
	}
	
	public int getFloorHeight(int floor) {
		return 6;
	}

	public int getMatchY(int floor) {
		return getFloorHeight(floor);
	}

	public abstract int getObsY(int floor);
	public abstract int getLightY(int floor);
	
	public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
		return getWallBlank(floor, blank);
	}

	public boolean isEnd(Dir d, HouseTile tile) {
		boolean alignStraight = ((HouseGenerator) tile.sub.parent).alignStraight;
		return (d.dx!=0)^alignStraight;
	}

	public IllumTileComponent getWallBlank(int floor, boolean blank) {
		return (blank ? blanks : walls)[floor];
	}

	protected IllumTileComponent getWallTopEnd(int floor, Dir d, HouseTile tile, boolean blank, IllumTileComponent topEnd) {
		if(!blank && floor>=walls.length-1 && isEnd(d, tile))
			return topEnd;
		else
			return getWallBlank(floor, blank);
	}

	public abstract DoorInfo getDefaultDoor();
	public abstract void createSetComponents();

	public static ArchitectureTileSet baseSet = new ArchitectureTileSet(3) {
		public IllumTileComponent topEnd;
		
		@Override
		public int getObsY(int floor) {
			return 1;
		}

		@Override
		public int getMatchY(int floor) {
			return floor==0 ? 5 : 6;
		}

		@Override
		public int getLightY(int floor) {
			return floor==1 ? 3 : 4;
		}

		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			return getWallTopEnd(floor, d, tile, blank, topEnd);
		}
		
		@Override
		public DoorInfo getDefaultDoor() {
			return DoorInfo.residential;
		}
		
		@Override
		public void createSetComponents() {
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
	
	public static ArchitectureTileSet officeSet = new ArchitectureTileSet(3) {
		public IllumTileComponent topEnd;
		
		@Override
		public int getObsY(int floor) {
			return 1;
		}

		@Override
		public int getLightY(int floor) {
			return 3;
		}

		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			return getWallTopEnd(floor, d, tile, blank, topEnd);
		}
		
		@Override
		public DoorInfo getDefaultDoor() {
			return DoorInfo.office;
		}
		
		@Override
		public void createSetComponents() {
			Texture illum = new Texture("models/house/office/illum.png", false, true, false);
			walls[0] = new IllumTileComponent(wall,
					new Texture("models/house/office/ground.png", false, true, false), illum);
			walls[1] = new IllumTileComponent(wall,
					new Texture("models/house/office/upper.png", false, true, false), illum);
			walls[2] = new IllumTileComponent(wall,
					new SeasonalTexture(new int[] {10, 77},
							new Texture("models/house/office/top.png", false, true, false),
							new Texture("models/house/office/top_winter.png", false, true, false)
					), illum);
			blanks[0] = new IllumTileComponent(wall,
					new Texture("models/house/office/ground_blank.png", false, true, false),
					TexColor.get(Color.BLACK));
			blanks[1] = new IllumTileComponent(wall,
					new Texture("models/house/office/upper_blank.png", false, true, false),
					TexColor.get(Color.BLACK));
			blanks[2] = new IllumTileComponent(wall,
					new Texture("models/house/office/top_blank.png", false, true, false),
					TexColor.get(Color.BLACK));
			topEnd = new IllumTileComponent(wall,
					new Texture("models/house/office/top_end.png", false, true, false), illum);
		}
	};
	
	public static ArchitectureTileSet shopSet = new ArchitectureTileSet(1) {
		@Override
		public int getObsY(int floor) {
			return 1;
		}

		@Override
		public int getLightY(int floor) {
			return 3;
		}

		@Override
		public DoorInfo getDefaultDoor() {
			return DoorInfo.shop;
		}

		@Override
		public void createSetComponents() {
			walls[0] = new IllumTileComponent(wall,
					new Texture("models/house/shop/ground.png", false, true, false),
					new Texture("models/house/shop/ground_illum.png", false, true, false));
			blanks[0] = new IllumTileComponent(wall,
					new Texture("models/house/shop/ground_blank.png", false, true, false),
					TexColor.get(Color.BLACK));
		}
	};
	
	public static StaticMesh wall;
	public static IllumTileComponent postSign;
	public static IllumTileComponent hospitalSign;
	public static IllumTileComponent hotelSign;
	
	public static void createComponents() {
		wall = BasicGeometry.wall(Tile.size, 6*Tile.ysize, ObjectShader.vertexInfo, null);
		
		StaticMesh sign = ObjMeshLoader.loadObj("models/house/sign/sign.obj", 0, 1f, ObjectShader.vertexInfo, null);
		postSign = new IllumTileComponent(sign,
				new Texture("models/house/sign/post.png", false, true, false),
				new Texture("models/house/sign/post_illum.png", false, true, false));
		hospitalSign = new IllumTileComponent(sign,
				new Texture("models/house/sign/hospital.png", false, true, false),
				new Texture("models/house/sign/hospital_illum.png", false, true, false));
		hotelSign = new IllumTileComponent(ObjMeshLoader.loadObj("models/house/sign/sign2.obj", 0, 1f, ObjectShader.vertexInfo, null),
				new Texture("models/house/sign/hotel.png", false, true, false),
				new Texture("models/house/sign/hotel_illum.png", false, true, false));
		
		for(DoorInfo door : DoorInfo.values())
			door.createComponent();
		baseSet.createSetComponents();
		officeSet.createSetComponents();
		shopSet.createSetComponents();
	}

	
}
