package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;
import com.xrbpowered.gl.scene.comp.InstancedMeshList;

public class LightTileComponent extends InstancedMeshList<LightTileObjectInfo> {

	public static ComponentRenderer<LightTileComponent> renderer;
	
	public LightTileComponent(StaticMesh mesh, Texture diffuse, Texture illum) {
		super(LightTileObjectShader.instanceInfo);
		setMesh(mesh);
		setTextures(new Texture[] {diffuse, illum});
		renderer.add(this);
	}

	@Override
	protected void bindTextures() {
		Texture.bindAll(2, textures);
	}
	
	@Override
	protected void setInstanceData(float[] data, LightTileObjectInfo obj, int index) {
		obj.setData(data, getDataOffs(index));
	}

	public int addInstance(TileObjectInfo obj) {
		if(obj instanceof LightTileObjectInfo)
			return addInstance((LightTileObjectInfo) obj);
		else
			return addInstance(new LightTileObjectInfo(obj));
	}

}
