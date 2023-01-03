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
	public SkyBuffer sky;
	public PointLightArray pointLights = null;
	public BlockLighting blockLighting = null;
	
	private ArrayList<StaticMeshActor> terrainActors = null;

	public LevelRenderer(Level level, SkyBuffer sky) {
		this.level = level;
		this.sky = sky;
		objShader = new ObjectShader();
		tiles = new TileRenderer();
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
		pointLights = new PointLightArray(level.levelSize);
		blockLighting = new BlockLighting(level.levelSize);
		tiles.startCreateInstances();
		terrain = new TerrainBuilder(level);
		level.createGeometry(this);
		terrainActors = terrain.createActors(objShader);
		tiles.finishCreateInstances();
		terrain = null;
		
		/*for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				Tile tile = level.map[x][z];
				if(tile!=null)
					pointLights.setLight(tile, 0, 2, 0);
			}*/
		pointLights.finish();
		blockLighting.finish();
	}
	
	public void render(RenderTarget target) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		tiles.shader.setLevel(this);
		tiles.lightShader.setLevel(this);
		tiles.drawInstances();
		
		objShader.setLevel(this);
		for(StaticMeshActor actor : terrainActors)
			actor.draw();

	}
}
