package com.xrbpowered.aethertown.render.tiles;

import java.util.ArrayList;

import com.xrbpowered.aethertown.render.LevelComponentRenderer;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public class TunnelTileComponent extends ScaledTileComponent {

	public static ArrayList<TileComponent> list = new ArrayList<>();

	public TunnelTileComponent(StaticMesh mesh, Texture diffuse) {
		super(list, mesh, diffuse);
	}

	public static LevelComponentRenderer createRenderer(LevelRenderer r, final ObjectInfoUser shader) {
		return createRenderer(list, r, LevelRenderer.solidRenderPass, shader);
	}

	public static void releaseRenderer(LevelRenderer r) {
		releaseRenderer(list, r);
	}

}
