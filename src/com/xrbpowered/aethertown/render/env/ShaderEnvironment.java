package com.xrbpowered.aethertown.render.env;

import static com.xrbpowered.gl.res.shader.Shader.uniform;

import java.awt.Color;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.shader.Shader;

public class ShaderEnvironment {

	public Vector3f lightDirection = new Vector3f(-0.55f, -0.65f, -0.45f);
	public Vector3f lightSkyDirection = new Vector3f(-0.55f, -0.65f, -0.45f);
	public Color lightColor = new Color(0xffffff);
	public Color midColor = new Color(0xccccbb);
	public Color shadowColor = new Color(0x8899bb);

	public Color cloudColor = new Color(0xf1f8fa);
	public Color bgColor = new Color(0xdaefef);
	public Color zenithColor = new Color(0x98d3ef);
	public Color lightSkyColor = new Color(0xf2f7ff);
	
	public float fogNear = 15f*Tile.size;
	public float fogFar = 70f*Tile.size;
	
	public boolean renderStars = false;
	public float lightWashTop = 1f;
	public float lightWashBottom = 1f;
	public float lightSkyWash = 1f;
	
	public ShaderEnvironment() {
	}
	
	public ShaderEnvironment(ShaderEnvironment s) {
		copyFrom(s);
	}
	
	public ShaderEnvironment(Vector3f lightDirection, Color lightColor, Color midColor, Color shadowColor, Color cloudColor, Color bgColor, Color zenithColor, Color lightSkyColor) {
		this.lightDirection.set(lightDirection);
		this.lightSkyDirection.set(lightDirection);
		this.lightColor = lightColor;
		this.midColor = midColor;
		this.shadowColor = shadowColor;
		this.cloudColor = cloudColor;
		this.bgColor = bgColor;
		this.zenithColor = zenithColor;
		this.lightSkyColor = lightSkyColor;
	}

	public ShaderEnvironment setRenderStars(boolean r, float lightWashTop, float lightWashBottom, float lightSkyWash) {
		this.renderStars = r;
		this.lightWashTop = lightWashTop;
		this.lightWashBottom = lightWashBottom;
		this.lightSkyWash = lightSkyWash;
		return this;
	}
	
	public ShaderEnvironment setLightSkyDirection(float x, float y, float z) {
		this.lightSkyDirection.set(x, y, z);
		return this;
	}

	public void updateShader(Shader shader) {
		int pId = shader.getProgramId();
		GL20.glUseProgram(pId);
		
		uniform(GL20.glGetUniformLocation(pId, "cloudColor"), cloudColor);
		uniform(GL20.glGetUniformLocation(pId, "bgColor"), bgColor);
		uniform(GL20.glGetUniformLocation(pId, "zenithColor"), zenithColor);
		uniform(GL20.glGetUniformLocation(pId, "lightSkyColor"), lightSkyColor);
		uniform(GL20.glGetUniformLocation(pId, "lightSkyDirection"), lightSkyDirection);
		
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogNear"), fogNear);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), fogFar);
		
		uniform(GL20.glGetUniformLocation(pId, "lightDirection"), lightDirection);
		uniform(GL20.glGetUniformLocation(pId, "lightColor"), lightColor);
		uniform(GL20.glGetUniformLocation(pId, "midColor"), midColor);
		uniform(GL20.glGetUniformLocation(pId, "shadowColor"), shadowColor);

		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "lightWashTop"), lightWashTop);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "lightWashBottom"), lightWashBottom);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "lightSkyWash"), lightSkyWash);

		GL20.glUseProgram(0);
	}

	public void copyFrom(ShaderEnvironment s) {
		lightDirection.set(s.lightDirection);
		lightSkyDirection.set(s.lightSkyDirection);
		lightColor = s.lightColor;
		midColor = s.midColor;
		shadowColor = s.shadowColor;

		cloudColor = s.cloudColor;
		bgColor = s.bgColor;
		zenithColor = s.zenithColor;
		lightSkyColor = s.lightSkyColor;
		
		fogNear = s.fogNear;
		fogFar = s.fogFar;
		
		renderStars = s.renderStars;
		lightWashTop = s.lightWashTop;
		lightWashBottom = s.lightWashBottom;
		lightSkyWash = s.lightSkyWash;
	}

	public void blend(ShaderEnvironment s0, ShaderEnvironment s1, float s) {
		lightColor = blend(s0.lightColor, s1.lightColor, s);
		midColor = blend(s0.midColor, s1.midColor, s);
		shadowColor = blend(s0.shadowColor, s1.shadowColor, s);

		cloudColor = blend(s0.cloudColor, s1.cloudColor, s);
		bgColor = blend(s0.bgColor, s1.bgColor, s);
		zenithColor = blend(s0.zenithColor, s1.zenithColor, s);
		lightSkyColor = blend(s0.lightSkyColor, s1.lightSkyColor, s);
		
		fogNear = MathUtils.lerp(s0.fogNear, s1.fogNear, s);
		fogFar = MathUtils.lerp(s0.fogFar, s1.fogFar, s);
		
		renderStars = s0.renderStars || s1.renderStars;
		lightWashTop = MathUtils.lerp(s0.lightWashTop, s1.lightWashTop, s);
		lightWashBottom = MathUtils.lerp(s0.lightWashBottom, s1.lightWashBottom, s);
		lightSkyWash = MathUtils.lerp(s0.lightSkyWash, s1.lightSkyWash, s);
	}
	
	private static Color blend(Color c0, Color c1, float s) {
		float r = MathUtils.clamp(MathUtils.lerp(c0.getRed(), c1.getRed(), s)/255f);
		float g = MathUtils.clamp(MathUtils.lerp(c0.getGreen(), c1.getGreen(), s)/255f);
		float b = MathUtils.clamp(MathUtils.lerp(c0.getBlue(), c1.getBlue(), s)/255f);
		return new Color(r, g, b);
	}
	
	public static ShaderEnvironment clearDay = new ShaderEnvironment();

	public static ShaderEnvironment evening = new ShaderEnvironment(
			new Vector3f(-0.55f, -0.25f, -0.45f),
			new Color(0xfff2d9),
			new Color(0x4d73cc),
			new Color(0x405999),
			new Color(0xefe8db),
			new Color(0xccd1e6),
			new Color(0x99a3bb),
			new Color(0xf2f7ff)
		);
	
	public static ShaderEnvironment dawn = new ShaderEnvironment(
			new Vector3f(-0.55f, -0.02f, -0.45f),
			new Color(0xaa776d),
			new Color(0x444455),
			new Color(0x22273e),
			new Color(0x50546d),
			new Color(0x5b6682), // 0x6b7692
			new Color(0x353c5a),
			new Color(0x8baacc)
		)
		.setRenderStars(true, 0.3f, 0.5f, 1f)
		.setLightSkyDirection(-0.55f, 0.45f, -0.45f);

	public static ShaderEnvironment clearNight = new ShaderEnvironment(
			new Vector3f(-0.55f, -1.75f, -0.45f),
			new Color(0x111527),
			new Color(0x020407),
			new Color(0x000000),
			new Color(0x0e1520), // 171c22),
			new Color(0x050b13),
			new Color(0x000204),
			new Color(0x00050e19, true)
		)
		.setRenderStars(true, 0f, 0.4f, 0f);

	public static ShaderEnvironment cloudyNight = new ShaderEnvironment(
			new Vector3f(-0.45f, -1.85f, -0.35f),
			new Color(0x111517),
			new Color(0x07090a),
			new Color(0x030505),
			new Color(0x17150e),
			new Color(0x0b0b07),
			new Color(0x12110b),
			new Color(0x0015140e, true)
		);

	public static ShaderEnvironment[] environments = {clearDay, evening, dawn, clearNight, cloudyNight, dawn, evening};

}
