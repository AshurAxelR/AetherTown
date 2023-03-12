package com.xrbpowered.aethertown.render.tiles;

import java.awt.Color;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.world.Tile;

public class IllumTileObjectInfo extends TileObjectInfo {

	public Vector3f illumMod = new Vector3f(1, 1, 1);
	
	public IllumTileObjectInfo(TileObjectInfo info) {
		super(info);
	}

	public IllumTileObjectInfo(Vector3f position) {
		super(position);
	}

	public IllumTileObjectInfo(float x, float y, float z) {
		super(x, y, z);
	}

	public IllumTileObjectInfo(Tile tile) {
		super(tile);
	}

	public IllumTileObjectInfo(Tile tile, float dx, float dy, float dz) {
		super(tile, dx, dy, dz);
	}

	public IllumTileObjectInfo(Tile tile, float dout, float dy) {
		super(tile, dout, dy);
	}

	public IllumTileObjectInfo illumMod(float r, float g, float b) {
		illumMod.set(r, g, b);
		return this;
	}

	public IllumTileObjectInfo illumMod(Vector3f c) {
		if(c==null)
			illumOff();
		else
			illumMod.set(c);
		return this;
	}
	
	public IllumTileObjectInfo illumMod(Color c) {
		if(c==null)
			illumOff();
		else
			illumMod.set(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
		return this;
	}
	
	public IllumTileObjectInfo illumOff() {
		illumMod.set(0, 0, 0);
		return this;
	}

}
