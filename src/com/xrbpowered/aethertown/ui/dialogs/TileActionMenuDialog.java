package com.xrbpowered.aethertown.ui.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class TileActionMenuDialog extends DialogBase {

	private class TileActionButton extends ClickButton {
		public final TileActionMenu.Item item;
		
		public TileActionButton(TileActionMenu.Item item) {
			super(menuContainer, null);
			this.item = item;
		}
		
		@Override
		public void onAction() {
			item.performAt(tile, alt, TileActionMenuDialog.this);
		}
		
		@Override
		public String getLabel() {
			return item.getLabel(tile, alt);
		}
		
		@Override
		protected Color getTextColor() {
			return item.isEnabled(tile, alt) ? super.getTextColor() : textColorDisabled;
		}
		
		@Override
		protected Color getBackgroundColor() {
			return item.isEnabled(tile, alt) && isHover() ? bgColorHover :
				item.isMenu() ? bgColor : SlotButton.bgColorEmpty;
		}
		
		@Override
		protected Font getFont() {
			return item.isMenu() ? Fonts.smallBold : Fonts.small;
		}
		
		@Override
		protected float getLabelAnchorX() {
			return 20;
		}
		
		@Override
		protected int getLabelAlign() {
			return GraphAssist.LEFT;
		}
		
		@Override
		public void paint(GraphAssist g) {
			super.paint(g);
			String info = item.getCostInfo(tile, alt);
			float x = getWidth()-10;
			float y = getHeight()/2;
			if(item.isMenu()) {
				paintArrow(g, (int)x, (int)y);
				x -= 20;
			}
			if(info!=null) {
				g.setFont(Fonts.small);
				g.drawString(info, x, y, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
		}

	}
	
	public final TileActionMenu menu;
	public final Tile tile;
	public final boolean alt;
	
	private String title;
	private String subtitle;
	
	protected UIContainer menuContainer;
	
	private LinkedList<TileActionMenu> history = new LinkedList<>();
	
	public TileActionMenuDialog(UIContainer parent, TileActionMenu menu, Tile tile, boolean alt, String title, String subtitle) {
		super(parent, 500, calcSize(menu, title!=null), true);
		this.menu = menu;
		this.tile = tile;
		this.alt = alt;
		this.title = title;
		this.subtitle = subtitle;
		
		menuContainer = new UIContainer(this) {
			@Override
			public void layout() {
				float y = 0f;
				for(UIElement c : children) {
					c.setSize(getWidth(), c.getHeight());
					c.setPosition(0, y);
					y += c.getHeight();
				}
			}
		};
		menuContainer.setPosition(10, (title!=null) ? 110 : 60);
		menuContainer.setSize(getWidth()-20, getHeight()-menuContainer.getY()-60);
		
		pushMenu(menu);
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		super.paintBackground(g);
		if(title!=null) {
			g.fillRect(10, 60, getWidth()-20, 40, Color.BLACK);
			g.setColor(Color.WHITE);
			g.setFont(Fonts.large);
			g.drawString(title, 30, 80, GraphAssist.LEFT, GraphAssist.CENTER);
			if(subtitle!=null) {
				g.setColor(ClickButton.textColorDisabled);
				g.setFont(Fonts.small);
				g.drawString(subtitle, getWidth()-20, 80, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
		}
	}
	
	private void switchMenu(TileActionMenu m) {
		menuContainer.removeAllChildren();
		for(TileActionMenu.Item item : m.items) {
			new TileActionButton(item);
		}
		buttonClose.label = (m==menu) ? "LEAVE" : "BACK";
	}
	
	public void pushMenu(TileActionMenu m) {
		switchMenu(m);
		history.push(m);
		repaint();
	}

	@Override
	public void close() {
		history.pop();
		if(history.isEmpty())
			super.close();
		else {
			switchMenu(history.getFirst());
			repaint();
		}
	}
	
	@Override
	public String getCloseLabel() {
		return "LEAVE";
	}

	private static int calcSize(TileActionMenu menu, boolean hasTitle) {
		return menu.getSize() * 32 + (hasTitle ? 170 : 120);
	}
	
	private static void paintArrow(GraphAssist g, int w, int h) {
		g.pushAntialiasing(true);
		g.pushPureStroke(true);
		g.graph.fillPolygon(
				new int[] {w-8, w-5, w, w-5, w-8, w-3},
				new int[] {h-8, h-8, h, h+8, h+8, h}, 6);
		g.popPureStroke();
		g.popAntialiasing();
	}

}
