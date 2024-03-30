package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.state.RegionVisits;

public abstract class LeisureActions {

	public static final TileAction viewMuseum = new InspirationAction("View collection", 3, 3).oncePerTile().setDelay(30);
	public static final TileAction study = new InspirationAction("Study", 0, 1).setDelay(30);
	public static final TileAction readBooks = new InspirationAction("Read books", 1).setInsCooldown(GlobalCooldowns.readIns.daily()).setDelay(30);
	public static final TileAction playBoardGames = new InspirationAction("Play board games", 1).setDelay(60);
	public static final TileAction playVideoGames = new InspirationAction("Play video games", 1).setDelay(40);
	public static final TileAction paintArt = new InspirationAction("Paint art", 5).setInsCooldown(GlobalCooldowns.artIns.daily()).setDelay(60);
	
	public static TileAction playMusic(String instrument, boolean bonus) {
		return new InspirationAction("Play "+instrument, bonus ? 12 : 5)
				.setInsCooldown(GlobalCooldowns.playMusicIns.daily())
				.setDelay(bonus ? 60 : 30);
	}

	public static TileAction watchMovies(boolean bonus) {
		return new InspirationAction("Watch movies", bonus ? 5 : 2)
				.setInsCooldown(GlobalCooldowns.moviesIns.daily())
				.setDelay(90);
	}

	public static final TileAction discover = new InspirationAction("Discover", 0, 5) {
		@Override
		public boolean isEnabled(com.xrbpowered.aethertown.world.Tile tile, boolean alt) {
			if(oncePerTile && RegionVisits.isTileVisited(tile))
				return false;
			else
				return super.isEnabled(tile, alt);
		}
		protected void onFail(com.xrbpowered.aethertown.world.Tile tile, boolean alt) {
			if(oncePerTile && RegionVisits.isTileVisited(tile))
				showToast("Already discovered");
			else
				super.onFail(tile, alt);
		}
	}.oncePerTile().setDelay(30);

}
