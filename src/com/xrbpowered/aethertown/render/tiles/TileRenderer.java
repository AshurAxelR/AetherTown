package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.env.ShaderEnvironment;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;

public class TileRenderer {

	public final TileObjectShader shader;
	public final IllumTileObjectShader lightShader;
	// public final SpriteShader spriteShader;
	public final ObjectShader objShader;

	public TileRenderer() {
		shader = new TileObjectShader();
		lightShader = new IllumTileObjectShader();
		// spriteShader = new SpriteShader();
		objShader = new ObjectShader();
	}
	
	public TileRenderer setCamera(CameraActor camera) {
		shader.setCamera(camera);
		lightShader.setCamera(camera);
		// spriteShader.setCamera(camera);
		objShader.setCamera(camera);
		return this;
	}

	public void updateEnvironment(ShaderEnvironment environment) {
		environment.updateShader(shader);
		environment.updateShader(lightShader);
		environment.updateShader(objShader);
	}
	
	public void setLevel(LevelRenderer r) {
		shader.setLevel(r);
		lightShader.setLevel(r);
		objShader.setLevel(r); // FIXME setLevel on shader.use()
	}
	
	public ComponentRenderer<?>[] createRenderers(LevelRenderer r) {
		return new ComponentRenderer<?>[] {
			TileComponent.createRenderer(r, shader),
			IllumTileComponent.createRenderer(r, lightShader),
			// SpriteComponent.createRenderer(r, spriteShader)
		};
	}
	
	public void releaseRenderers(LevelRenderer r) {
		TileComponent.releaseRenderer(r);
		IllumTileComponent.releaseRenderer(r);
		// SpriteComponent.releaseRenderer(r);
	}
	
	public void startCreateInstances(LevelRenderer r) {
		for(ComponentRenderer<?>cr : r.renderers)
			cr.startCreateInstances();
	}

	public void finishCreateInstances(LevelRenderer r) {
		for(ComponentRenderer<?> cr : r.renderers)
			cr.finishCreateInstances();
	}

	public void drawInstances(LevelRenderer r) {
		for(ComponentRenderer<?> cr : r.renderers)
			cr.drawInstances();
	}

}
