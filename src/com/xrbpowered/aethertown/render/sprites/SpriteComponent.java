package com.xrbpowered.aethertown.render.sprites;

import java.util.ArrayList;

import com.xrbpowered.aethertown.render.LevelComponentRenderer;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.tiles.ObjectInfoUser;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.pane.PaneShader;

public class SpriteComponent extends TileComponent {

	public static ArrayList<TileComponent> list = new ArrayList<>();
	
	public SpriteComponent(Texture color) {
		super(list, createSpriteQuad(), new Texture[] {color});
	}
	
	public static LevelComponentRenderer createRenderer(LevelRenderer r, final ObjectInfoUser shader) {
		return createRenderer(list, r, LevelRenderer.spriteRenderPass, shader);
	}

	public static void releaseRenderer(LevelRenderer r) {
		releaseRenderer(list, r);
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
