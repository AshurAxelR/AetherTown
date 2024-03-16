package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.bgColor;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.checkCloseKey;

import java.awt.Color;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public abstract class DialogBase extends UIPane implements KeyInputHandler {

	public static final Color borderColor = new Color(0xdddddd);

	protected final boolean showStats;
	protected ClickButton buttonClose, buttonStats;
	
	public DialogBase(UIContainer parent, int w, int h, boolean showStats) {
		super(parent, false);
		this.showStats = showStats;
		setSize(w, h);
		
		buttonClose = new ClickButton(this, getCloseLabel()) {
			@Override
			public void onAction() {
				close();
			}
		};
		buttonClose.setPosition(10, getHeight()-buttonClose.getHeight()-10);
		
		buttonStats = new ClickButton(this, "STATS");
		buttonStats.setEnabled(false);
		buttonStats.setSize(80, buttonStats.getHeight());
		buttonStats.setPosition(getWidth()-buttonStats.getWidth()-10, 10);
	}

	public String getCloseLabel() {
		return "CLOSE";
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
		g.setColor(borderColor);
		g.line(10, getHeight()-50, getWidth()-10, getHeight()-50);

		if(showStats) {
			g.line(10, 50, getWidth()-10, 50);
			g.setColor(Color.WHITE);
			g.setFont(Fonts.large);
			g.drawString(TileAction.formatCurrency(player.cash), 30, 26, GraphAssist.LEFT, GraphAssist.CENTER);
			g.setFont(Fonts.small);
			g.drawString(String.format("%d INS", player.getInspiration()), buttonStats.getX()-10, 26, GraphAssist.RIGHT, GraphAssist.CENTER);
		}
	}
	
	public void close() {
		DialogContainer.close(this);
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		checkCloseKey(this, code);
		return true;
	}

}
