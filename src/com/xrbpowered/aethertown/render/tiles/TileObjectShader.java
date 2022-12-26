package com.xrbpowered.aethertown.render.tiles;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.gl.res.shader.CameraShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;

public class TileObjectShader extends CameraShader {

	public static final InstanceInfo instanceInfo = new InstanceInfo(ObjectShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_ScaleXZ", 1)
			.addAttrib("ins_ScaleY", 1)
			.addAttrib("ins_Rotation", 1);
	
	public static final String[] samplerNames = {"texSky", "dataPointLights", "texDiffuse"};
	
	private int viewYLocation;
	
	protected TileObjectShader(InstanceInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
	} 
	
	public TileObjectShader() {
		this(instanceInfo, "tileobj_v.glsl", "obj_f.glsl");
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		initSamplers(getSamplerNames());
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
