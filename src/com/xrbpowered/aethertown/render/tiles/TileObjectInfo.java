package com.xrbpowered.aethertown.render.tiles;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;

public class TileObjectInfo extends ObjectInfo {

	public float rotation = 0f;

	public TileObjectInfo(TileObjectInfo info) {
		super(info);
		this.rotation = info.rotation;
	}
	
	public TileObjectInfo(Vector3f position) {
		super(position);
	}

	public TileObjectInfo(float x, float y, float z) {
		super(x, y, z);
	}

	public TileObjectInfo(Tile tile) {
		super(tile);
		rotate(tile.d);
	}

	public TileObjectInfo(Tile tile, float dx, float dy, float dz) {
		super(tile, dx, dy, dz);
		rotate(tile.d);
	}

	public TileObjectInfo(Tile tile, float dout, float dy) {
		super(tile, tile.d.dx*dout, dy, tile.d.dz*dout);
		rotate(tile.d);
	}

	public TileObjectInfo rotate(float a) {
		this.rotation = a;
		return this;
	}
	
	public TileObjectInfo rotate(Dir d) {
		this.rotation = d.rotation;
		return this;
	}

}
