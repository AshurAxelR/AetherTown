package com.xrbpowered.aethertown.render.sprites;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.tiles.ObjectInfo;
import com.xrbpowered.aethertown.render.tiles.ObjectInfoUser;
import com.xrbpowered.gl.res.shader.CameraShader;
import com.xrbpowered.gl.res.shader.InstanceInfo;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.shader.VertexInfo;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;

public class SpriteShader extends CameraShader implements ObjectInfoUser {

	public static final VertexInfo vertexInfo = new VertexInfo()
			.addAttrib("in_Position", 2)
			.addAttrib("in_TexCoord", 2);
	
	public static final InstanceInfo instanceInfo = new InstanceInfo(vertexInfo)
			.addAttrib("ins_Position", 3)
			.addAttrib("ins_Size", 1);
	
	public static final String[] samplerNames = {"texColor"};
	
	public LevelRenderer level;

	private int invAspectRatioLocation;
	private int fovFactorLocation;
	private int levelOffsetLocation;
	
	protected SpriteShader(VertexInfo info, String pathVS, String pathFS) {
		super(info, pathVS, pathFS);
	} 
	
	public SpriteShader() {
		super(instanceInfo, "shaders/tiles/sprite_v.glsl", "shaders/tiles/sprite_f.glsl");
	}

	protected String[] getSamplerNames() {
		return samplerNames;
	}
	
	@Override
	protected void storeUniformLocations() {
		super.storeUniformLocations();
		invAspectRatioLocation = GL20.glGetUniformLocation(pId, "invAspectRatio");
		fovFactorLocation = GL20.glGetUniformLocation(pId, "fovFactor");
		levelOffsetLocation = GL20.glGetUniformLocation(pId, "levelOffset");
		initSamplers(getSamplerNames());
	}
	
	@Override
	public void updateUniforms() {
		super.updateUniforms();
		
		glDepthMask(false);
		glEnable(GL11.GL_BLEND);
		glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

		GL20.glUniform1f(invAspectRatioLocation, 1f/camera.getAspectRatio());
		float ff = 1f/(float)Math.tan(Math.toRadians(((CameraActor.Perspective)camera).getFov()) / 2.0);
		GL20.glUniform1f(fovFactorLocation, ff);
		uniform(levelOffsetLocation, level.levelOffset);
	}
	
	@Override
	public void unuse() {
		glDepthMask(true);
		glDisable(GL11.GL_BLEND);
		super.unuse();
	}
	
	@Override
	public InstanceInfo getInstInfo() {
		return instanceInfo;
	}
	
	@Override
	public Shader getShader() {
		return this;
	}
	
	@Override
	public void bindTextures(Texture[] textures) {
		Texture.bindAll(textures);
	}
	
	@Override
	public void setData(ObjectInfo aobj, float[] data, int offs) {
		SpriteInfo obj = (SpriteInfo) aobj;
		data[offs+0] = obj.position.x;
		data[offs+1] = obj.position.y;
		data[offs+2] = obj.position.z;
		data[offs+3] = obj.size;
	}
}
