package com.xrbpowered.aethertools;

import java.awt.Color;
import java.awt.Rectangle;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.HouseGenerator;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.tiles.Street;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIPanView;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class LevelMapView extends UIElement {

	public static final int tileSize = 16;
	
	public static final Color colorBg = new Color(0xf5f5f5);
	public static final Color colorLabelBg = new Color(0x22000000, true);
	public static final Color colorLabel = Color.WHITE;

	public static Level level;
	
	public LevelMapView(UIContainer parent) {
		super(new UIPanView(parent) {
			@Override
			protected void paintSelf(GraphAssist g) {
				g.fill(this, colorBg);
			}
			@Override
			protected void paintChildren(GraphAssist g) {
				super.paintChildren(g);
				// render general UI text
			}
		});
		((UIPanView) getParent()).pan(
				-level.levelSize*tileSize/2+getBase().getWindow().getClientWidth()/2,
				-level.levelSize*tileSize/2+getBase().getWindow().getClientHeight()/2);
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
				Color c = (tile==null) ? null : tile.t.minimapColor;
				if(tile!=null && tile.sub!=null && (tile.sub.parent instanceof HouseGenerator)) {
					c = ((HouseGenerator) tile.sub.parent).role.previewColor;
				}
				if(c==null)
					continue;
				
				g.setColor(c);
				g.fillRect(x*tileSize, z*tileSize, tileSize, tileSize);
				if(tile==null)
					continue;
				if(Street.isAnyStreet(tile.t)) {
					g.setColor(new Color(0x999999));
					for(Dir d : Dir.values()) {
						Tile adj = tile.getAdj(d);
						if(!(adj!=null && Street.isAnyStreet(adj.t)))
							drawBorder(g, x, z, d);
					}
				}
				else if(tile.sub!=null && (tile.sub.parent instanceof HouseGenerator)) {
					HouseGenerator house = (HouseGenerator) tile.sub.parent;
					g.setColor(Color.WHITE);
					for(Dir d : Dir.values()) {
						Tile adj = tile.getAdj(d);
						if(!(adj!=null && adj.sub!=null && adj.sub.parent==house))
							drawBorder(g, x, z, d);
					}
				}
			}

		g.setFont(Fonts.small);
		for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				Tile tile = level.map[x][z];
				if(tile!=null && tile.sub!=null && (tile.sub.parent instanceof HouseGenerator) && tile.sub.i==0 && tile.sub.j==0) {
					HouseGenerator house = (HouseGenerator) tile.sub.parent;
					g.setColor(colorLabel);
					g.drawString(Integer.toString(house.index+1),
							(x + house.d.dx*house.fwd/2f + house.dr.dx*(house.right-house.left)/2f)*tileSize+tileSize/2,
							(z + house.d.dz*house.fwd/2f + house.dr.dz*(house.right-house.left)/2f)*tileSize+tileSize/2,
							GraphAssist.CENTER, GraphAssist.CENTER);
				}
			}

		g.popAntialiasing();
	}

	public static void main(String[] args) {
		AssetManager.defaultAssets = new FileAssetManager("assets_src", new FileAssetManager("assets", AssetManager.defaultAssets));
		LevelNames.load();
		Fonts.load();

		level = AetherTown.generateLevel(System.currentTimeMillis());
		
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("AetherTown level map", 1920, 1080);
		new LevelMapView(frame.getContainer());
		frame.show();
	}

}
