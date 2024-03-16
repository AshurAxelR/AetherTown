package com.xrbpowered.aethertown.ui.hud;

import static com.xrbpowered.aethertown.AetherTown.*;

import java.awt.Color;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class Hud extends UINode {

	public static final Color bgColor = new Color(0x22000000, true);

	private Tile lookAtTile = null;
	private TileAction tileAction = null;
	private TileAction tileAltAction = null;

	private int shownHour = -1;
	
	private UIPane uiTime, uiLookInfo, uiActionInfo, uiDebugInfo;
	private ToastPane uiToast;

	public Hud(UIContainer parent) {
		super(parent);
		
		if(settings.showFps) {
			uiDebugInfo = new UIPane(this, false) {
				@Override
				protected void paintBackground(GraphAssist g) {
					clear(g, bgColor);
					g.setColor(Color.WHITE);
					g.setFont(Fonts.small);
					aether.paintDebugInfo(g);
				}
				@Override
				public void updateTime(float dt) {
					repaint();
				}
			};
			uiDebugInfo.setSize(180, 50);
			uiDebugInfo.setPosition(20, 20);
		}
		
		uiTime = new UIPane(this, false) {
			@Override
			protected void paintBackground(GraphAssist g) {
				clear(g, bgColor);
				g.setColor(Color.WHITE);
				g.setFont(Fonts.large);
				g.drawString(WorldTime.getFormattedTime(), 50, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
				g.setFont(Fonts.small);
				g.drawString(String.format("DAY %d, %s", WorldTime.getDay()+1, WorldTime.getFormattedDate()),
						100, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
			}
		};
		uiTime.setSize(220, 32);
		
		uiLookInfo = new UIPane(this, false) {
			@Override
			public boolean isVisible() {
				return super.isVisible() && ui.isEmpty();
			}
			@Override
			protected void paintBackground(GraphAssist g) {
				clear(g, bgColor);
				if(lookAtTile!=null) {
					g.setColor(Color.WHITE);
					g.setFont(Fonts.small);
					g.drawString(lookAtTile.t.getTileInfo(lookAtTile),
							getWidth()/2, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
				}
			}
		};
		uiLookInfo.setSize(600, 32);
		uiLookInfo.setVisible(false);

		uiActionInfo = new ActionInfoPane(this);
		uiActionInfo.setSize(400, 32);
		uiActionInfo.setVisible(false);

		uiToast = new ToastPane(this);
	}
	
	@Override
	public boolean isInside(float px, float py) {
		return false;
	}
	
	@Override
	public void layout() {
		uiTime.setPosition(20, getHeight()-uiTime.getHeight()-20);
		uiLookInfo.setPosition(getWidth()/2-uiLookInfo.getWidth()/2, uiTime.getY());
		uiActionInfo.setPosition(getWidth()/2-uiActionInfo.getWidth()/2, uiTime.getY()-uiActionInfo.getHeight()-60);
		uiToast.setPosition(20, getHeight()/2-uiToast.getHeight()/2);
		super.layout();
	}

	@Override
	public void updateTime(float dt) {
		uiTime.repaint();
		
		int h = WorldTime.getHourOfDay();
		if(shownHour!=h) {
			shownHour = h;
			uiActionInfo.setVisible(tileAction!=null && tileAction.isEnabled(lookAtTile, false)
					|| tileAltAction!=null && tileAltAction.isEnabled(lookAtTile, true));
			repaint();
		}
		
		super.updateTime(dt);
	}
	
	public Tile getLookAtTile() {
		return lookAtTile;
	}
	
	public TileAction getTileAction() {
		return tileAction;
	}
	
	public TileAction getTileAltAction() {
		return tileAltAction;
	}
	
	public void setLookAtTile(Tile tile) {
		if(tile!=lookAtTile) {
			lookAtTile = tile;
			String info = (tile==null) ? "" : tile.t.getTileInfo(tile);
			boolean vis = !info.isEmpty();
			if(uiLookInfo.isVisible() != vis) {
				uiLookInfo.setVisible(vis);
				repaint();
			}
			
			TileAction action = (tile==null) ? null : tile.t.getTileAction(tile);
			TileAction altAction = (tile==null) ? null : tile.t.getTileAltAction(tile);
			if(action!=tileAction || altAction!=tileAltAction) {
				tileAction = action;
				tileAltAction = altAction;
				uiActionInfo.repaint();
				shownHour = -1;
			}
		}
	}
	
	public static void performAction(boolean alt) {
		if(hud==null)
			return;
		TileAction action = alt ? hud.tileAltAction : hud.tileAction;
		if(action!=null)
			action.performAt(hud.lookAtTile, alt);
	}
	
	public static void showToast(String msg) {
		if(hud!=null)
			hud.uiToast.queue.push(msg);
		System.out.printf("> %s\n", msg);
	}

	public static void showToast(String fmt, Object... args) {
		showToast(String.format(fmt, args));
	}

}
