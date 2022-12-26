package com.xrbpowered.aethertown.render;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class ObjectShader extends ActorShader {

	public static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_Normal", 3)
			.addAttrib("in_TexCoord", 2);
	
	public static final String[] samplerNames = {"texSky", "dataPointLights", "texDiffuse"};
	
	private int viewYLocation;

	public ObjectShader() {
		super(vertexInfo, "placeobj_v.glsl", "obj_f.glsl");
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		viewYLocation = GL20.glGetUniformLocation(pId, "viewY");
		initSamplers(samplerNames);
	}
	
	@Override
	public void updateUniforms() {
		super.updateUniforms();
		GL20.glUniform1f(viewYLocation, camera.position.y);
	}
	
	public void setLevel(LevelRenderer level) {
		int pId = getProgramId();
		GL20.glUseProgram(pId);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "levelSize"), level.level.levelSize);
		level.sky.bindTexture(0);
		level.pointLights.bind(1);
	}
	
}
