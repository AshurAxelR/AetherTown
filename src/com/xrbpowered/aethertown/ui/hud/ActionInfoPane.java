package com.xrbpowered.aethertown.ui.hud;

import static com.xrbpowered.aethertown.AetherTown.ui;
import static com.xrbpowered.aethertown.ui.hud.Hud.bgColor;

import java.awt.Color;
import java.awt.FontMetrics;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;

public class ActionInfoPane extends UIPane {

	private final Hud hud;
	
	public ActionInfoPane(Hud hud) {
		super(hud, false);
		this.hud = hud;
	}

	@Override
	public boolean isVisible() {
		return super.isVisible() && ui.isEmpty();
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, transparent);
		
		g.setFont(Fonts.small);
		FontMetrics fm = g.getFontMetrics();
		
		String s1 = getActionLabel(hud.getTileAction(), hud.getLookAtTile(), false);
		float w1 = 0f;
		if(s1!=null)
			w1 = fm.stringWidth(s1) + 80;
		String s2 = getActionLabel(hud.getTileAltAction(), hud.getLookAtTile(), true);
		float w2 = 0f;
		if(s2!=null)
			w2 = fm.stringWidth(s2) + 80;
		float w = w1+w2;
		if(s1!=null && s2!=null)
			w +=  40;
		
		if(w>0f) {
			float x = getWidth()/2 - w/2;
			if(s1!=null) {
				paintActionKey(g, x, "E", s1, w1, getHeight());
				x += w1+40;
			}
			if(s2!=null)
				paintActionKey(g, x, "R", s2, w2, getHeight());
		}
	}

	private static String getActionLabel(TileAction action, Tile tile, boolean alt) {
		if(action==null || !action.isEnabled(tile, alt))
			return null;
		else
			return action.getLabel(tile, alt);
	}

	private static void paintActionKey(GraphAssist g, float x, String key, String action, float w, float h) {
		g.fillRect(x, 0, 40, h, Color.WHITE);
		g.fillRect(x+40, 0, w-40, h, bgColor);
		g.setColor(Color.BLACK);
		g.setFont(Fonts.large);
		g.drawString(key, x+20, h/2, GraphAssist.CENTER, GraphAssist.CENTER);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.small);
		g.drawString(action, x+60, h/2, GraphAssist.LEFT, GraphAssist.CENTER);
	}
	

}
