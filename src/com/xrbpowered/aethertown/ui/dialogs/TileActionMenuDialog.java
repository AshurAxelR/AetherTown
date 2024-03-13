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

	public final TileActionMenu menu;
	public final Tile tile;
	
	protected UIContainer menuContainer;
	
	private LinkedList<TileActionMenu> history = new LinkedList<>();
	
	public TileActionMenuDialog(UIContainer parent, TileActionMenu menu, Tile tile) {
		super(parent, 500, calcSize(menu), true);
		this.menu = menu;
		this.tile = tile;
		
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
		menuContainer.setPosition(10, 60);
		menuContainer.setSize(getWidth()-20, getHeight()-120);
		
		pushMenu(menu);
	}
	
	private void switchMenu(TileActionMenu m) {
		menuContainer.removeAllChildren();
		for(TileActionMenu.Item item : m.items) {
			new ClickButton(menuContainer, item.getLabel()) {
				@Override
				public void onAction() {
					item.performAt(tile, TileActionMenuDialog.this);
				}
				@Override
				protected Color getBackgroundColor() {
					return !item.isMenu() && !isHover() ? SlotButton.bgColorEmpty : super.getBackgroundColor();
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
					if(item.isMenu()) {
						g.pushAntialiasing(true);
						g.pushPureStroke(true);
						int w = (int)getWidth();
						int h = (int)getHeight()/2;
						g.graph.fillPolygon(
								new int[] {w-18, w-15, w-10, w-15, w-18, w-13},
								new int[] {h-8, h-8, h, h+8, h+8, h}, 6);
						g.popPureStroke();
						g.popAntialiasing();
					}
				}
			};
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

	private static int calcSize(TileActionMenu menu) {
		return menu.getSize() * 32 + 120;
	}

}
