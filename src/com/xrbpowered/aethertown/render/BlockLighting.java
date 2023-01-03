package com.xrbpowered.aethertown.render;

import java.awt.Color;

import org.joml.Vector4f;

import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.texture.FloatDataTexture;

public class BlockLighting extends FloatDataTexture {

	private static final float deltav = 0.075f;
	private static final int maxr = (int)Math.ceil(1/deltav)+1;
	
	public final int size;
	public Vector4f[][] colors;
	
	public BlockLighting(int levelSize) {
		super(levelSize, levelSize, false, true, true);
		this.size = levelSize;
		colors = PointLightArray.createVectors(levelSize, new Vector4f(0, 0, 0, 1));
	}

	public void addLight(int x0, int z0, float v, float[][] vmap) {
		if(vmap[x0][z0]>=v)
			return;
		vmap[x0][z0] = v;
		for(Dir8 d : Dir8.values()) {
			int x = x0+d.dx;
			int z = z0+d.dz;
			float dv = deltav*d.len;
			addLight(x, z, v-dv, vmap);
		}
	}
	
	public void addLight(Tile tile, Color color, float v0) {
		float[][] vmap = new float[maxr*2][maxr*2];
		addLight(maxr, maxr, v0, vmap);
		
		float red = color.getRed()/255f;
		float green = color.getGreen()/255f;
		float blue = color.getBlue()/255f;
		
		for(int x=0; x<maxr*2; x++)
			for(int z=0; z<maxr*2; z++) {
				int mx = tile.x-maxr+x;
				int mz = tile.z-maxr+z;
				if(mx<0 || mx>=size || mz<0 || mz>=size)
					continue;
				Vector4f c = colors[mx][mz];
				float v = vmap[x][z];
				v = v*v;
				c.x += v*red;
				if(c.x>1f) c.x = 1f;
				c.y += v*green;
				if(c.y>1f) c.y = 1f;
				c.z += v*blue;
				if(c.z>1f) c.z = 1f;
			}
	}

	public void addLight(Tile tile, Color color) {
		addLight(tile, color, 1f);
	}

	public FloatDataTexture finish() {
		setData(colors).freeBuffer();
		colors = null;
		return this;
	}

}
