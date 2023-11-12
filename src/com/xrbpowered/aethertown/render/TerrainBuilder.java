package com.xrbpowered.aethertown.render;

import java.awt.Color;
import java.util.ArrayList;

import com.xrbpowered.aethertown.render.TerrainChunkBuilder.TerrainMeshActor;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.Fences.FenceType;
import com.xrbpowered.gl.res.shader.ActorShader;

public class TerrainBuilder {

	public static final int chunkSize = 64; 
	
	public static final Color cliffColor = new Color(0x7d7f6e);
	public static final Color wallColor = new Color(0x8f8b7e); // new Color(0x949187); // new Color(0x85857a);

	public final Level level;
	public final int csize;
	private TerrainChunkBuilder[][] chunks;
	
	public TerrainBuilder(Level level) {
		this.level = level;
		if(level.levelSize%chunkSize!=0)
			System.err.println("Level size is not a multiple of chunk size");
		this.csize = level.levelSize/chunkSize;
		this.chunks = new TerrainChunkBuilder[csize][csize];
		for(int cx=0; cx<csize; cx++)
			for(int cz=0; cz<csize; cz++) {
				chunks[cx][cz] = new TerrainChunkBuilder(level, cx, cz);
			}
	}
	
	public void addHillTile(TerrainMaterial c, int x, int z, int y00, int y01, int y10, int y11, boolean diag) {
		int cx = x/chunkSize;
		int cz = z/chunkSize;
		chunks[cx][cz].addHillTile(c, x, z, y00, y01, y10, y11, diag);
	}
	
	public void addHillTile(TerrainMaterial c, int x, int z) {
		HeightMap h = level.h;
		addHillTile(c, x, z,
			h.y[x][z],
			h.y[x][z+1],
			h.y[x+1][z],
			h.y[x+1][z+1],
			h.diag[x][z]
		);
	}

	public void addHillTile(TerrainMaterial c, Tile tile) {
		addHillTile(c, tile.x, tile.z);
	}

	public void addFlatTile(TerrainMaterial c, int x, int y, int z) {
		addHillTile(c, x, z,
			y, y, y, y, true
		);
	}

	public void addFlatTile(TerrainMaterial c, Tile tile) {
		addFlatTile(c, tile.x, tile.basey, tile.z);
	}

	public void addWall(int x, int z, Dir d, int y00, int y01, int y10, int y11) {
		int cx = x/chunkSize;
		int cz = z/chunkSize;
		chunks[cx][cz].addWall(x, z, d, y00, y01, y10, y11);
	}

	public void addWall(int x, int z, Dir d, int basey0, int basey1) {
		int cx = x/chunkSize;
		int cz = z/chunkSize;
		chunks[cx][cz].addWall(x, z, d, basey0, basey1);
	}

	public void addWalls(int x, int z, int basey) {
		for(Dir d : Dir.values()) {
			addWall(x, z, d, basey, basey);
		}
	}
	
	public void addWalls(Tile tile) {
		for(Dir d : Dir.values()) {
			if(tile.getFence(d)!=FenceType.stepsOut)
				addWall(tile.x, tile.z, d, tile.basey, tile.basey);
		}
	}

	public ArrayList<TerrainMeshActor> createActors(ActorShader shader) {
		ArrayList<TerrainMeshActor> actors = new ArrayList<>();
		for(int cx=0; cx<csize; cx++)
			for(int cz=0; cz<csize; cz++) {
				chunks[cx][cz].createActors(shader, actors);
			}
		return actors;
	}
	
}
