package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.ui;
import static com.xrbpowered.aethertown.ui.controls.ClickButton.bgColorHover;
import static com.xrbpowered.aethertown.ui.controls.ClickButton.textColorDisabled;

import java.awt.event.KeyEvent;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIButtonBase;

public class InventoryDialog extends DialogBase {

	public InventoryDialog(UIContainer parent) {
		super(parent, 700, 550, true);
		
		new ClickButton(this, "BACKPACK").setPosition(10, 60);

		for(int i=0; i<12; i++) {
			// final int index = i;
			UIButtonBase item =  new UIButtonBase(this) {
				@Override
				public void onAction() {
				}
				@Override
				public void paint(GraphAssist g) {
					g.fill(this, isHover() ? bgColorHover : SlotButton.bgColorEmpty);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(textColorDisabled);
					g.setFont(Fonts.small);
					g.drawString("[EMPTY]", 20, getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
				}
			};
			
			item.setSize(260, 32);
			item.setPosition(10, 102+i*(item.getHeight()));
		}
	}

	@Override
	public int getCloseKeyCode() {
		return KeyEvent.VK_Q;
	}
	
	public static void show() {
		new InventoryDialog(ui);
		ui.reveal();
	}
}
