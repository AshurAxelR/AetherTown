package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.event.KeyEvent;

import com.xrbpowered.aethertown.ui.ImageBrowserPane;
import com.xrbpowered.aethertown.ui.LevelMapImage;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class LevelMapDialog extends ImageBrowserPane implements KeyInputHandler {
	
	private Level level;
	private boolean unlockRegion; 
	
	private LevelMapDialog(UIContainer parent, Level level, boolean unlockRegion) {
		super(parent);
		this.level = level;
		this.unlockRegion = unlockRegion;
		setImage(new LevelMapImage(level).create());
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		switch(code) {
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_E:
			case KeyEvent.VK_M:
				remove();
				ui.reveal();
				break;
			case KeyEvent.VK_N:
				if(unlockRegion) {
					remove();
					RegionMapDialog.show(level);
					ui.repaint();
				}
			default:
				break;
		}
		return true;
	}
	
	public static void show(Level level, boolean unlockRegion) {
		ui.hideTop();
		new LevelMapDialog(ui, level, unlockRegion);
		ui.reveal();
	}

}