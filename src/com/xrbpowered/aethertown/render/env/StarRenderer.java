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
		
		public StarShader(float renderScale) {
			super(vertexInfo, "shaders/env/stars_v.glsl", "shaders/env/stars_f.glsl");
			followCamera = true;
			updateRenderScale(renderScale);
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
		
		public void updateRenderScale(float renderScale) {
			double exposure = 0.33 / Math.pow(renderScale, 2.7);
			double contrast = 1.4 - Math.log(renderScale) / Math.log(2) * 0.3;
			double saturation = 0.5;
			GL20.glUseProgram(pId);
			GL20.glUniform1f(GL20.glGetUniformLocation(pId, "exposure"), (float) exposure);
			GL20.glUniform1f(GL20.glGetUniformLocation(pId, "contrast"), (float) contrast);
			GL20.glUniform1f(GL20.glGetUniformLocation(pId, "saturation"), (float) saturation);
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
	private long seed = -1L;
	private StaticMesh otherStars = null;
	private long otherSeed = -1L;
	
	protected final Matrix4f transform = new Matrix4f();

	public StarRenderer(float renderScale) {
		starShader = new StarShader(renderScale);
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
	
	public void updateStars(long seed) {
		if(this.seed!=seed) {
			StaticMesh m = stars;
			long s = this.seed;
			stars = otherStars;
			this.seed = otherSeed;
			otherStars = m;
			otherSeed = s;
		}
		if(this.seed!=seed) {
			if(stars!=null)
				stars.release();
			this.seed = seed;
			System.out.printf("Generating stars for *%04d\n", seed%10000L);
			ArrayList<Star> data = StarData.generate(new Random(seed));
			stars = new StaticMesh(vertexInfo, createPointData(data), 1, data.size(), false);
		}
	}
	
	public void render() {
		if(stars==null)
			return;
		starShader.use();
		stars.draw();
		starShader.unuse();
	}
}
