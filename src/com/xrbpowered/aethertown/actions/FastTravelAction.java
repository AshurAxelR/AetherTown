package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.state.GlobalCooldowns;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.TravelTokenItem;
import com.xrbpowered.aethertown.ui.dialogs.FastTravelDialog;
import com.xrbpowered.aethertown.world.Tile;

public class FastTravelAction extends TileAction {

	public static final FastTravelAction action = new FastTravelAction();
	
	public FastTravelAction() {
		super("Travel");
		setCooldown(GlobalCooldowns.fastTravel.hours(2));
	}

	private boolean hasTravelTokens() {
		for(int i=0; i<player.backpack.size; i++) {
			Item aitem = player.backpack.get(i);
			if(aitem==null)
				break;
			if(aitem instanceof TravelTokenItem)
				return true;
		}
		return false;
	}
	
	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		return super.isEnabled(tile, alt) && hasTravelTokens();
	}
	
	@Override
	protected void onFail(Tile tile, boolean alt) {
		if(!hasTravelTokens())
			showToast("No travel tokens");
		else
			super.onFail(tile, alt);
	}
	
	@Override
	public void onSuccess(Tile tile, boolean alt) {
		FastTravelDialog.show();
	}
	
}
