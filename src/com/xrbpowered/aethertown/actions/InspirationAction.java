package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.state.RegionVisits;
import com.xrbpowered.aethertown.world.Tile;

public class InspirationAction extends TileAction {

	public static final TileAction throwCoin = new InspirationAction("Throw coin", 1) {
		@Override
		public int getInspiration(Tile tile) {
			if(RegionVisits.isTileVisited(tile))
				return 0;
			else
				return super.getInspiration(tile);
		}
		@Override
		protected void onSuccess(Tile tile, boolean alt) {
			super.onSuccess(tile, alt);
			RegionVisits.visitTile(tile);
		}
	}.setCost(10);
	
	public final int inspiration;
	
	public InspirationAction(String name, int ins) {
		super(name);
		this.inspiration = ins;
	}
	
	public int getInspiration(Tile tile) {
		return inspiration;
	}

	@Override
	protected void onSuccess(Tile tile, boolean alt) {
		super.onSuccess(tile, alt);
		int ins = player.addInspiration(getInspiration(tile));
		if(ins>0)
			showToast(String.format("%+d inspiration", ins));
	}

}
