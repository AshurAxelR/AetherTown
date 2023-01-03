package com.xrbpowered.aethertown.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;

public abstract class Template {

	private static final ArrayList<Template> templates = new ArrayList<>();

	public static final Street street = new Street();
	public static final Park park = new Park();
	public static final Hill hill = new Hill();
	public static final Monument monument = new Monument();
	public static final Plaza plaza = new Plaza();

	public static final HouseT house = new HouseT();

	public static void registerTemplate(Template t) {
		templates.add(t);
	}
	
	public static void createAllComponents() {
		for(Template t : templates) {
			t.createComponents();
		}
	}

	public final Color minimapColor;
	
	public Template(Color minimapColor) {
		registerTemplate(this);
		this.minimapColor = minimapColor;
	}
	
	public Tile createTile() {
		return new Tile(this);
	}

	public String getTileInfo(Tile tile) {
		return "";
	}
	
	public int getFixedYStrength() {
		return 2;
	}
	
	public int getGroundY(Tile tile) {
		return tile.basey;
	}

	public int getFenceY(Tile tile, Corner c) {
		return tile.basey;
	}
	
	public int getLightBlockY(Tile tile) {
		return tile.basey;
	}
	
	public float getYAt(Tile tile, float sx, float sz) {
		return Tile.ysize*tile.basey;
	}

	public boolean finalizeTile(Tile tile, Random random) {
		return false;
	}
	
	public abstract void createComponents();
	public abstract void createGeometry(Tile tile, LevelRenderer renderer, Random random);

}
