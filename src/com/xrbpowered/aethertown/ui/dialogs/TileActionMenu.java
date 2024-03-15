package com.xrbpowered.aethertown.ui.dialogs;

import java.util.ArrayList;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.world.Tile;

public class TileActionMenu {

	public abstract class Item {
		public abstract void performAt(Tile tile, TileActionMenuDialog dialog);
		public abstract String getLabel(Tile tile);
		
		public String getCostInfo(Tile tile) {
			return null;
		}
		
		public boolean isMenu() {
			return false;
		}
		
		public boolean isEnabled(Tile tile) {
			return true;
		}
	}
	
	public class ActionItem extends Item {
		public final TileAction action;
		
		public ActionItem(TileAction action) {
			this.action = action;
		}
		
		@Override
		public String getLabel(Tile tile) {
			return action.getLabel(tile);
		}
		
		@Override
		public String getCostInfo(Tile tile) {
			return action.getCostInfo(tile);
		}
		
		@Override
		public boolean isEnabled(Tile tile) {
			return action.isEnabled(tile);
		}
		
		@Override
		public void performAt(Tile tile, TileActionMenuDialog dialog) {
			action.performAt(tile);
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
		public String getLabel(Tile tile) {
			return label;
		}
		
		@Override
		public String getCostInfo(Tile tile) {
			return delay>0 ? TileAction.formatDelay(delay) : null;
		}
		
		public boolean isEnabled(Tile tile) {
			return menu.isEnabled(tile);
		}
		
		@Override
		public void performAt(Tile tile, TileActionMenuDialog dialog) {
			if(isEnabled(tile))
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
