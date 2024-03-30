package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.*;
import static com.xrbpowered.aethertown.ui.dialogs.DialogContainer.bgColor;

import java.awt.Color;
import java.util.ArrayList;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.actions.CooldownSettings;
import com.xrbpowered.aethertown.actions.FastTravelAction;
import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.TravelTokenItem;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.SlotButton;
import com.xrbpowered.aethertown.ui.hud.Hud;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class FastTravelDialog extends UIPane implements KeyInputHandler {

	private final CooldownSettings cooldown;
	private final ArrayList<TravelTokenItem> tokens;
	private int selected = -1;

	private ClickButton buttonClose, buttonTravel;

	private FastTravelDialog(UIContainer parent) {
		super(parent, false);
		this.cooldown = FastTravelAction.action.getCooldown();
		
		tokens = new ArrayList<>();
		for(int i=0; i<player.backpack.size; i++) {
			Item aitem = player.backpack.get(i);
			if(aitem==null)
				break;
			if(aitem instanceof TravelTokenItem)
				tokens.add((TravelTokenItem) aitem);
		}

		setSize(500, 100+tokens.size()*32);

		for(int i=0; i<tokens.size(); i++) {
			final int index = i;
			final TravelTokenItem token = tokens.get(index);
			
			SlotButton item =  new SlotButton(this) {
				@Override
				public void onAction() {
					selected = index;
					NamedLevelRef ref = token.destination;
					LevelInfo active = AetherTown.level.info;
					buttonTravel.setEnabled(!active.isRef(ref) && !cooldown.isOnCooldown());
					repaint();
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
					if(levelInfo.isRef(token.destination))
						paintDot(g);
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
		
		buttonTravel = new ClickButton(this, "TRAVEL") {
			@Override
			public void onAction() {
				if(cooldown.isOnCooldown())
					return;
				aether.teleportTo(tokens.get(selected).destination.find(regionCache));
				cooldown.start();
				close();
				Hud.fadeIn(Color.WHITE, 1f);
			}
		};
		buttonTravel.setSize(200, buttonTravel.getHeight());
		buttonTravel.setEnabled(false);
		buttonTravel.setPosition(getWidth()-buttonTravel.getWidth()-10, getHeight()-buttonTravel.getHeight()-10);
	}

	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.small);
		g.drawString("Select destination:", 30, 26, GraphAssist.LEFT, GraphAssist.CENTER);
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
		new FastTravelDialog(ui);
		ui.reveal();
	}
}
