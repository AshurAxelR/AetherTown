package com.xrbpowered.aethertown.ui;

import java.awt.Color;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIButtonBase;

public class ClickButton extends UIButtonBase {

	public static final Color bgColor = new Color(0x555555);
	public static final Color bgColorHover = new Color(0x777777);
	public static final Color textColor = Color.WHITE;
	public static final Color textColorDisabled = new Color(0x999999);

	public String label;
	
	public ClickButton(UIContainer parent, String label) {
		super(parent);
		this.label = label;
		setSize(120, 32);
	}
	
	public String getLabel() {
		return label;
	}

	@Override
	public void paint(GraphAssist g) {
		g.fill(this, hover ? bgColorHover : bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(isEnabled() ? textColor : textColorDisabled);
		g.setFont(Fonts.smallBold);
		g.drawString(getLabel(), getWidth()/2f, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
	}

}
