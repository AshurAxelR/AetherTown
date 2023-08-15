package com.xrbpowered.aethertown.render.env;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import com.xrbpowered.aethertown.world.stars.BlackBodySpectrum;
import com.xrbpowered.aethertown.world.stars.StarData;
import com.xrbpowered.aethertown.world.stars.StarData.Star;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.CameraShader;
import com.xrbpowered.gl.res.shader.VertexInfo;

public class StarRenderer {

	public static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 3)
			.addAttrib("in_Magnitude", 1)
			.addAttrib("in_Color", 3);

	public static final float latitude = (float)Math.toRadians(45); // 0 - north pole, 180 - south pole

	public class StarShader extends CameraShader {
		private int modelMatrixLocation;
		
		public StarShader() {
			super(vertexInfo, "shaders/env/stars_v.glsl", "shaders/env/stars_f.glsl");
			followCamera = true;
		}
		
		@Override
		protected void storeUniformLocations() {
			super.storeUniformLocations();
			modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");
		}
		
		@Override
		public void updateUniforms() {
			super.updateUniforms();
			
			glDisable(GL_DEPTH_TEST);
			glEnable(GL20.GL_POINT_SPRITE);
			glEnable(GL32.GL_PROGRAM_POINT_SIZE);
			glEnable(GL11.GL_BLEND);
			glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			
			uniform(modelMatrixLocation, transform);
		}
		
		@Override
		public void unuse() {
			glDisable(GL11.GL_BLEND);
			glEnable(GL_DEPTH_TEST);
			super.unuse();
		}
	}
	
	private StarShader starShader;
	private StaticMesh stars = null;
	
	protected final Matrix4f transform = new Matrix4f();

	public StarRenderer() {
		starShader = new StarShader();
	}
	
	public void update(Vector4f sun) {
		float y = WorldTime.getTimeOfYear();
		transform.rotationXYZ(-latitude, -(WorldTime.time-0.5f+y)*(float)Math.PI*2f, 0);
		StarData.updateSun(y);
		starPos(StarData.sun.ra, StarData.sun.de, sun);
		transform.transform(sun);
	}
	
	public StarShader getShader() {
		return starShader;
	}
	
	private static void starPos(double ra, double de, Vector4f pos) {
		double r = Math.cos(de);
		pos.x = (float)(Math.sin(ra)*r);
		pos.y = (float)(Math.sin(de));
		pos.z = (float)(Math.cos(ra)*r);
		pos.w = 1;
	}
	
	private static float[] createPointData(ArrayList<Star> stars) {
		int numStars = stars.size();
		float[] data = vertexInfo.createData(numStars+1);
		int offs = 0;
		Vector4f pos = new Vector4f();
		for(Star star : stars) {
			starPos(star.ra, star.de, pos);
			data[offs++] = pos.x;
			data[offs++] = pos.y;
			data[offs++] = pos.z;
			data[offs++] = star.mag;
			
			//data[offs++] = star.temp;
			data[offs++] = (float)BlackBodySpectrum.getRed(star.temp);
			data[offs++] = (float)BlackBodySpectrum.getGreen(star.temp);
			data[offs++] = (float)BlackBodySpectrum.getBlue(star.temp);
		}
		return data;
	}
	
	public void createStars(long seed) {
		if(stars!=null)
			stars.release();
		Random random = new Random(seed);
		ArrayList<Star> data = StarData.generate(random);
		stars = new StaticMesh(vertexInfo, createPointData(data), 1, data.size(), false);
	}
	
	public void render() {
		if(stars==null)
			return;
		starShader.use();
		stars.draw();
		starShader.unuse();
	}
}
