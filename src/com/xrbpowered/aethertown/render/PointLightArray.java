package com.xrbpowered.aethertown.render;

import org.joml.Vector4f;

import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.texture.FloatDataTexture;

public class PointLightArray extends FloatDataTexture {

	public static final float pointLightRadius = 4.5f;
	
	public Vector4f[][] positions;
	
	public PointLightArray(int levelSize) {
		super(levelSize, levelSize, false);
		positions = new Vector4f[levelSize][levelSize];
		for(int x=0; x<levelSize; x++)
			for(int z=0; z<levelSize; z++)
				positions[x][z] = new Vector4f(0, 0, 0, 0);
	}

	public void setLight(int mx, int mz, float x, float y, float z, float radius) {
		positions[mx][mz].set(x, y, z, radius);
	}
	
	public void setLight(Tile tile, float dx, float dy, float dz, float radius) {
		setLight(tile.x, tile.z, (tile.x+dx)*Tile.size, (tile.basey+dy)*Tile.ysize, (tile.z+dz)*Tile.size, radius);
	}

	public void setLight(Tile tile, float dx, float dy, float dz) {
		setLight(tile, dx, dy, dz, pointLightRadius);
	}

	public FloatDataTexture finish() {
		setData(positions).freeBuffer();
		positions = null;
		return this;
	}

}
