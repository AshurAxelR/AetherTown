package com.xrbpowered.aethertown.world;

import java.io.IOException;
import java.util.Random;

import com.xrbpowered.gl.res.asset.AssetManager;

public abstract class LevelNames {

	private static String[] nouns;
	private static String[] adjectives;
	
	public static final String[] rankNames = {"Inn", "Outpost", "Village", "Town", "City"};
	
	public static int levelRank(int numHouses) {
		if(numHouses<5)
			return 0;
		else if(numHouses<15)
			return 1;
		else if(numHouses<40)
			return 2;
		else if(numHouses<100)
			return 3;
		else
			return 4;
	}
	
	public static String next(Random random, int numHouses) {
		String noun = nouns[random.nextInt(nouns.length)];
		String adj = adjectives[random.nextInt(adjectives.length)];
		return String.format("%s %s %s", adj, noun, rankNames[levelRank(numHouses)]);
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
