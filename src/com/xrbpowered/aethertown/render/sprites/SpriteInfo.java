package com.xrbpowered.aethertown.render.sprites;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.tiles.ObjectInfo;
import com.xrbpowered.aethertown.world.Tile;

public class SpriteInfo extends ObjectInfo {

	public float size = 1f;

	public SpriteInfo(Vector3f position) {
		super(position);
	}

	public SpriteInfo(float x, float y, float z) {
		super(x, y, z);
	}

	public SpriteInfo(Tile tile) {
		super(tile);
	}

	public SpriteInfo(Tile tile, float dx, float dy, float dz) {
		super(tile, dx, dy, dz);
	}

	public SpriteInfo size(float s) {
		this.size = s;
		return this;
	}
	
}
