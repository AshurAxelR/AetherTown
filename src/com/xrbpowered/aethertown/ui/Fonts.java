package com.xrbpowered.aethertown.ui;

import java.awt.Font;
import java.io.IOException;

import com.xrbpowered.gl.res.asset.AssetManager;

public abstract class Fonts {

	public static Font small;
	public static Font large;
	
	public static void load() {
		System.out.println("Loading fonts...");
		try {
			small = AssetManager.defaultAssets.loadFont("fonts/RobotoCondensed-Regular.ttf").deriveFont(16f);
			large = AssetManager.defaultAssets.loadFont("fonts/RobotoCondensed-Bold.ttf").deriveFont(24f);
		}
		catch(IOException e) {
			e.printStackTrace();
			small = null;
			large = null;
		}
		System.out.println("Done.");
	}

}
