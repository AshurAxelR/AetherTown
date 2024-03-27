package com.xrbpowered.aethertown.state;

import com.xrbpowered.aethertown.utils.RandomSeed;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;

public class TileRef {

	public final int x, z;
	
	public TileRef(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public TileRef(Tile tile) {
		this.x = tile.x;
		this.z = tile.z;
	}
	
	@Override
	public int hashCode() {
		return tileHash(x, z);
	}

	@Override
	public boolean equals(Object obj) {
		TileRef ref = (TileRef) obj;
		return this.x==ref.x && this.z==ref.z;
	}
	
	public static int tileHash(int x, int z) {
		return RandomSeed.smallHashXY(x, z);
	}
	
	public static int calcTileTemplateHash(TileTemplate t) {
		return t.getClass().getTypeName().hashCode();
	}
	
}
