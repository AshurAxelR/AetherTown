package com.xrbpowered.aethertown.render.env;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import com.xrbpowered.aethertown.render.TerrainMaterial;
import com.xrbpowered.gl.res.texture.Texture;

public class TerrainTexture extends SeasonalTexture {

	public static final int[] days = {14, 22, 35, 48, 56, 70, 77};
	
	private static final TerrainMaterial defaultTerrain = TerrainMaterial.rock;
	
	private Texture[] ts = null;
	private TerrainMaterial[][] map;
	
	public TerrainTexture(int xsize, int zsize) {
		this.width = xsize;
		this.height = zsize;
		this.map = new TerrainMaterial[xsize][zsize];
	}
	
	public void set(int x, int z, TerrainMaterial c) {
		map[x][z] = c;
	}
	
	public TerrainTexture create() {
		ts = new Texture[days.length];
		for(int i=0; i<ts.length; i++) {
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			int[] data = new int[width * height * 4];
			int offs = 0;
			for(int z=0; z<height; z++)
				for(int x=0; x<width; x++) {
					TerrainMaterial m = map[x][z];
					if(m==null)
						m = defaultTerrain;
					Color col = m.colors[i];
					data[offs+3] = 255;
					data[offs+0] = col.getRed();
					data[offs+1] = col.getGreen();
					data[offs+2] = col.getBlue();
					offs += 4;
				}
			img.getRaster().setPixels(0, 0, width, height, data);
			ts[i] = new Texture(img, true, false);
		}
		fillArray(days, ts);
		return this;
	}

	public void release() {
		if(ts!=null) {
			for(Texture t : ts)
				GL11.glDeleteTextures(t.getId());
		}
	}
}
