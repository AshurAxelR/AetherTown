package com.xrbpowered.aethertown.render.env;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.render.sprites.SpriteComponent;
import com.xrbpowered.aethertown.render.sprites.SpriteShader;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public class SunRenderer {

	public class SunShader extends SpriteShader {
		
		private int positionLocation;
		
		public SunShader() {
			super(SpriteShader.vertexInfo, "sun_v.glsl", "sun_f.glsl");
			followCamera = true;
		}
		
		@Override
		protected String[] getSamplerNames() {
			return null;
		}
		
		@Override
		protected void storeUniformLocations() {
			super.storeUniformLocations();
			positionLocation = GL20.glGetUniformLocation(pId, "position");
		}
		
		@Override
		public void updateUniforms() {
			super.updateUniforms();

			glDisable(GL_DEPTH_TEST);
			glEnable(GL11.GL_BLEND);
			glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			uniform(positionLocation, position);
		}
		
		@Override
		public void unuse() {
			glDisable(GL11.GL_BLEND);
			glEnable(GL_DEPTH_TEST);
			super.unuse();
		}
	}

	private SunShader shader;
	private StaticMesh quad;
	private Texture color;
	
	public final Vector4f position = new Vector4f();

	public SunRenderer() {
		quad = SpriteComponent.createSpriteQuad();
		shader = new SunShader();
		color = new Texture("checker.png");
	}

	public SunShader getShader() {
		return shader;
	}
	
	public void render() {
		if(position.y<-0.3f)
			return;
		shader.use();
		color.bind(0);
		quad.draw();
		shader.unuse();
	}
	
}
