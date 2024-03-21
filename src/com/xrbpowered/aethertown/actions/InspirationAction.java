package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.state.RegionVisits;
import com.xrbpowered.aethertown.world.Tile;

public class InspirationAction extends TileAction {

	public static final TileAction throwCoin = new InspirationAction("Throw coin", 1).oncePerTile().setCost(10);
	
	public final int inspiration;
	public final int xp;
	
	protected boolean oncePerTile = false;
	protected CooldownSettings insCooldown = null;
	
	public InspirationAction(String name, int ins, int xp) {
		super(name);
		this.inspiration = ins;
		this.xp = xp;
	}

	public InspirationAction(String name, int ins) {
		this(name, ins, 0);
	}

	public InspirationAction oncePerTile() {
		this.oncePerTile = true;
		return this;
	}
	
	public InspirationAction setInsCooldown(CooldownSettings insCooldown) {
		this.insCooldown = insCooldown;
		return this;
	}
	
	protected boolean isGranting(Tile tile, boolean alt) {
		if(oncePerTile && RegionVisits.isTileVisited(tile))
			return false;
		else if(insCooldown!=null && insCooldown.isOnCooldown())
			return false;
		else
			return true;
	}
	
	public int getInspiration(Tile tile, boolean alt) {
		return isGranting(tile, alt) ? inspiration : 0;
	}

	@Override
	protected void onSuccess(Tile tile, boolean alt) {
		super.onSuccess(tile, alt);
		int ins = player.addInspiration(getInspiration(tile, alt));
		// TODO grant XP
		if(ins>0) {
			showToast(String.format("%+d inspiration", ins));
			if(insCooldown!=null)
				insCooldown.start();
		}
		if(oncePerTile)
			RegionVisits.visitTile(tile);
	}

}
