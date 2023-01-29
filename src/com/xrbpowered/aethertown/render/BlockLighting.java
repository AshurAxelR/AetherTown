package com.xrbpowered.aethertown.render;

import java.awt.Color;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.texture.FloatDataTexture;

public class BlockLighting extends FloatDataTexture {

	private static final float deltav = 0.08f;
	private static final float deltavy = deltav/12f;
	private static final int maxr = (int)Math.ceil(1/deltav)+1;
	
	public final Level level;
	public Vector4f[][] colors;
	
	public float cloudTop = 0;
	public float cloudBottom = -40;
	
	private int count = 0;
	
	public BlockLighting(Level level) {
		super(level.levelSize, level.levelSize, false, true, true);
		this.level = level;
		colors = PointLightArray.createVectors(level.levelSize, new Vector4f(0, 0, 0, 1));
	}
	
	public void addLight(Tile tile, int x0, int y0, int z0, float v, float[][] vmap, boolean skip) {
		if(vmap[x0][z0]>=v)
			return;
		if(!skip) {
			int mx = tile.x-maxr+x0;
			int mz = tile.z-maxr+z0;
			if(level.isInside(mx, mz)) {
				Tile t = level.map[mx][mz];
				if(t!=null) {
					int by = t.t.getLightBlockY(t);
					if(by>y0) {
						float s = 1f - MathUtils.clamp((by - cloudTop) / (cloudBottom - cloudTop), 0, 1);
						v -= deltavy*s*(by-y0);
						if(v<0) v = 0;
					}
				}
			}
			vmap[x0][z0] = v;
		}
		for(Dir8 d : Dir8.values()) {
			int x = x0+d.dx;
			int z = z0+d.dz;
			float dv = deltav*d.len;
			addLight(tile, x, y0, z, v-dv, vmap, false);
		}
	}
	
	public void addLight(Tile tile, int y0, float red, float green, float blue, float v0, boolean skipFirst) {
		float[][] vmap = new float[maxr*2][maxr*2];
		addLight(tile, maxr, y0, maxr, v0, vmap, skipFirst);

		for(int x=0; x<maxr*2; x++)
			for(int z=0; z<maxr*2; z++) {
				int mx = tile.x-maxr+x;
				int mz = tile.z-maxr+z;
				if(!level.isInside(mx, mz))
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
		
		count++;
	}

	public void addLight(Tile tile, int y0, Color color, float v0, boolean skipFirst) {
		if(color==null)
			return;
		float red = color.getRed()/255f;
		float green = color.getGreen()/255f;
		float blue = color.getBlue()/255f;
		addLight(tile, y0, red, green, blue, v0, skipFirst);
	}

	public void addLight(Tile tile, int y0, Vector3f color, float v0, boolean skipFirst) {
		if(color==null)
			return;
		addLight(tile, y0, color.x, color.y, color.z, v0, skipFirst);
	}

	public FloatDataTexture finish() {
		setData(colors).freeBuffer();
		colors = null;
		System.out.printf("%d block light sources\n", count);
		return this;
	}

}
