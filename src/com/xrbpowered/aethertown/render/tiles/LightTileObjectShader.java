package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.env.SkyBuffer;
import com.xrbpowered.gl.res.shader.InstanceInfo;

public class LightTileObjectShader extends TileObjectShader {

	public static final InstanceInfo instanceInfo = new InstanceInfo(ObjectShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_ScaleXZ", 1)
			.addAttrib("ins_ScaleY", 1)
			.addAttrib("ins_Rotation", 1)
			.addAttrib("ins_illumMod", 3);
	
	public static final String[] samplerNames = {"texSky", "texDiffuse", "texIllum"};
	
	public LightTileObjectShader(SkyBuffer sky) {
		super(instanceInfo, "lighttileobj_v.glsl", "lightobj_f.glsl", sky);
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
}
