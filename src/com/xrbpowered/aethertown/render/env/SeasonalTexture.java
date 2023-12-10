package com.xrbpowered.aethertown.render.env;

import java.awt.Color;

import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.res.texture.Texture;

public class SeasonalTexture extends Texture {

	protected final Texture[] array = new Texture[WorldTime.daysInYear];
	
	protected SeasonalTexture() {
	}
	
	public SeasonalTexture(int[] days, Color[] colors) {
		Texture[] ts = new Texture[days.length];
		for(int i=0; i<ts.length; i++)
			ts[i] = TexColor.get(colors[i]);
		fillArray(days, ts);
	}

	public SeasonalTexture(int[] days, Texture... ts) {
		fillArray(days, ts);
	}

	protected void fillArray(int[] days, Texture[] ts) {
		Texture last = ts[ts.length-1];
		Texture t = last;
		int si = 0;
		for(int d=0; d<array.length; d++) {
			if(si<ts.length && days[si]<=d) {
				t = ts[si];
				si++;
			}
			array[d] = t;
		}
	}
	
	public SeasonalTexture copy() {
		SeasonalTexture tex = new SeasonalTexture();
		for(int d=0; d<array.length; d++)
			tex.array[d] = array[d];
		return tex;
	}
	
	public SeasonalTexture replace(int begin, int end, Texture t) {
		if(begin>end)
			end += WorldTime.daysInYear;
		for(int d=begin; d<end; d++)
			array[d % WorldTime.daysInYear] = t;
		return this;
	}
	
	public void release() {
		// do not release TexColor textures
		// for(Texture t : ts)
		//	GL11.glDeleteTextures(t.getId());
	}
	
	public void updateCurrent() {
		Texture t = array[WorldTime.getDayOfYear()];
		this.width = t.getWidth();
		this.height = t.getHeight();
		this.texId = t.getId();
	}
	
	public int getWidth() {
		updateCurrent();
		return super.getWidth();
	}
	
	public int getHeight() {
		updateCurrent();
		return super.getHeight();
	}
	
	public int getId() {
		updateCurrent();
		return super.getId();
	}
	
	@Override
	public void bind(int index) {
		updateCurrent();
		super.bind(index);
	}

}
