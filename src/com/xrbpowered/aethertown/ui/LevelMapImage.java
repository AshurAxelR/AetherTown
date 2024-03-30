package com.xrbpowered.aethertown.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TunnelTileTemplate.TunnelTile;
import com.xrbpowered.aethertown.world.gen.plot.ChurchGenerator;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseRole;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.tiles.Alcove;
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

public class LevelMapImage extends ImageGenerator {

	public static final int tileSize = 16;
	private static final int benchMarkerSize = 6;
	private static final int gridStep = 16;
	
	private static final Color colorStreetBorder = new Color(0x999999);
	private static final Color colorPark = new Color(0xddeebb);
	private static final Color colorWater = new Color(0x5294a5);
	private static final Color colorPavillion = new Color(0xc4c1b8);
	private static final Color colorPavillionBorder = new Color(0x777777);

	public final Level level;
	
	public LevelMapImage(Level level) {
		this.level = level;
	}
	
	@Override
	public BufferedImage create() {
		return create(
			level.levelSize*tileSize + margin*2,
			level.levelSize*tileSize + margin*2 + marginTop + lineSize*countHouseInfoLines(level)
		);
	}

	@Override
	protected void paint(GraphAssist g, int w, int h) {
		g.fillRect(0, 0, w, h, colorMargin);
		g.setColor(colorMarginText);
		g.setFont(Fonts.large);
		float y = (margin+marginTop)/2;
		g.drawString(level.info.name, w/2, y, GraphAssist.CENTER, GraphAssist.CENTER);
		
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
			g.setColor(colorMarginText);
			g.drawString(Integer.toString(house.index+1), x+24, y, GraphAssist.RIGHT, GraphAssist.CENTER);
			g.drawString(house.role.title, x+64, y, GraphAssist.LEFT, GraphAssist.CENTER);
			g.setColor(colorMarginTextDim);
			g.drawString(String.format("%s%d", getXBinName(house.startToken.x/gridStep), house.startToken.z/gridStep+1),
					x+40, y, GraphAssist.CENTER, GraphAssist.CENTER);
			
			g.fillRect(x+56, y-lineSize/2, 2, lineSize, house.role.previewColor);
			
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
		g.fillRect(10, 10, 350, 55, colorInfoBg);
		int x = 20;
		int y = 35;
		int h = 18;
		g.setFont(Fonts.small);
		g.setColor(colorInfoText);
		g.drawString(String.format("[%d, %d] %s", hoverx, hoverz, level.info.name), x, y, GraphAssist.LEFT, GraphAssist.BOTTOM); y += h;
		Tile tile = level.map[hoverx][hoverz];
		if(tile!=null && (tile.t==HouseT.template || tile.t==ChurchT.template)) {
			String info = null;
			if(tile.sub.parent instanceof HouseGenerator)
				info = ((HouseGenerator) tile.sub.parent).getRoleTitles();
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
			g.line(x*tileSize, 0, x*tileSize, level.levelSize*tileSize-1);
			float tx = x*tileSize+gridStep*tileSize/2;
			String s = getXBinName(x/gridStep);
			g.drawString(s, tx, 24, GraphAssist.CENTER, GraphAssist.CENTER);
			g.drawString(s, tx, level.levelSize*tileSize-24, GraphAssist.CENTER, GraphAssist.CENTER);
		}
		for(int z=0; z<level.levelSize; z+=gridStep) {
			g.line(0, z*tileSize, level.levelSize*tileSize-1, z*tileSize);
			float tz = z*tileSize+gridStep*tileSize/2;
			String s = Integer.toString(z/gridStep+1);
			g.drawString(s, 24, tz, GraphAssist.CENTER, GraphAssist.CENTER);
			g.drawString(s, level.levelSize*tileSize-24, tz, GraphAssist.CENTER, GraphAssist.CENTER);
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
						if(adj==null || !Street.isAnyPath(adj.t) || tile.hasFence(d) || adj.hasFence(d.flip()))
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
					g.setColor(colorPavillionBorder);
					g.drawRect(x*tileSize, z*tileSize, tileSize-1, tileSize-1);
				}
				else if(tile.t instanceof Bench && ((Bench) tile.t).type!=BenchType.empty) {
					g.setColor(colorInfoText);
					g.fillRect(x*tileSize+tileSize/2-benchMarkerSize/2, z*tileSize+tileSize/2-benchMarkerSize/2,
							benchMarkerSize, benchMarkerSize);
				}
				else if(tile.t==Alcove.template) {
					g.setColor(colorWater);
					g.fillRect(x*tileSize+tileSize/2-benchMarkerSize/2, z*tileSize+tileSize/2-benchMarkerSize/2,
							benchMarkerSize, benchMarkerSize);
				}
			}

		g.setFont(Fonts.small);
		for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				Tile tile = level.map[x][z];
				if(tile!=null && tile.sub!=null && tile.sub.i==0 && tile.sub.j==0) {
					String s = null;
					if(tile.sub.parent instanceof HouseGenerator) {
						HouseGenerator house = (HouseGenerator) tile.sub.parent;
						s = Integer.toString(house.index+1);
						g.setColor(Color.WHITE);
					}
					else if(tile.sub.parent instanceof ChurchGenerator) {
						ChurchGenerator church = (ChurchGenerator) tile.sub.parent;
						s = String.format("St. %s", church.name);
						g.setColor(Color.BLACK);
					}
					if(s!=null) {
						PlotGenerator plot = tile.sub.parent;
						float tx = (x + plot.d.dx*plot.fwd/2f + plot.dr.dx*(plot.right-plot.left)/2f)*tileSize+tileSize/2;
						float tz = (z + plot.d.dz*plot.fwd/2f + plot.dr.dz*(plot.right-plot.left)/2f)*tileSize+tileSize/2;
						g.drawString(s, tx, tz, GraphAssist.CENTER, GraphAssist.CENTER);
					}
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
			g.fillRect(x-2, z-h/2, w+4, h, colorInfoBg);
			g.setColor(Color.BLACK);
			g.drawString(name, x, z, GraphAssist.LEFT, GraphAssist.CENTER);
		}

	}

}
