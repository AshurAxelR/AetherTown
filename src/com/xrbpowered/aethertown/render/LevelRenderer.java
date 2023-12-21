package com.xrbpowered.aethertown.render;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import com.xrbpowered.aethertown.render.TerrainChunkBuilder.TerrainMeshActor;
import com.xrbpowered.aethertown.render.env.SkyBuffer;
import com.xrbpowered.aethertown.render.tiles.TileRenderer;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.buffer.RenderTarget;

public class LevelRenderer {

	public static final int solidRenderPass = 0;
	public static final int spriteRenderPass = 1;
	public static final int[] renderPassList = {solidRenderPass, spriteRenderPass};
	
	public final Level level;
	public final SkyBuffer sky;
	public final TileRenderer tiles;
	public final LevelComponentRenderer[] renderers;
	
	public final Vector2f levelOffset = new Vector2f();
	
	public TerrainBuilder terrain = null;
	public PointLightArray pointLights = null;
	public BlockLighting blockLighting = null;
	public TunnelDepthMap tunnelDepthMap = null;
	
	private ArrayList<TerrainMeshActor> terrainActors = null;

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
		pointLights = new PointLightArray(level.levelSize);
		blockLighting = new BlockLighting(level);
		tunnelDepthMap = new TunnelDepthMap(level);
		tiles.startCreateInstances(this);
		terrain = new TerrainBuilder(level);
		level.createGeometry(this);
		terrainActors = terrain.createActors(tiles.objShader);
		tiles.finishCreateInstances(this);
		terrain = null;
		
		pointLights.finish();
		blockLighting.finish();
		tunnelDepthMap.finish();
	}
	
	public void render(RenderTarget target, int renderPass) {
		tiles.setLevel(this);
		if(renderPass==solidRenderPass) {
			GL11.glEnable(GL11.GL_CULL_FACE);
			for(TerrainMeshActor actor : terrainActors)
				actor.draw();
		}
		tiles.drawInstances(this, renderPass);
	}
	
	public void release() {
		tiles.releaseRenderers(this);
		pointLights.release();
		blockLighting.release();
		tunnelDepthMap.release();
		for(TerrainMeshActor actor : terrainActors) {
			actor.release();
		}
	}
	
}
