package com.xrbpowered.aethertools;

import static com.xrbpowered.zoomui.MouseInfo.LEFT;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.state.SaveState;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.utils.ColorBlend;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.region.LevelSettlementType;
import com.xrbpowered.aethertown.world.tiles.Hill;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIPanView;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class HeightMapView extends UIElement {

	public static final int tileSize = 4;
	
	public static final Color colorBg = new Color(0xf5f5f5);
	public static final Color colorGrid = new Color(0x11ffffff, true);
	public static final Color colorBorder = new Color(0x77ffffff, true);
	public static final ColorBlend heightColor = new ColorBlend(
			new Color[] {
					new Color(0x005500),
					new Color(0x307d15),
					new Color(0x7da547),
					new Color(0xc0e755),
					new Color(0xf5ee45),
					new Color(0xf5cd45),
			},
			new float[] {-100, -50, -20, 40, 70, 100}
		);
	
	private static boolean showGuide = false;
	private int offsX, offsZ;
	
	private HashMap<LevelInfo, BufferedImage> imgCache = new HashMap<>();
	
	public HeightMapView(UIContainer parent) {
		super(new UIPanView(parent) {
			@Override
			protected void paintBackground(GraphAssist g) {
				g.fill(this, colorBg);
			}
			@Override
			protected void paintChildren(GraphAssist g) {
				super.paintChildren(g);
				paintInfo(g);
			}
		});
		UIPanView view = (UIPanView) getParent();
		view.setSize(getRoot().getWindow().getClientWidth(), getRoot().getWindow().getClientHeight());

		offsX = AetherTown.levelCache.activeLevel().info.x0*LevelInfo.baseSize;
		offsZ = AetherTown.levelCache.activeLevel().info.z0*LevelInfo.baseSize;

		// centerAt(Region.sizez/2, Region.sizez/2);
	}
	
	private static void paintInfo(GraphAssist g) {
		// print info
		
		g.pushAntialiasing(false);
		for(int y=-100; y<=100; y++) {
			int x = (y+100)*tileSize+20;
			g.setColor(heightColor.get(y));
			g.graph.fillRect(x, 20, tileSize, 20);
		}
		g.setColor(Color.BLACK);
		g.graph.drawRect(20, 20, 201*tileSize-1, 19);
		g.popAntialiasing();
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
	
	private BufferedImage createLevelImage(Level level) {
		BufferedImage img = new BufferedImage(level.levelSize*tileSize, level.levelSize*tileSize, BufferedImage.TYPE_INT_RGB);
		GraphAssist g = new GraphAssist((Graphics2D) img.getGraphics());
		for(int x=0; x<level.levelSize; x++)
			for(int z=0; z<level.levelSize; z++) {
				Tile tile = level.map[x][z];
				if(tile==null)
					continue;
				Color c;
				if(showGuide)
					c = heightColor.get(level.heightGuide.gety(x, z));
				else
					c = heightColor.get(tile.basey);
				if(!(tile.t==Hill.template || tile.t instanceof Park)) {
					float b = 0.3f*c.getRed()/255f + 0.59f*c.getGreen()/255f + 0.11f*c.getBlue()/255f;
					c = new Color(b, b, b);
				}
				g.setColor(c);
				g.fillRect(x*tileSize, z*tileSize, tileSize, tileSize);
			}
		g.setColor(colorGrid);
		int d = LevelInfo.baseSize/2;
		for(int x=0; x<level.levelSize; x+=d)
			for(int z=0; z<level.levelSize; z+=d) {
				int sx = x*tileSize;
				int sz = z*tileSize;
				g.line(sx, sz, sx, sz+d*tileSize);
				g.line(sx, sz, sx+d*tileSize, sz);
			}
		g.setColor(colorBorder);
		g.drawRect(1, 1, level.levelSize*tileSize-3, level.levelSize*tileSize-3);
		g.setColor(Color.WHITE);
		g.drawString(level.info.terrain.name, 10, 20);
		return img;
	}
	
	@Override
	public void paint(GraphAssist g) {
		g.pushAntialiasing(false);
		for(Level level : AetherTown.levelCache.list()) {
			if(level!=null && level.info!=null) {
				BufferedImage img = imgCache.get(level.info);
				if(img==null) {
					img = createLevelImage(level);
					imgCache.put(level.info, img);
				}
				g.graph.drawImage(img, (level.info.x0*LevelInfo.baseSize-offsX)*tileSize, (level.info.z0*LevelInfo.baseSize-offsZ)*tileSize, null);
			}
		}
		g.popAntialiasing();
	}
	
	@Override
	public boolean onMouseDown(float x, float y, MouseInfo mouse) {
		if(mouse.eventButton==LEFT) {
			LevelInfo level = AetherTown.region.getLevel(
					((int)x/tileSize+offsX)/LevelInfo.baseSize,
					((int)y/tileSize+offsZ)/LevelInfo.baseSize
				);
			System.out.printf("Selected: [%d, %d]\n", level.x0, level.z0);
			AetherTown.levelCache.addAllAdj(level, false, false);
			AetherTown.levelCache.setActive(level, true);
			repaint();
			return true;
		}
		else
			return super.onMouseDown(x, y, mouse);
	}

	public static void main(String[] args) {
		for(LevelSettlementType settlement : LevelSettlementType.values()) {
			System.out.printf("(%d) %s margin: ", settlement.ordinal(), settlement.title);
			for(int s=1; s<=3; s++)
				System.out.printf("size(%d)=%d; ", s, settlement.getStreetMargin(s*LevelInfo.baseSize, true));
			System.out.println();
		}
		
		AssetManager.defaultAssets = new FileAssetManager("assets_src", new FileAssetManager("assets", AssetManager.defaultAssets));
		LevelNames.load();
		Fonts.load();

		AetherTown.settings.load();
		SaveState save = new SaveState();
		AetherTown.generateRegion(save);
		AetherTown.levelCache.setActive(AetherTown.region.startLevel, true);
		
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("AetherTown height map", 1280, 960);
		new HeightMapView(frame.getContainer());
		frame.show();
	}
	
}
