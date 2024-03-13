package com.xrbpowered.aethertown.ui;

import static com.xrbpowered.aethertown.AetherTown.*;
import static com.xrbpowered.aethertown.state.Bookmarks.*;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.bgColor;

import java.awt.event.KeyEvent;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class BookmarkPane extends UIPane implements KeyInputHandler {
	
	private ClickButton buttonAdd, buttonDelete, buttonTravel;
	private int selected = -1;
	
	public BookmarkPane(UIContainer parent) {
		super(parent, false);
		setSize(500, 420);
		
		for(int i=0; i<numBookmarks; i++) {
			final int index = i;
			
			SlotButton item =  new SlotButton(this) {
				@Override
				public void onAction() {
					selected = index;
					updateSelection();
				}
				@Override
				public boolean isEmpty() {
					return bookmarks[index]==null;
				}
				@Override
				public boolean isSelected() {
					return selected==index;
				}
				@Override
				public String getItemName() {
					return bookmarks[index].getFullName();
				}
				@Override
				protected float getLabelAnchorX() {
					return 50;
				}
				@Override
				public void paint(GraphAssist g) {
					super.paint(g);
					g.setFont(Fonts.smallBold);
					g.drawString(String.format("%d.", index+1), 40, getHeight()/2f, GraphAssist.RIGHT, GraphAssist.CENTER);
					if(levelInfo.isRef(bookmarks[index]))
						paintDot(g);
				}
			};
			
			item.setSize(getWidth()-20, 32);
			item.setPosition(10, 10+i*(item.getHeight()));
		}
		
		buttonDelete = new ClickButton(this, "DELETE") {
			@Override
			public void onAction() {
				bookmarks[selected] = null;
				updateSelection();
			}
		};
		buttonDelete.setEnabled(false);
		buttonDelete.setPosition(10, getHeight()-buttonDelete.getHeight()-10);

		buttonAdd = new ClickButton(this, "ADD") {
			@Override
			public void onAction() {
				bookmarks[selected] = new NamedLevelRef(level.info);
				updateSelection();
			}
		};
		buttonAdd.setEnabled(false);
		buttonAdd.setPosition(10, getHeight()-buttonAdd.getHeight()-buttonDelete.getHeight()-14);
		
		buttonTravel = new ClickButton(this, "TRAVEL") {
			@Override
			public void onAction() {
				aether.teleportTo(bookmarks[selected].find(regionCache));
				updateSelection();
			}
		};
		buttonTravel.setSize(200, buttonTravel.getHeight());
		buttonTravel.setEnabled(false);
		buttonTravel.setPosition(getWidth()-buttonTravel.getWidth()-10, getHeight()-buttonTravel.getHeight()-10);
	}
	
	public void selectNone() {
		selected = -1;
		updateSelection();
	}

	public void updateSelection() {
		if(selected<0) {
			buttonAdd.setEnabled(false);
			buttonDelete.setEnabled(false);
			buttonTravel.setEnabled(false);
		}
		else {
			NamedLevelRef ref = bookmarks[selected];
			LevelInfo active = AetherTown.level.info;
			if(ref==null) {
				buttonAdd.setEnabled(!active.isPortal() && !isBookmarked(active));
				buttonDelete.setEnabled(false);
				buttonTravel.setEnabled(false);
			}
			else {
				buttonAdd.setEnabled(false);
				buttonDelete.setEnabled(true);
				buttonTravel.setEnabled(!active.isRef(ref));
			}
		}
		repaint();
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		switch(code) {
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_B:
				remove();
				ui.repaint();
				break;
			default:
				break;
		}
		return true;
	}
}
