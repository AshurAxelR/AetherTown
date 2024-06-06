package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.AetherTown.ui;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.bgColor;

import java.awt.Color;
import java.util.ArrayList;

import com.xrbpowered.aethertown.state.TokenArchive;
import com.xrbpowered.aethertown.state.TokenArchive.ArchiveEntry;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.ItemType;
import com.xrbpowered.aethertown.state.items.TravelTokenItem;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.OptionBox;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.aethertown.ui.hud.Hud;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class ArchiveGetDialog extends UIPane implements KeyInputHandler {

	public static final int itemsPerPage = 10;
	
	private final ArrayList<ArchiveEntry> archive;
	private int selected = -1;
	private ArchiveEntry selectedItem = null;

	private OptionBox optPage;
	private ClickButton buttonClose, buttonGet, buttonDelete;
	
	private ArchiveGetDialog(UIContainer parent) {
		super(parent, false);
		setSize(500, 100+itemsPerPage*32);
		
		archive = TokenArchive.getList();
		
		optPage = new OptionBox(this, 0, pageCount()-1, 0) {
			@Override
			public void onValueChanged(int value) {
				deselect();
			}
			@Override
			public String getText(int value) {
				return String.format("Archive page %d / %d", value+1, maxValue+1);
			}
		};
		optPage.setPosition(10, 10);
		optPage.setSize(getWidth()-20, optPage.getHeight());
		
		for(int i=0; i<itemsPerPage; i++) {
			final int index = i;
			
			SlotButton item =  new SlotButton(this) {
				private ArchiveEntry getEntry() {
					int i = index + optPage.value*itemsPerPage;
					return i<archive.size() ? archive.get(i) : null;
				}
				@Override
				public void onAction() {
					ArchiveEntry e = getEntry();
					if(e==null)
						return;
					selected = index;
					selectedItem = e;
					buttonGet.setEnabled(true);
					buttonDelete.setEnabled(true);
					repaint();
				}
				@Override
				public boolean isEmpty() {
					return getEntry()==null;
				}
				@Override
				public boolean isSelected() {
					return selected==index;
				}
				@Override
				public String getItemName() {
					return getEntry().destination.getFullName();
				}
				@Override
				public void paint(GraphAssist g) {
					super.paint(g);
					ArchiveEntry e = getEntry();
					if(e==null)
						return;
					
					int count = 0;
					for(int i=0; i<player.backpack.size; i++) {
						Item aitem = player.backpack.get(i);
						if(aitem==null)
							break;
						if(aitem.type==ItemType.travelToken) {
							TravelTokenItem item = (TravelTokenItem) aitem;
							if(item.destination.equals(e.destination))
								count++;
						}
					}
					
					if(count>0) {
						String s = String.format("(have %d)", count);
						g.drawString(s, getWidth()-10, getHeight()/2f, GraphAssist.RIGHT, GraphAssist.CENTER);
					}
				}
			};
			
			item.setSize(getWidth()-20, 32);
			item.setPosition(10, 50+i*(item.getHeight()));
		}
		
		buttonClose = new ClickButton(this, "CLOSE") {
			@Override
			public void onAction() {
				close();
			}
		};
		buttonClose.setPosition(10, getHeight()-buttonClose.getHeight()-10);
		
		buttonDelete = new ClickButton(this, "X") {
			@Override
			public void onAction() {
				if(selectedItem==null)
					return;
				ConfirmDialog.show("Remove", String.format(
					"<p>Remove this token from Archive?<br>"+
					"<span class=\"w\">%s</span></p>",
					selectedItem.destination.getFullName()),
					180, "REMOVE", () -> {
						TokenArchive.remove(selectedItem.destination);
						archive.remove(selectedItem);
						deselect();
						int pages = pageCount();
						optPage.maxValue = pages-1;
						if(optPage.value>optPage.maxValue)
							optPage.value = optPage.maxValue;
					});
			}
			@Override
			protected Color getBackgroundColor() {
				return isHover() ? bgDeleteColorHover : bgDeleteColor;
			}
		};
		buttonDelete.setSize(40, buttonDelete.getHeight());
		buttonDelete.setEnabled(false);
		buttonDelete.setPosition(getWidth()-buttonDelete.getWidth()-10, getHeight()-buttonDelete.getHeight()-10);
		
		buttonGet = new ClickButton(this, "TOKEN") {
			@Override
			public void onAction() {
				if(!player.backpack.isFull() && selectedItem!=null)
					player.backpack.put(new TravelTokenItem(selectedItem.destination));
				else
					Hud.showToast("Inventory full");
				repaint();
			}
		};
		buttonGet.setEnabled(false);
		buttonGet.setPosition(buttonDelete.getX()-buttonGet.getWidth(), buttonDelete.getY());
	}
	
	private int pageCount() {
		return (archive.size()+itemsPerPage-1)/itemsPerPage;
	}
	
	private void deselect() {
		selected = -1;
		selectedItem = null;
		buttonGet.setEnabled(false);
		buttonDelete.setEnabled(false);
	}

	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
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
		new ArchiveGetDialog(ui);
		ui.reveal();
	}

}
