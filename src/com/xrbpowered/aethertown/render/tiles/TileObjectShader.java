package com.xrbpowered.aethertown.render.tiles;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.env.SkyBuffer;
import com.xrbpowered.gl.res.shader.CameraShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;

public class TileObjectShader extends CameraShader {

	public static final InstanceInfo instanceInfo = new InstanceInfo(ObjectShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_ScaleXZ", 1)
			.addAttrib("ins_ScaleY", 1)
			.addAttrib("ins_Rotation", 1);
	
	public static final String[] samplerNames = {"texSky", "texDiffuse"};
	
	public final SkyBuffer sky;
	private int viewYLocation;
	
	protected TileObjectShader(InstanceInfo info, String pathVS, String pathFS, SkyBuffer sky) {
		super(info, pathVS, pathFS);
		this.sky = sky;
	} 
	
	public TileObjectShader(SkyBuffer sky) {
		this(instanceInfo, "tileobj_v.glsl", "obj_f.glsl", sky);
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
	
	public void bindSkyTexture() {
		sky.bindTexture(0);
	}

}
