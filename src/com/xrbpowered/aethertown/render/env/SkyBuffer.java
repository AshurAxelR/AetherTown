package com.xrbpowered.aethertown.render.env;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.render.sprites.SpriteComponent;
import com.xrbpowered.gl.res.buffer.OffscreenBuffer;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.CameraShader;
import com.xrbpowered.gl.scene.CameraActor.Perspective;
import com.xrbpowered.gl.ui.pane.PaneShader;

public class SkyBuffer {

	private static final int skyBufferScale = 8;
	
	public class SkyShader extends CameraShader {
		public SkyShader() {
			super(PaneShader.vertexInfo, "skyq_v.glsl", "skyq_f.glsl");
			followCamera = true;
		}
		
		private int fovLocation, aspectRatioLocation, viewYLocation;
		@Override
		protected void storeUniformLocations() {
			super.storeUniformLocations();
			fovLocation = GL20.glGetUniformLocation(pId, "fov");
			aspectRatioLocation = GL20.glGetUniformLocation(pId, "aspectRatio");
			viewYLocation = GL20.glGetUniformLocation(pId, "viewY");
		}
		
		@Override
		public void updateUniforms() {
			super.updateUniforms();
			GL20.glUniform1f(fovLocation, (float)Math.toRadians(((Perspective)camera).getFov()));
			GL20.glUniform1f(aspectRatioLocation, camera.getAspectRatio());
			GL20.glUniform1f(viewYLocation, camera.position.y);
		}
	}
	
	private StaticMesh screenQuad;
	private SkyShader shader;
	private OffscreenBuffer buffer = null;

	public SkyBuffer() {
		screenQuad = SpriteComponent.createFullQuad();
		shader = new SkyShader();
	}
	
	public void createBuffer(int w, int h) {
		if(buffer!=null)
			buffer.release();
		buffer = new OffscreenBuffer(w/skyBufferScale, h/skyBufferScale, false);
	}
	
	public SkyShader getShader() {
		return shader;
	}
	
	public void render(RenderTarget target) {
		buffer.use();
		GL11.glDepthMask(false);
		shader.use();
		screenQuad.draw();
		shader.unuse();
		GL11.glDepthMask(true);
		buffer.resolve();
		RenderTarget.blit(buffer, target, true);
		target.use();
	}
	
	public void bindTexture(int index) {
		buffer.bindColorBuffer(index);
	}
	
}
