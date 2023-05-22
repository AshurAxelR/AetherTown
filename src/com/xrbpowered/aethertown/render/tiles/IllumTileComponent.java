package com.xrbpowered.aethertown.render.tiles;

import java.util.ArrayList;

import com.xrbpowered.aethertown.render.LevelComponentRenderer;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;

public class IllumTileComponent extends TileComponent {

	public static ArrayList<TileComponent> list = new ArrayList<>();

	public IllumTileComponent(StaticMesh mesh, Texture diffuse, Texture illum) {
		super(list, mesh, new Texture[] {diffuse, illum});
	}

	@Override
	public void addInstance(LevelRenderer r, ObjectInfo obj) {
		if(obj instanceof IllumTileObjectInfo)
			super.addInstance(r, (IllumTileObjectInfo) obj);
		else
			super.addInstance(r, new IllumTileObjectInfo((TileObjectInfo) obj));
	}
	
	public static LevelComponentRenderer createRenderer(LevelRenderer r, final ObjectInfoUser shader) {
		return createRenderer(list, r, LevelRenderer.solidRenderPass, shader);
	}

	public static void releaseRenderer(LevelRenderer r) {
		releaseRenderer(list, r);
	}
}
