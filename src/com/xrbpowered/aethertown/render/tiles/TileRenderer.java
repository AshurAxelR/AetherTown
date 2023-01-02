package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.render.env.ShaderEnvironment;
import com.xrbpowered.aethertown.render.sprites.SpriteComponent;
import com.xrbpowered.aethertown.render.sprites.SpriteShader;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;

public class TileRenderer {

	public final TileObjectShader shader;
	public final IllumTileObjectShader lightShader;
	public final SpriteShader spriteShader;

	public ComponentRenderer<?>[] renderers;
	
	public TileRenderer() {
		shader = new TileObjectShader();
		lightShader = new IllumTileObjectShader();
		spriteShader = new SpriteShader();
		
		TileComponent.renderer = new ComponentRenderer<TileComponent>() {
			@Override
			protected Shader getShader() {
				return shader;
			}
		};
		IllumTileComponent.renderer = new ComponentRenderer<IllumTileComponent>() {
			@Override
			protected Shader getShader() {
				return lightShader;
			}
		};
		SpriteComponent.renderer = new ComponentRenderer<SpriteComponent>() {
			@Override
			protected Shader getShader() {
				return spriteShader;
			}
		};
		
		renderers = new ComponentRenderer<?>[] {
			TileComponent.renderer,
			IllumTileComponent.renderer,
			SpriteComponent.renderer
		};
	}
	
	public TileRenderer setCamera(CameraActor camera) {
		shader.setCamera(camera);
		lightShader.setCamera(camera);
		spriteShader.setCamera(camera);
		return this;
	}

	public void updateEnvironment(ShaderEnvironment environment) {
		environment.updateShader(shader);
		environment.updateShader(lightShader);
	}
	
	public void startCreateInstances() {
		for(ComponentRenderer<?> r : renderers)
			r.startCreateInstances();
	}

	public void finishCreateInstances() {
		for(ComponentRenderer<?> r : renderers)
			r.finishCreateInstances();
	}

	public void drawInstances() {
		for(ComponentRenderer<?> r : renderers)
			r.drawInstances();
	}

}
