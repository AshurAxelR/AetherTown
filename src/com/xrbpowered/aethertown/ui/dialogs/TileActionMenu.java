package com.xrbpowered.aethertown.ui.dialogs;

import java.util.ArrayList;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.world.Tile;

public class TileActionMenu {

	public abstract class Item {
		public abstract void performAt(Tile tile, boolean alt, TileActionMenuDialog dialog);
		public abstract String getLabel(Tile tile, boolean alt);
		
		public String getCostInfo(Tile tile, boolean alt) {
			return null;
		}
		
		public boolean isMenu() {
			return false;
		}
		
		public boolean isEnabled(Tile tile, boolean alt) {
			return true;
		}
	}
	
	public class ActionItem extends Item {
		public final TileAction action;
		
		public ActionItem(TileAction action) {
			this.action = action;
		}
		
		@Override
		public String getLabel(Tile tile, boolean alt) {
			return action.getLabel(tile, alt);
		}
		
		@Override
		public String getCostInfo(Tile tile, boolean alt) {
			return action.getCostInfo(tile, alt);
		}
		
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			return action.isEnabled(tile, alt);
		}
		
		@Override
		public void performAt(Tile tile, boolean alt, TileActionMenuDialog dialog) {
			action.performAt(tile, alt);
			dialog.repaint();
		}
	}
	
	public class MenuItem extends Item {
		public final String label;
		public final TileActionMenu menu;
		public final int delay;
		
		public MenuItem(String label, TileActionMenu menu, int delay) {
			this.label = label;
			this.menu = menu;
			this.delay = delay;
		}
		
		@Override
		public boolean isMenu() {
			return true;
		}
		
		@Override
		public String getLabel(Tile tile, boolean alt) {
			return label;
		}
		
		@Override
		public String getCostInfo(Tile tile, boolean alt) {
			return delay>0 ? TileAction.formatDelay(delay) : null;
		}
		
		@Override
		public boolean isEnabled(Tile tile, boolean alt) {
			return menu.isEnabled(tile);
		}
		
		@Override
		public void performAt(Tile tile, boolean alt, TileActionMenuDialog dialog) {
			if(isEnabled(tile, alt))
				dialog.pushMenu(menu);
			else
				menu.disabledAction(tile, dialog);
		}
	}
	
	public final ArrayList<Item> items = new ArrayList<>();
	
	public void addAction(TileAction action) {
		items.add(new ActionItem(action));
	}
	
	public void addMenu(String label, TileActionMenu menu) {
		items.add(new MenuItem(label, menu, 0));
	}

	public void addMenu(String label, TileActionMenu menu, int delayMin) {
		items.add(new MenuItem(label, menu, delayMin));
	}

	public boolean isEnabled(Tile tile) {
		return true;
	}
	
	public int getSize() {
		int max = items.size();
		for(Item item : items) {
			if(item instanceof MenuItem)
				max = Math.max(max, ((MenuItem) item).menu.getSize());
		}
		return max;
	}

	public void disabledAction(Tile tile, TileActionMenuDialog dialog) {
	}
}
