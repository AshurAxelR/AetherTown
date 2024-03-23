package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.player;
import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.state.Inventory;
import com.xrbpowered.aethertown.state.items.Item;
import com.xrbpowered.aethertown.state.items.ItemType;
import com.xrbpowered.aethertown.world.Tile;

public abstract class GetItemAction extends TileAction {

	public GetItemAction(String name, ItemType type) {
		super(name + " " + type.name);
	}

	public GetItemAction(String name) {
		super(name);
	}
	
	public Inventory getTargetInventory(Tile tile, boolean alt) {
		return player.backpack;
	}

	protected abstract Item generateItem(Tile tile, boolean alt);
	protected abstract boolean isSameItem(Item item, Tile tile, boolean alt);
	
	protected int countInventoryItems(Tile tile, boolean alt) {
		Inventory inv = getTargetInventory(tile, alt);
		int count = 0;
		for(int i=0; i<inv.size; i++) {
			Item item = inv.get(i);
			if(item==null)
				break;
			if(isSameItem(item, tile, alt))
				count++;
		}
		return count;
	}
	
	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		return super.isEnabled(tile, alt) && !player.backpack.isFull();
	}
	
	@Override
	public String getLabel(Tile tile, boolean alt) {
		int count = countInventoryItems(tile, alt);
		if(count > 0)
			return String.format("%s (have %d)", super.getLabel(tile, alt), count);
		else
			return super.getLabel(tile, alt);
	}
	
	@Override
	protected void onFail(Tile tile, boolean alt) {
		if(player.backpack.isFull())
			showToast("Inventory full");
		else
			super.onFail(tile, alt);
	}
	
	@Override
	public void onSuccess(Tile tile, boolean alt) {
		Item item = generateItem(tile, alt);
		if(player.backpack.put(item)) {
			showToast(item.getName()+" added");
			super.onSuccess(tile, alt);
		}
	}
}
