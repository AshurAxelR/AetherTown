package com.xrbpowered.aethertown.render.tiles;

import com.xrbpowered.aethertown.AetherTown;

public class IllumLayer {

	public static final IllumLayer[] layers = new IllumLayer[8];
	private static int nextIndex = 0;
	
	public static final IllumLayer alwaysOn = new IllumLayer(0, 24);
	public static final IllumLayer living = new IllumLayer(6, 23);
	public static final IllumLayer officeHours = new IllumLayer(8, 19);
	public static final IllumLayer leisure = new IllumLayer(9, 22);
	public static final IllumLayer shopping = new IllumLayer(9, 20);
	public static final IllumLayer education = new IllumLayer(7, 21);

	public final int index;
	
	private final int open;
	private final int close;

	private IllumLayer(int open, int close) {
		this.index = nextIndex++;
		layers[index] = this;
		this.open = open;
		this.close = close;
	}

	public int getOpenTime() {
		return AetherTown.settings.noClosingTime ? 0 : open;
	}
	
	public int getCloseTime() {
		return AetherTown.settings.noClosingTime ? 24 : close;
	}
	
	public boolean isActive(int hour) {
		if(AetherTown.settings.noClosingTime)
			return true;
		else
			return hour>=open && hour<close;
	}
	
	public int mask() {
		return 1<<index;
	}
	
	private static int[] maskMap = null;
	
	public static int getMask(int hour) {
		if(maskMap==null) {
			maskMap = new int[24];
			for(int t=0; t<24; t++) {
				int m = 0;
				for(int i=0; i<layers.length; i++) {
					if(layers[i]!=null && layers[i].isActive(t))
						m |= layers[i].mask();
				}
				maskMap[t] = m;
			}
		}
		return maskMap[hour%24];
	}
	
}
