package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.player;

import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HouseTileRef;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class HouseKeyItem extends Item {

	public final HouseTileRef house;
	
	public HouseKeyItem(HouseTileRef house, double time) {
		super(ItemType.houseKey, time);
		this.house = house;
	}
	
	public HouseKeyItem(HomeData home) {
		this(home.ref, WorldTime.time);
	}
	
	public int getHomeIndex() {
		HomeData h = HomeData.forHouse(house);
		return (h==null) ? -1 : h.getIndex();
	}
	
	@Override
	public String getName() {
		int index = getHomeIndex();
		if(index>=0)
			return String.format("%s #%d", super.getName(), index+1);
		else
			return "Abandoned "+super.getName();
	}
	
	@Override
	public String getFullName() {
		return String.format("%s for %s", super.getFullName(), house.getFullAddress());
	}

	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		if(getHomeIndex()>=0)
			return String.format(
				"<p>A door key for your home at<br>"+
				"<span class=\"w\">%s</span></p>"+
				"<p>If you lose a key, you can always request a copy at Civic Centre.</p>",
				house.getFullAddress());
		else
			return "<p>A door key for a house that has been abandoned.<br>It no longer works.</p>";
	}
	
	@Override
	public boolean markDot(Tile tile, boolean alt) {
		return getHomeIndex()>=0 && house.level.isLevel(tile.level.info); 
	}

	public static boolean hasKey(HouseTileRef ref) {
		for(int i=0; i<player.backpack.size; i++) {
			Item item = player.backpack.get(i);
			if(item==null)
				break;
			if(item.type==ItemType.houseKey) {
				HouseKeyItem key = (HouseKeyItem) item;
				if(ref.equals(key.house))
					return true;
			}
		}
		return false;
	}

}
