package com.xrbpowered.aethertown.ui.controls;

import java.awt.Color;

import com.xrbpowered.zoomui.UIContainer;

public class SelectButton extends ClickButton {

	public static final Color bgColorSelected = Color.WHITE;
	public static final Color textColorSelected = Color.BLACK;

	public SelectButton(UIContainer parent, String label) {
		super(parent, label);
	}

	public boolean isSelected() {
		return false;
	}
	
	@Override
	protected Color getBackgroundColor() {
		return isSelected() ? bgColorSelected : super.getBackgroundColor();
	}
	
	@Override
	protected Color getTextColor() {
		return isSelected() ? textColorSelected : super.getTextColor();
	}

}
