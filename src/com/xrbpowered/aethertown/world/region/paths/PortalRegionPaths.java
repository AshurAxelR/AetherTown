package com.xrbpowered.aethertown.world.region.paths;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Rand;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelTerrainModel;
import com.xrbpowered.aethertown.world.region.Region;

public class PortalRegionPaths {

	public final Region region;
	public final Dir din, dout, dnext;

	public PortalRegionPaths(Region region, Dir d) {
		this.region = region;
		this.din = d.flip();
		this.dout = d;
		this.dnext = d.cw();
	}
	
	public boolean scanAndPlace(int index, int startMargin, Rand random) {
		int isize = dnext.dx!=0 ? region.sizex : region.sizez;
		int jsize = din.dx!=0 ? region.sizex : region.sizez;
		int startx, startz;
		if(dnext.dx!=0)
			startx = random.nextInt(region.sizex-startMargin*2)+startMargin;
		else
			startx = dout.rightCorner().dx<0 ? 0 : region.sizex-1;
		if(dnext.dz!=0)
			startz = random.nextInt(region.sizez-startMargin*2)+startMargin;
		else
			startz = dout.rightCorner().dz<0 ? 0 : region.sizez-1;
		
		for(int i=0; i<isize-1; i++) {
			int xi = startx + (i%2==0 ? 1 : -1)*(i/2)*dnext.dx;
			int zi = startz + (i%2==0 ? 1 : -1)*(i/2)*dnext.dz;
			for(int j=0, xj=xi, zj=zi; j<jsize; j++, xj+=din.dx, zj+=din.dz) {
				if(!region.isInside(xj, zj))
					break;
				LevelInfo level = region.map[xj][zj];
				if(level!=null) {
					if(canConnectWithPath(xj, zj, dout, null)) {
						connectWithPath(index, xj, zj, dout, null, random);
						return true;
					}
					break;
				}
			}
		}
		return false;
	}
	
	private boolean canConnectWithPath(int x, int z, Dir d, LevelTerrainModel path) {
		if(!region.isInside(x, z))
			return false;
		LevelInfo level = region.map[x][z];

		LevelTerrainModel next;
		if(path==null) {
			if(level==null || level.fixed || level.conns.isEmpty())
				return false;
			next = level.terrain.pathToBottom;
		}
		else {
			if(level!=null)
				return false;
			next = path.pathToBottom;
		}
			
		if(next==null)
			return region.canGeneratePortal(x+2*d.dx, z+2*d.dz);
		else
			return canConnectWithPath(x+d.dx, z+d.dz, d, next);
	}

	private void connectWithPath(int index, int x, int z, Dir d, LevelTerrainModel path, Rand random) {
		LevelTerrainModel next;
		if(path==null) {
			next = region.map[x][z].terrain.pathToBottom;
		}
		else {
			LevelInfo add = new LevelInfo(region, x, z, 1, random.nextLong());
			add.setTerrain(path);
			add.place();
			region.connectLevels(x, z, d.flip());
			next = path.pathToBottom;
		}
			
		if(next==null) {
			region.generatePortal(index, x+2*d.dx, z+2*d.dz, d.flip(), random);
			region.connectLevels(x, z, d);
		}
		else {
			connectWithPath(index, x+d.dx, z+d.dz, d, next, random);
		}
	}

}
