package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.player;

import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public abstract class TileAction {

	public final String name;
	
	protected int delay = 0;
	protected int cost = 0;

	public TileAction(String name) {
		this.name = name;
	}
	
	public boolean isEnabled(Tile tile) {
		return true;
	}
	
	public TileAction setCost(int cost) {
		this.cost = cost;
		return this;
	}
	
	public int getCost(Tile tile) {
		return cost;
	}
	
	public TileAction setDelay(int delay) {
		this.delay = delay;
		return this;
	}
	
	public int getDelay(Tile tile) {
		return delay;
	}

	public String getLabel(Tile tile) {
		return name;
	}
	
	public String getCostInfo(Tile tile) {
		return formatCostInfo(getDelay(tile), getCost(tile));
	}
	
	protected void applyCost(Tile tile) {
		player.cash -= getCost(tile);
		WorldTime.time += getDelay(tile) * WorldTime.minute;
	}
	
	protected void onFail(Tile tile) {
	}

	protected void onSuccess(Tile tile) {
		applyCost(tile);
	}

	public void performAt(Tile tile) {
		if(isEnabled(tile))
			onSuccess(tile);
		else
			onFail(tile);
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
