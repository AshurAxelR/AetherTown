package com.xrbpowered.aethertown.render.tiles;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Tile;

public class TileObjectInfo {

	public Vector3f position = new Vector3f();
	public float scaleXZ = 1f;
	public float scaleY = 1f;
	public float rotation = 0f;

	public TileObjectInfo(TileObjectInfo info) {
		this.position.set(info.position);
		this.scaleXZ = info.scaleXZ;
		this.scaleY = info.scaleY;
		this.rotation = info.rotation;
	}
	
	public TileObjectInfo(Vector3f position) {
		this.position.set(position);
	}

	public TileObjectInfo(float x, float y, float z) {
		this.position.set(x, y, z);
	}

	public TileObjectInfo(Tile tile) {
		this.position.set(tile.x*Tile.size, tile.basey*Tile.ysize, tile.z*Tile.size);
		rotate(tile.d);
	}

	public TileObjectInfo(Tile tile, float dx, float dy, float dz) {
		this.position.set((tile.x+dx)*Tile.size, (tile.basey+dy)*Tile.ysize, (tile.z+dz)*Tile.size);
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
	
	public void setData(float[] data, int offs) {
		data[offs+0] = position.x;
		data[offs+1] = position.y;
		data[offs+2] = position.z;
		data[offs+3] = scaleXZ;
		data[offs+4] = scaleY;
		data[offs+5] = rotation;
	}

}
