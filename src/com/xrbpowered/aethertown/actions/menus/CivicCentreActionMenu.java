package com.xrbpowered.aethertown.actions.menus;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.state.HomeData.*;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.DummyAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.actions.WaitAction;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;

public class CivicCentreActionMenu extends TileActionMenu {

	public static final TileAction claimHomeAction = new TileAction("Claim home") {
		
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			return super.isEnabled(tile, alt) &&
					tile.level.info.settlement.maxHouses>0 &&
					!hasLocalHome(tile.level.info) &&
					!player.backpack.isFull();
		}
		
		@Override
		public int getCost(Tile tile, boolean alt) {
			return 10000 * totalClaimed();
		}
		
		@Override
		protected void onFail(Tile tile, boolean alt) {
			if(player.backpack.isFull())
				showToast("Inventory full");
			else if(hasLocalHome(tile.level.info))
				showToast("Already claimed in "+tile.level.info.name);
			else
				super.onFail(tile, alt);
		}
		
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			super.onSuccess(tile, alt);
			HouseGenerator sel = selectRandomRes(tile.level, 1).get(0);
			claim(sel);
			showToast(sel.getAddress()+" claimed");
		}
		
	}.setDelay(15);
	
	public CivicCentreActionMenu(boolean civic, boolean post) {
		// TODO complete civic centre actions
		if(civic) {
			TileActionMenu res = new TileActionMenu();
			res.addAction(claimHomeAction);
			res.addAction(new DummyAction("Recover key").setEnabled(false));
			res.addAction(new DummyAction("Abandon home").setEnabled(false));
			addMenu("HOME OFFICE", res);
		}
		if(post) {
			addAction(new DummyAction("Collect earnings").setEnabled(false));
			addAction(new DummyAction("Order goods").setEnabled(false));
		}

		addMenu("MAPS", MapsMenu.menu);
		addAction(new WaitAction(20));
	}

}
