package com.xrbpowered.aethertown.render.sprites;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.world.Tile;

public class SpriteInfo {

	public Vector3f position = new Vector3f();
	public float size = 1f;

	public SpriteInfo(Vector3f position) {
		this.position.set(position);
	}

	public SpriteInfo(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	public SpriteInfo(Tile tile) {
		this.position.set(tile.x*Tile.size, tile.basey*Tile.ysize, tile.z*Tile.size);
	}

	public SpriteInfo(Tile tile, float dx, float dy, float dz) {
		this.position.set((tile.x+dx)*Tile.size, (tile.basey+dy)*Tile.ysize, (tile.z+dz)*Tile.size);
	}

	public SpriteInfo size(float s) {
		this.size = s;
		return this;
	}
	
	public void setData(float[] data, int offs) {
		data[offs+0] = position.x;
		data[offs+1] = position.y;
		data[offs+2] = position.z;
		data[offs+3] = size;
	}

}
