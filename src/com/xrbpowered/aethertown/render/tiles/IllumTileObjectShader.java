package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;

public class IllumTileObjectShader extends TileObjectShader {

	public static final InstanceInfo instanceInfo = new InstanceInfo(ObjectShader.vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_ScaleXZ", 1)
			.addAttrib("ins_ScaleY", 1)
			.addAttrib("ins_Rotation", 1)
			.addAttrib("ins_illumMod", 3)
			.addAttrib("ins_illumMask", 1)
			.addAttrib("ins_illumTrigger", 1);
	
	public static final String[] samplerNames = {"texSky", "dataPointLights", "dataBlockLighting", "texDiffuse", "texIllum"};
	public static final String[] shaderDefs = {"ILLUM_TILE"};
	
	public IllumTileObjectShader() {
		super(instanceInfo, "shaders/tiles/tileobj_v.glsl", "shaders/tiles/obj_f.glsl", shaderDefs);
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
		IllumTileObjectInfo obj = (IllumTileObjectInfo) aobj;
		data[offs+6] = obj.illumMod.x;
		data[offs+7] = obj.illumMod.y;
		data[offs+8] = obj.illumMod.z;
		data[offs+9] = obj.illumMask;
		data[offs+10] = obj.illumTrigger;
	}
	
}
