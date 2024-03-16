package com.xrbpowered.aethertown.actions.menus;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.world.Tile;

public class DummyAction extends TileAction {

	private boolean enabled = true;
	
	public DummyAction(String name) {
		super(name);
	}
	
	public TileAction setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		return enabled && super.isEnabled(tile, alt);
	}

}
