package com.xrbpowered.aethertown.ui.hud;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.ui.pane.PaneShader;

public class RotatedPaneShader extends Shader {

	public static VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 2)
			.addAttrib("in_TexCoord", 2);

	public final StaticMesh quad;
	
	private RotatedPaneShader() {
		super(vertexInfo, "shaders/rot_pane_v.glsl", "shaders/rot_pane_f.glsl");
		quad = new StaticMesh(PaneShader.vertexInfo, new float[] {
				-1, -1, 0, 0,
				1, -1, 1, 0,
				1, 1, 1, 1,
				-1, 1, 0, 1
		}, new short[] {
				0, 2, 1, 0, 3, 2
		});
	}

	private int panePositionLocation;
	private int paneRadiusLocation;
	private int angleLocation;
	private int ydownLocation;
	private int clipLocation;
	private int alphaLocation;
	private int screenSizeLocation;
	
	private float screenWidth = 0;
	private float screenHeight = 0;
	
	@Override
	protected void storeUniformLocations() {
		alphaLocation = GL20.glGetUniformLocation(pId, "alpha");
		angleLocation = GL20.glGetUniformLocation(pId, "angle");
		panePositionLocation = GL20.glGetUniformLocation(pId, "panePosition");
		paneRadiusLocation = GL20.glGetUniformLocation(pId, "paneRadius");
		ydownLocation = GL20.glGetUniformLocation(pId, "ydown");
		clipLocation = GL20.glGetUniformLocation(pId, "clip");
		screenSizeLocation = GL20.glGetUniformLocation(pId, "screenSize");
		GL20.glUseProgram(pId);
		GL20.glUniform1i(GL20.glGetUniformLocation(pId, "tex"), 0);
		GL20.glUseProgram(0);
	}
	
	@Override
	public void use() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		super.use();
	}
	
	@Override
	public void updateUniforms() {
	}

	public void updateScreenSize(float width, float height) {
		if(width!=screenWidth || height!=screenHeight) {
			GL20.glUniform2f(screenSizeLocation, width, height);
			screenWidth = width;
			screenHeight = height;
		}
	}

	public void updateScreenSize(RenderTarget target) {
		updateScreenSize(target.getWidth(), target.getHeight());
	}
	
	public void updateUniforms(float x, float y, float radius, float angle, float alpha, boolean ydown, boolean clip) {
		GL20.glUniform2f(panePositionLocation, x, y);
		GL20.glUniform1f(paneRadiusLocation, radius);
		GL20.glUniform1f(angleLocation, ydown ? angle : -angle);
		GL20.glUniform1f(alphaLocation, alpha);
		GL20.glUniform1i(ydownLocation, ydown ? 1 : 0);
		GL20.glUniform1i(clipLocation, clip ? 1 : 0);
	}
	
	@Override
	public void unuse() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		super.unuse();
	}
	
	@Override
	public void release() {
		super.release();
		quad.release();
	}
	
	private static RotatedPaneShader instance = null;
	
	public static void createInstance() {
		instance = new RotatedPaneShader();
	}
	
	public static RotatedPaneShader getInstance() {
		return instance;
	}
	
	public static void releaseInstance() {
		instance.release();
	}
}
