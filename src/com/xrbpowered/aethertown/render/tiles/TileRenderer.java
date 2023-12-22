package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.render.LevelComponentRenderer;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.env.ShaderEnvironment;
import com.xrbpowered.aethertown.render.sprites.SpriteComponent;
import com.xrbpowered.aethertown.render.sprites.SpriteShader;
import com.xrbpowered.gl.scene.CameraActor;

public class TileRenderer {

	public final TileObjectShader shader;
	public final ScaledTileObjectShader scaleShader;
	public final IllumTileObjectShader lightShader;
	public final TunnelTileObjectShader tunnelShader;
	public final SpriteShader spriteShader;
	public final ObjectShader objShader;

	public int illumMask = 0;
	
	public TileRenderer() {
		shader = new TileObjectShader();
		scaleShader = new ScaledTileObjectShader();
		lightShader = new IllumTileObjectShader();
		tunnelShader = new TunnelTileObjectShader();
		spriteShader = new SpriteShader();
		objShader = new ObjectShader();
	}
	
	public TileRenderer setCamera(CameraActor camera) {
		shader.setCamera(camera);
		scaleShader.setCamera(camera);
		lightShader.setCamera(camera);
		tunnelShader.setCamera(camera);
		spriteShader.setCamera(camera);
		objShader.setCamera(camera);
		return this;
	}

	public void updateEnvironment(ShaderEnvironment environment) {
		environment.updateShader(shader);
		environment.updateShader(scaleShader);
		environment.updateShader(lightShader);
		environment.updateShader(tunnelShader);
		environment.updateShader(spriteShader);
		environment.updateShader(objShader);
		illumMask = environment.illumMask;
	}
	
	public void setLevel(LevelRenderer r) {
		shader.level = r;
		scaleShader.level = r;
		lightShader.level = r;
		tunnelShader.level = r;
		spriteShader.level = r;
		objShader.level = r;
	}
	
	public LevelComponentRenderer[] createRenderers(LevelRenderer r) {
		return new LevelComponentRenderer[] {
			TileComponent.createRenderer(r, shader),
			ScaledTileComponent.createRenderer(r, scaleShader),
			TunnelTileComponent.createRenderer(r, tunnelShader),
			IllumTileComponent.createRenderer(r, lightShader),
			SpriteComponent.createRenderer(r, spriteShader)
		};
	}
	
	public void releaseRenderers(LevelRenderer r) {
		TileComponent.releaseRenderer(r);
		ScaledTileComponent.releaseRenderer(r);
		TunnelTileComponent.releaseRenderer(r);
		IllumTileComponent.releaseRenderer(r);
		SpriteComponent.releaseRenderer(r);
	}
	
	public void startCreateInstances(LevelRenderer r) {
		for(LevelComponentRenderer cr : r.renderers)
			cr.startCreateInstances();
	}

	public void finishCreateInstances(LevelRenderer r) {
		for(LevelComponentRenderer cr : r.renderers)
			cr.finishCreateInstances();
	}

	public void drawInstances(LevelRenderer r, int renderPass) {
		for(LevelComponentRenderer cr : r.renderers) {
			if(renderPass==cr.renderPass)
				cr.drawInstances();
		}
	}

}
