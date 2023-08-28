package com.xrbpowered.aethertown.render.env;

import java.awt.Color;

import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.res.texture.Texture;

public class SeasonalTexture extends Texture {

	protected final Texture[] ts;
	protected final Texture[] array = new Texture[WorldTime.daysInYear];
	
	protected SeasonalTexture(int numDays) {
		ts = new Texture[numDays];
	}
	
	public SeasonalTexture(int[] days, Color[] colors) {
		this(days.length);
		for(int i=0; i<ts.length; i++)
			ts[i] = TexColor.get(colors[i]);
		fillArray(days);
	}

	public SeasonalTexture(int[] days, Texture... ts) {
		this(days.length);
		for(int i=0; i<ts.length; i++)
			this.ts[i] = ts[i];
		fillArray(days);
	}

	protected void fillArray(int[] days) {
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
