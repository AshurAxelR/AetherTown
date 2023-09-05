package com.xrbpowered.aethertown.world.gen.plot;

import static com.xrbpowered.aethertown.world.gen.plot.ArchitectureTileSet.*;

import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.HouseT.HouseTile;

public class ArchitectureStyle {

	protected final int[] floory;
	protected final int[] obsy;
	protected final int[] matchy;
	protected final int[] lighty;

	public final int floorCount;
	protected final ArchitectureTileSet defaultSet;
	
	public ArchitectureStyle(int floorCount, ArchitectureTileSet defaultSet) {
		this.floorCount = floorCount;
		this.defaultSet = defaultSet;
		this.floory = new int[floorCount+1];
		this.obsy = new int[floorCount];
		this.matchy = new int[floorCount];
		this.lighty = new int[floorCount];
		int y = 0;
		for(int i=0; i<floorCount; i++) {
			ArchitectureTileSet set = getTileSet(i);
			floory[i] = y;
			obsy[i] = y + set.getObsY(i);
			matchy[i] = y + set.getMatchY(i);
			lighty[i] = y + set.getLightY(i);
			y += set.getFloorHeight(i);
		}
		floory[floorCount] = y;
	}

	public ArchitectureStyle(int floorCount) {
		this(floorCount, null);
	}

	public ArchitectureTileSet getTileSet(int floor) {
		return defaultSet;
	}
	
	public IllumTileComponent getDoor() {
		return getTileSet(0).getDefaultDoor();
	}

	public IllumTileComponent getWall(int floor, Dir d, HouseTile tile, boolean blank) {
		return getTileSet(floor).getWall(floor, d, tile, blank || forceBlank(floor, d, tile));
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
		return floory[floorCount];
	}

	public int maxGround() {
		return matchy[floorCount-1];
	}
	
	public boolean matchGround(HouseTile tile, int max) {
		int ground = 0;
		for(int i=0; i<floorCount; i++) {
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
	
	protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
		return false;
	}

	private static boolean back(Dir d) {
		return d==Dir.north;
	}

	private static boolean notFront(Dir d) {
		return d!=Dir.south;
	}

	private static boolean groundNotFront(int floor, Dir d) {
		return floor==0 && d!=Dir.south;
	}

	public static ArchitectureStyle residential2 = new ArchitectureStyle(2, baseSet);
	public static ArchitectureStyle residential3 = new ArchitectureStyle(3, baseSet);

	public static ArchitectureStyle office2 = new ArchitectureStyle(2, officeSet);
	public static ArchitectureStyle office3 = new ArchitectureStyle(3, officeSet);

	public static ArchitectureStyle shop1 = new ArchitectureStyle(1, shopSet) {
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return notFront(d);
		}
	};

	public static ArchitectureStyle shop2 = new ArchitectureStyle(2) {
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? shopSet : officeSet;
		}
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return groundNotFront(floor, d);
		}
	};
	
	public static ArchitectureStyle shop3 = new ArchitectureStyle(3) {
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? shopSet : officeSet;
		}
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return groundNotFront(floor, d);
		}
	};

	public static ArchitectureStyle local2 = new ArchitectureStyle(2) {
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? shopSet : baseSet;
		}
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return groundNotFront(floor, d);
		}
	};

	public static ArchitectureStyle local3 = new ArchitectureStyle(3) {
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? shopSet : baseSet;
		}
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return groundNotFront(floor, d);
		}
	};

	public static ArchitectureStyle openShop1 = new ArchitectureStyle(1, shopSet) {
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return back(d);
		}
	};
	
	public static ArchitectureStyle hotel2 = new ArchitectureStyle(2) {
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? officeSet : baseSet;
		}
	};

	public static ArchitectureStyle hotel3 = new ArchitectureStyle(3) {
		@Override
		public ArchitectureTileSet getTileSet(int floor) {
			return floor==0 ? officeSet : baseSet;
		}
	};

}
