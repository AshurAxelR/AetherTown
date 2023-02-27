package com.xrbpowered.aethertown.render;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import com.xrbpowered.aethertown.render.env.SkyBuffer;
import com.xrbpowered.aethertown.render.tiles.TileRenderer;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;

public class LevelRenderer {

	public final Level level;
	public final SkyBuffer sky;
	public final TileRenderer tiles;
	public final ComponentRenderer<?>[] renderers;
	
	public final Vector2f levelOffset = new Vector2f();
	
	public TerrainBuilder terrain = null;
	public PointLightArray pointLights = null;
	public BlockLighting blockLighting = null;
	
	private ArrayList<StaticMeshActor> terrainActors = null;

	public LevelRenderer(Level level, SkyBuffer sky, TileRenderer tiles) {
		this.level = level;
		this.sky = sky;
		this.tiles = tiles;
		this.renderers = tiles.createRenderers(this);
	}
	
	public float levelDist(float x, float z) {
		float r = level.levelSize*Tile.size*0.5f;
		float dx = levelOffset.x + r - x;
		float dz = levelOffset.y + r - z;
		return (float)Math.sqrt(dx*dx+dz*dz) - r*(float)Math.sqrt(2);
	}

	public void createLevelGeometry() {
		pointLights = new PointLightArray(level.levelSize); // FIXME point light problem for larger levels
		blockLighting = new BlockLighting(level);
		tiles.startCreateInstances(this);
		terrain = new TerrainBuilder(level);
		level.createGeometry(this);
		terrainActors = terrain.createActors(tiles.objShader);
		tiles.finishCreateInstances(this);
		terrain = null;
		
		pointLights.finish();
		blockLighting.finish();
	}
	
	public void render(RenderTarget target) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		tiles.setLevel(this);
		tiles.drawInstances(this);
		tiles.objShader.setLevel(this);
		for(StaticMeshActor actor : terrainActors)
			actor.draw();
	}
	
	public void release() {
		tiles.releaseRenderers(this);
		pointLights.release();
		blockLighting.release();
		for(StaticMeshActor actor : terrainActors) {
			StaticMesh mesh = actor.getMesh();
			if(mesh!=null)
				mesh.release();
		}
	}
	
}
