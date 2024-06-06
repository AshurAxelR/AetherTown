package com.xrbpowered.aethertown.ui.controls;

import java.awt.Color;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenuDialog;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class OptionBox extends UIContainer {

	public int value;
	public int minValue, maxValue;
	
	private ClickButton buttonDec, buttonInc;

	public OptionBox(UIContainer parent, int min, int max, int initValue) {
		super(parent);
		this.minValue = min;
		this.maxValue = max;
		this.value = initValue;
		
		buttonDec = new ClickButton(this, null) {
			@Override
			public void onAction() {
				if(value>minValue) {
					value--;
					onValueChanged(value);
					repaint();
				}
			}
			@Override
			protected void paintLabel(GraphAssist g) {
				g.setColor(getTextColor());
				TileActionMenuDialog.paintArrow(g, 16, (int)getHeight()/2, -1);
			}
		};

		buttonInc = new ClickButton(this, null) {
			@Override
			public void onAction() {
				if(value<maxValue) {
					value++;
					onValueChanged(value);
					repaint();
				}
			}
			@Override
			protected void paintLabel(GraphAssist g) {
				g.setColor(getTextColor());
				TileActionMenuDialog.paintArrow(g, (int)getWidth()-16, (int)getHeight()/2, 1);
			}
		};
		
		setSize(280, buttonDec.getHeight());
	}
	
	public void onValueChanged(int value) {
	}
	
	@Override
	public void layout() {
		buttonDec.setSize(40, getHeight());
		buttonDec.setPosition(0, 0);
		buttonInc.setSize(40, getHeight());
		buttonInc.setPosition(getWidth()-buttonInc.getWidth(), 0);
	}

	public String getText(int value) {
		return Integer.toString(value);
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		g.fillRect(40, 0, getWidth()-80, getHeight(), SlotButton.bgColorEmpty);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.small);
		g.drawString(getText(value), getWidth()/2, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
	}
}
