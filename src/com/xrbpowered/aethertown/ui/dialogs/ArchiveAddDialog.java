package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.AetherTown.ui;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.bgColor;

import java.awt.Color;
import java.util.ArrayList;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.TokenArchive;
import com.xrbpowered.aethertown.state.items.TravelTokenItem;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class ArchiveAddDialog extends UIPane implements KeyInputHandler {

	public static final int cost = 500;
	
	private final ArrayList<TravelTokenItem> tokens;
	private int selected = -1;

	private ClickButton buttonClose, buttonAdd;
	
	private ArchiveAddDialog(UIContainer parent) {
		super(parent, false);
		
		tokens = TravelTokenItem.listTravelTokens();
		setSize(500, 100+tokens.size()*32);
		
		for(int i=0; i<tokens.size(); i++) {
			final int index = i;
			final TravelTokenItem token = tokens.get(index);
			
			SlotButton item =  new SlotButton(this) {
				@Override
				public void onAction() {
					selected = index;
					buttonAdd.setEnabled(!TokenArchive.contains(token.destination));
					repaint();
				}
				@Override
				public boolean isEnabled() {
					return !TokenArchive.contains(token.destination);
				}
				@Override
				public boolean isEmpty() {
					return false;
				}
				@Override
				public boolean isSelected() {
					return selected==index;
				}
				@Override
				public String getItemName() {
					return token.destination.getFullName();
				}
				@Override
				public void paint(GraphAssist g) {
					super.paint(g);
					if(!TokenArchive.contains(token.destination))
						g.drawString("NEW", getWidth()-10, getHeight()/2f, GraphAssist.RIGHT, GraphAssist.CENTER);
				}
			};
			
			item.setSize(getWidth()-20, 32);
			item.setPosition(10, 40+i*(item.getHeight()));
		}
		
		buttonClose = new ClickButton(this, "CLOSE") {
			@Override
			public void onAction() {
				close();
			}
		};
		buttonClose.setPosition(10, getHeight()-buttonClose.getHeight()-10);
		
		buttonAdd = new ClickButton(this, "RECORD") {
			@Override
			public void onAction() {
				player.cash -= cost;
				TokenArchive.add(tokens.get(selected));
				buttonAdd.setEnabled(false);
				selected = -1;
				repaint();
			}
		};
		buttonAdd.setSize(160, buttonAdd.getHeight());
		buttonAdd.setEnabled(false);
		buttonAdd.setPosition(getWidth()-buttonAdd.getWidth()-10, getHeight()-buttonAdd.getHeight()-10);
	}

	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.small);
		g.drawString("Record Travel Tokens into Archive:", 30, 26, GraphAssist.LEFT, GraphAssist.CENTER);
		g.drawString(TileAction.formatCost(cost), buttonAdd.getX()-10,
				buttonAdd.getY()+buttonAdd.getHeight()/2, GraphAssist.RIGHT, GraphAssist.CENTER);
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

	public static void show() {
		ui.hideTop();
		new ArchiveAddDialog(ui);
		ui.reveal();
	}

}
