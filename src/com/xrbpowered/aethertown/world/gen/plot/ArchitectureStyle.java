package com.xrbpowered.aethertown.world.gen.plot;

import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureTileSet.DoorInfo;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.HouseT.HouseTile;

public class ArchitectureStyle {

	protected final int[] floory;
	protected final int[] obsy;
	protected final int[] matchy;
	protected final int[] lighty;

	public final int floorCount;
	protected final ArchitectureTileSet groundSet, tileSet;
	protected IllumPattern groundIllum, illum;
	protected DoorInfo doorInfo;
	
	public ArchitectureStyle(int floorCount, ArchitectureTileSet groundSet, ArchitectureTileSet tileSet) {
		this.floorCount = floorCount;
		this.groundSet = groundSet;
		this.tileSet = tileSet;
		this.groundIllum = null;
		this.illum = null;
		
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

	public ArchitectureStyle(int floorCount, ArchitectureTileSet defaultSet) {
		this(floorCount, defaultSet, defaultSet);
	}

	public ArchitectureStyle(int floorCount) {
		this(floorCount, null, null);
	}
	
	public ArchitectureStyle setIllum(IllumPattern groundIllum, IllumPattern illum) {
		this.groundIllum = groundIllum;
		this.illum = illum;
		return this;
	}

	public ArchitectureStyle setIllum(IllumPattern illum) {
		return setIllum(illum, illum);
	}
	
	public ArchitectureStyle setDoorInfo(DoorInfo doorInfo) {
		this.doorInfo = doorInfo;
		return this;
	}

	public ArchitectureTileSet getTileSet(int floor) {
		return floor==0 ? groundSet : tileSet;
	}
	
	public IllumPattern getIllum(int floor) {
		return floor==0 ? groundIllum : illum;
	}

	public DoorInfo getDoorInfo() {
		return doorInfo==null ? getTileSet(0).getDefaultDoor() : doorInfo;
	}

	public IllumTileComponent getDoor() {
		return getDoorInfo().door;
	}
	
	public boolean allowLampAtDoor() {
		return getDoorInfo().allowLamp;
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
			if(adj.t==Hill.template)
				gy = adj.getGroundY();
			else
				gy = adj.t.getBlockY(adj);
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

	public static class BlankBack extends ArchitectureStyle {
		public BlankBack(int floorCount, ArchitectureTileSet groundSet, ArchitectureTileSet tileSet) {
			super(floorCount, groundSet, tileSet);
		}
		public BlankBack(int floorCount, ArchitectureTileSet defaultSet) {
			super(floorCount, defaultSet);
		}
		public BlankBack(int floorCount) {
			super(floorCount);
		}
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return d==Dir.north;
		}
	}
	
	public static class BlankGroundBack extends ArchitectureStyle {
		public BlankGroundBack(int floorCount, ArchitectureTileSet groundSet, ArchitectureTileSet tileSet) {
			super(floorCount, groundSet, tileSet);
		}
		public BlankGroundBack(int floorCount, ArchitectureTileSet defaultSet) {
			super(floorCount, defaultSet);
		}
		public BlankGroundBack(int floorCount) {
			super(floorCount);
		}
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return floor==0 && d==Dir.north;
		}
	}

	public static class BlankNotFront extends ArchitectureStyle {
		public BlankNotFront(int floorCount, ArchitectureTileSet groundSet, ArchitectureTileSet tileSet) {
			super(floorCount, groundSet, tileSet);
		}
		public BlankNotFront(int floorCount, ArchitectureTileSet defaultSet) {
			super(floorCount, defaultSet);
		}
		public BlankNotFront(int floorCount) {
			super(floorCount);
		}
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return d!=Dir.south;
		}
	}

	public static class BlankGroundNotFront extends ArchitectureStyle {
		public BlankGroundNotFront(int floorCount, ArchitectureTileSet groundSet, ArchitectureTileSet tileSet) {
			super(floorCount, groundSet, tileSet);
		}
		public BlankGroundNotFront(int floorCount, ArchitectureTileSet defaultSet) {
			super(floorCount, defaultSet);
		}
		public BlankGroundNotFront(int floorCount) {
			super(floorCount);
		}
		@Override
		protected boolean forceBlank(int floor, Dir d, HouseTile tile) {
			return floor==0 && d!=Dir.south;
		}
	}

	public static ArchitectureStyle fallback = new ArchitectureStyle(3, ArchitectureTileSet.baseSet);

}
