package com.xrbpowered.aethertown.render;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.render.env.SkyBuffer;
import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class ObjectShader extends ActorShader {

	public static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_Normal", 3)
			.addAttrib("in_TexCoord", 2);
	
	public static final String[] samplerNames = {"texSky", "texDiffuse"};
	
	public final SkyBuffer sky;
	private int viewYLocation;

	public ObjectShader(SkyBuffer sky) {
		super(vertexInfo, "placeobj_v.glsl", "obj_f.glsl");
		this.sky = sky;
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
	
	public void bindSkyTexture() {
		sky.bindTexture(0);
	}
	
}
