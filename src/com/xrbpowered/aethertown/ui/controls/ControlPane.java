package com.xrbpowered.aethertown.ui.controls;

import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class ControlPane extends UIPane {

	public ControlPane(UIContainer parent) {
		super(parent, false);
	}

	@Override
	public void layout() {
		for(UIElement c : children) {
			c.setPosition(0, 0);
			c.setSize(getWidth(), getHeight());
			c.layout();
		}
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, transparent);
	}
}
