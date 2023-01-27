package com.xrbpowered.aethertown.render.tiles;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.world.Tile;

public class ObjectInfo {

	public Vector3f position = new Vector3f();

	public ObjectInfo(ObjectInfo info) {
		this.position.set(info.position);
	}
	
	public ObjectInfo(Vector3f position) {
		this.position.set(position);
	}

	public ObjectInfo(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	public ObjectInfo(Tile tile) {
		this.position.set(tile.x*Tile.size, tile.basey*Tile.ysize, tile.z*Tile.size);
	}

	public ObjectInfo(Tile tile, float dx, float dy, float dz) {
		this.position.set((tile.x+dx)*Tile.size, (tile.basey+dy)*Tile.ysize, (tile.z+dz)*Tile.size);
	}
	
}
