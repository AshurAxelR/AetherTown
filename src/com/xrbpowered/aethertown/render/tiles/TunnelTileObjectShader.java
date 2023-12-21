package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.gl.res.texture.Texture;

public class TunnelTileObjectShader extends ScaledTileObjectShader {

	public static final String[] samplerNames = {"texSky", "dataPointLights", "dataBlockLighting", "dataTunnelDepth", "texDiffuse"};
	public static final int numGlobalSamplers = 4;
	public static final String[] shaderDefs = {"SCALED_TILE", "TUNNEL_TILE"};

	public TunnelTileObjectShader() {
		super(shaderDefs);
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
	
	@Override
	public void updateUniforms() {
		super.updateUniforms();
		level.tunnelDepthMap.bind(3);
	}
	
	@Override
	public void bindTextures(Texture[] textures) {
		Texture.bindAll(numGlobalSamplers, textures);
	}

}
