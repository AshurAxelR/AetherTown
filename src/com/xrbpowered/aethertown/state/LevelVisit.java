package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.region.LevelInfo;

public class LevelVisit extends LevelRef {

	private static class TileVisit extends TileRef {
		public final int templateHash;
		
		public TileVisit(int x, int z, int templateHash) {
			super(x, z);
			this.templateHash = templateHash;
		}
		
		public TileVisit(Tile tile) {
			super(tile);
			this.templateHash = calcTileTemplateHash(tile.t);
		}
	}

	private final HashMap<TileRef, TileVisit> visitedTiles = new HashMap<>();

	private boolean validated = false;

	public LevelVisit(long seed, int x, int z) {
		super(seed, x, z);
	}

	public LevelVisit(LevelInfo level) {
		super(level);
	}
	
	public void resetVisits() {
		visitedTiles.clear();
	}
	
	public boolean validateVisits(Level level) {
		if(validated)
			return true;
		if(!this.isLevel(level.info))
			return false;
		for(TileVisit v : visitedTiles.values()) {
			if(!level.isInside(v.x, v.z))
				return false;
			Tile tile = level.map[v.x][v.z];
			if(TileRef.calcTileTemplateHash(tile.t)!=v.templateHash)
				return false;
		}
		validated = true;
		return true;
	}
	
	public boolean isVisited(Tile tile) {
		return visitedTiles.get(tile.ref)!=null;
	}
	
	private void add(TileVisit v) {
		visitedTiles.put(v, v);
	}
	
	public void visitTile(Tile tile) {
		if(!isVisited(tile))
			add(new TileVisit(tile));
	}
	
	public void loadVisits(DataInputStream in) throws IOException {
		int n = in.readInt();
		for(int i=0; i<n; i++) {
			int x = in.readShort();
			int z = in.readShort();
			int hash = in.readInt();
			add(new TileVisit(x, z, hash));
		}
	}

	public void saveVisits(DataOutputStream out) throws IOException {
		out.writeInt(visitedTiles.size());
		for(TileVisit v : visitedTiles.values()) {
			out.writeShort(v.x);
			out.writeShort(v.z);
			out.writeInt(v.templateHash);
		}
	}

}
