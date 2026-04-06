package com.xrbpowered.aethertown.actions.menus;

import java.util.ArrayList;

import com.xrbpowered.aethertown.actions.LeisureActions;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.ui.dialogs.ConfirmDialog;
import com.xrbpowered.aethertown.ui.dialogs.StarChartDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.chart.ChartStar;

public class MuseumActionMenu extends TileActionMenu {

	public MuseumActionMenu() {
		addAction(LeisureActions.viewMuseum);
		addAction(LeisureActions.portalKnowledge);
		addMenu("MAPS", MapsMenu.noToken);
		
		TileActionMenu stars = new TileActionMenu();
		
		stars.addAction(new TileAction("Overview") {
			@Override
			protected void onSuccess(Tile tile, boolean alt) {
				ArrayList<String> lines = ChartStar.getStarInfo(tile.level.info.region.getStarChartData());
				if(lines.isEmpty())
					ConfirmDialog.show("Star knowledge", "<p>No notable stars.</p>", 120);
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
		});
		
		stars.addAction(new TileAction("View chart: north") {
			@Override
			public void onSuccess(Tile tile, boolean alt) {
				StarChartDialog.show(tile.level.info.region, StarChartDialog.nhemi);
			}
		});

		stars.addAction(new TileAction("View chart: equator") {
			@Override
			public void onSuccess(Tile tile, boolean alt) {
				StarChartDialog.show(tile.level.info.region, StarChartDialog.cylinder);
			}
		});

		addMenu("STARS", stars);

	}

}
