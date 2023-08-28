package com.xrbpowered.aethertown.world.gen.plot;

import static com.xrbpowered.aethertown.world.gen.plot.ArchitectureTileSet.*;

import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.HouseT.HouseTile;

public abstract class ArchitectureStyle {

	protected int[] floory;
	protected int[] obsy;
	protected int[] matchy;
	protected int[] lighty;
	
	public int floorCount() {
		return obsy.length;
	}
	
	public abstract ArchitectureTileSet getTileSet(int floor);
	
	public IllumTileComponent getDoor(int type) {
		IllumTileComponent[] doors = getTileSet(0).doors;
		return type<doors.length ? doors[type] : doors[0];
	}

	public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
		return getTileSet(floor).getWall(floor, d, tile, blank);
	}

	private static boolean isObstructed(Tile tile, int[] yloc, int y, Dir d) {
		int y0 = yloc[d.leftCorner().ordinal()];
		int y1 = yloc[d.rightCorner().ordinal()];
		int gy = y;
		Tile adj = tile.getAdj(d);
		if(adj!=null) {
			if(adj.t==HouseT.template) {
				HouseGenerator adjHouse = (HouseGenerator) adj.sub.parent;
				gy = adj.basey + adjHouse.arch.getRoofY() + HouseT.roofHeight;
			}
			else {
				gy = adj.getGroundY();
			}
		}
		return (y0>y) || (y1>y) || (gy>y);
	}
	
	public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, int[] yloc) {
		return getWall(floor, d, tile, isObstructed(tile, yloc, tile.basey+obsy[floor], tile.d.apply(d)));
	}
	
	public int getFloorY(int floor) {
		return floory[floor];
	}
	
	public int getRoofY() {
		return floory[obsy.length];
	}

	public int maxGround() {
		return matchy[matchy.length-1];
	}
	
	public boolean matchGround(HouseTile tile, int max) {
		int ground = 0;
		for(int i=0; i<matchy.length; i++) {
			if(max-tile.basey < matchy[i])
				break;
			ground = matchy[i];
		}
		if(ground==0)
			return false;
		ground += tile.basey;
		if(ground>tile.groundy) {
			tile.groundy = ground;
			return true;
		}
		else {
			return false;
		}
	}
	
	public int getLightY(int floor) {
		return lighty[floor];
	}
	
	public static ArchitectureStyle residential2 = new ArchitectureStyle() {{
			this.floory = new int[] {0, 6, 12};
			this.obsy = new int[] {2, 7};
			this.matchy = new int[] {5, 11};
			this.lighty = new int[] {4, 9};
		}
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return baseSet;
		}
	};

	public static ArchitectureStyle residential3 = new ArchitectureStyle() {{
			this.floory = new int[] {0, 6, 12, 18};
			this.obsy = new int[] {2, 7, 13};
			this.matchy = new int[] {5, 11, 17};
			this.lighty = new int[] {4, 9, 16};
		}
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return baseSet;
		}
	};
	
	public static ArchitectureStyle shop1 = new ArchitectureStyle() {{
			this.floory = new int[] {0, 6};
			this.obsy = new int[] {1};
			this.matchy = new int[] {6};
			this.lighty = new int[] {3};
		}
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return shopSet;
		}
		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			return super.getWall(floor, d, tile, blank || d!=Dir.south);
		}
	};

	public static ArchitectureStyle shop2 = new ArchitectureStyle() {{
			this.floory = new int[] {0, 6, 12};
			this.obsy = new int[] {1, 6};
			this.matchy = new int[] {6, 10};
			this.lighty = new int[] {3, 9};
		}
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? shopSet : officeSet;
		}
		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			return super.getWall(floor, d, tile, blank || floor==0 && d!=Dir.south);
		}
	};
	
	public static ArchitectureStyle shop3 = new ArchitectureStyle() {{
		this.floory = new int[] {0, 6, 12, 18};
		this.obsy = new int[] {1, 6, 12};
		this.matchy = new int[] {6, 10, 16};
		this.lighty = new int[] {3, 9, 15};
		}
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? shopSet : baseSet;
		}
		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			return super.getWall(floor, d, tile, blank || floor==0 && d!=Dir.south);
		}
	};

	public static ArchitectureStyle local2 = new ArchitectureStyle() {{
			this.floory = new int[] {0, 6, 12};
			this.obsy = new int[] {1, 7};
			this.matchy = new int[] {6, 11};
			this.lighty = new int[] {3, 9};
		}
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? shopSet : officeSet;
		}
		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			return super.getWall(floor, d, tile, blank || floor==0 && d!=Dir.south);
		}
	};

	public static ArchitectureStyle local3 = new ArchitectureStyle() {{
			this.floory = new int[] {0, 6, 12, 18};
			this.obsy = new int[] {1, 7, 13};
			this.matchy = new int[] {6, 11, 17};
			this.lighty = new int[] {3, 9, 16};
		}
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? shopSet : baseSet;
		}
		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			return super.getWall(floor, d, tile, blank || floor==0 && d!=Dir.south);
		}
	};

	public static ArchitectureStyle openShop1 = new ArchitectureStyle() {{
			this.floory = new int[] {0, 6};
			this.obsy = new int[] {1};
			this.matchy = new int[] {6};
			this.lighty = new int[] {3};
		}
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return shopSet;
		}
		@Override
		public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
			return super.getWall(floor, d, tile, blank || d==Dir.north);
		}
	};
	

}
