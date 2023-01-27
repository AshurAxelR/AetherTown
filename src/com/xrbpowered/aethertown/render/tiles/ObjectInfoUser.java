package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.gl.res.shader.InstanceInfo;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.texture.Texture;

public interface ObjectInfoUser {

	public Shader getShader();
	public InstanceInfo getInstInfo();
	public void bindTextures(Texture[] textures);
	public void setData(ObjectInfo obj, float[] data, int offs);
	
}
