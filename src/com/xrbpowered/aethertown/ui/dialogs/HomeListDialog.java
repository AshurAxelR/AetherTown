package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.AetherTown.ui;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.bgColor;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.ArrayList;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HomeImprovements;
import com.xrbpowered.aethertown.state.items.HouseKeyItem;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.InfoBox;
import com.xrbpowered.aethertown.ui.controls.SelectButton;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class HomeListDialog extends UIPane implements KeyInputHandler {

	private class HouseInfo {
		public final int index;
		public final String level;
		// TODO impromvements
		
		public HomeData home;
		public HouseGenerator house;
		
		public HouseInfo(HomeData home) {
			this.home = home;
			this.house = null;
			this.index = home.ref.houseIndex;
			this.level = home.ref.level.getFullName();
		}
		
		public HouseInfo(HouseGenerator house) {
			this.home = null;
			this.house = house;
			this.index = house.index;
			this.level = house.startToken.level.info.name;
		}
	}
	
	private static final int buttonWidth = 60;

	private final boolean claim;
	
	private ArrayList<HouseInfo> infoList = new ArrayList<>();
	private int selected = -1;
	private HouseInfo selectedInfo = null;
	
	private ClickButton buttonClose, buttonMap, buttonAction;
	
	public HomeListDialog(UIContainer parent, ArrayList<HouseGenerator> claimOptions) {
		super(parent, false);
		this.claim = (claimOptions!=null);
		
		if(!claim) {
			int i = 0;
			int x = 10;
			for(HomeData home : HomeData.list()) {
				if(i>0 && i%10==0)
					x += buttonWidth;
				
				infoList.add(new HouseInfo(home));
				final int index = i;
				final boolean local = home.ref.level.isLevel(AetherTown.levelInfo);
				if(selected<0 || local)
					selected = index;
				
				SelectButton button = new SelectButton(this, String.format("#%d", index+1)) {
					@Override
					public void onAction() {
						select(index);
					}
					@Override
					public boolean isEnabled() {
						return infoList.get(index).home!=null;
					}
					@Override
					public boolean isSelected() {
						return selected==index;
					}
					@Override
					protected float getLabelAnchorX() {
						return super.getLabelAnchorX() - (local ? 6 : 0);
					}
					@Override
					protected void paintLabel(GraphAssist g) {
						super.paintLabel(g);
						if(local) {
							FontMetrics fm = g.graph.getFontMetrics();
							float w = fm.stringWidth(getLabel());
							g.pushPureStroke(true);
							g.fillCircle(getLabelAnchorX() + w/2f + 7, getHeight()/2, 5);
							g.popPureStroke();
						}
					}
				};
				
				button.setSize(buttonWidth, button.getHeight());
				button.setPosition(x, 50+i*(button.getHeight()));
				i++;
			}
			setSize(x+buttonWidth+320, 360);
		}
		else {
			int i = 0;
			int x = 10;
			for(HouseGenerator house : claimOptions) {
				if(i>0 && i%10==0)
					x += buttonWidth;
				
				infoList.add(new HouseInfo(house));
				final int index = i;
				if(selected<0)
					selected = index;
				
				SelectButton button = new SelectButton(this, Integer.toString(house.index+1)) {
					@Override
					public void onAction() {
						select(index);
					}
					@Override
					public boolean isSelected() {
						return selected==index;
					}
				};
				
				button.setSize(buttonWidth, button.getHeight());
				button.setPosition(x, 50+i*(button.getHeight()));
				i++;
			}
			setSize(x+buttonWidth+320, 360);
			
			Level level = claimOptions.get(0).startToken.level;
			buttonMap = new ClickButton(this, "MAP") {
				public void onAction() {
					LevelMapDialog.show(level, false);
				}
			};
			buttonMap.setSize(80, buttonMap.getHeight());
			buttonMap.setPosition(getWidth()-buttonMap.getWidth()-10, 10);
		}
		
		buttonClose = new ClickButton(this, "CLOSE") {
			@Override
			public void onAction() {
				close();
			}
		};
		buttonClose.setPosition(10, getHeight()-buttonClose.getHeight()-10);
		
		buttonAction = new ClickButton(this, claim ? "CLAIM" : "ABANDON") {
			@Override
			public void onAction() {
				if(claim) {
					if(player.backpack.isFull())
						showToast("Inventory full");
					else if(HomeData.hasLocalHome(selectedInfo.house.startToken.level.info))
						showToast("Already own a home here");
					else {
						HomeData home = HomeData.claim(selectedInfo.house);
						player.backpack.put(new HouseKeyItem(home));
						showToast(selectedInfo.house.getAddress()+" claimed");
						close();
					}
				}
				else {
					ConfirmDialog.show("Abandon home", String.format(
							"<p>Do you want to stop ownership of this home?<br>"+
							"<span class=\"w\">%s</span></p>"+
							"<p>All stored items will be permanently disposed.</p>",
							selectedInfo.home.ref.getFullAddress()),
							200, "ABANDON", new Runnable() {
								@Override
								public void run() {
									if(HomeData.abandon(selectedInfo.home)) {
										selectedInfo.home = null;
										select(-1);
									}
								}
							});
				}
			}
			
			@Override
			protected Color getTextColor() {
				return !claim || canClaim() ? textColor : textColorDisabled;
			}
			
			@Override
			protected Color getBackgroundColor() {
				if(claim)
					return super.getBackgroundColor();
				else
					return isHover() ? bgDeleteColorHover : bgDeleteColor;
			}
		};
		buttonAction.setPosition(getWidth()-buttonAction.getWidth()-10, getHeight()-buttonClose.getHeight()-10);
		select(selected);
	}
	
	private boolean canClaim() {
		return claim &&
				!HomeData.hasLocalHome(selectedInfo.house.startToken.level.info) &&
				!player.backpack.isFull();
	}
	
	private void select(int index) {
		selected = index;
		selectedInfo = (index<0) ? null : infoList.get(index);
		buttonAction.setVisible(selectedInfo!=null);
		repaint();
	}

	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.large);
		g.drawString(claim ? "Available Homes" : "Your Homes",
				10, 26, GraphAssist.LEFT, GraphAssist.CENTER);
		
		if(selectedInfo!=null) {
			float x = getWidth()-290;
			g.fillRect(x-20, 50, 300, getHeight()-100, Color.BLACK);
			g.setColor(Color.WHITE);
			g.setFont(Fonts.large);
			g.drawString(Integer.toString(selectedInfo.index+1), x, 72, GraphAssist.LEFT, GraphAssist.CENTER);
			g.setFont(Fonts.smallBold);
			float y = 10+g.drawString(selectedInfo.level, x, 94, GraphAssist.LEFT, GraphAssist.CENTER);
			
			g.setFont(Fonts.small);
			for(HomeImprovements imp : HomeImprovements.values()) {
				g.setColor(InfoBox.textColor);
				g.drawString(imp.name, x, y, GraphAssist.LEFT, GraphAssist.CENTER);
				g.setColor(Color.WHITE);
				y = g.drawString("YES", x+180, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		}
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

	public static void showClaim(ArrayList<HouseGenerator> claimOptions) {
		ui.hideTop();
		new HomeListDialog(ui, claimOptions);
		ui.reveal();
	}

	public static void showList() {
		ui.hideTop();
		new HomeListDialog(ui, null);
		ui.reveal();
	}

}
