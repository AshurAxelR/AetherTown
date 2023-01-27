package com.xrbpowered.aethertown.render.tiles;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;

public class TileObjectInfo extends ObjectInfo {

	public float scaleXZ = 1f;
	public float scaleY = 1f;
	public float rotation = 0f;

	public TileObjectInfo(TileObjectInfo info) {
		super(info);
		this.scaleXZ = info.scaleXZ;
		this.scaleY = info.scaleY;
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

	public TileObjectInfo scale(float xz, float y) {
		this.scaleXZ = xz;
		this.scaleY = y;
		return this;
	}

	public TileObjectInfo scale(float s) {
		this.scaleXZ = s;
		this.scaleY = s;
		return this;
	}

	public TileObjectInfo rotate(float a) {
		this.rotation = a;
		return this;
	}
	
	public TileObjectInfo rotate(Dir d) {
		this.rotation = (float)Math.PI*d.ordinal()*0.5f;
		return this;
	}

}
