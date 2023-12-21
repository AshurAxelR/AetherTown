package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;

public class ScaledTileObjectShader extends TileObjectShader {

	public static final InstanceInfo instanceInfo = new InstanceInfo(ObjectShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_Rotation", 1)
			.addAttrib("ins_ScaleXZ", 1)
			.addAttrib("ins_ScaleY", 1);

	
	public static final String[] samplerNames = {"texSky", "dataPointLights", "dataBlockLighting", "texDiffuse"};
	public static final String[] shaderDefs = {"SCALED_TILE"};

	protected ScaledTileObjectShader(String[] defs) {
		super(instanceInfo, "shaders/tiles/tileobj_v.glsl", "shaders/tiles/obj_f.glsl", defs);
	}

	public ScaledTileObjectShader() {
		this(shaderDefs);
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
	
	@Override
	public InstanceInfo getInstInfo() {
		return instanceInfo;
	}
	
	@Override
	public void setData(ObjectInfo aobj, float[] data, int offs) {
		super.setData(aobj, data, offs);
		ScaledTileObjectInfo obj = (ScaledTileObjectInfo) aobj;
		data[offs+4] = obj.scaleXZ;
		data[offs+5] = obj.scaleY;
	}

}
