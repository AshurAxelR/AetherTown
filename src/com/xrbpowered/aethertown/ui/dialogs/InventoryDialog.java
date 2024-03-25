package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.Color;

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

	private final Tile tile;
	private final boolean alt;

	private Inventory inventory;

	private int selected = -1;
	private Item selectedItem = null;
	
	protected ClickButton buttonUse, buttonDelete;
	protected InfoBox infoBox;

	public InventoryDialog(UIContainer parent, Tile tile, boolean alt) {
		super(parent, 700, 550, true);
		this.tile = tile;
		this.alt = alt;
		
		inventory = player.backpack;
		
		SelectButton invButton = new SelectButton(this, "BACKPACK") {
			@Override
			public boolean isSelected() {
				return true;
			}
		};
		invButton.setPosition(10, 60);

		for(int i=0; i<inventory.size; i++) {
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
					return inventory.isEmptySlot(index);
				}
				@Override
				public String getItemName() {
					return  inventory.get(index).getName();
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
					180, "DISPOSE", new Runnable() {
						@Override
						public void run() {
							inventory.remove(selected);
							select(-1);
						}
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
