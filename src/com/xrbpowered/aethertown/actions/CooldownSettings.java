package com.xrbpowered.aethertown.actions;

import com.xrbpowered.aethertown.state.GlobalCooldowns;

public class CooldownSettings {
	public final GlobalCooldowns cooldown;
	public final boolean daily;
	public final double h;

	public CooldownSettings(GlobalCooldowns cooldown, boolean daily, double h) {
		this.cooldown = cooldown;
		this.daily = daily;
		this.h = h;
	}
	
	public boolean isOnCooldown() {
		return cooldown.isOnCooldown();
	}
	
	public void start() {
		if(daily)
			cooldown.startDailyH(h);
		else
			cooldown.startH(h);
	}
}