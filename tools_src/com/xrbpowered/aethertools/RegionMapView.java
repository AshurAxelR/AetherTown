package com.xrbpowered.aethertools;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashSet;

import com.xrbpowered.aethertown.ui.BookmarkPane;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.region.LevelSettlementType;
import com.xrbpowered.aethertown.world.region.LevelTerrainModel;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIPanView;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class RegionMapView extends UIElement {

	public static final int tileSize = 20;
	
	public static final Color colorBg = new Color(0xf5f5f5);
	public static final Color colorTextBg = new Color(0xbbffffff, true);
	public static final Color colorText = new Color(0x777777);
	public static final Color colorNotVisited = new Color(0xfafafa);
	public static final Color colorActive = new Color(0xdd0000);
	
	public static final Color[] colorLevel = { new Color(0xecf4db), new Color(0xddeebb), new Color(0xccdd88) };
	public static final Color colorLevelBorder = new Color(0xdddddd);
	public static final Color colorPaths = new Color(0x777777);
	public static final Color colorTown = new Color(0x000000);
	
	public static Region region;
	public static LevelInfo active = null;
	public static boolean showVisited = true;
	
	private static int hoverx, hoverz;
	
	public BookmarkPane bookmarks = null;
	
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
		g.fillRect(10, 10, 350, 55, colorTextBg);
		int x = 20;
		int y = 35;
		int h = 18;
		g.setFont(Fonts.small);
		g.setColor(colorText);
		LevelInfo level = region.map[hoverx][hoverz];
		String terrain = "-";
		if(level!=null)
			terrain = level.terrain.name;
		g.drawString(String.format("[%d, %d] %s", hoverx, hoverz, terrain), x, y, GraphAssist.LEFT, GraphAssist.BOTTOM); y += h;
		if(level!=null) {
			boolean visited = !showVisited || level.visited;
			if(visited)
				g.setFont(Fonts.smallBold);
			g.setColor(Color.BLACK);
			g.drawString(visited ? level.name : "(not visited)", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM); y += h;
		}
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
	
	private static Color getLevelColor(LevelTerrainModel terrain) {
		if(terrain==LevelTerrainModel.low)
			return colorLevel[0];
		else if(terrain==LevelTerrainModel.hill || terrain==LevelTerrainModel.flat)
			return colorLevel[1];
		else if(terrain==LevelTerrainModel.peak)
			return colorLevel[2];
		else
			return Color.WHITE;
	}
	
	private static int getSettlementRectSize(LevelSettlementType settlement) {
		return settlement.ordinal()+1;
	}
	
	@Override
	public void paint(GraphAssist g) {
		g.pushAntialiasing(false);
		
		HashSet<LevelInfo> bookmarks = new HashSet<>();
		if(this.bookmarks!=null) {
			for(LevelInfo level : this.bookmarks.bookmarks)
				bookmarks.add(level);
		}
		
		for(int x=0; x<Region.sizex; x++)
			for(int z=0; z<Region.sizez; z++) {
				LevelInfo level = region.map[x][z];
				if(level==null)
					continue;
				if(level.x0!=x || level.z0!=z)
					continue;
				
				if(!showVisited || level.visited) {
					g.setColor(getLevelColor(level.terrain));
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
					
					if(level.settlement!=LevelSettlementType.none) {
						int s = getSettlementRectSize(level.settlement);
						g.setColor(bookmarks.contains(level) ? colorActive : colorTown);
						g.fillRect(x*tileSize+level.size*tileSize/2-s, z*tileSize+level.size*tileSize/2-s, s*2+1, s*2+1);
					}
					else if(bookmarks.contains(level)) {
						g.setColor(colorActive);
						g.drawRect(x*tileSize, z*tileSize, level.size*tileSize-1, level.size*tileSize-1);
					}
				}
				else {
					g.setColor(colorNotVisited);
					g.fillRect(x*tileSize, z*tileSize, level.size*tileSize, level.size*tileSize);
				}
			}

		if(active!=null) {
			g.pushAntialiasing(true);
			g.pushPureStroke(true);
			g.setColor(colorActive);
			g.setStroke(3f);
			g.graph.drawOval(active.x0*tileSize-3, active.z0*tileSize-3, active.size*tileSize+6, active.size*tileSize+6);
			g.popAntialiasing();
			g.resetStroke();
			g.popPureStroke();
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

		long seed = System.currentTimeMillis();
		System.out.printf("Region seed: %dL\n", seed);
		region = new Region(seed);
		region.generate();
		
		active = region.startLevel;
		showVisited = false;
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("AetherTown region map", 1920, 1080);
		new RegionMapView(frame.getContainer());
		frame.show();
	}

}
