package com.xrbpowered.aethertown.render.env;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.xrbpowered.aethertown.stars.BlackBodySpectrum;
import com.xrbpowered.aethertown.stars.RandomStarData;
import com.xrbpowered.aethertown.stars.RandomStarData.StarData;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.scene.Actor;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.ui.pane.PaneShader;

public class StarRenderer {

	public static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_Magnitude", 1)
			.addAttrib("in_Color", 3);
	
	private static final double pointDist = 1f;

	public class StarShader extends Shader {
		private int projectionMatrixLocation;
		private int viewMatrixLocation;
		private int modelMatrixLocation;
		private int viewYLocation;
		
		protected final Matrix4f view = new Matrix4f();
		protected final Matrix4f model = new Matrix4f();
		
		public StarShader() {
			super(PaneShader.vertexInfo, "stars_v.glsl", "stars_f.glsl");
		}
		
		@Override
		protected void storeUniformLocations() {
			projectionMatrixLocation = GL20.glGetUniformLocation(pId, "projectionMatrix");
			viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
			modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");
			viewYLocation = GL20.glGetUniformLocation(pId, "viewY");
		}
		@Override
		public void updateUniforms() {
			glDisable(GL_DEPTH_TEST);
			glEnable(GL20.GL_POINT_SPRITE);
			glEnable(GL32.GL_PROGRAM_POINT_SIZE);
			glEnable(GL11.GL_BLEND);
			glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			
			uniform(projectionMatrixLocation, camera.getProjection());
			view.identity();
			Actor.rotateYawPitchRoll(camera.rotation, view);
			view.invert();
			uniform(viewMatrixLocation, view);
			GL20.glUniform1f(viewYLocation, camera.position.y);
			
			model.rotation(cycleTime, rotationAxis);
			uniform(modelMatrixLocation, model);
		}
		
		@Override
		public void unuse() {
			glDisable(GL11.GL_BLEND);
			glEnable(GL_DEPTH_TEST);
			super.unuse();
		}
	}
	
	private StarShader starShader;
	private CameraActor camera = null;
	private StaticMesh stars = null;
	
	public Vector3f rotationAxis = new Vector3f(-0.2f, 0.4f, 0.6f).normalize();
	public float cycleTime = 0f;
	
	public StarRenderer() {
		starShader = new StarShader();
	}
	
	public void setCamera(CameraActor camera) {
		this.camera = camera;
	}
	
	public void updateTime(float dt) {
		cycleTime += dt*0.005f;
	}
	
	public StarShader getShader() {
		return starShader;
	}
	
	public static float[] createPointData(ArrayList<StarData> stars) {
		int numStars = stars.size();
		float[] data = vertexInfo.createData(numStars);
		int offs = 0;
		for(StarData star : stars) {
			double r = Math.cos(star.de)*pointDist;
			data[offs++] = (float)(Math.sin(star.ra)*r);
			data[offs++] = (float)(Math.sin(star.de)*pointDist);
			data[offs++] = (float)(Math.cos(star.ra)*r);
			data[offs++] = star.mag;
			
			//data[offs++] = star.temp;
			data[offs++] = (float)BlackBodySpectrum.getRed(star.temp);
			data[offs++] = (float)BlackBodySpectrum.getGreen(star.temp);
			data[offs++] = (float)BlackBodySpectrum.getBlue(star.temp);
		}
		return data;
	}
	
	public void createStars(long seed) {
		Random random = new Random(seed);
		ArrayList<StarData> data = RandomStarData.generate(random);
		stars = new StaticMesh(vertexInfo, createPointData(data), 1, data.size(), false);
	}
	
	public void render() {
		if(stars==null || camera==null)
			return;
		starShader.use();
		stars.draw();
		starShader.unuse();
	}
}
