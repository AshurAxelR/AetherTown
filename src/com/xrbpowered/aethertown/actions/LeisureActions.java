package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.state.GlobalCooldowns;

public abstract class LeisureActions {

	public static final TileAction viewMuseum = new InspirationAction("View collection", 3, 3).oncePerTile().setDelay(30);
	public static final TileAction study = new InspirationAction("Study", 0, 1).setDelay(30);
	public static final TileAction readBooks = new InspirationAction("Read books", 1).setInsCooldown(GlobalCooldowns.readIns.daily()).setDelay(30);
	public static final TileAction playBoardGames = new InspirationAction("Play board games", 1).setDelay(60);
	public static final TileAction playVideoGames = new InspirationAction("Play video games", 1).setDelay(40);
	
	public static TileAction playMusic(boolean bonus) {
		return new InspirationAction("Play music", bonus ? 12 : 5)
				.setInsCooldown(GlobalCooldowns.playMusicIns.daily())
				.setDelay(bonus ? 60 : 30);
	}

	public static TileAction watchMovies(boolean bonus) {
		return new InspirationAction("Watch movies", bonus ? 5 : 2)
				.setInsCooldown(GlobalCooldowns.moviesIns.daily())
				.setDelay(90);
	}
	
}
