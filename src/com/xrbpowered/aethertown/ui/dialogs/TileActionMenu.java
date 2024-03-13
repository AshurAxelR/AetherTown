package com.xrbpowered.aethertown.ui.dialogs;

import java.util.ArrayList;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.world.Tile;

public class TileActionMenu {

	public abstract class Item {
		public abstract void performAt(Tile tile, TileActionMenuDialog dialog);
		public abstract String getLabel();
		
		public String getCostInfo() {
			return null;
		}
		
		public boolean isMenu() {
			return false;
		}
	}
	
	private class ActionItem extends Item {
		public final TileAction action;
		
		public ActionItem(TileAction action) {
			this.action = action;
		}
		
		@Override
		public String getLabel() {
			return action.getName();
		}
		
		@Override
		public void performAt(Tile tile, TileActionMenuDialog dialog) {
			action.performAt(tile);
			dialog.repaint();
		}
	}
	
	private class SubmenuItem extends Item {
		public final String label;
		public final TileActionMenu menu;
		
		public SubmenuItem(String label, TileActionMenu menu) {
			this.label = label;
			this.menu = menu;
		}
		
		@Override
		public boolean isMenu() {
			return true;
		}
		
		@Override
		public String getLabel() {
			return label;
		}
		
		@Override
		public void performAt(Tile tile, TileActionMenuDialog dialog) {
			dialog.pushMenu(menu);
		}
	}
	
	public final ArrayList<Item> items = new ArrayList<>();
	
	public void addAction(TileAction action) {
		items.add(new ActionItem(action));
	}
	
	public void addMenu(String label, TileActionMenu menu) {
		items.add(new SubmenuItem(label, menu));
	}
	
	public int getSize() {
		int max = items.size();
		for(Item item : items) {
			if(item instanceof SubmenuItem)
				max = Math.max(max, ((SubmenuItem) item).menu.getSize());
		}
		return max;
	}

}
