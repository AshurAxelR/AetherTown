package com.xrbpowered.aethertown.ui;

import static com.xrbpowered.aethertown.data.Bookmarks.bookmarks;
import static com.xrbpowered.aethertown.data.Bookmarks.numBookmarks;
import static com.xrbpowered.aethertown.ui.ClickButton.*;

import java.awt.Color;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIButtonBase;

public class BookmarkPane extends UIPane {

	public static final Color bgColor = new Color(0x44000000, true);
	public static final Color bgColorSelected = Color.WHITE;
	public static final Color textColorSelected = Color.BLACK;
	
	private ClickButton buttonAdd, buttonDelete, buttonTravel;
	private int selected = -1;
	
	public BookmarkPane(UIContainer parent) {
		super(parent, false);
		setSize(500, 420);
		
		for(int i=0; i<numBookmarks; i++) {
			final int index = i;
			
			UIButtonBase item =  new UIButtonBase(this) {
				@Override
				public void onAction() {
					selected = index;
					updateSelection();
				}
				@Override
				public void paint(GraphAssist g) {
					g.fill(this, selected==index ? bgColorSelected : isHover() ? bgColorHover : ClickButton.bgColor);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.setColor(isEnabled() ? (selected==index ? textColorSelected : textColor) : textColorDisabled);

					g.setFont(Fonts.smallBold);
					g.drawString(String.format("%d.", index+1), 40, getHeight()/2f, GraphAssist.RIGHT, GraphAssist.CENTER);
					g.setFont(Fonts.small);
					
					LevelInfo level = bookmarks[index];
					String s;
					if(level==null) {
						s = "[EMPTY]";
						g.setColor(textColorDisabled);
					}
					else
						s = String.format("%s [%d, %d]", level.name, level.x0, level.z0);
					g.drawString(s, 50, getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
				}
			};
			
			item.setSize(getWidth()-20, 32);
			item.setPosition(10, 10+i*(item.getHeight()));
		}
		
		buttonDelete = new ClickButton(this, "DELETE") {
			@Override
			public void onAction() {
				bookmarks[selected] = null;
				AetherTown.regionCache.verifyBookmarks(bookmarks);
				updateSelection();
			}
		};
		buttonDelete.setEnabled(false);
		buttonDelete.setPosition(10, getHeight()-buttonDelete.getHeight()-10);

		buttonAdd = new ClickButton(this, "ADD") {
			@Override
			public void onAction() {
				bookmarks[selected] = AetherTown.level.info;
				AetherTown.regionCache.verifyBookmarks(bookmarks);
				updateSelection();
			}
		};
		buttonAdd.setEnabled(false);
		buttonAdd.setPosition(10, getHeight()-buttonAdd.getHeight()-buttonDelete.getHeight()-14);
		
		buttonTravel = new ClickButton(this, "TRAVEL") {
			@Override
			public void onAction() {
				AetherTown.aether.teleportTo(bookmarks[selected]);
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
			LevelInfo level = bookmarks[selected];
			LevelInfo active = AetherTown.level.info;
			if(level==null) {
				buttonDelete.setEnabled(false);
				buttonTravel.setEnabled(false);
				
				boolean add = true;
				for(int i=0; i<numBookmarks; i++) {
					if(active==bookmarks[i]) {
						add = false;
						break;
					}
				}
				buttonAdd.setEnabled(add);
			}
			else {
				buttonAdd.setEnabled(false);
				buttonDelete.setEnabled(true);
				buttonTravel.setEnabled(level!=active);
			}
		}
		repaint();
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
	}
	
}
