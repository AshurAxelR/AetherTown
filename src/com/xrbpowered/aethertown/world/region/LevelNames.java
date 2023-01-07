package com.xrbpowered.aethertown.world.region;

import java.io.IOException;
import java.util.Random;

import com.xrbpowered.gl.res.asset.AssetManager;

public abstract class LevelNames {

	private static String[] nouns;
	private static String[] adjectives;
	private static String[] saints;
	
	public static final String[] rankNames = {"Inn", "Outpost", "Village", "Town", "City"};
	
	public static String next(Random random, int numHouses) {
		String noun = nouns[random.nextInt(nouns.length)];
		String adj = adjectives[random.nextInt(adjectives.length)];
		return String.format("%s %s %s", adj, noun, rankNames[HouseAssignment.levelRank(numHouses)]);
	}

	public static String nextSaint(Random random) {
		return saints[random.nextInt(saints.length)];
	}

	public static void load() {
		try {
			nouns = AssetManager.defaultAssets.loadString("str/levelname_nouns.txt").trim().split("(\\s*\\n\\s*)+");
			adjectives = AssetManager.defaultAssets.loadString("str/levelname_adjectives.txt").trim().split("(\\s*\\n\\s*)+");
			saints = AssetManager.defaultAssets.loadString("str/st_names.txt").trim().split("(\\s*\\n\\s*)+");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
