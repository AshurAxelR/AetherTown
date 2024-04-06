package com.xrbpowered.aethertown.actions.menus;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.GetItemAction;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.actions.WaitAction;
import com.xrbpowered.aethertown.state.Earnings;
import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HomeImprovement;
import com.xrbpowered.aethertown.state.items.HouseKeyItem;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.ItemType;
import com.xrbpowered.aethertown.ui.dialogs.ConfirmDialog;
import com.xrbpowered.aethertown.ui.dialogs.HomeListDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class CivicCentreActionMenu extends TileActionMenu {

	public static final TileAction collectEarningsAction = new TileAction("Collect earnings") {
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			return !Earnings.isEmpty();
		}
		
		@Override
		protected void onFail(Tile tile, boolean alt) {
			if(Earnings.isEmpty())
				showToast("Nothing to collect");
			else
				super.onFail(tile, alt);
		}
		
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			super.onSuccess(tile, alt);
			String report = Earnings.collect();
			ConfirmDialog.show("Summary", report, 250);
		}
	};
	
	public static final TileAction claimHomeUIAction = new TileAction("Claim home") {
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			return super.isEnabled(tile, alt) && tile.level.info.settlement.claimOptions>0;
		}
		
		@Override
		public String getLabel(Tile tile, boolean alt) {
			if(HomeData.hasLocalHome(tile.level.info))
				return super.getLabel(tile, alt) + " (view only)";
			else
				return super.getLabel(tile, alt);
		}
		
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			super.onSuccess(tile, alt);
			HomeListDialog.showClaim(HomeData.selectRandomRes(tile.level, tile.level.info.settlement.claimOptions));
		}
	};
	
	public static final TileAction recoverKeyAction = new GetItemAction("Recover key") {
		@Override
		protected boolean isSameItem(Item aitem, Tile tile, boolean alt) {
			if(aitem.type==ItemType.houseKey) {
				HouseKeyItem item = (HouseKeyItem) aitem;
				if(item.house.level.isLevel(tile.level.info))
					return true;
			}
			return false;
		}
		
		@Override
		protected Item generateItem(Tile tile, boolean alt) {
			HomeData home = HomeData.getLocal(tile.level.info);
			return home==null ? null : new HouseKeyItem(home);
		}
	}.setDelay(5).reqHome();
	
	public static final TileAction listHomesUIAction = new TileAction("List owned homes") {
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			return super.isEnabled(tile, alt) && HomeData.totalClaimed()>0;
		}
		
		@Override
		public String getLabel(Tile tile, boolean alt) {
			int num = HomeData.totalClaimed();
			if(num>0)
				return String.format("%s (%d)", super.getLabel(tile, alt), num);
			else
				return super.getLabel(tile, alt);
		}
		
		@Override
		protected void onFail(Tile tile, boolean alt) {
			if(HomeData.totalClaimed()==0)
				showToast("You don't own any homes");
			else
				super.onFail(tile, alt);
		}
		
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			super.onSuccess(tile, alt);
			HomeListDialog.showList();
		}
	};
	
	public CivicCentreActionMenu(boolean civic, boolean post) {
		if(civic) {
			TileActionMenu res = new TileActionMenu();
			res.addAction(claimHomeUIAction);
			res.addAction(recoverKeyAction);
			res.addAction(listHomesUIAction);
			addMenu("HOME OFFICE", res);
			addMenu("OFFICE", OfficeActionMenu.menu);
		}
		if(post) {
			addAction(collectEarningsAction);
			
			TileActionMenu order = new TileActionMenu();
			for(HomeImprovement imp : HomeImprovement.values())
				order.addAction(new ShopActionMenu.BuyHomeImprovementAction(imp, true));
			addMenu("ORDER GOODS", order);
		}

		addMenu("MAPS", MapsMenu.menu);
		addAction(new WaitAction(20));
	}

}
