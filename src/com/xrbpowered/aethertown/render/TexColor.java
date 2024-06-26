package com.xrbpowered.aethertown.render;

import java.awt.Color;
import java.util.HashMap;

import com.xrbpowered.aethertown.render.env.SeasonalTexture;
import com.xrbpowered.gl.res.texture.Texture;

public class TexColor {

	private static HashMap<Integer, Texture> textureCache = new HashMap<>();

	public static Texture getAlpha(int color) {
		return get(new Color(color, true));
	}

	public static Texture get(int color) {
		return get(new Color(color));
	}

	public static Texture get(Color color) {
		int key = color.getRGB();
		Texture texture = textureCache.get(key);
		if(texture==null) {
			texture = new Texture(color);
			textureCache.put(key, texture);
		}
		return texture;
	}
	
	private static Texture palette = null;
	
	public static Texture getPalette() {
		if(palette==null)
			palette = new SeasonalTexture(new int[] {10, 77},
				new Texture("models/palette.png", false, true, false),
				new Texture("models/palette_winter.png", false, true, false)
			);
		return palette;
	}

}
