package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;
import com.xrbpowered.gl.scene.comp.InstancedMeshList;

public class TileComponent extends InstancedMeshList<TileObjectInfo> {

	public static ComponentRenderer<TileComponent> renderer;
	
	public TileComponent(StaticMesh mesh, Texture diffuse) {
		super(TileObjectShader.instanceInfo);
		setMesh(mesh);
		setTextures(new Texture[] {diffuse});
		renderer.add(this);
	}

	@Override
	protected void bindTextures() {
		Texture.bindAll(1, textures);
	}
	
	@Override
	protected void setInstanceData(float[] data, TileObjectInfo obj, int index) {
		obj.setData(data, getDataOffs(index));
	}

}
