package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.*;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.bgColor;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EnumSet;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HomeImprovement;
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

	public static final int costPerHome = 10000;
	
	private class HouseInfo {
		public final int index;
		public final String level;
		public final EnumSet<HomeImprovement> improvements;
		
		public HomeData home;
		public HouseGenerator house;
		
		public HouseInfo(HomeData home) {
			this.home = home;
			this.house = null;
			this.index = home.ref.houseIndex;
			this.level = home.ref.level.getFullName();
			this.improvements = home.improvements;
		}
		
		public HouseInfo(HouseGenerator house) {
			this.home = null;
			this.house = house;
			this.index = house.index;
			this.level = house.startToken.level.info.name;
			this.improvements = HomeImprovement.generateDefaults(house);
		}
	}
	
	private static final int buttonWidth = 60;

	private final boolean claim;
	
	private ArrayList<HouseInfo> infoList = new ArrayList<>();
	private int selected = -1;
	private HouseInfo selectedInfo = null;
	private int cost = 0;
	
	private ClickButton buttonClose, buttonMap, buttonAction;
	
	public HomeListDialog(UIContainer parent, ArrayList<HouseGenerator> claimOptions) {
		super(parent, false);
		this.claim = (claimOptions!=null);

		int x = 10;
		int y = 0;
		if(!claim) {
			int i = 0;
			for(HomeData home : HomeData.list()) {
				if(i>0 && i%8==0) {
					x += buttonWidth;
					y = 0;
				}
				
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
				button.setPosition(x, 50+y);
				y += button.getHeight();
				i++;
			}
			setSize(x+buttonWidth+320, 356);
		}
		else {
			Level level = claimOptions.get(0).startToken.level;
			HomeData owned = HomeData.getLocal(level.info	);
			int ownedIndex = (owned==null) ? -1 : owned.ref.houseIndex;
			
			int i = 0;
			for(HouseGenerator house : claimOptions) {
				if(house.index==ownedIndex)
					continue;
				if(i>0 && i%8==0) {
					x += buttonWidth;
					y = 0;
				}
				
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
				button.setPosition(x, 50+y);
				y += button.getHeight();
				i++;
			}
			setSize(x+buttonWidth+320, 356);
			
			buttonMap = new ClickButton(this, "MAP") {
				public void onAction() {
					LevelMapDialog.show(level, false);
				}
			};
			buttonMap.setSize(80, buttonMap.getHeight());
			buttonMap.setPosition(getWidth()-buttonMap.getWidth()-10, 10);
			
			cost = HomeData.totalClaimed() * costPerHome;
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
					if(HomeData.hasLocalHome(selectedInfo.house.startToken.level.info))
						showToast("Already own a home here");
					else if(!canAfford())
						showToast("Not enough funds");
					else if(player.backpack.isFull())
						showToast("Inventory full");
					else {
						HomeData home = HomeData.claim(selectedInfo.house);
						player.cash -= cost;
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
							200, "ABANDON", () -> {
								if(HomeData.abandon(selectedInfo.home)) {
									selectedInfo.home = null;
									select(-1);
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
			
			@Override
			protected void paintLabel(GraphAssist g) {
				super.paintLabel(g);
				if(claim) {
					g.setColor(canAfford() ? textColor : textColorDisabled);
					g.drawString(TileAction.formatCost(cost), -10, getHeight()/2, GraphAssist.RIGHT, GraphAssist.CENTER);
				}
			}
		};
		buttonAction.setPosition(getWidth()-buttonAction.getWidth()-10, getHeight()-buttonClose.getHeight()-10);
		select(selected);
	}
	
	private boolean canAfford() {
		return settings.homeCrediting || cost==0 || player.cash>=cost;
	}
	
	private boolean canClaim() {
		return claim &&
				!HomeData.hasLocalHome(selectedInfo.house.startToken.level.info) &&
				canAfford() &&
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
			for(HomeImprovement imp : HomeImprovement.values()) {
				g.setColor(InfoBox.textColor);
				g.drawString(imp.name, x, y, GraphAssist.LEFT, GraphAssist.CENTER);
				String s;
				if(selectedInfo.improvements.contains(imp)) {
					g.setColor(Color.WHITE);
					s = "YES";
				}
				else {
					g.setColor(InfoBox.darkColor);
					s = "NO";
				}
				y = g.drawString(s, x+180, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		}
	}
	
	public void close() {
		DialogContainer.close(this);
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		switch(code) {
			case KeyEvent.VK_M:
				if(claim)
					LevelMapDialog.show(level, false);
				break;
			default:
				if(DialogContainer.isCloseKey(this, code))
					close();
				break;
		}
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
