package com.xrbpowered.aethertools;

import java.awt.Color;
import java.awt.Rectangle;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TunnelTileTemplate.TunnelTile;
import com.xrbpowered.aethertown.world.gen.plot.ChurchGenerator;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;
import com.xrbpowered.aethertown.world.gen.plot.PlotGenerator;
import com.xrbpowered.aethertown.world.region.HouseRole;
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
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIPanView;

public class LevelMapView extends UIElement {

	public static final int tileSize = 16;
	private static final int benchMarkerSize = 6;
	
	public static final Color colorBg = new Color(0xeeeeee);
	public static final Color colorStreetBorder = new Color(0x999999);
	public static final Color colorTextBg = new Color(0xbbffffff, true);
	public static final Color colorText = new Color(0x777777);
	
	
	public static final Color colorPark = new Color(0xddeebb);
	public static final Color colorWater = new Color(0x5294a5);
	public static final Color colorPavillion = new Color(0xc4c1b8);
	public static final Color colorDefault = new Color(0xfafafa);

	public static Level level;
	
	private static int hoverx, hoverz;
	
	public LevelMapView(UIContainer parent) {
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
		if(level!=null)
			centerAt(level.getStartX(), level.getStartZ());
	}
	
	private static void paintInfo(GraphAssist g) {
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
				info = ((HouseGenerator) tile.sub.parent).getRoleTitle();
			else if(tile.sub.parent instanceof ChurchGenerator)
				info = tile.t.getTileInfo(tile);
			g.setFont(Fonts.smallBold);
			g.setColor(Color.BLACK);
			g.drawString(info, x, y, GraphAssist.LEFT, GraphAssist.BOTTOM); y += h;
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
	
	private void drawBorder(GraphAssist g, int x, int z, Dir d) {
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
	
	@Override
	public void paint(GraphAssist g) {
		g.pushAntialiasing(false);
		
		for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				Tile tile = level.map[x][z];
				if(tile==null)
					continue;
				Color c = colorDefault;
				Color addc = null;
				if(tile instanceof TunnelTile && ((TunnelTile) tile).tunnel!=null)
					c = colorStreetBorder;
				else if(Street.isAnyPath(tile.t))
					c = Street.streetColor;
				else if(tile.t instanceof Park)
					c = colorPark;
				else if(tile.t instanceof Plaza) {
					if(tile.t==Plaza.tunnelSideTemplate)
						c = colorDefault;
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
				
				g.setColor(c);
				g.fillRect(x*tileSize, z*tileSize, tileSize, tileSize);
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
	}
	
	@Override
	public void onMouseMoved(float x, float y, int mods) {
		hoverx = (int)(x/tileSize);
		hoverz = (int)(y/tileSize);
		repaint();
	}

}
