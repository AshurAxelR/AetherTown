package com.xrbpowered.aethertown.render;

import com.xrbpowered.aethertown.render.tiles.ObjectInfoUser;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;

public class LevelComponentRenderer extends ComponentRenderer<TileComponent.InstanceList> {

	public final int renderPass;
	public final ObjectInfoUser shader;
	
	public LevelComponentRenderer(int renderPass, ObjectInfoUser shader) {
		this.renderPass = renderPass;
		this.shader = shader;
	}

	@Override
	protected Shader getShader() {
		return shader.getShader();
	}

}
