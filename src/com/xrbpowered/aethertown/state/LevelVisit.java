package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.region.LevelInfo;

public class LevelVisit extends LevelRef {

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
		
		boolean res = true;
		for(TileVisit v : visitedTiles.values()) {
			if(!v.isValid(level)) {
				res = false;
				break;
			}
		}
		validated = true;
		return res;
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
		for(int i=0; i<n; i++)
			add(TileVisit.load(in));
	}

	public void saveVisits(DataOutputStream out) throws IOException {
		out.writeInt(visitedTiles.size());
		for(TileVisit v : visitedTiles.values())
			TileVisit.save(out, v);
	}

}
