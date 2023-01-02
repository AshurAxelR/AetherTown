package com.xrbpowered.aethertown.world;

import java.io.IOException;
import java.util.Random;

import com.xrbpowered.gl.res.asset.AssetManager;

public abstract class LevelNames {

	private static String[] nouns;
	private static String[] adjectives;
	
	public static String next(Random random, int numHouses) {
		String noun = nouns[random.nextInt(nouns.length)];
		String adj = adjectives[random.nextInt(adjectives.length)];
		String rank;
		if(numHouses<5)
			rank = "Inn";
		else if(numHouses<15)
			rank = "Outpost";
		else if(numHouses<40)
			rank = "Village";
		else if(numHouses<100)
			rank = "Town";
		else
			rank = "City";
		return String.format("%s %s %s", adj, noun, rank);
	}
	
	public static void load() {
		try {
			nouns = AssetManager.defaultAssets.loadString("str/levelname_nouns.txt").trim().split("(\\s*\\n\\s*)+");
			adjectives = AssetManager.defaultAssets.loadString("str/levelname_adjectives.txt").trim().split("(\\s*\\n\\s*)+");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
