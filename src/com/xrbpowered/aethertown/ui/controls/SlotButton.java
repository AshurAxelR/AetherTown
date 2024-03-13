package com.xrbpowered.aethertown.ui.controls;

import java.awt.Color;
import java.awt.Font;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public abstract class SlotButton extends SelectButton {

	public static final Color bgColorEmpty = new Color(0x222222);

	public SlotButton(UIContainer parent) {
		super(parent, null);
	}
	
	public boolean isEmpty() {
		return true;
	}
	
	public abstract String getItemName();
	
	@Override
	public String getLabel() {
		return isEmpty() ? "[EMPTY]" : getItemName();
	}
	
	@Override
	protected Color getBackgroundColor() {
		return isEmpty() && !isSelected() && !isHover() ? bgColorEmpty : super.getBackgroundColor();
	}
	
	@Override
	protected Color getTextColor() {
		return isEmpty() ? textColorDisabled : super.getTextColor();
	}
	
	@Override
	protected Font getFont() {
		return Fonts.small;
	}
	
	@Override
	protected float getLabelAnchorX() {
		return 20;
	}
	
	@Override
	protected int getLabelAlign() {
		return GraphAssist.LEFT;
	}
	
	protected void paintDot(GraphAssist g) {
		g.pushPureStroke(true);
		g.fillCircle(getWidth() - 20, getHeight()/2, 5);
		g.popPureStroke();
	}

}
