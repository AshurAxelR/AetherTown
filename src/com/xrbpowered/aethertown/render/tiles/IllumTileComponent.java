package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.comp.ComponentRenderer;
import com.xrbpowered.gl.scene.comp.InstancedMeshList;

public class IllumTileComponent extends InstancedMeshList<IllumTileObjectInfo> {

	public static ComponentRenderer<IllumTileComponent> renderer;
	
	public IllumTileComponent(StaticMesh mesh, Texture diffuse, Texture illum) {
		super(IllumTileObjectShader.instanceInfo);
		setMesh(mesh);
		setTextures(new Texture[] {diffuse, illum});
		renderer.add(this);
	}

	@Override
	protected void bindTextures() {
		Texture.bindAll(2, textures);
	}
	
	@Override
	protected void setInstanceData(float[] data, IllumTileObjectInfo obj, int index) {
		obj.setData(data, getDataOffs(index));
	}

	public int addInstance(TileObjectInfo obj) {
		if(obj instanceof IllumTileObjectInfo)
			return addInstance((IllumTileObjectInfo) obj);
		else
			return addInstance(new IllumTileObjectInfo(obj));
	}

}
