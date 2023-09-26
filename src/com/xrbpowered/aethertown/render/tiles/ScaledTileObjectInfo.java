package com.xrbpowered.aethertown.render.tiles;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.world.Tile;

public class ScaledTileObjectInfo extends TileObjectInfo {

	public float scaleXZ = 1f;
	public float scaleY = 1f;

	public ScaledTileObjectInfo(TileObjectInfo info) {
		super(info);
	}

	public ScaledTileObjectInfo(Vector3f position) {
		super(position);
	}

	public ScaledTileObjectInfo(float x, float y, float z) {
		super(x, y, z);
	}

	public ScaledTileObjectInfo(Tile tile) {
		super(tile);
	}

	public ScaledTileObjectInfo(Tile tile, float dx, float dy, float dz) {
		super(tile, dx, dy, dz);
	}

	public ScaledTileObjectInfo(Tile tile, float dout, float dy) {
		super(tile, dout, dy);
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


}
