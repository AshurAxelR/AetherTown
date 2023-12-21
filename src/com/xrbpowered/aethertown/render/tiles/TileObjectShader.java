package com.xrbpowered.aethertown.render.tiles;

import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.gl.res.shader.CameraShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.texture.Texture;

public class TileObjectShader extends CameraShader implements ObjectInfoUser {

	public static final InstanceInfo instanceInfo = new InstanceInfo(ObjectShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_Rotation", 1);
	
	public static final String[] samplerNames = {"texSky", "dataPointLights", "dataBlockLighting", "texDiffuse"};
	public static final int numGlobalSamplers = 3;
	
	public LevelRenderer level;

	private int viewYLocation;
	private int levelSizeLocation;
	private int levelOffsetLocation;

	protected TileObjectShader(InstanceInfo info, String pathVS, String pathFS, String[] defs) {
		super(info, pathVS, pathFS, defs);
	} 
	
	public TileObjectShader() {
		this(instanceInfo, "shaders/tiles/tileobj_v.glsl", "shaders/tiles/obj_f.glsl", null);
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		viewYLocation = GL20.glGetUniformLocation(pId, "viewY");
		levelSizeLocation = GL20.glGetUniformLocation(pId, "levelSize");
		levelOffsetLocation = GL20.glGetUniformLocation(pId, "levelOffset");
		initSamplers(getSamplerNames());
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

	@Override
	public InstanceInfo getInstInfo() {
		return instanceInfo;
	}
	
	@Override
	public Shader getShader() {
		return this;
	}
	
	@Override
	public void bindTextures(Texture[] textures) {
		Texture.bindAll(numGlobalSamplers, textures);
	}
	
	@Override
	public void setData(ObjectInfo aobj, float[] data, int offs) {
		TileObjectInfo obj = (TileObjectInfo) aobj;
		data[offs+0] = obj.position.x;
		data[offs+1] = obj.position.y;
		data[offs+2] = obj.position.z;
		data[offs+3] = obj.rotation;
	}
	
}
