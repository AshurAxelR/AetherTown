package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.Color;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.InfoBox;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;

public class ConfirmDialog extends UIPane implements KeyInputHandler {

	private String title;
	
	protected ClickButton buttonCancel, buttonConfirm;

	private ConfirmDialog(UIContainer parent, String title, String html, int h, String confirm, Runnable action) {
		super(parent, false);
		setSize(400, h);
		this.title = title;
		boolean ok = (confirm==null || action==null);
		
		InfoBox infoBox = new InfoBox(this, html);
		infoBox.setPosition(20, 50);
		infoBox.setSize(getWidth()-40, getHeight()-100);
		
		buttonCancel = new ClickButton(this, ok ? "OK" : "CANCEL") {
			@Override
			public void onAction() {
				close();
			}
		};
		if(ok)
			buttonCancel.setPosition(getWidth()-buttonCancel.getWidth()-20, getHeight()-buttonCancel.getHeight()-10);
		else
			buttonCancel.setPosition(20, getHeight()-buttonCancel.getHeight()-10);
		
		if(!ok) {
			buttonConfirm = new ClickButton(this, confirm) {
				@Override
				public void onAction() {
					action.run();
					close();
				}
			};
			buttonConfirm.setPosition(getWidth()-buttonConfirm.getWidth()-20, getHeight()-buttonConfirm.getHeight()-10);
		}
	}

	@Override
	public boolean isHit(float px, float py) {
		return true;
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		g.pixelBorder(this, 1, Color.BLACK, DialogBase.borderColor);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.large);
		g.drawString(title, 20, 30, GraphAssist.LEFT, GraphAssist.CENTER);
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

	@Override
	public boolean onMouseDown(float x, float y, MouseInfo mouse) {
		return true;
	}
	
	public static void show(String title, String html, int h, String confirm, Runnable action) {
		new ConfirmDialog(ui, title, html, h, confirm, action);
		ui.reveal();
	}

	public static void show(String title, String html, int h) {
		new ConfirmDialog(ui, title, html, h, null, null);
		ui.reveal();
	}

}
