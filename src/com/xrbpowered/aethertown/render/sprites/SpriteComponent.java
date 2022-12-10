package com.xrbpowered.aethertown.render.sprites;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;
import com.xrbpowered.gl.scene.comp.InstancedMeshList;
import com.xrbpowered.gl.ui.pane.PaneShader;

public class SpriteComponent extends InstancedMeshList<SpriteInfo> {

	public static ComponentRenderer<SpriteComponent> renderer;
	
	public SpriteComponent(Texture color) {
		super(SpriteShader.instanceInfo);
		StaticMesh quad = createSpriteQuad();
		setMesh(quad);
		setTextures(new Texture[] {color});
		renderer.add(this);
	}

	@Override
	protected void setInstanceData(float[] data, SpriteInfo obj, int index) {
		obj.setData(data, getDataOffs(index));
	}
	
	public static StaticMesh createQuad(float r, boolean flipu, boolean flipv) {
		float u = flipu ? 0 : 1;
		float v = flipv ? 0 : 1;
		return new StaticMesh(PaneShader.vertexInfo, new float[] {
				-r, -r, u, 1-v,
				r, -r, 1-u, 1-v,
				r, r, 1-u, v,
				-r, r, u, v
		}, new short[] {
				0, 1, 2, 0, 2, 3
		});
	}

	public static StaticMesh createSpriteQuad() {
		return createQuad(0.5f, false, false);
	}
	
	public static StaticMesh createFullQuad() {
		return createQuad(1f, false, false);
	}

}
