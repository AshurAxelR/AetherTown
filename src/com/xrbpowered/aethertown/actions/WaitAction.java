package com.xrbpowered.aethertown.actions;

public class WaitAction extends TileAction {

	public WaitAction(String name, int delay) {
		super(name);
		setDelay(delay);
	}

	public WaitAction(int delay) {
		super("Wait");
		setDelay(delay);
	}

}
