package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.Color;

import com.xrbpowered.aethertown.state.Inventory;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.SelectButton;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class InventoryDialog extends DialogBase {

	private Inventory inventory;
	
	private int selected = -1;
	private Item selectedItem = null;
	
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
	}
	
	private void select(int index) {
		selected = index;
		selectedItem = inventory.get(index);
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		super.paintBackground(g);
		
		if(selectedItem!=null) {
			g.fillRect(280, 102, getWidth()-290, 384, Color.BLACK);
			g.setColor(Color.WHITE);
			g.setFont(Fonts.large);
			g.drawString(selectedItem.getName(), 300, 122, GraphAssist.LEFT, GraphAssist.CENTER);
		}
	}

	public static void show() {
		ui.hideTop();
		new InventoryDialog(ui);
		ui.reveal();
	}
}
