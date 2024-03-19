package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.levelCache;
import static com.xrbpowered.aethertown.AetherTown.regionCache;

import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.ui.dialogs.LevelMapDialog;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.region.LevelInfo;

public class LevelMapItem extends Item {

	public final NamedLevelRef level;
	
	public LevelMapItem(LevelInfo level) {
		super(ItemType.map);
		this.level = new NamedLevelRef(level);
	}
	
	@Override
	public String getUseActionName() {
		return "VIEW";
	}
	
	@Override
	public void useItem() {
		LevelInfo info = this.level.find(regionCache);
		Level level = levelCache.getLevel(info, true);
		LevelMapDialog.show(level, false);
	}

	@Override
	public String getInfoHtml() {
		return String.format(
			"<p><span class=\"w\">%s</span></p>"+
			"<p>Map of location.</p>",
			level.getFullName());
	}
}
