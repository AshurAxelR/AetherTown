package com.xrbpowered.aethertown.render;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class ObjectShader extends ActorShader {

	public static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_Normal", 3)
			.addAttrib("in_TexCoord", 2);
	
	public static final String[] samplerNames = {"texSky", "dataPointLights", "dataBlockLighting", "texDiffuse"};
	
	public LevelRenderer level;
	
	private int viewYLocation;
	private int levelSizeLocation;
	private int levelOffsetLocation;

	public ObjectShader() {
		super(vertexInfo, "shaders/tiles/placeobj_v.glsl", "shaders/tiles/obj_f.glsl");
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		viewYLocation = GL20.glGetUniformLocation(pId, "viewY");
		levelSizeLocation = GL20.glGetUniformLocation(pId, "levelSize");
		levelOffsetLocation = GL20.glGetUniformLocation(pId, "levelOffset");
		initSamplers(samplerNames);
	}
	
	@Override
	public void updateUniforms() {
		super.updateUniforms();
		GL20.glUniform1f(viewYLocation, camera.position.y);
		GL20.glUniform1f(levelSizeLocation, level.level.levelSize);
		uniform(levelOffsetLocation, level.levelOffset);
		level.sky.bindTexture(0);
		level.pointLights.bind(1);
		level.blockLighting.bind(level.tiles.illumMask, 2);
	}
	
}
