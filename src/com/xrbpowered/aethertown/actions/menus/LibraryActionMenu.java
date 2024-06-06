package com.xrbpowered.aethertown.actions.menus;

import static com.xrbpowered.aethertown.ui.hud.Hud.showToast;

import com.xrbpowered.aethertown.actions.LeisureActions;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.state.TokenArchive;
import com.xrbpowered.aethertown.state.items.TravelTokenItem;
import com.xrbpowered.aethertown.ui.dialogs.ArchiveAddDialog;
import com.xrbpowered.aethertown.ui.dialogs.ArchiveGetDialog;
import com.xrbpowered.aethertown.ui.dialogs.TileActionMenu;
import com.xrbpowered.aethertown.world.Tile;

public class LibraryActionMenu extends TileActionMenu {

	public LibraryActionMenu() {
		addAction(LeisureActions.discover);
		addAction(LeisureActions.study);
		addAction(LeisureActions.readBooks);
		addAction(LeisureActions.portalKnowledge);
		
		TileActionMenu arc = new TileActionMenu();
		
		arc.addAction(new TileAction("Record tokens") {
			@Override
			public boolean isEnabled(Tile tile, boolean alt) {
				return super.isEnabled(tile, alt) && TravelTokenItem.hasTravelTokens();
			}
			@Override
			protected void onFail(Tile tile, boolean alt) {
				if(!TravelTokenItem.hasTravelTokens())
					showToast("No travel tokens");
				else
					super.onFail(tile, alt);
			}
			@Override
			protected void onSuccess(Tile tile, boolean alt) {
				ArchiveAddDialog.show();
			}
		});
		
		arc.addAction(new TileAction("Get tokens from Archive") {
			@Override
			public boolean isEnabled(Tile tile, boolean alt) {
				return super.isEnabled(tile, alt) && !TokenArchive.isEmpty();
			}
			@Override
			protected void onFail(Tile tile, boolean alt) {
				if(TokenArchive.isEmpty())
					showToast("Archive is empty");
				else
					super.onFail(tile, alt);
			}
			@Override
			protected void onSuccess(Tile tile, boolean alt) {
				ArchiveGetDialog.show();
			}
		});
		
		addMenu("ARCHIVE", arc);
		addMenu("MAPS", MapsMenu.menu);
	}

}
