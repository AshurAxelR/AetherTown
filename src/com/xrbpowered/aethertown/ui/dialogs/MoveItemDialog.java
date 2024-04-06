package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.Color;
import java.util.ArrayList;

import com.xrbpowered.aethertown.state.Inventory;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class MoveItemDialog extends UIPane implements KeyInputHandler {

	private final String prompt;

	private ClickButton buttonClose;
	
	public MoveItemDialog(UIContainer parent, String itemName, ArrayList<Inventory> inventories,
			Inventory current, int itemIndex, Runnable after) {
		
		super(parent, false);
		prompt = String.format("Move %s to:", itemName);
		
		setSize(400, 100+inventories.size()*32);

		for(int i=0; i<inventories.size(); i++) {
			final Inventory inv = inventories.get(i);
			
			SlotButton item =  new SlotButton(this) {
				@Override
				public void onAction() {
					if(inv!=current) {
						if(inv.isFull())
							return;
						Item item = current.remove(itemIndex);
						inv.put(item);
						if(after!=null)
							after.run();
					}
					close();
				}
				@Override
				public boolean isEnabled() {
					return !inv.isFull();
				}
				@Override
				public boolean isEmpty() {
					return false;
				}
				@Override
				public boolean isSelected() {
					return inv==current;
				}
				@Override
				public String getItemName() {
					if(inv.isFull())
						return inv.name + " (full)";
					else
						return inv.name;
				}
				@Override
				public void paint(GraphAssist g) {
					super.paint(g);
					if(inv==current)
						paintDot(g);
				}
			};
			
			item.setSize(getWidth()-20, 32);
			item.setPosition(10, 40+i*(item.getHeight()));
		}
		
		buttonClose = new ClickButton(this, "CANCEL") {
			@Override
			public void onAction() {
				close();
			}
		};
		buttonClose.setPosition(10, getHeight()-buttonClose.getHeight()-10);
	}

	@Override
	protected void paintBackground(GraphAssist g) {
		g.pixelBorder(this, 1, Color.BLACK, DialogBase.borderColor);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.small);
		g.drawString(prompt, 30, 26, GraphAssist.LEFT, GraphAssist.CENTER);
	}
	
	public void close() {
		DialogContainer.close(this);
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		if(DialogContainer.isCloseKey(this, code))
			close();
		return true;
	}

	public static void show(String itemName, ArrayList<Inventory> inventories,
			Inventory current, int itemIndex, Runnable after) {
		new MoveItemDialog(ui, itemName, inventories, current, itemIndex, after);
		ui.reveal();
	}

}
