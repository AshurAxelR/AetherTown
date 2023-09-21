package com.xrbpowered.aethertown.render.tiles;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.BlockLighting;
import com.xrbpowered.aethertown.world.Tile;

public class IllumTileObjectInfo extends TileObjectInfo {

	public Vector3f illumMod = new Vector3f(1, 1, 1);
	public int illumMask = IllumLayer.alwaysOn.mask();
	public float illumTrigger = BlockLighting.illumTrigger;
	
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

	public IllumTileObjectInfo illumMod(Vector3f mod) {
		if(mod==null)
			illumOff();
		else
			illumMod.set(mod);
		return this;
	}
	
	public IllumTileObjectInfo illumOff() {
		illumMod.set(0, 0, 0);
		return this;
	}
	
	public IllumTileObjectInfo illum(IllumLayer layer) {
		illumMask = (layer==null) ? 0 : layer.mask();
		return this;
	}

	public IllumTileObjectInfo illum(IllumLayer layer, float trigger) {
		illum(layer);
		illumTrigger = trigger;
		return this;
	}

}
