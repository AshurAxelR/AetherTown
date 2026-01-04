package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import java.util.ArrayList;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.state.RegionVisits;
import com.xrbpowered.aethertown.ui.dialogs.ConfirmDialog;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.StarData;

public abstract class LeisureActions {

	public static final TileAction viewMuseum = new InspirationAction("View collection", 5, 5).oncePerTile().setDelay(30);
	public static final TileAction study = new InspirationAction("Study", 0, 3).setDelay(30);
	public static final TileAction readBooks = new InspirationAction("Read books", 1).setInsCooldown(GlobalCooldowns.readIns.daily()).setDelay(30);
	public static final TileAction playBoardGames = new InspirationAction("Play board games", 1).setDelay(60);
	public static final TileAction playVideoGames = new InspirationAction("Play video games", 1).setDelay(45);
	public static final TileAction paintArt = new InspirationAction("Paint art", 5).setInsCooldown(GlobalCooldowns.artIns.daily()).setDelay(60);
	
	public static TileAction playMusic(String instrument, boolean bonus) {
		return new InspirationAction("Play "+instrument, bonus ? 12 : 5)
				.setInsCooldown(GlobalCooldowns.playMusicIns.daily())
				.setDelay(bonus ? 60 : 30);
	}

	public static TileAction watchMovies(boolean bonus) {
		return new InspirationAction("Watch movies", bonus ? 6 : 2)
				.setInsCooldown(GlobalCooldowns.moviesIns.daily())
				.setDelay(90);
	}

	public static final TileAction discover = new InspirationAction("Discover", 0, 5) {
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			if(oncePerTile && RegionVisits.isTileVisited(tile))
				return false;
			else
				return super.isEnabled(tile, alt);
		}
		@Override
		protected void onFail(Tile tile, boolean alt) {
			if(oncePerTile && RegionVisits.isTileVisited(tile))
				showToast("Already discovered");
			else
				super.onFail(tile, alt);
		}
	}.oncePerTile().setDelay(20);

	public static final TileAction portalKnowledge = new TileAction("Portal knowledge") {
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			if(AetherTown.regionCache.portals.numPortals==0)
				return false;
			else
				return super.isEnabled(tile, alt);
		}
		@Override
		protected void onFail(Tile tile, boolean alt) {
			if(AetherTown.regionCache.portals.numPortals==0)
				showToast("No portals");
			else
				super.onFail(tile, alt);
		}
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			String report = AetherTown.regionCache.portals.createKnowledgeReport(tile.level.info.region);
			int lines = AetherTown.regionCache.portals.numPortals;
			ConfirmDialog.show("Portals", report, lines*20+180);
			super.onSuccess(tile, alt);
		}
	};
	
	public static final TileAction starInfo = new TileAction("Star info") {
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			ArrayList<String> lines = StarData.getStarInfo(tile.level.info.region.getStarChartData());
			if(lines.isEmpty())
				ConfirmDialog.show("Star info", "<p>No notable stars.</p>", 120);
			else {
				StringBuilder sb = new StringBuilder();
				sb.append("<table>");
				for(String s : lines) {
					sb.append("<tr><td>&bull; ");
					sb.append(s);
					sb.append("</td></tr>");
				}
				sb.append("</table>");
				ConfirmDialog.show("Star info", sb.toString(), lines.size()*20+120);
			}
			super.onSuccess(tile, alt);
		}
	};
	
}
