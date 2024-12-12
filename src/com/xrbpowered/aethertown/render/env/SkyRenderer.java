package com.xrbpowered.aethertown.render.env;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.scene.CameraActor;

public class SkyRenderer {

	public final SkyBuffer buffer;
	public final StarRenderer stars;
	public final SunRenderer sun;
	
	private boolean renderStars;

	public SkyRenderer(float renderScale) {
		buffer = new SkyBuffer();
		stars = new StarRenderer(renderScale);
		sun = new SunRenderer();
	}
	
	public SkyRenderer setCamera(CameraActor camera) {
		buffer.getShader().setCamera(camera);
		stars.getShader().setCamera(camera);
		sun.getShader().setCamera(camera);
		return this;
	}
	
	public void updateEnvironment(ShaderEnvironment environment) {
		environment.updateShader(buffer.getShader());
		environment.updateShader(stars.getShader());
		environment.updateShader(sun.getShader());
		renderStars = environment.renderStars;
	}
	
	public void updateTime(float dt) {
		WorldTime.updateTime(dt);
		stars.update(sun.position);
	}
	
	public void render(RenderTarget target, LevelRenderer level) {
		buffer.render(target, level);
		if(renderStars)
			stars.render(level);
		sun.render(level);
	}

}
