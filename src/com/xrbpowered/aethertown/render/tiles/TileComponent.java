package com.xrbpowered.aethertown.render.tiles;

import java.util.ArrayList;
import java.util.HashMap;

import com.xrbpowered.aethertown.render.LevelComponentRenderer;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.comp.InstancedMeshList;

public class TileComponent {

	public class InstanceList extends InstancedMeshList<ObjectInfo> {
		public final ObjectInfoUser shader;
		
		public InstanceList(ObjectInfoUser shader) {
			super(shader.getInstInfo());
			this.shader = shader;
			setMesh(TileComponent.this.mesh);
			setTextures(TileComponent.this.textures);
			setCulling(TileComponent.this.culling);
		}

		@Override
		protected void bindTextures() {
			shader.bindTextures(textures);
		}
		
		@Override
		protected void setInstanceData(float[] data, ObjectInfo obj, int index) {
			shader.setData(obj, data, getDataOffs(index));
		}
	}
	
	public static ArrayList<TileComponent> list = new ArrayList<>();
	
	public StaticMesh mesh;
	public Texture[] textures;
	public boolean culling = true;

	private HashMap<LevelRenderer, InstanceList> instMap = new HashMap<>(); 

	public TileComponent(StaticMesh mesh, Texture diffuse) {
		this(list, mesh, new Texture[] {diffuse});
	}

	protected TileComponent(ArrayList<TileComponent> list, StaticMesh mesh, Texture[] textures) {
		this.mesh = mesh;
		this.textures = textures;
		list.add(this);
	}

	public TileComponent setCulling(boolean culling) {
		this.culling = culling;
		return this;
	}
	
	public void addInstance(LevelRenderer r, ObjectInfo info) {
		instMap.get(r).addInstance(info);
	}
	
	public InstanceList createInstList(ObjectInfoUser shader) {
		return new InstanceList(shader);
	}

	protected static LevelComponentRenderer createRenderer(ArrayList<TileComponent> list, LevelRenderer r, int renderPass, final ObjectInfoUser shader) {
		LevelComponentRenderer renderer = new LevelComponentRenderer(renderPass, shader);
		for(TileComponent comp : list) {
			InstanceList inst = comp.createInstList(shader);
			comp.instMap.put(r, inst);
			renderer.add(inst);
		}
		return renderer;
	}
	
	protected static void releaseRenderer(ArrayList<TileComponent> list, LevelRenderer r) {
		for(TileComponent comp : list) {
			InstanceList inst = comp.instMap.get(r);
			inst.releaseInstances();
			comp.instMap.remove(r);
		}
	}

	public static LevelComponentRenderer createRenderer(LevelRenderer r, final ObjectInfoUser shader) {
		return createRenderer(list, r, LevelRenderer.solidRenderPass, shader);
	}

	public static void releaseRenderer(LevelRenderer r) {
		releaseRenderer(list, r);
	}

}
