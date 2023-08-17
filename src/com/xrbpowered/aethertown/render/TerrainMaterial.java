package com.xrbpowered.aethertown.render;

import java.awt.Color;

public enum TerrainMaterial {

	park(new Color(0x70a545), new Color(0xf4fcfd)),
	hillGrass(new Color(0x7da547), new Color(0xf4fcfd)),
	rock(TerrainBuilder.cliffColor, TerrainBuilder.cliffColor);
	
	public final Color[] colors;
	
	private TerrainMaterial(Color... colors) {
		this.colors = colors;
	}
	
}
