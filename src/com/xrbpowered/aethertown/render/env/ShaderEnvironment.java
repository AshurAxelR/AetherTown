package com.xrbpowered.aethertown.render.env;

import java.awt.Color;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.shader.Shader;

import static com.xrbpowered.gl.res.shader.Shader.uniform;

public class ShaderEnvironment {

	public Vector3f lightDirection = new Vector3f(-0.55f, -0.65f, -0.45f);
	public Color lightColor = new Color(0xffffff);
	public Color midColor = new Color(0xccccbb);
	public Color shadowColor = new Color(0x8899bb);

	public Color cloudColor = new Color(0xf1f8fa);
	public Color bgColor = new Color(0xdaefef);
	public Color zenithColor = new Color(0x98d3ef);
	
	public float fogNear = 15f*Tile.size;
	public float fogFar = 70f*Tile.size;
	
	public boolean renderStars = false;
	public float lightWash = 0f;
	
	public ShaderEnvironment() {
	}

	public ShaderEnvironment(Vector3f lightDirection, Color lightColor, Color midColor, Color shadowColor, Color cloudColor, Color bgColor, Color zenithColor) {
		this.lightDirection.set(lightDirection);
		this.lightColor = lightColor;
		this.midColor = midColor;
		this.shadowColor = shadowColor;
		this.cloudColor = cloudColor;
		this.bgColor = bgColor;
		this.zenithColor = zenithColor;
	}

	public ShaderEnvironment setRenderStars(boolean r, float lightWash) {
		this.renderStars = r;
		this.lightWash = lightWash;
		return this;
	}
	
	public void updateShader(Shader shader) {
		int pId = shader.getProgramId();
		GL20.glUseProgram(pId);
		
		uniform(GL20.glGetUniformLocation(pId, "cloudColor"), cloudColor);
		uniform(GL20.glGetUniformLocation(pId, "bgColor"), bgColor);
		uniform(GL20.glGetUniformLocation(pId, "zenithColor"), zenithColor);
		
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogNear"), fogNear);
		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "fogFar"), fogFar);
		
		uniform(GL20.glGetUniformLocation(pId, "lightDirection"), lightDirection);
		uniform(GL20.glGetUniformLocation(pId, "lightColor"), lightColor);
		uniform(GL20.glGetUniformLocation(pId, "midColor"), midColor);
		uniform(GL20.glGetUniformLocation(pId, "shadowColor"), shadowColor);

		GL20.glUniform1f(GL20.glGetUniformLocation(pId, "lightWash"), lightWash);

		GL20.glUseProgram(0);
	}

	public static ShaderEnvironment clearDay = new ShaderEnvironment();

	public static ShaderEnvironment evening = new ShaderEnvironment(
			new Vector3f(-0.55f, -0.25f, -0.45f),
			new Color(0xfff2d9),
			new Color(0x4d73cc),
			new Color(0x405999),
			new Color(0xefe8db),
			new Color(0xccd1e6),
			new Color(0x99a3bb)
		).setRenderStars(true, 0.9f);
	
	public static ShaderEnvironment dawn = new ShaderEnvironment(
			new Vector3f(-0.55f, -0.02f, -0.45f),
			new Color(0x996e66),
			new Color(0x555566),
			new Color(0x333344),
			new Color(0x555266),
			new Color(0x5b6682),
			new Color(0x303755)
		).setRenderStars(true, 0.4f);

	public static ShaderEnvironment clearNight = new ShaderEnvironment(
			new Vector3f(-0.55f, -0.75f, -0.45f),
			new Color(0x111527),
			new Color(0x020407),
			new Color(0x000000),
			new Color(0x0e1520), // 171c22),
			new Color(0x050b13),
			new Color(0x000204)
		).setRenderStars(true, 0f);

	public static ShaderEnvironment cloudyNight = new ShaderEnvironment(
			new Vector3f(-0.45f, -0.85f, -0.35f),
			new Color(0x111517),
			new Color(0x07090a),
			new Color(0x030505),
			new Color(0x17150e),
			new Color(0x0b0b07),
			new Color(0x12110b)
		);

	public static ShaderEnvironment[] environments = {clearDay, evening, dawn, clearNight, cloudyNight, dawn, evening};

}
