package com.xrbpowered.aethertown.render;

import java.awt.Color;
import java.util.HashMap;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.texture.FloatDataTexture;
import com.xrbpowered.gl.res.texture.Texture;

public class BlockLighting {

	public static final float illumTrigger = 2.1f;
	
	private static final float deltav = 0.08f;
	private static final float deltavy = deltav/12f;
	private static final int maxr = (int)Math.ceil(1/deltav)+1;

	private static final float cloudTop = 0;
	private static final float cloudBottom = -40;

	public final Level level;
	public Vector4f[][][] colors = new Vector4f[IllumLayer.layers.length][][];
	
	private int count = 0;
	private int maskFilter = 0;
	
	private HashMap<Integer, FloatDataTexture> textures = null;
	private static Texture black = null;
	
	public BlockLighting(Level level) {
		this.level = level;
	}
	
	private Vector4f[][] createLayer(IllumLayer layer) {
		if(colors[layer.index]==null)
			colors[layer.index] = PointLightArray.createVectors(level.levelSize, new Vector4f(0, 0, 0, 1));
		return colors[layer.index];
	}
	
	private void addLight(Tile tile, int x0, int y0, int z0, float v, float[][] vmap, boolean skip) {
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
						// no blocking in full fog
						float s = 1f - MathUtils.clamp((by - cloudTop) / (cloudBottom - cloudTop), 0, 1);
						v -= deltavy*s*(by-y0);
						if(v<0) v = 0;
						if(vmap[x0][z0]>=v)
							return;
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
	
	private static void addColor(Vector4f[][] colors, int x, int z, float red, float green, float blue) {
		Vector4f c = colors[x][z];
		c.x += red;
		if(c.x>1f) c.x = 1f;
		c.y += green;
		if(c.y>1f) c.y = 1f;
		c.z += blue;
		if(c.z>1f) c.z = 1f;
	}
	
	public void addLight(IllumLayer layer, Tile tile, int y0, float red, float green, float blue, float v0, boolean skipFirst) {
		float[][] vmap = new float[maxr*2][maxr*2];
		addLight(tile, maxr, y0, maxr, v0, vmap, skipFirst);

		Vector4f[][] colors = createLayer(layer);
		for(int x=0; x<maxr*2; x++)
			for(int z=0; z<maxr*2; z++) {
				int mx = tile.x-maxr+x;
				int mz = tile.z-maxr+z;
				if(!level.isInside(mx, mz))
					continue;
				float v = vmap[x][z];
				v = v*v;
				addColor(colors, mx, mz, v*red, v*green, v*blue);
				/*Vector4f c = colors[mx][mz];
				float v = vmap[x][z];
				v = v*v;
				c.x += v*red;
				if(c.x>1f) c.x = 1f;
				c.y += v*green;
				if(c.y>1f) c.y = 1f;
				c.z += v*blue;
				if(c.z>1f) c.z = 1f;*/
			}
		
		count++;
	}

	public void addLight(IllumLayer layer, Tile tile, int y0, Color color, float v0, boolean skipFirst) {
		if(color==null || layer==null)
			return;
		float red = color.getRed()/255f;
		float green = color.getGreen()/255f;
		float blue = color.getBlue()/255f;
		addLight(layer, tile, y0, red, green, blue, v0, skipFirst);
	}

	public void addLight(IllumLayer layer, Tile tile, int y0, Vector3f color, float v0, boolean skipFirst) {
		if(color==null || layer==null)
			return;
		addLight(layer, tile, y0, color.x, color.y, color.z, v0, skipFirst);
	}

	private Vector4f[][] mergeLayers(int mask) {
		Vector4f[][] colors = PointLightArray.createVectors(level.levelSize, new Vector4f(0, 0, 0, 1));
		for(int i=0; i<IllumLayer.layers.length; i++) {
			Vector4f[][] layer = this.colors[i];
			if(layer==null)
				continue;
			if((IllumLayer.layers[i].mask() & mask)==0)
				continue;
			for(int x=0; x<level.levelSize; x++)
				for(int z=0; z<level.levelSize; z++) {
					Vector4f v = layer[x][z];
					addColor(colors, x, z, v.x, v.y, v.z);
				}
		}
		return colors;
	}
	
	public void finish() {
		textures = new HashMap<>();
		maskFilter = 0;
		for(int i=0; i<IllumLayer.layers.length; i++) {
			if(colors[i]!=null)
				maskFilter |= IllumLayer.layers[i].mask();
		}
		for(int t=0; t<24; t++) {
			int m = IllumLayer.getMask(t) & maskFilter;
			if(!textures.containsKey(m)) {
				FloatDataTexture tex = new FloatDataTexture(level.levelSize, level.levelSize, false, true, true);
				tex.setData(mergeLayers(m)).freeBuffer();
				textures.put(m, tex);
			}
		}
		if(black==null)
			black = TexColor.get(Color.BLACK);
		colors = null;
		System.out.printf("%d block light sources, %d textures\n", count, textures.size());
	}
	
	public void bind(int illumMask, int index) {
		Texture tex = textures.get(illumMask & maskFilter);
		(tex==null ? black : tex).bind(index);
	}
	
	public void release() {
		for(FloatDataTexture tex : textures.values())
			tex.release();
		textures = null;
	}

}
