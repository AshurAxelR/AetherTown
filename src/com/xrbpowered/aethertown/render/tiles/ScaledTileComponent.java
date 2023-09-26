package com.xrbpowered.aethertown.render.tiles;

import java.util.ArrayList;

import com.xrbpowered.aethertown.render.LevelComponentRenderer;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public class ScaledTileComponent extends TileComponent {

	public static ArrayList<TileComponent> list = new ArrayList<>();

	public ScaledTileComponent(StaticMesh mesh, Texture diffuse) {
		super(list, mesh, new Texture[] {diffuse});
	}

	@Override
	public void addInstance(LevelRenderer r, ObjectInfo obj) {
		if(obj instanceof ScaledTileObjectInfo)
			super.addInstance(r, (ScaledTileObjectInfo) obj);
		else
			super.addInstance(r, new ScaledTileObjectInfo((TileObjectInfo) obj));
	}
	
	public static LevelComponentRenderer createRenderer(LevelRenderer r, final ObjectInfoUser shader) {
		return createRenderer(list, r, LevelRenderer.solidRenderPass, shader);
	}

	public static void releaseRenderer(LevelRenderer r) {
		releaseRenderer(list, r);
	}

}
