package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;

public class IllumTileObjectShader extends TileObjectShader {

	public static final InstanceInfo instanceInfo = new InstanceInfo(ObjectShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_ScaleXZ", 1)
			.addAttrib("ins_ScaleY", 1)
			.addAttrib("ins_Rotation", 1)
			.addAttrib("ins_illumMod", 3);
	
	public static final String[] samplerNames = {"texSky", "dataPointLights", "texDiffuse", "texIllum"};
	public static final String[] shaderDefs = {"ILLUM_TILE"};
	
	public IllumTileObjectShader() {
		super(instanceInfo, "shaders/tiles/tileobj_v.glsl", "shaders/tiles/obj_f.glsl", shaderDefs);
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
}
