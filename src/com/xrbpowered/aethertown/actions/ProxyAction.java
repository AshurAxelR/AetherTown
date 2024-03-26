package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.world.Tile;

public abstract class ProxyAction extends TileAction {

	public final TileAction action;
	
	public ProxyAction(TileAction action) {
		super(action.name);
		this.action = action;
	}
	
	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		return action.isEnabled(tile, alt);
	}
	
	@Override
	public String getCostInfo(Tile tile, boolean alt) {
		return formatCostInfo(getDelay(tile, alt) + action.getDelay(tile, alt), getCost(tile, alt) + action.getCost(tile, alt));
	}
	
	@Override
	protected void onFail(Tile tile, boolean alt) {
		action.onFail(tile, alt);
	}
	
	@Override
	protected void onSuccess(Tile tile, boolean alt) {
		super.onSuccess(tile, alt);
		action.onSuccess(tile, alt);
	}

}
