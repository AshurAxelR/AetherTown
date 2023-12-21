package com.xrbpowered.aethertown.render;

import org.joml.Vector4f;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.Tunnels;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelInfo;
import com.xrbpowered.gl.res.texture.FloatDataTexture;

public class TunnelDepthMap extends FloatDataTexture {

	public final Level level;
	
	public float[][] map;
	
	public TunnelDepthMap(Level level) {
		super(level.levelSize, level.levelSize, false);
		this.level = level;
		this.map = new float[level.levelSize][level.levelSize];
	}
	
	private void addWall(int x0, int z0, Dir d0) {
		int x = x0+d0.dx;
		int z = z0+d0.dz;
		Tile tile = level.map[x][z];
		if(!Tunnels.hasTunnel(tile)) {
			int sum = 0;
			int n = 0;
			for(Dir d : Dir.values()) {
				TunnelInfo adjTunnel = Tunnels.adjTunnel(tile, d);
				if(adjTunnel!=null) {
					sum += adjTunnel.depth;
					n++;
				}
			}
			map[x][z] = (float) sum / n;
		}
	}
	
	public void addTunnel(TunnelInfo tunnel) {
		int x = tunnel.below.x;
		int z = tunnel.below.z;
		map[x][z] = tunnel.depth;
		if(tunnel.rank==0) {
			switch(tunnel.type) {
				case junction:
				case object:
					for(Dir d : Dir.values())
						addWall(x, z, d);
					break;
				case straight:
					addWall(x, z, tunnel.below.d.cw());
					addWall(x, z, tunnel.below.d.ccw());
					break;
				case fixed:
					break;
			}
		}
	}
	
	public FloatDataTexture finish() {
		Vector4f[][] data = PointLightArray.createVectors(level.levelSize, new Vector4f(0, 0, 0, 1));
		for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				data[x][z].x = map[x][z];
			}
		setData(data).freeBuffer();
		map = null;
		return this;
	}

}
