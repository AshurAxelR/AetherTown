package com.xrbpowered.aethertown.render;

import java.awt.Color;

import com.xrbpowered.aethertown.world.tiles.Plaza;

public enum TerrainMaterial {

	park(
		new Color(0x699b47),
		new Color(0x699b47),
		new Color(0x65993d),
		new Color(0x6a9c40),
		new Color(0x729d47),
		new Color(0x729d47),
		TerrainBuilder.snowColor
	),
	hillGrass(
		new Color(0x5e8c47),
		new Color(0x689447),
		new Color(0x729c47),
		new Color(0x7da547),
		new Color(0x8eaa55),
		new Color(0xa3b167),
		TerrainBuilder.snowColor
	),
	rock(
		TerrainBuilder.cliffColor,
		TerrainBuilder.cliffColor,
		TerrainBuilder.cliffColor,
		TerrainBuilder.cliffColor,
		TerrainBuilder.cliffColor,
		TerrainBuilder.cliffColor,
		TerrainBuilder.cliffColor
	),
	plaza(
		Plaza.plazaColor,
		Plaza.plazaColor,
		Plaza.plazaColor,
		Plaza.plazaColor,
		Plaza.plazaColor,
		Plaza.plazaColor,
		new Color(0xd5dadc)
	);
	
	public final Color[] colors;
	
	private TerrainMaterial(Color... colors) {
		this.colors = colors;
	}
	
}
