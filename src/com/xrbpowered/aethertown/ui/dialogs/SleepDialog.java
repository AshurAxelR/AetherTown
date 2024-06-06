package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.AetherTown.ui;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.bgColor;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import java.awt.Color;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.InfoBox;
import com.xrbpowered.aethertown.ui.controls.OptionBox;
import com.xrbpowered.aethertown.ui.hud.Hud;
import com.xrbpowered.aethertown.world.stars.Sunrise;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class SleepDialog extends UIPane implements KeyInputHandler {

	private String repaintTime = null;
	
	private OptionBox optHours;
	private ClickButton buttonClose, buttonSleep, buttonExit;
	
	private SleepDialog(UIContainer parent) {
		super(parent, false);
		setSize(440, 200);
		
		optHours = new OptionBox(this, 1, 8, 1) {
			@Override
			public String getText(int value) {
				float until = WorldTime.getTimeOfDay(value * (float)WorldTime.hour);
				return String.format("%d %s until %s", value, value!=1 ? "hours" : "hour", WorldTime.getFormattedTime(until));
			}
		};
		optHours.setPosition(10, 40);
		optHours.setSize(getWidth()-20, optHours.getHeight());

		buttonClose = new ClickButton(this, "CANCEL") {
			@Override
			public void onAction() {
				close();
			}
		};
		buttonClose.setPosition(10, getHeight()-buttonClose.getHeight()-10);
		
		buttonExit = new ClickButton(this, "SLEEP AND EXIT") {
			@Override
			public void onAction() {
				sleep();
				AetherTown.aether.requestExit();
			}
		};
		buttonExit.setSize(160, buttonExit.getHeight());
		buttonExit.setPosition(getWidth()/2 - buttonExit.getWidth()/2, getHeight()-buttonClose.getHeight()-10);
		
		buttonSleep = new ClickButton(this, "SLEEP") {
			@Override
			public void onAction() {
				sleep();
				close();
				Hud.fadeIn(Color.BLACK, 2f);
			}
		};
		buttonSleep.setPosition(getWidth()-buttonSleep.getWidth()-10, getHeight()-buttonSleep.getHeight()-10);
	}
	
	private void sleep() {
		WorldTime.time += optHours.value * WorldTime.hour;
		int ins = player.addInspiration(getIns());
		showToast(String.format("%+d inspiration", ins));
	}

	private int getIns() {
		return (optHours.value+1)/2;
	}
	
	@Override
	public void updateTime(float dt) {
		String t = WorldTime.getFormattedTime();
		if(repaintTime==null || !repaintTime.equals(t)) {
			repaintTime = t;
			repaint();
		}
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
		g.fillRect(10, 80, getWidth()-20, getHeight()-130, Color.BLACK);
		
		g.setColor(Color.WHITE);
		g.setFont(Fonts.small);
		g.drawString("Sleep for:", getWidth()/2, 26, GraphAssist.CENTER, GraphAssist.CENTER);
		
		g.setColor(InfoBox.textColor);
		String s = String.format("%+d inspiration", getIns());
		float y = 104;
		y = g.drawString(s, 30, y, GraphAssist.LEFT, GraphAssist.CENTER);
		
		int day = WorldTime.getDayOfYear();
		Float sunrise = Sunrise.sunrise(day);
		Float sunset = Sunrise.sunset(day);
		s = String.format("Sunrise %s, sunset %s",
				sunrise==null ? "--" : WorldTime.getFormattedTime(sunrise),
				sunset==null ? "--" : WorldTime.getFormattedTime(sunset));
		g.drawString(s, 30, y, GraphAssist.LEFT, GraphAssist.CENTER);
	}
	
	public void close() {
		DialogContainer.close(this);
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		if(DialogContainer.isCloseKey(this, code))
			close();
		return true;
	}

	public static void show() {
		ui.hideTop();
		new SleepDialog(ui);
		ui.reveal();
	}

}
