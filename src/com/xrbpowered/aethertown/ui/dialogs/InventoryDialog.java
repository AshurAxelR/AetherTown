package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.Color;
import java.util.ArrayList;

import com.xrbpowered.aethertown.state.Inventory;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.InfoBox;
import com.xrbpowered.aethertown.ui.controls.SelectButton;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class InventoryDialog extends DialogBase {

	private class SelectInvButton extends SelectButton {
		public final Inventory inv;
		
		public SelectInvButton(int index, Inventory inv) {
			super(InventoryDialog.this, inv.name);
			this.inv = inv;
			setSize(140, getHeight());
			setPosition(10+index*getWidth(), 60);
		}
		
		@Override
		public void onAction() {
			if(!isSelected()) {
				for(int i=0; i<itemButtons.length; i++)
					itemButtons[i].setVisible(i<inv.size);
				inventory = inv;
				select(-1);
			}
			repaint();
		}
		
		@Override
		public boolean isSelected() {
			return inventory==inv;
		}
	}
	
	private final Tile tile;
	private final boolean alt;

	private Inventory inventory;
	private ArrayList<Inventory> inventoryList = null;

	private int selected = -1;
	private Item selectedItem = null;
	
	private ClickButton buttonUse, buttonDelete, buttonMove;
	private InfoBox infoBox;
	private SlotButton[] itemButtons;

	public InventoryDialog(UIContainer parent, Tile tile, boolean alt) {
		super(parent, 720, 550, true);
		this.tile = tile;
		this.alt = alt;
		
		new SelectInvButton(0, player.backpack);
		Inventory[] tileInv = Inventory.getTileInventory(tile, alt);
		if(tileInv!=null) {
			inventoryList = new ArrayList<>();
			inventoryList.add(player.backpack);
			for(int i=0; i<tileInv.length; i++) {
				new SelectInvButton(i+1, tileInv[i]);
				inventoryList.add(tileInv[i]);
			}
		}
		inventory = player.backpack;

		// maximum displayed slots is <= player.backpack.size
		itemButtons = new SlotButton[player.backpack.size];
		for(int i=0; i<itemButtons.length; i++) {
			final int index = i;
			SlotButton item =  new SlotButton(this) {
				@Override
				public void onAction() {
					if(!isEmpty())
						select(index);
				}
				@Override
				public boolean isSelected() {
					return selected==index;
				}
				@Override
				public boolean isEmpty() {
					return index<inventory.size && inventory.isEmptySlot(index);
				}
				@Override
				public String getItemName() {
					return inventory.get(index).getName();
				}
				@Override
				public void paint(GraphAssist g) {
					super.paint(g);
					if(!isEmpty() && inventory.get(index).markDot(tile, alt))
						paintDot(g);
				}
			};
			
			item.setSize(260, 32);
			item.setPosition(10, 102+i*(item.getHeight()));
			itemButtons[i] = item;
		}
		
		infoBox = new InfoBox(this);
		infoBox.setPosition(300, 160);
		infoBox.setSize(getWidth()-330, 260);
		infoBox.setVisible(false);
		
		buttonUse = new ClickButton(this, "USE") {
			@Override
			public void onAction() {
				if(selectedItem.useItem(tile, alt) && selectedItem.isConsumable()) {
					inventory.remove(selected);
					select(-1);
				}
				repaint();
			}
			@Override
			protected Color getBackgroundColor() {
				return isHover() && isUseEnabled() ? bgColorHover : bgColor;
			}
			@Override
			protected Color getTextColor() {
				return isUseEnabled() ? textColor : textColorDisabled;
			}
		};
		buttonUse.setSize(150, buttonUse.getHeight());
		buttonUse.setPosition(getWidth()-buttonUse.getWidth()-20, 486-buttonUse.getHeight()-10);
		buttonUse.setVisible(false);
		
		buttonDelete = new ClickButton(this, "X") {
			@Override
			public void onAction() {
				ConfirmDialog.show("Dispose", String.format(
					"<p>Do you want to dispose this item?<br>"+
					"<span class=\"w\">%s</span></p>",
					selectedItem.getFullName()),
					180, "DISPOSE", () -> {
						inventory.remove(selected);
						select(-1);
					});
			}
			@Override
			protected Color getBackgroundColor() {
				return isHover() ? bgDeleteColorHover : bgDeleteColor;
			}
		};
		buttonDelete.setSize(40, buttonDelete.getHeight());
		buttonDelete.setPosition(290, 486-buttonDelete.getHeight()-10);
		buttonDelete.setVisible(false);
		
		if(inventoryList!=null) {
			buttonMove = new ClickButton(this, "MOVE") {
				@Override
				public void onAction() {
					MoveItemDialog.show(selectedItem.getName(), inventoryList,
							inventory, selected, () -> { select(-1); });
				}
			};
			buttonMove.setSize(80, buttonMove.getHeight());
			buttonMove.setPosition(buttonDelete.getX()+buttonDelete.getWidth(), buttonDelete.getY());
			buttonMove.setVisible(false);
		}
	}
	
	private boolean isUseEnabled() {
		return selectedItem!=null && selectedItem.isUseEnabled(tile, alt);
	}
	
	private void select(int index) {
		selected = index;
		selectedItem = index<0 ? null : inventory.get(index);
		
		String info = selectedItem==null ? null : selectedItem.getInfoHtml(tile, alt);
		if(info!=null) {
			infoBox.setVisible(true);
			infoBox.setHtml(info);
		}
		else
			infoBox.setVisible(false);
		
		String use = selectedItem==null ? null : selectedItem.getUseActionName();
		if(use!=null) {
			buttonUse.setVisible(true);
			buttonUse.label = use;
		}
		else
			buttonUse.setVisible(false);
		
		buttonDelete.setVisible(selectedItem!=null);
		if(inventoryList!=null)
			buttonMove.setVisible(selectedItem!=null);
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		super.paintBackground(g);
		
		if(selectedItem!=null) {
			g.fillRect(280, 102, getWidth()-290, 384, Color.BLACK);
			g.setColor(Color.WHITE);
			g.setFont(Fonts.large);
			g.drawString(selectedItem.getName(), 300, 130, GraphAssist.LEFT, GraphAssist.CENTER);
		}
	}
	
	public static void show(Tile tile, boolean alt) {
		ui.hideTop();
		new InventoryDialog(ui, tile, alt);
		ui.reveal();
	}
}
