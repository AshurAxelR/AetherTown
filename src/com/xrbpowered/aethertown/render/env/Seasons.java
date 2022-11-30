package com.xrbpowered.aethertown.render.env;

import java.awt.Color;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.gl.res.texture.Texture;

public class Seasons {

	public static final int summer = 0;
	public static final int winter = 1;

	public final Color[] colors;
	
	public Seasons(Color... colors) {
		this.colors = colors;
	}
	
	public Color color() {
		return colors[AetherTown.season];
	}
	
	public Texture texture() {
		return TexColor.get(color());
	}

}
