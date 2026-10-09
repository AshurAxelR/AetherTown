package com.xrbpowered.aethertown.world.region.paths;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Rand;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelSettlementType;
import com.xrbpowered.aethertown.world.region.LevelTerrainModel;
import com.xrbpowered.aethertown.world.region.Region;

public class GridGeneratePaths {

	public final Region region;
	public final Rand random;
	
	public final int radius;
	
	private final int size;
	private final int margin;
	
	public GridGeneratePaths(Region region, Rand random, int radius) {
		this.region = region;
		this.random = random;
		this.radius = radius;
		this.size = radius*2+1;
		this.margin = (region.sizex - size*3)/2;
	}
	
	private static final WRandom wMainSettlement = new WRandom(0.1, 0, 0, 1, 1.5, 2, 1, 0.25, 0.05);
	private static final WRandom wPathSettlement = new WRandom(0.5, 0.2, 0.1, 0.05);
	private static final WRandom wSidePathSettlement = new WRandom(0.75, 0.05, 0.1, 0.2);
	private static final WRandom wterrain = new WRandom(0, 0.5, 1, 0.25);
	
	private void addPath(int x, int z, Dir d) {
		region.connectLevels(x, z, d);
		LevelInfo level = region.map[x][z];
		if(level.settlement==LevelSettlementType.none && level!=region.startLevel)
			level.setSettlement(LevelSettlementType.values()[wPathSettlement.next(random)]);
	}

	private void addSidePath(int x, int z, Dir d) {
		if(random.nextFloat()>0.35f)
			return;
		region.connectLevels(x, z, d);
		LevelInfo level = region.map[x][z];
		if(level.settlement==LevelSettlementType.none && level!=region.startLevel)
			level.setSettlement(LevelSettlementType.values()[wSidePathSettlement.next(random)]);
	}

	private boolean addPortal(int index, int dgx, int dgz, Dir d) {
		int x = (dgx+radius)*3+margin+1;
		int z = (dgz+radius)*3+margin+1;
		while(region.map[x][z]==null) {
			x += d.dx;
			z += d.dz;
		}
		PortalRegionPaths portalPaths = new PortalRegionPaths(region, d);
		if(!portalPaths.canConnectWithPath(x, z, d.flip(), null)) {
			System.out.printf("Can't connect to [%d, %d]\n", x, z);
			return false;
		}
		portalPaths.connectWithPath(index, x, z, d.flip(), null, random);
		return true;
	}
	
	public boolean generatePaths() {
		for(int gx=0; gx<size; gx++)
			for(int gz=0; gz<size; gz++) {
				int x = gx*3+margin+random.nextInt(2);
				int z = gz*3+margin+random.nextInt(2);
				LevelInfo level = new LevelInfo(region, x, z, 2, random.nextLong());
				level.place();
				if(gx==radius && gz==radius) {
					level.setSettlement(LevelSettlementType.none);
				 	level.setTerrain(LevelTerrainModel.low);
				 	region.startLevel = level;
				}
				else {
				 	level.setSettlement(LevelSettlementType.values()[wMainSettlement.next(random)]);
				 	level.setTerrain(LevelTerrainModel.list[wterrain.next(random)]);
				}
			}
		
		if(!addPortal(0, -radius/2-1, radius+1, Dir.north))
			return false;
		if(!addPortal(1, -radius-1, 0, Dir.east))
			return false;
		if(!addPortal(2, -radius/2-1, -radius-1, Dir.south))
			return false;
		if(!addPortal(3, radius/2+1, -radius-1, Dir.south))
			return false;
		if(!addPortal(4, radius+1, 0, Dir.west))
			return false;
		if(!addPortal(5, radius/2+1, radius+1, Dir.north))
			return false;
		
		region.expand(random, 1, true);
		for(int gx=0; gx<size; gx++)
			for(int gz=0; gz<size; gz++) {
				int x = gx*3+margin+1;
				int z = gz*3+margin+1;
				if(gx<size-1) {
					addPath(x, z, Dir.east);
					addSidePath(x, z, Dir.north);
					addSidePath(x, z, Dir.south);
					addPath(x+1, z, Dir.east);
					addSidePath(x+1, z, Dir.north);
					addSidePath(x+1, z, Dir.south);
					addPath(x+2, z, Dir.east);
					addSidePath(x+2, z, Dir.north);
					addSidePath(x+2, z, Dir.south);
				}
				if(gz<size-1) {
					addPath(x, z, Dir.south);
					addSidePath(x, z, Dir.east);
					addSidePath(x, z, Dir.west);
					addPath(x, z+1, Dir.south);
					addSidePath(x, z+1, Dir.east);
					addSidePath(x, z+1, Dir.west);
					addPath(x, z+2, Dir.south);
					addSidePath(x, z+2, Dir.east);
					addSidePath(x, z+2, Dir.west);
				}
			}
		
		return true;
	}

}
