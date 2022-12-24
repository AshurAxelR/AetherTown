package com.xrbpowered.aethertown.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.xrbpowered.aethertown.render.env.ShaderEnvironment;
import com.xrbpowered.aethertown.render.env.SkyBuffer;
import com.xrbpowered.aethertown.render.tiles.TileRenderer;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class LevelRenderer {

	public final Level level;
	
	public final ObjectShader objShader;
	public final TileRenderer tiles;

	public TerrainBuilder terrain = null;
	
	private ArrayList<StaticMeshActor> terrainActors = null;

	public LevelRenderer(Level level, SkyBuffer sky) {
		this.level = level;
		objShader = new ObjectShader(sky);
		tiles = new TileRenderer(sky);
	}

	public LevelRenderer setCamera(CameraActor camera) {
		tiles.setCamera(camera);
		objShader.setCamera(camera);
		return this;
	}
	
	public void updateEnvironment(ShaderEnvironment environment) {
		environment.updateShader(objShader);
		tiles.updateEnvironment(environment);
	}
	
	public void createLevelGeometry() {
		tiles.startCreateInstances();
		terrain = new TerrainBuilder(level);
		level.createGeometry(this);
		terrainActors = terrain.createActors(objShader);
		tiles.finishCreateInstances();
		terrain = null;
	}
	
	public void render(RenderTarget target) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		tiles.shader.bindSkyTexture();
		tiles.lightShader.bindSkyTexture();
		tiles.drawInstances();
		
		objShader.bindSkyTexture();
		for(StaticMeshActor actor : terrainActors)
			actor.draw();

	}
}
