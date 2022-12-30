package com.xrbpowered.aethertown.render.sprites;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.shader.CameraShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.scene.CameraActor;

public class SpriteShader extends CameraShader {

	public static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 2)
			.addAttrib("in_TexCoord", 2);
	
	public static final InstanceInfo instanceInfo = new InstanceInfo(vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_Size", 1);
	
	public static final String[] samplerNames = {"texColor"};
	
	private int invAspectRatioLocation;
	private int fovFactorLocation;
	
	protected SpriteShader(VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
	} 
	
	public SpriteShader() {
		super(instanceInfo, "shaders/tiles/sprite_v.glsl", "shaders/tiles/sprite_f.glsl");
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		invAspectRatioLocation = GL20.glGetUniformLocation(pId, "invAspectRatio");
		fovFactorLocation = GL20.glGetUniformLocation(pId, "fovFactor");
		initSamplers(getSamplerNames());
	}
	
	@Override
	public void updateUniforms() {
		super.updateUniforms();
		GL20.glUniform1f(invAspectRatioLocation, 1f/camera.getAspectRatio());
		float ff = 1f/(float)Math.tan(Math.toRadians(((CameraActor.Perspective)camera).getFov()) / 2.0);
		GL20.glUniform1f(fovFactorLocation, ff);
	}
	
}
