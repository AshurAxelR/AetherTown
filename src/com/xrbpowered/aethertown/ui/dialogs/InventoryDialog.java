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
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class InventoryDialog extends DialogBase {

	private Inventory inventory;
	
	private int selected = -1;
	private Item selectedItem = null;
	
	protected ClickButton buttonUse;
	protected InfoBox infoBox;

	public InventoryDialog(UIContainer parent) {
		super(parent, 700, 550, true);
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
				selectedItem.useItem();
			}
		};
		buttonUse.setSize(150, buttonUse.getHeight());
		buttonUse.setPosition(getWidth()-buttonUse.getWidth()-20, 486-buttonUse.getHeight()-10);
		buttonUse.setVisible(false);
	}
	
	private void select(int index) {
		selected = index;
		selectedItem = inventory.get(index);
		
		String info = selectedItem.getInfoHtml();
		if(info!=null) {
			infoBox.setVisible(true);
			infoBox.setHtml(info);
		}
		else
			infoBox.setVisible(false);
		
		String use = selectedItem.getUseActionName();
		if(use!=null) {
			buttonUse.setVisible(true);
			buttonUse.label = use;
		}
		else
			buttonUse.setVisible(false);
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

	public static void show() {
		ui.hideTop();
		new InventoryDialog(ui);
		ui.reveal();
	}
}
