package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.event.KeyEvent;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.ui.ImageBrowserPane;
import com.xrbpowered.aethertown.ui.RegionMapImage;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.UIContainer;

public class RegionMapDialog extends FullscreenDialogNode {

	private final ImageBrowserPane image;
	
	private final Level level;
	
	private RegionMapDialog(UIContainer parent, Region region, LevelInfo info, Level level) {
		super(parent);
		this.level = level;
		
		image = new ImageBrowserPane(content);
		image.setImage(new RegionMapImage(region, info).create());
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		switch(code) {
			case KeyEvent.VK_N:
				remove();
				ui.reveal();
				break;
			case KeyEvent.VK_M:
				if(level!=null) {
					remove();
					LevelMapDialog.show(level, true);
					ui.repaint();
				}
				break;
			default:
				return super.onKeyPressed(c, code, input);
		}
		return true;
	}
	
	public static void show(Level level) {
		ui.hideTop();
		new RegionMapDialog(ui, level.info.region, level.info, level);
		ui.reveal();
	}

	public static void show(Region region) {
		ui.hideTop();
		LevelInfo active = (region==AetherTown.region) ? AetherTown.levelInfo : null;
		new RegionMapDialog(ui, region, active, null);
		ui.reveal();
	}

}
