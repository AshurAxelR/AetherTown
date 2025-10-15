package com.xrbpowered.aethertools;

import static com.xrbpowered.aethertown.AetherTown.settings;
import static com.xrbpowered.aethertown.ui.ImageGenerator.colorMargin;
import static com.xrbpowered.aethertown.ui.RegionMapImage.*;

import java.awt.Color;
import java.awt.Rectangle;

import com.xrbpowered.aethertools.ui.UISeedControls;
import com.xrbpowered.aethertown.AetherTown;
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
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class RegionMapView extends UIElement {

	public static Region region;
	public static LevelInfo active = null;
	public static boolean showVisited = true;
	
	public static UIRoot root;
	
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
				if(region!=null)
					paintInfo(g, region, hoverx, hoverz, showVisited);
			}
		});
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
		if(region!=null)
			paintMap(g, region, active, showVisited);
	}
	
	@Override
	public void onMouseMoved(float x, float y, MouseInfo mouse) {
		hoverx = (int)(x/tileSize);
		hoverz = (int)(y/tileSize);
		repaint();
	}

	private static class UITopControls extends UIContainer {

		public static final Color colorPanel = new Color(0xf2f2f2);
		public static final Color colorPanelBorder = new Color(0xcccccc);
		
		public final UISeedControls seedControls;
		
		public UITopControls(UIContainer parent) {
			super(parent);
			setSize(0, UIButton.defaultHeight+16);
			seedControls = new UISeedControls(this) {
				@Override
				public void apply(long seed) {
					startRegion(seed);
				}
			};
		}
		
		@Override
		public void layout() {
			seedControls.setPosition(getWidth() - seedControls.getWidth(), 0);
			super.layout();
		}
		
		@Override
		protected void paintBackground(GraphAssist g) {
			g.fill(this, colorPanel);
			g.resetStroke();
			g.hborder(this, GraphAssist.BOTTOM, colorPanelBorder);
		}

	}
	
	private static class UIRoot extends UIContainer {
		public final UITopControls top;
		public final RegionMapView map;
		public final UIContainer view;

		public UIRoot(UIContainer parent) {
			super(parent);
			map = new RegionMapView(this);
			view = map.getParent();
			top = new UITopControls(this);
		}
		
		@Override
		public void layout() {
			float htop = top.getHeight();
			top.setPosition(0, 0);
			top.setSize(getWidth(), htop);
			view.setPosition(0, htop);
			view.setSize(getWidth(), getHeight() - htop);
			super.layout();
		}
	}
	
	public static void startRegion(long regionSeed) {
		RegionCache.useLegacy(settings.legacyRandom);
		region = new RegionCache(settings.regionMode).get(regionSeed);
		
		if(region!=null) {
			root.map.centerAt(region.sizez/2, region.sizez/2);
			active = region.startLevel;
		}
		root.repaint();
	}
	
	public static void main(String[] args) {
		AssetManager.defaultAssets = new FileAssetManager("assets_src", new FileAssetManager("assets", AssetManager.defaultAssets));
		LevelNames.load();
		Fonts.load();

		settings = new AetherTown.ClientConfig();
		//settings.load();
		settings.regionMode = new RegionMode.Linear(64);
		settings.regionSeed = 2211452794341358531L;
		settings.legacyRandom = false;
		
		showVisited = false;
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("AetherTown region map", 1920, 1080);
		root = new UIRoot(frame.getContainer());
		frame.show();
		
		startRegion(settings.regionSeed);
	}

}
