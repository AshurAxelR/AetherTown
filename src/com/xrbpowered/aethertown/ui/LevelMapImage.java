package com.xrbpowered.aethertown.ui;

import java.awt.Color;
import java.awt.FontMetrics;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TunnelTileTemplate.TunnelTile;
import com.xrbpowered.aethertown.world.gen.plot.ChurchGenerator;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;
import com.xrbpowered.aethertown.world.region.HouseRole;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.tiles.Bench;
import com.xrbpowered.aethertown.world.tiles.Bench.BenchTile;
import com.xrbpowered.aethertown.world.tiles.Bench.BenchType;
import com.xrbpowered.aethertown.world.tiles.ChurchT;
import com.xrbpowered.aethertown.world.tiles.Fountain;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Pavillion;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.zoomui.GraphAssist;

public class LevelMapImage extends GeneratedImage {

	public static final int tileSize = 16;
	private static final int benchMarkerSize = 6;
	private static final int gridStep = 16;
	private static final int margin = 16;
	private static final int marginTop = 64;
	private static final int lineSize = 24;
	
	private static final Color colorMargin = new Color(0x555555);
	private static final Color colorBg = new Color(0xfaf5ee);
	private static final Color colorGrid = new Color(0xeee5dd);
	private static final Color colorStreetBorder = new Color(0x999999);
	private static final Color colorTextBg = new Color(0xbbffffff, true);
	private static final Color colorText = new Color(0x777777);
	private static final Color colorTextDim = new Color(0x999999);
	
	private static final Color colorPark = new Color(0xddeebb);
	private static final Color colorWater = new Color(0x5294a5);
	private static final Color colorPavillion = new Color(0xc4c1b8);

	public final Level level;
	
	public LevelMapImage(Level level) {
		super(
			level.levelSize*tileSize + margin*2,
			level.levelSize*tileSize + margin*2 + marginTop + lineSize*countHouseInfoLines(level)
		);
		this.level = level;
		paint();
	}

	@Override
	protected void paint(GraphAssist g) {
		g.fillRect(0, 0, getWidth(), getHeight(), colorMargin);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.large);
		float y = (margin+marginTop)/2;
		g.drawString(level.info.name, getWidth()/2, y, GraphAssist.CENTER, GraphAssist.CENTER);
		
		g.translate(margin, marginTop);
		paintMap(g, level);
		
		g.translate(0, level.levelSize*tileSize+margin);
		paintHouseList(g, level);
	}

	private static int countHouseInfoLines(Level level) {
		int cols = level.levelSize/32;
		return (level.houseCount+cols-1)/cols;
	}
	
	private static String getXBinName(int bin) {
		return Character.toString((char)(bin+'A'));
	}
	
	private static void paintHouseList(GraphAssist g, Level level) {
		int lines = countHouseInfoLines(level);
		float y = lineSize/2;
		float x = 0;
		int row = 0;
		g.setFont(Fonts.small);
		for(HouseGenerator house : level.houses) {
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(house.index+1), x+24, y, GraphAssist.RIGHT, GraphAssist.CENTER);
			g.drawString(house.getRoleTitle(true), x+56, y, GraphAssist.LEFT, GraphAssist.CENTER);
			g.setColor(colorTextDim);
			g.drawString(String.format("%s%d", getXBinName(house.startToken.x/gridStep), house.startToken.z/gridStep+1),
					x+40, y, GraphAssist.CENTER, GraphAssist.CENTER);
			
			y += lineSize;
			row++;
			if(row==lines) {
				row = 0;
				y = lineSize/2;
				x += tileSize*32;
			}
		}
	}
	
	public static void paintInfo(GraphAssist g, Level level, int hoverx, int hoverz) {
		if(!level.isInside(hoverx, hoverz))
			return;
		g.fillRect(10, 10, 350, 55, colorTextBg);
		int x = 20;
		int y = 35;
		int h = 18;
		g.setFont(Fonts.small);
		g.setColor(colorText);
		g.drawString(String.format("[%d, %d] %s", hoverx, hoverz, level.info.name), x, y, GraphAssist.LEFT, GraphAssist.BOTTOM); y += h;
		Tile tile = level.map[hoverx][hoverz];
		if(tile!=null && (tile.t==HouseT.template || tile.t==ChurchT.template)) {
			String info = null;
			if(tile.sub.parent instanceof HouseGenerator)
				info = ((HouseGenerator) tile.sub.parent).getRoleTitle(true);
			else if(tile.sub.parent instanceof ChurchGenerator)
				info = tile.t.getTileInfo(tile);
			g.setFont(Fonts.smallBold);
			g.setColor(Color.BLACK);
			g.drawString(info, x, y, GraphAssist.LEFT, GraphAssist.BOTTOM); y += h;
		}
	}
	
	private static void drawBorder(GraphAssist g, int x, int z, Dir d) {
		switch(d) {
			case north:
				g.line(x*tileSize, z*tileSize, x*tileSize+tileSize-1, z*tileSize);
				break;
			case east:
				g.line(x*tileSize+tileSize-1, z*tileSize, x*tileSize+tileSize-1, z*tileSize+tileSize-1);
				break;
			case south:
				g.line(x*tileSize, z*tileSize+tileSize-1, x*tileSize+tileSize-1, z*tileSize+tileSize-1);
				break;
			case west:
				g.line(x*tileSize, z*tileSize, x*tileSize, z*tileSize+tileSize-1);
				break;
		}
	}

	private static void paintMap(GraphAssist g, Level level) {
		g.pushAntialiasing(false);
		
		g.fillRect(0, 0, level.levelSize*tileSize, level.levelSize*tileSize, colorBg);
		g.resetStroke();
		g.setColor(colorGrid);
		g.setFont(Fonts.large);
		for(int x=0; x<level.levelSize; x+=gridStep) {
			g.line(x*tileSize, 0, x*tileSize, level.levelSize*tileSize);
			g.line(0, x*tileSize, level.levelSize*tileSize, x*tileSize);
			
			float tx = x*tileSize+gridStep*tileSize/2;
			String s = getXBinName(x/gridStep);
			g.drawString(s, tx, 24, GraphAssist.CENTER, GraphAssist.CENTER);
			g.drawString(s, tx, level.levelSize*tileSize-24, GraphAssist.CENTER, GraphAssist.CENTER);
			s = Integer.toString(x/gridStep+1);
			g.drawString(s, 24, tx, GraphAssist.CENTER, GraphAssist.CENTER);
			g.drawString(s, level.levelSize*tileSize-24, tx, GraphAssist.CENTER, GraphAssist.CENTER);
		}
		
		for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				Tile tile = level.map[x][z];
				if(tile==null)
					continue;
				Color c = null;
				Color addc = null;
				if(tile instanceof TunnelTile && ((TunnelTile) tile).tunnel!=null)
					c = colorStreetBorder;
				else if(Street.isAnyPath(tile.t))
					c = Street.streetColor;
				else if(tile.t instanceof Park)
					c = colorPark;
				else if(tile.t instanceof Plaza) {
					if(tile.t==Plaza.tunnelSideTemplate)
						c = Plaza.plazaColor;
					else if(tile.t==Monument.template)
						c = Monument.statueColor;
					else if(tile.t==Fountain.template)
						c = colorWater;
					else if(tile.t==Pavillion.template)
						c = colorPavillion;
					else if(tile.t instanceof Bench)
						c = ((BenchTile) tile).plaza ? Plaza.plazaColor : colorPark;
					else
						c = Plaza.plazaColor;
				}
				else if(tile.sub!=null && (tile.t==HouseT.template || tile.t==ChurchT.template)) {
					if(tile.sub.parent instanceof HouseGenerator) {
						HouseGenerator house = (HouseGenerator) tile.sub.parent;
						c = house.role.previewColor;
						if(house.addRole!=null)
							addc = house.addRole.previewColor;
					}
					else if(tile.sub.parent instanceof ChurchGenerator)
						c = HouseRole.colorChurch;
				}
				
				if(c!=null) {
					g.setColor(c);
					g.fillRect(x*tileSize, z*tileSize, tileSize, tileSize);
				}
				if(addc!=null && x==tile.sub.parent.maxx() && z==tile.sub.parent.maxz()) {
					g.setColor(addc);
					g.graph.fillPolygon(
							new int[] {x*tileSize, x*tileSize+tileSize, x*tileSize+tileSize},
							new int[] {z*tileSize+tileSize, z*tileSize+tileSize, z*tileSize},
							3);
					g.setColor(Color.WHITE);
					g.line(x*tileSize, z*tileSize+tileSize, x*tileSize+tileSize, z*tileSize);
				}
				
				if(Street.isAnyPath(tile.t)) {
					g.setColor(colorStreetBorder);
					for(Dir d : Dir.values()) {
						Tile adj = tile.getAdj(d);
						if(adj==null || !Street.isAnyPath(adj.t))
							drawBorder(g, x, z, d);
					}
				}
				else if(tile.sub!=null && (tile.t==HouseT.template || tile.t==ChurchT.template)) {
					PlotGenerator plot =  tile.sub.parent;
					g.setColor(Color.WHITE);
					for(Dir d : Dir.values()) {
						Tile adj = tile.getAdj(d);
						if(!(adj!=null && adj.t==tile.t && adj.sub!=null && adj.sub.parent==plot))
							drawBorder(g, x, z, d);
					}
				}
				else if(tile.t==Pavillion.template) {
					g.setColor(colorText);
					g.drawRect(x*tileSize, z*tileSize, tileSize-1, tileSize-1);
				}
				else if(tile.t instanceof Bench && ((Bench) tile.t).type!=BenchType.none) {
					g.setColor(colorText);
					g.fillRect(x*tileSize+tileSize/2-benchMarkerSize/2, z*tileSize+tileSize/2-benchMarkerSize/2,
							benchMarkerSize, benchMarkerSize);
				}
			}

		g.setFont(Fonts.small);
		for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				Tile tile = level.map[x][z];
				if(tile!=null && tile.sub!=null && (tile.sub.parent instanceof HouseGenerator) && tile.sub.i==0 && tile.sub.j==0) {
					HouseGenerator house = (HouseGenerator) tile.sub.parent;
					g.setColor(Color.WHITE);
					g.drawString(Integer.toString(house.index+1),
							(x + house.d.dx*house.fwd/2f + house.dr.dx*(house.right-house.left)/2f)*tileSize+tileSize/2,
							(z + house.d.dz*house.fwd/2f + house.dr.dz*(house.right-house.left)/2f)*tileSize+tileSize/2,
							GraphAssist.CENTER, GraphAssist.CENTER);
				}
			}

		g.popAntialiasing();
		
		float[] dx = {0.8f, 0.2f, 0.8f, -0.2f};
		float[] dz = {0.2f, -1.1f, -0.2f, -1.1f};
		for(LevelConnection lc : level.info.conns) {
			float x = (lc.getLevelX() + dx[lc.d.ordinal()])*tileSize+tileSize/2;
			float z = (lc.getLevelZ() + dz[lc.d.ordinal()])*tileSize+tileSize/2;
			String name = lc.getAdj().name;
			FontMetrics fm = g.getFontMetrics();
			float w = fm.stringWidth(name);
			if(lc.d==Dir.east) x -= w;
			float h = fm.getAscent() - fm.getDescent() + 8;
			g.fillRect(x-2, z-h/2, w+4, h, colorTextBg);
			g.setColor(Color.BLACK);
			g.drawString(name, x, z, GraphAssist.LEFT, GraphAssist.CENTER);
		}

	}

}
