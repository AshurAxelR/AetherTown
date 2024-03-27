package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.*;

import com.xrbpowered.aethertown.state.NamedLevelRef;
import com.xrbpowered.aethertown.ui.dialogs.LevelMapDialog;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class LevelMapItem extends Item {

	public final NamedLevelRef level;
	
	public LevelMapItem(NamedLevelRef ref, double time) {
		super(ItemType.map, time);
		this.level = ref;
	}
	
	public LevelMapItem(LevelInfo level) {
		this(new NamedLevelRef(level), WorldTime.time);
	}
	
	@Override
	public String getFullName() {
		return String.format("%s of %s", super.getFullName(), level.getFullName());
	}

	@Override
	public String getUseActionName() {
		return "VIEW";
	}
	
	@Override
	public boolean useItem(Tile tile, boolean alt) {
		LevelInfo info = this.level.find(regionCache);
		Level level = levelCache.getLevel(info, true);
		LevelMapDialog.show(level, false);
		return true;
	}

	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return String.format(
			"<p><span class=\"w\">%s</span>%s</p>"+
			"<p>Map of location.</p>",
			level.getFullName(), markDot(tile, alt) ? "<br>(You are here)": "");
	}
	
	@Override
	public boolean markDot(Tile tile, boolean alt) {
		return levelInfo.isRef(level);
	}
	
	public static boolean isSameItem(Item aitem, LevelInfo info) {
		if(aitem.type==ItemType.map) {
			LevelMapItem item = (LevelMapItem) aitem;
			if(item.level.isLevel(info))
				return true;
		}
		return false;
	}
	
	public static boolean hasLevelMap(LevelInfo info) {
		for(int i=0; i<player.backpack.size; i++) {
			Item aitem = player.backpack.get(i);
			if(aitem==null)
				break;
			if(isSameItem(aitem, info))
				return true;
		}
		return false;
	}
}
