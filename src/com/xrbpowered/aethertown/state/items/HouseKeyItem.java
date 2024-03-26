package com.xrbpowered.aethertown.state.items;

import static com.xrbpowered.aethertown.AetherTown.player;

import com.xrbpowered.aethertown.state.HomeData;
import com.xrbpowered.aethertown.state.HouseTileRef;
import com.xrbpowered.aethertown.state.Inventory;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class HouseKeyItem extends Item {

	public final HouseTileRef house;
	
	private transient int index = -1;
	
	public HouseKeyItem(HouseTileRef house, double time) {
		super(ItemType.houseKey, time);
		this.house = house;
	}
	
	public HouseKeyItem(HomeData home) {
		this(home.ref, WorldTime.time);
	}
	
	public void updateHomeIndex() {
		HomeData h = HomeData.forHouse(house);
		index = (h==null) ? -1 : h.getIndex();
	}
	
	public int getHomeIndex() {
		if(index<0)
			updateHomeIndex();
		return index;
	}
	
	@Override
	public String getName() {
		return String.format("%s #%d", super.getName(), getHomeIndex()+1);
	}
	
	@Override
	public String getFullName() {
		return String.format("%s for %s", super.getFullName(), house.getFullAddress());
	}

	@Override
	public String getInfoHtml(Tile tile, boolean alt) {
		return String.format(
			"<p>A door key for your home at<br>"+
			"<span class=\"w\">%s</span></p>"+
			"<p>If you lose a key, you can always request a copy at Civic Centre.</p>",
			house.getFullAddress());
	}
	
	@Override
	public boolean markDot(Tile tile, boolean alt) {
		return house.level.isLevel(tile.level.info); 
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

	public static void removeKeys(Inventory inv, HouseTileRef ref) {
		boolean removed = false;
		for(int i=0; i<inv.size; i++) {
			Item item = inv.get(i);
			if(item==null)
				break;
			if(item.type==ItemType.houseKey) {
				HouseKeyItem key = (HouseKeyItem) item;
				key.index = -1;
				if(ref.equals(key.house)) {
					inv.remove(i, false);
					removed = true;
				}
			}
		}
		if(removed)
			inv.sort();
	}
}
