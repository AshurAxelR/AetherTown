package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.actions.HouseTileAction.closingSoon;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public abstract class TileAction {

	public final String name;
	
	protected int delay = 0;
	protected int cost = 0;
	
	protected CooldownSettings cooldown = null;
	protected boolean reqHome = false;

	public TileAction(String name) {
		this.name = name;
	}
	
	public boolean isEnabled(Tile tile, boolean alt) {
		if(isReqHome() && !HomeData.hasLocalHome(tile.level.info))
			return false;
		else if(cooldown!=null && cooldown.isOnCooldown())
			return false;
		else
			return !closingSoon(tile, alt, this);
	}
	
	public TileAction setCost(int cost) {
		this.cost = cost;
		return this;
	}
	
	public int getCost(Tile tile, boolean alt) {
		return cost;
	}
	
	public TileAction setDelay(int delay) {
		this.delay = delay;
		return this;
	}
	
	public int getDelay(Tile tile, boolean alt) {
		return delay;
	}

	public TileAction setCooldown(CooldownSettings cd) {
		this.cooldown = cd;
		return this;
	}
	
	public CooldownSettings getCooldown() {
		return cooldown;
	}
	
	public TileAction reqHome() {
		this.reqHome = true;
		return this;
	}
	
	public boolean isReqHome() {
		return reqHome;
	}

	public String getLabel(Tile tile, boolean alt) {
		return name;
	}
	
	public String getCostInfo(Tile tile, boolean alt) {
		return formatCostInfo(getDelay(tile, alt), getCost(tile, alt));
	}
	
	public void applyCost(Tile tile, boolean alt) {
		player.cash -= getCost(tile, alt);
		WorldTime.time += getDelay(tile, alt) * WorldTime.minute;
		if(cooldown!=null)
			cooldown.start();
	}
	
	protected void onFail(Tile tile, boolean alt) {
		if(isReqHome() && !HomeData.hasLocalHome(tile.level.info))
			showToast("Requires local home");
		else if(closingSoon(tile, alt, this))
			showToast("Closing soon");
		else if(cooldown!=null && cooldown.isOnCooldown())
			showToast(cooldown.cooldown.formatRemaining());
	}

	protected void onSuccess(Tile tile, boolean alt) {
		applyCost(tile, alt);
	}

	public final boolean performAt(Tile tile, boolean alt) {
		if(isEnabled(tile, alt)) {
			onSuccess(tile, alt);
			return true;
		}
		else {
			onFail(tile, alt);
			return false;
		}
	}
	
	public static String formatCostInfo(int delay, int cost) {
		if(delay>0 && cost>0)
			return String.format("%s, %s", formatDelay(delay), formatCost(cost));
		else if(delay>0)
			return formatDelay(delay);
		else if(cost>0)
			return formatCost(cost);
		else
			return null;
	}
	
	public static String formatDelay(int min) {
		int h = min/60;
		int m = min%60;
		if(h>0 && m>0)
			return String.format("%dh %dmin", h, m);
		else if(h>0)
			return String.format("%dh", h);
		else
			return String.format("%dmin", m);
	}

	public static String formatCost(int cost) {
		return String.format("£%d.%02d", cost/100, cost%100);
	}

	public static String formatCurrency(int c) {
		if(c>=0)
			return formatCost(c);
		else
			return "-"+formatCost(-c);
	}

}
