package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.aether;
import static com.xrbpowered.aethertown.AetherTown.player;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.MenuContainer;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.UIContainer;

public class TileActionMenuDialog extends DialogBase {

	private class TileActionButton extends ClickButton {
		public final TileActionMenu.Command item;
		
		public TileActionButton(TileActionMenu.Command item) {
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
				paintArrow(g, (int)x, (int)y, 1);
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
	protected ClickButton buttonInventory;
	
	private double repaintTime = 0.0;
	private LinkedList<TileActionMenu> history = new LinkedList<>();
	
	public TileActionMenuDialog(UIContainer parent, TileActionMenu menu, Tile tile, boolean alt, String title, String subtitle) {
		super(parent, 500, calcSize(menu, title!=null), true);
		this.menu = menu;
		this.tile = tile;
		this.alt = alt;
		this.title = title;
		this.subtitle = subtitle;
		
		buttonInventory = new ClickButton(this, "INVENTORY") {
			@Override
			public void onAction() {
				InventoryDialog.show(tile, alt);
			}
		};
		buttonInventory.setSize(150, buttonInventory.getHeight());
		buttonInventory.setPosition(getWidth()-buttonInventory.getWidth()-10, getHeight()-buttonInventory.getHeight()-10);
		
		menuContainer = new MenuContainer(this);
		menuContainer.setPosition(10, (title!=null) ? 110 : 60);
		menuContainer.setSize(getWidth()-20, getHeight()-menuContainer.getY()-60);
		
		pushMenu(menu);
	}
	
	@Override
	public void updateTime(float dt) {
		super.updateTime(dt);
		if(WorldTime.time>=repaintTime)
			repaint();
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
		repaintTime = WorldTime.time + WorldTime.minute/2;
	}
	
	private void switchMenu(TileActionMenu m) {
		menuContainer.removeAllChildren();
		for(TileActionMenu.Command item : m.items) {
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
		if(history.isEmpty()) {
			super.close();
			player.endAction();
			aether.flipCamera();
		}
		else {
			switchMenu(history.getFirst());
			repaint();
		}
	}
	
	@Override
	public String getCloseLabel() {
		return "LEAVE";
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		if(code==KeyEvent.VK_Q) {
			InventoryDialog.show(tile, alt);
			return true;
		}
		else
			return super.onKeyPressed(c, code, input);
	}

	private static int calcSize(TileActionMenu menu, boolean hasTitle) {
		return menu.getSize() * 32 + (hasTitle ? 170 : 120);
	}
	
	public static void paintArrow(GraphAssist g, int x, int y, int d) {
		g.pushAntialiasing(true);
		g.pushPureStroke(true);
		g.graph.fillPolygon(
				new int[] {x-d*8, x-d*5, x, x-d*5, x-d*8, x-d*3},
				new int[] {y-8, y-8, y, y+8, y+8, y}, 6);
		g.popPureStroke();
		g.popAntialiasing();
	}

}
