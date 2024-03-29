package com.xrbpowered.aethertown.actions.menus;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.HotelBooking;
import com.xrbpowered.aethertown.state.RegionVisits;
import com.xrbpowered.aethertown.ui.dialogs.ConfirmDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenuDialog;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class HotelActionMenu extends TileActionMenu {

	public static final int initialCost = 500;
	public static final int visitedCost = 2500;
	
	public static final TileAction checkInAction = new TileAction("Check in") {
		@Override
		public int getCost(Tile tile, boolean alt) {
			return RegionVisits.isTileVisited(tile) ? visitedCost : initialCost;
		}
		
		private boolean canAfford(Tile tile) {
			if(RegionVisits.isTileVisited(tile))
				return AetherTown.player.cash >= visitedCost;
			else
				return true;
		}
		
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			if(HotelBooking.hasBooking(tile))
				return false;
			if(!canAfford(tile))
				return false;
			return super.isEnabled(tile, alt);
		}

		private void showBooking() {
			HotelBooking booking = HotelBooking.getBooking();
			if(booking!=null)
				showToast("Booked until "+WorldTime.getFormattedTimestamp(booking.expires));
		}
		
		@Override
		protected void onFail(Tile tile, boolean alt) {
			if(HotelBooking.hasBooking(tile))
				showBooking();
			else if(!canAfford(tile))
				showToast("Not enough funds");
			else
				super.onFail(tile, alt);
		}
		
		protected void onSuccess(Tile tile, boolean alt) {
			if(HotelBooking.hasBooking(null)) {
				ConfirmDialog.show("Check in", String.format(
						"<p>You already have a reservation at<br>"+
						"<span class=\"w\">%s</span></p>"+
						"<p>Do you want to cancel it and check in to this hotel?</p>",
						HotelBooking.getBooking().hotel.getFullAddress()),
						200, "CHECK IN", () -> {
							HotelBooking.cancel();
							onSuccess(tile, alt);
						});
				return;
			}
			super.onSuccess(tile, alt);
			HotelBooking.book(tile);
			showBooking();
		}
	}.setDelay(5);
	
	public HotelActionMenu(boolean inn) {
		TileActionMenu reception = new TileActionMenu();
		reception.addAction(checkInAction);
		reception.addMenu("MAPS", MapsMenu.menu);
		reception.addAction(CivicCentreActionMenu.collectEarningsAction);
		addMenu("RECEPTION", reception);

		if(inn)
			addMenu("RESTAURANT", FoodActionMenu.restaurant);
		else
			addMenu("BAR", FoodActionMenu.bar);

		TileActionMenu room = new RoomMenu() {
			@Override
			public boolean isEnabled(Tile tile) {
				return HotelBooking.hasBooking(tile);
			}
			@Override
			public void disabledAction(Tile tile, TileActionMenuDialog dialog) {
				showToast("Requires reservation");
			}
		};
		addMenu("ROOM", room, 5);
	}
	
}
