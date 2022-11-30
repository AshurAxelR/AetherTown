package com.xrbpowered.aethertown.render.tiles;

import java.awt.Color;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.world.Tile;

public class LightTileObjectInfo extends TileObjectInfo {

	public Vector3f illumMod = new Vector3f(1, 1, 1);
	
	public LightTileObjectInfo(TileObjectInfo info) {
		super(info);
	}

	public LightTileObjectInfo(Vector3f position) {
		super(position);
	}

	public LightTileObjectInfo(float x, float y, float z) {
		super(x, y, z);
	}

	public LightTileObjectInfo(Tile tile) {
		super(tile);
	}

	public LightTileObjectInfo(Tile tile, float dx, float dy, float dz) {
		super(tile, dx, dy, dz);
	}
	
	public LightTileObjectInfo illumMod(float r, float g, float b) {
		illumMod.set(r, g, b);
		return this;
	}

	public LightTileObjectInfo illumMod(Vector3f c) {
		if(c==null)
			illumOff();
		else
			illumMod.set(c);
		return this;
	}
	
	public LightTileObjectInfo illumMod(Color c) {
		illumMod.set(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
		return this;
	}
	
	public LightTileObjectInfo illumOff() {
		illumMod.set(0, 0, 0);
		return this;
	}

	public void setData(float[] data, int offs) {
		super.setData(data, offs);
		data[offs+6] = illumMod.x;
		data[offs+7] = illumMod.y;
		data[offs+8] = illumMod.z;
	}

}
