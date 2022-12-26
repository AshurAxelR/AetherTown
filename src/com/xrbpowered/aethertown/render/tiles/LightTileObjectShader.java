package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;

public class LightTileObjectShader extends TileObjectShader {

	public static final InstanceInfo instanceInfo = new InstanceInfo(ObjectShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_ScaleXZ", 1)
			.addAttrib("ins_ScaleY", 1)
			.addAttrib("ins_Rotation", 1)
			.addAttrib("ins_illumMod", 3);
	
	public static final String[] samplerNames = {"texSky", "dataPointLights", "texDiffuse", "texIllum"};
	
	public LightTileObjectShader() {
		super(instanceInfo, "lighttileobj_v.glsl", "lightobj_f.glsl");
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
}
