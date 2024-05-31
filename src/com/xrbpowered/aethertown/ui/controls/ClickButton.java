package com.xrbpowered.aethertown.ui.controls;

import java.awt.Color;
import java.awt.Font;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIButtonBase;

public class ClickButton extends UIButtonBase {

	public static final Color bgColor = new Color(0x555555);
	public static final Color bgColorHover = new Color(0x777777);
	public static final Color textColor = Color.WHITE;
	public static final Color textColorDisabled = new Color(0x999999);
	public static final Color bgDeleteColor = new Color(0x773333);
	public static final Color bgDeleteColorHover = new Color(0xaa0000);


	public String label;
	
	public ClickButton(UIContainer parent, String label) {
		super(parent);
		this.label = label;
		setSize(120, 32);
	}
	
	@Override
	public boolean repaintOnHover() {
		return true;
	}
	
	public String getLabel() {
		return label;
	}
	
	protected boolean showEnabled() {
		return isEnabled();
	}
	
	protected Color getBackgroundColor() {
		return isHover() && showEnabled() ? bgColorHover : bgColor;
	}

	protected Font getFont() {
		return Fonts.smallBold;
	}
	
	protected Color getTextColor() {
		return showEnabled() ? textColor : textColorDisabled;
	}
	
	protected float getLabelAnchorX() {
		return getWidth()/2f;
	}
	
	protected int getLabelAlign() {
		return GraphAssist.CENTER;
	}
	
	protected void paintBackground(GraphAssist g) {
		g.fill(this, getBackgroundColor());
	}
	
	protected void paintLabel(GraphAssist g) {
		String s = getLabel();
		if(s==null)
			return;
		g.setColor(getTextColor());
		g.setFont(getFont());
		g.drawString(s, getLabelAnchorX(), getHeight()/2f, getLabelAlign(), GraphAssist.CENTER);
	}
	
	@Override
	public void paint(GraphAssist g) {
		paintBackground(g);
		paintLabel(g);
	}

}
