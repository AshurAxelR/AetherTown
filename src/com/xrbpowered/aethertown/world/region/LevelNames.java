package com.xrbpowered.aethertown.world.region;

import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.gl.res.asset.AssetManager;

public abstract class LevelNames {

	private static String[] nouns;
	private static String[] adjectives;
	private static String[] saints;
	private static WRandom wsaints;
	
	public static String next(Random random, LevelSettlementType settlement) {
		String noun = nouns[random.nextInt(nouns.length)];
		String adj = adjectives[random.nextInt(adjectives.length)];
		return String.format("%s %s %s", adj, noun, settlement.title);
	}

	public static String nextSaint(Random random) {
		return saints[wsaints.next(random)];
	}

	private static String[] loadList(String path) throws IOException {
		return AssetManager.defaultAssets.loadString(path).trim().split("(\\s*\\n\\s*)+");
	}
	
	private static WRandom getWeights(String[] names) {
		double[] w = new double[names.length];
		Pattern regex = Pattern.compile("^([0-9]+\\.?[0-9]*)\\s+(.*)");
		for(int i=0; i<names.length; i++) {
			String s = names[i];
			Matcher m = regex.matcher(s);
			if(m.matches()) {
				w[i] = Double.parseDouble(m.group(1));
				names[i] = m.group(2);
			}
			else {
				w[i] = 1.0;
			}
		}
		return new WRandom(w);
	}
	
	public static void load() {
		try {
			nouns = loadList("str/levelname_nouns.txt");
			adjectives = loadList("str/levelname_adjectives.txt");
			saints = loadList("str/st_names.txt");
			wsaints = getWeights(saints);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
