package com.xrbpowered.aethertools;

import java.awt.Color;
import java.awt.Rectangle;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIPanView;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class RegionMapView extends UIElement {

	public static final int tileSize = 12;
	
	public static final Color colorBg = new Color(0xf5f5f5);
	public static final Color colorLevel = new Color(0xffffff);
	public static final Color colorLevelBorder = new Color(0xdddddd);
	public static final Color colorPaths = new Color(0x777777);
	
	public static Region region;
	
	private static int hoverx, hoverz;
	
	public RegionMapView(UIContainer parent) {
		super(new UIPanView(parent) {
			@Override
			protected void paintSelf(GraphAssist g) {
				g.fill(this, colorBg);
			}
			@Override
			protected void paintChildren(GraphAssist g) {
				super.paintChildren(g);
				paintInfo(g);
			}
		});
		UIPanView view = (UIPanView) getParent();
		view.setSize(getBase().getWindow().getClientWidth(), getBase().getWindow().getClientHeight());
		centerAt(Region.sizez/2, Region.sizez/2);
	}
	
	private static void paintInfo(GraphAssist g) {
		if(!Region.isInside(hoverx, hoverz))
			return;
		LevelInfo level = region.map[hoverx][hoverz];
		if(level==null)
			return;
		// print info
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
	public boolean isInside(float x, float y) {
		return true;
	}
	
	@Override
	public void paint(GraphAssist g) {
		g.pushAntialiasing(false);
		
		for(int x=0; x<Region.sizex; x++)
			for(int z=0; z<Region.sizez; z++) {
				LevelInfo level = region.map[x][z];
				if(level==null)
					continue;
				if(level.x0!=x || level.z0!=z)
					continue;
				
				g.setColor(colorLevel);
				g.fillRect(x*tileSize, z*tileSize, level.size*tileSize, level.size*tileSize);
				g.setColor(colorLevelBorder);
				g.drawRect(x*tileSize, z*tileSize, level.size*tileSize-1, level.size*tileSize-1);
				
				g.pushAntialiasing(true);
				g.setColor(colorPaths);
				int mx = x*tileSize+level.size*tileSize/2;
				int mz = z*tileSize+level.size*tileSize/2;
				for(LevelConnection c : level.conns) {
					int cx = (c.d.dx==0) ? x*tileSize+c.i*tileSize+tileSize/2 : mx + c.d.dx*level.size*tileSize/2;
					int cz = (c.d.dz==0) ? z*tileSize+c.i*tileSize+tileSize/2 : mz + c.d.dz*level.size*tileSize/2;
					g.line(mx, mz, cx, cz);
				}
				g.popAntialiasing();
			}

		g.popAntialiasing();
	}
	
	@Override
	public void onMouseMoved(float x, float y, int mods) {
		hoverx = (int)(x/tileSize);
		hoverz = (int)(y/tileSize);
		repaint();
	}

	public static void main(String[] args) {
		AssetManager.defaultAssets = new FileAssetManager("assets_src", new FileAssetManager("assets", AssetManager.defaultAssets));
		LevelNames.load();
		Fonts.load();

		region = new Region(System.currentTimeMillis());
		region.generate();
		
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("AetherTown region map", 1920, 1080);
		new RegionMapView(frame.getContainer());
		frame.show();
	}

}