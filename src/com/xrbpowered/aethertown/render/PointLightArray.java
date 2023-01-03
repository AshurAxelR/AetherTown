package com.xrbpowered.aethertown.render;

import org.joml.Vector4f;

import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.texture.FloatDataTexture;

public class PointLightArray extends FloatDataTexture {

	public static final float pointLightRadius = 4.5f;
	
	public Vector4f[][] positions;
	
	public PointLightArray(int levelSize) {
		super(levelSize, levelSize, false);
		positions = createVectors(levelSize, new Vector4f(0, 0, 0, 0));
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
	
	public static Vector4f[][] createVectors(int size, Vector4f init) {
		Vector4f[][] v = new Vector4f[size][size];
		for(int x=0; x<size; x++)
			for(int z=0; z<size; z++)
				v[x][z] = new Vector4f(init);
		return v;
	}

}
