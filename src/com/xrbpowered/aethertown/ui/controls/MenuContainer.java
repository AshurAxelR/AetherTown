package com.xrbpowered.aethertown.ui.controls;

import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class MenuContainer extends UIContainer {

	public MenuContainer(UIContainer parent) {
		super(parent);
	}

	@Override
	public void layout() {
		float y = 0f;
		for(UIElement c : children) {
			c.setSize(getWidth(), c.getHeight());
			c.setPosition(0, y);
			y += c.getHeight();
		}
	}
	
}
