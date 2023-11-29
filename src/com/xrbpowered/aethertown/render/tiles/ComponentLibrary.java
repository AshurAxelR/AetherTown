package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.tiles.Alcove;
import com.xrbpowered.aethertown.world.tiles.Bench;
import com.xrbpowered.aethertown.world.tiles.ChurchT;
import com.xrbpowered.aethertown.world.tiles.Fountain;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Pavillion;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.aethertown.world.tiles.StreetSlope;

public class ComponentLibrary {

	private static final TileTemplate[] allTemplates = {
		Alcove.template,
		Bench.templatePlaza,
		ChurchT.template,
		Fountain.template,
		Hill.template,
		HouseT.template,
		Monument.template,
		Park.template,
		Pavillion.template,
		Plaza.template,
		Street.template,
		StreetSlope.template1,
		StreetSlope.template2,
		StreetSlope.template4
	};
	
	public static void createAllComponents() {
		for(TileTemplate t : allTemplates)
			t.createComponents();
	}
}
