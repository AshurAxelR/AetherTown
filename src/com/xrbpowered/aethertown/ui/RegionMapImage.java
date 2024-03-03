package com.xrbpowered.aethertown.ui;

import static com.xrbpowered.aethertown.AetherTown.settings;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;

import com.xrbpowered.aethertown.data.RegionVisits;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.region.LevelSettlementType;
import com.xrbpowered.aethertown.world.region.LevelTerrainModel;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.zoomui.GraphAssist;

public class RegionMapImage extends ImageGenerator {

	public static final int tileSize = 20;
	private static final int gridStep = 8;

	private static final Color colorNotVisited = new Color(0xfafafa);
	private static final Color colorActive = new Color(0xdd0000);
	private static final Color colorPortal = new Color(0x00aaff);
	
	private static final Color[] colorLevel = { new Color(0xecf4db), new Color(0xddeebb), new Color(0xccdd88) };
	private static final Color colorLevelBorder = new Color(0xdddddd);
	private static final Color colorPaths = new Color(0x777777);
	private static final Color colorTown = new Color(0x000000);

	public final Region region;
	public final LevelInfo active;
	public final BookmarkPane bookmarks;
	
	private final int minx, minz, maxx, maxz;
	
	public RegionMapImage(Region region, LevelInfo active, BookmarkPane bookmarks) {
		this.region = region;
		this.active = active;
		this.bookmarks = bookmarks;
		minx = getMapMinX(region);
		minz = getMapMinZ(region);
		maxx = getMapMaxX(region);
		maxz = getMapMaxZ(region);
	}
	
	@Override
	public BufferedImage create() {
		return create(
			(maxx-minx)*tileSize + margin*2,
			(maxz-minz)*tileSize + margin + marginTop
		);
	}

	@Override
	protected void paint(GraphAssist g, int w, int h) {
		g.fillRect(0, 0, w, h, colorMargin);
		g.setColor(colorMarginText);
		g.setFont(Fonts.large);
		float y = (margin+marginTop)/2;
		g.drawString(RegionVisits.getRegionTitle(region)+" Map", w/2, y, GraphAssist.CENTER, GraphAssist.CENTER);
		
		g.translate(margin - minx*tileSize, marginTop - minz*tileSize);
		paintMap(g, region, active, !settings.revealRegion, bookmarks, minx, minz, maxx, maxz);
	}

	public static void paintInfo(GraphAssist g, Region region, int hoverx, int hoverz, boolean showVisited) {
		if(!region.isInside(hoverx, hoverz))
			return;
		g.fillRect(10, 10, 350, 55, colorInfoBg);
		int x = 20;
		int y = 35;
		int h = 18;
		g.setFont(Fonts.small);
		g.setColor(colorInfoText);
		LevelInfo level = region.map[hoverx][hoverz];
		String terrain = "-";
		if(level!=null)
			terrain = level.terrain.name;
		g.drawString(String.format("[%d, %d] %s", hoverx, hoverz, terrain), x, y, GraphAssist.LEFT, GraphAssist.BOTTOM); y += h;
		if(level!=null) {
			boolean visited = !showVisited || RegionVisits.isVisited(level);
			if(visited)
				g.setFont(Fonts.smallBold);
			g.setColor(Color.BLACK);
			g.drawString(visited ? level.name : "(not visited)", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM); y += h;
		}
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

	public static int getMapMinX(Region region) {
		return Math.max(0, ((region.getMinX()-2)/8)*8);
	}

	public static int getMapMinZ(Region region) {
		return Math.max(0, ((region.getMinZ()-2)/8)*8);
	}

	public static int getMapMaxX(Region region) {
		return Math.min(region.sizex, ((region.getMaxX()+10)/8)*8);
	}

	public static int getMapMaxZ(Region region) {
		return Math.min(region.sizez, ((region.getMaxZ()+10)/8)*8);
	}

	public static void paintMap(GraphAssist g, Region region, LevelInfo active, boolean showVisited, BookmarkPane bookmarkPane) {
		paintMap(g, region, active, showVisited, bookmarkPane, 0, 0, region.sizex, region.sizez);
	}

	public static void paintMap(GraphAssist g, Region region, LevelInfo active, boolean showVisited, BookmarkPane bookmarkPane,
			int minx, int minz, int maxx, int maxz) {
		g.pushAntialiasing(false);
		
		HashSet<LevelInfo> bookmarks = new HashSet<>();
		if(bookmarkPane!=null) {
			for(LevelInfo level : bookmarkPane.bookmarks)
				bookmarks.add(level);
		}
		
		g.fillRect(minx*tileSize, minz*tileSize, (maxx-minx)*tileSize, (maxz-minz)*tileSize, colorBg);
		g.resetStroke();
		g.setColor(colorGrid);
		g.setFont(Fonts.smallBold);
		if(minx==0 && minz==0)
			g.drawString("0", 8, 8, GraphAssist.LEFT, GraphAssist.TOP);
		for(int x=minx+gridStep; x<maxx; x+=gridStep) {
			g.line(x*tileSize, minz*tileSize, x*tileSize, maxz*tileSize-1);
			float tx = x*tileSize+8;
			String s = Integer.toString(x);
			g.drawString(s, tx, minz*tileSize+8, GraphAssist.LEFT, GraphAssist.TOP);
			g.drawString(s, tx, maxz*tileSize-8, GraphAssist.LEFT, GraphAssist.BOTTOM);
		}
		for(int z=minz+gridStep; z<maxz; z+=gridStep) {
			g.line(minx*tileSize, z*tileSize, maxx*tileSize-1, z*tileSize);
			float tz = z*tileSize+8;
			String s = Integer.toString(z);
			g.drawString(s, minx*tileSize+8, tz, GraphAssist.LEFT, GraphAssist.TOP);
			g.drawString(s, maxx*tileSize-8, tz, GraphAssist.RIGHT, GraphAssist.TOP);
		}
		
		for(int x=minx; x<maxx; x++)
			for(int z=minz; z<maxz; z++) {
				LevelInfo level = region.map[x][z];
				if(level==null)
					continue;
				if(level.x0!=x || level.z0!=z)
					continue;
				
				if(!showVisited || RegionVisits.isVisited(level)) {
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
				
				if(level.isPortal()) {
					g.pushAntialiasing(true);
					g.pushPureStroke(true);
					g.setColor(colorPortal);
					g.setStroke(2f);
					g.graph.drawOval(level.x0*tileSize+3, level.z0*tileSize+3, tileSize-6, tileSize-6);
					g.popAntialiasing();
					g.resetStroke();
					g.popPureStroke();
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

}
