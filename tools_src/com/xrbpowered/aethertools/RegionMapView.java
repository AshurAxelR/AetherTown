package com.xrbpowered.aethertools;

import static com.xrbpowered.aethertown.AetherTown.settings;
import static com.xrbpowered.aethertown.ui.ImageGenerator.colorMargin;
import static com.xrbpowered.aethertown.ui.RegionMapImage.*;

import java.awt.Rectangle;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.state.SaveState;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.RegionCache;
import com.xrbpowered.aethertown.world.region.RegionMode;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIPanView;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class RegionMapView extends UIElement {

	public static Region region;
	public static LevelInfo active = null;
	public static boolean showVisited = true;
	
	private static int hoverx, hoverz;
	
	public RegionMapView(UIContainer parent) {
		super(new UIPanView(parent) {
			@Override
			protected void paintBackground(GraphAssist g) {
				g.fill(this, colorMargin);
			}
			@Override
			protected void paintChildren(GraphAssist g) {
				super.paintChildren(g);
				paintInfo(g, region, hoverx, hoverz, showVisited);
			}
		});
		UIPanView view = (UIPanView) getParent();
		view.setSize(getRoot().getWindow().getClientWidth(), getRoot().getWindow().getClientHeight());
		if(region!=null)
			centerAt(region.sizez/2, region.sizez/2);
	}
	
	public void centerAt(int x, int z) {
		UIPanView view = (UIPanView) getParent();
		view.setPan(
				x*tileSize-view.getWidth()/2,
				z*tileSize-view.getHeight()/2);
	}
	
	@Override
	public boolean isVisible(Rectangle clip) {
		return super.isVisible();
	}

	@Override
	public boolean isHit(float x, float y) {
		return true;
	}
	
	@Override
	public void paint(GraphAssist g) {
		paintMap(g, region, active, showVisited);
	}
	
	@Override
	public void onMouseMoved(float x, float y, MouseInfo mouse) {
		hoverx = (int)(x/tileSize);
		hoverz = (int)(y/tileSize);
		repaint();
	}

	public static void main(String[] args) {
		AssetManager.defaultAssets = new FileAssetManager("assets_src", new FileAssetManager("assets", AssetManager.defaultAssets));
		LevelNames.load();
		Fonts.load();

		settings = new AetherTown.ClientConfig();
		//settings.load();
		settings.regionMode = new RegionMode.Linear(64);
		settings.regionSeed = 5764483515444918203L;
		settings.legacyRandom = false;
		
		SaveState save = new SaveState();
		RegionCache.useLegacy(settings.legacyRandom);
		region = new RegionCache(save.regionMode).get(save.regionSeed);
		
		active = region.startLevel;
		showVisited = false;
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("AetherTown region map", 1920, 1080);
		new RegionMapView(frame.getContainer());
		frame.show();
	}

}
