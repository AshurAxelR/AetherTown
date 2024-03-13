package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.aether;
import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.event.KeyEvent;

import com.xrbpowered.aethertown.ui.ImageBrowserPane;
import com.xrbpowered.aethertown.ui.RegionMapImage;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class RegionMapDialog extends ImageBrowserPane implements KeyInputHandler {

	private Level level;
	
	private RegionMapDialog(UIContainer parent, Region region, LevelInfo info, Level level) {
		super(parent);
		this.level = level;
		aether.disableController();
		setImage(new RegionMapImage(region, info).create());
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		switch(code) {
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_E:
			case KeyEvent.VK_N:
				remove();
				ui.repaint();
				break;
			case KeyEvent.VK_M:
				if(level!=null) {
					remove();
					LevelMapDialog.show(level, true);
					ui.repaint();
				}
				break;
			default:
				break;
		}
		return true;
	}
	
	public static void show(Level level) {
		new RegionMapDialog(ui, level.info.region, level.info, level);
		ui.reveal();
	}

}
