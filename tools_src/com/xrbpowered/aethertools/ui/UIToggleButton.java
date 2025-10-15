package com.xrbpowered.aethertools.ui;

import java.awt.Color;
import java.awt.GradientPaint;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.std.UIButton;

public class UIToggleButton extends UIButton {

	public static Color colorToggled = new Color(0xbbbbbb);

	protected boolean toggled = false;
	
	public UIToggleButton(UIContainer parent, String label) {
		super(parent, label);
	}
	
	public boolean isToggled() {
		return toggled;
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}
	
	public void toggle() {
		toggled = !toggled;
	}
	
	@Override
	public void onAction() {
		toggle();
	}

	@Override
	public void paint(GraphAssist g) {
		if(isToggled())
			g.setColor(colorToggled);
		else if(isEnabled()) {
			if(down)
				g.setColor(colorDown);
			else
				g.setPaint(new GradientPaint(0, 0, colorGradTop, 0, getHeight(), colorGradBottom));
		}
		else
			g.setColor(colorDisabled);
		g.fill(this);
		
		g.border(this, isHover() ? colorText : colorBorder);
		g.setColor(isEnabled() ? colorText : colorTextDisabled);
		g.setFont(font);
		g.drawString(label, getWidth()/2f, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
	}
}
