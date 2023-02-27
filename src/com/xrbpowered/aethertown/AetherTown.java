package com.xrbpowered.aethertown;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.aethertools.LevelMapView;
import com.xrbpowered.aethertown.render.LevelCache;
import com.xrbpowered.aethertown.render.env.DaytimeEnvironment;
import com.xrbpowered.aethertown.render.env.SkyRenderer;
import com.xrbpowered.aethertown.render.tiles.ComponentLibrary;
import com.xrbpowered.aethertown.render.tiles.TileRenderer;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.region.LevelInfo.LevelConnection;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.OffscreenBuffer;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.shader.Shader;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.CameraActor;
import com.xrbpowered.gl.scene.CameraActor.Perspective;
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.scene.WalkController;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.common.UIFpsOverlay;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIElement;

public class AetherTown extends UIClient {

	private static final boolean testFps = false;
	public static final float pawnHeight = 1.55f;

	public static final Color bgColor = new Color(0x22000000, true);
	
	private DaytimeEnvironment environment = new DaytimeEnvironment();

	public static Region region;
	public static LevelCache levelCache;
	private static Level level;

	private CameraActor camera;
	private Controller flyController, walkController;
	private Controller activeController = null;
	private boolean controllerEnabled = false;
	
	private SkyRenderer sky;
	private TileRenderer tiles;
	
	private StaticMeshActor pointActor;

	private int hoverx, hoverz;
	private String lookAtInfo = null;
	private boolean showPointer = false;
	
	private UIOffscreen uiRender;
	private UINode uiRoot;
	private UIPane uiTime, uiCompass, uiLookInfo;
	private UIPane uiLevelMap;
	private LevelMapView uiLevelMapView;

	public AetherTown() {
		super("Aether Town", 1f);
		windowedWidth = 1920;
		windowedHeight = 1080;
		if(testFps) {
			vsync = false;
			noVsyncSleep = 2;
		}
		
		Fonts.load();
		
		uiRender = new UIOffscreen(getContainer()) {
			@Override
			public void setSize(float width, float height) {
				super.setSize(width, height);
				camera.setAspectRatio(getWidth(), getHeight());
			}
			
			@Override
			protected OffscreenBuffer createOffscreenBuffer(int w, int h) {
				sky.buffer.createBuffer(w, h);
				return super.createOffscreenBuffer(w, h);
			}
			
			@Override
			public void setupResources() {
				System.out.println("Loading resources...");
				Shader.resolveIncludes = true;
				Shader.xunifyDefs = true;
				
				clearColor = environment.bgColor;
				camera = new CameraActor.Perspective().setRange(0.05f, environment.fogFar).setAspectRatio(getWidth(), getHeight());
				
				walkController = new WalkController(input).setActor(camera);
				walkController.moveSpeed = 4.8f;
				flyController = new Controller(input).setActor(camera);
				flyController.moveSpeed = 24f;
				activeController = walkController;
				
				sky = new SkyRenderer().setCamera(camera);
				tiles = new TileRenderer().setCamera(camera);
				System.out.println("Creating components...");
				ComponentLibrary.createAllComponents();
				
				pointActor = StaticMeshActor.make(FastMeshBuilder.cube(0.5f, tiles.objShader.info, null), tiles.objShader, new Texture(Color.RED));

				changeRegion();
				super.setupResources();
			}
			
			@Override
			public boolean onMouseDown(float x, float y, Button button, int mods) {
				if(button==UIElement.Button.right) {
					controllerEnabled = true;
					getBase().resetFocus();
					activeController.setMouseLook(true);
				}
				return true;
			}
			
			@Override
			public void updateTime(float dt) {
				if(!uiRender.isVisible())
					return;
				
				if(controllerEnabled) {
					activeController.update(dt);
					updateWalkY();
				}
				
				float dtDay = dt;
				if(input.isKeyDown(KeyEvent.VK_OPEN_BRACKET))
					dtDay = -100*dt;
				else if(input.isKeyDown(KeyEvent.VK_CLOSE_BRACKET))
					dtDay = 100*dt;
				sky.updateTime(dtDay);
				uiTime.repaint();
				environment.recalc(sky.sun.position);
				updateEnvironment();
				
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				super.renderBuffer(target);
				sky.render(target, levelCache.activeLevelRenderer());
				levelCache.renderAll(target, (Perspective)camera);
				
				if(showPointer) {
					tiles.objShader.setLevel(levelCache.activeLevelRenderer());
					pointActor.draw();
				}
			}
		};
		
		if(testFps)
			new UIFpsOverlay(this);
		
		uiRoot = new UINode(getContainer()) {
			@Override
			public void layout() {
				uiTime.setLocation(20, getHeight()-uiTime.getHeight()-20);
				uiCompass.setLocation(getWidth()-uiCompass.getWidth()-20, uiTime.getY());
				uiLookInfo.setLocation(getWidth()/2-uiLookInfo.getWidth()/2, uiTime.getY());
				super.layout();
			}
		};
		
		uiTime = new UIPane(uiRoot, false) {
			@Override
			protected void paintSelf(GraphAssist g) {
				clear(g, bgColor);
				g.setColor(Color.WHITE);
				g.setFont(Fonts.large);
				String s = WorldTime.getFormattedTime();
				g.drawString(s, getWidth()/2, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		};
		uiTime.setSize(100, 32);
		
		uiCompass = new UIPane(uiRoot, false) {
			@Override
			protected void paintSelf(GraphAssist g) {
				clear(g, bgColor);
				g.setColor(Color.WHITE);
				g.setFont(Fonts.large);
				String s = Dir8.values()[compass].name().toUpperCase();
				g.drawString(s, getWidth()/2, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		};
		uiCompass.setSize(100, 32);
		
		uiLookInfo = new UIPane(uiRoot, false) {
			@Override
			protected void paintSelf(GraphAssist g) {
				clear(g, bgColor);
				g.setColor(Color.WHITE);
				g.setFont(Fonts.small);
				g.drawString(lookAtInfo, getWidth()/2, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		};
		uiLookInfo.setSize(400, 32);
		uiLookInfo.setVisible(false);
		
		uiLevelMap = new UIPane(getContainer(), true) {
			@Override
			public void layout() {
				for(UIElement c : children) {
					c.setLocation(0, 0);
					c.setSize(getWidth(), getHeight());
					c.layout();
				}
			}
		};
		uiLevelMapView = new LevelMapView(uiLevelMap);
		uiLevelMap.setVisible(false);
	}
	
	private int compass = -1;
	
	private void updateWalkY() {
		boolean inside = Level.hoverInside(level.levelSize, camera.position.x, camera.position.z);
		if(!inside)
			changeLevel();

		hoverx = Level.hover(camera.position.x);
		hoverz = Level.hover(camera.position.z);
		
		String info = "";
		if(activeController==walkController) {
			Dir d = Dir.values()[(int)Math.round(-camera.rotation.y*2.0/Math.PI) & 0x03];
			Tile look = level.getAdj(hoverx, hoverz, d);
			if(look!=null)
				info = look.t.getTileInfo(look);
		}
		if(!info.equals(lookAtInfo)) {
			lookAtInfo = info;
			if(info.isEmpty()) {
				uiLookInfo.setVisible(false);
			}
			else {
				uiLookInfo.setVisible(true);
				uiLookInfo.getParent().repaint();
			}
		}
		int comp = (int)Math.round(-camera.rotation.y*4.0/Math.PI) & 0x07;
		if(comp!=compass) {
			compass = comp;
			uiCompass.repaint();
		}
		
		pointActor.position.x = camera.position.x;
		pointActor.position.z = camera.position.z;
		pointActor.position.y = level.isInside(hoverx, hoverz) ? level.gety(camera.position.x, camera.position.y-pawnHeight, camera.position.z) : 0;
		pointActor.updateTransform();
		
		if(activeController==walkController) {
			camera.position.y = pointActor.position.y+pawnHeight;
			camera.updateTransform();
		}
	}
	
	private void updateEnvironment() {
		sky.updateEnvironment(environment);
		tiles.updateEnvironment(environment);
	}
	
	private void changeLevel() {
		Level l = levelCache.findHover(camera.position.x, camera.position.z);
		if(l==null || l==level)
			return;
		LevelCache.adjustCameraPosition(level.info, l.info, camera.position);
		camera.updateTransform();
		level = levelCache.setActive(l.info);
		LevelMapView.level = level;
		
		System.out.printf("Level switched to [%d, %d]\n", level.info.x0, level.info.z0);
	}
	
	private void changeRegion() {
		level = levelCache.setActive(region.startLevel);
		LevelMapView.level = level;

		sky.stars.createStars(region.seed);
		levelCache.createRenderers(sky.buffer, tiles);

		updateEnvironment();
		
		if(level.info.conns.size()>0) {
			LevelConnection lc = level.info.conns.get(0);
			camera.position.x = lc.getLevelX()*Tile.size;
			camera.position.z = lc.getLevelZ()*Tile.size;
			camera.rotation.y = -lc.d.flip().ordinal() * (float)Math.PI/2f;
		}
		else {
			camera.position.x = level.getStartX()*Tile.size;
			camera.position.z = level.getStartZ()*Tile.size;
			camera.rotation.y = 0;
		}
		activeController = walkController;
		updateWalkY();
	}
	
	private static void printTileDebug(int hoverx, int hoverz, Tile tile) {
		System.out.printf("Rendering %d levels\n", levelCache.renderedLevels);
		System.out.printf("hover at [%d, %d]:\n", hoverx, hoverz);
		if(level.heightGuide!=null)
			System.out.printf("\theightGuide: %d\n", level.heightGuide.gety(hoverx, hoverz));
		if(level.heightLimiter!=null)
			System.out.printf("\theightLimiter: %d, %d\n", level.heightLimiter.miny[hoverx][hoverz], level.heightLimiter.maxy[hoverx][hoverz]);
		if(tile!=null) {
			System.out.printf("\t%s: basey=%d, ground=%d, d=%s\n", tile.t.getClass().getSimpleName(), tile.basey, tile.getGroundY() , tile.d.name());
			if(tile.sub!=null)
				System.out.printf("\t%s: [%d, %d]\n", tile.sub.parent.getClass().getSimpleName(), tile.sub.i, tile.sub.j);
			System.out.print("\tfenceY: ");
			for(Corner c : Corner.values())
				System.out.printf("(%s)%d; ", c.name(), tile.t.getFenceY(tile, c));
			System.out.println();
		}
		else
			System.out.println("\tnull");
		int[] yloc = level.h.yloc(hoverx, hoverz);
		System.out.print("\tyloc: ");
		for(int i=0; i<4; i++)
			System.out.printf("%d; ", yloc[i]);
		System.out.println();
	}
	
	private void showLevelMap(boolean show) {
		uiLevelMap.setVisible(show);
		if(show)
			uiLevelMapView.centerAt(level.getStartX(), level.getStartZ());
		uiRender.setVisible(!show);
		getContainer().repaint();
	}
	
	private void disableController() {
		if(controllerEnabled) {
			activeController.setMouseLook(false);
			controllerEnabled = false;
		}
	}
	
	@Override
	public void mouseDown(float x, float y, int button) {
		if(controllerEnabled && getMouseButton(button)==UIElement.Button.right)
			disableController();
		else
			super.mouseDown(x, y, button);
	}
	
	@Override
	public void keyPressed(char c, int code) {
		switch(code) {
			case KeyEvent.VK_ESCAPE:
				if(uiLevelMap.isVisible())
					showLevelMap(false);
				else
					requestExit();
				break;
			case KeyEvent.VK_TAB:
				if(controllerEnabled)
					activeController.setMouseLook(false);
				if(activeController==flyController) {
					activeController = walkController;
					updateWalkY();
				}
				else {
					activeController = flyController;
				}
				if(controllerEnabled)
					activeController.setMouseLook(true);
				break;
			case KeyEvent.VK_F1: {
					if(level.isInside(hoverx, hoverz)) {
						printTileDebug(hoverx, hoverz, level.map[hoverx][hoverz]);
					}
					else {
						System.out.println("Outside level");
					}
				}
				break;
			case KeyEvent.VK_F2:
				showPointer = !showPointer;
				System.out.println("Pointer "+(showPointer ? "on" : "off"));
				break;
			case KeyEvent.VK_M:
				disableController();
				showLevelMap(!uiLevelMap.isVisible());
				break;
			case KeyEvent.VK_ENTER: {
				/*disableController();
				long seed = System.currentTimeMillis();
				Level level = generateLevel(seed);
				changeLevel(level);*/
				break;
			}
			default:
				super.keyPressed(c, code);
		}
	}
	
	public static void generateRegion(long seed) {
		System.out.printf("Region seed: %dL\n", seed);
		region = new Region(seed);
		region.generate();
		levelCache = new LevelCache();
		// levelCache.addAll(region.displayLevels);
		levelCache.addAllAdj(region.startLevel);
	}
	
	public static void main(String[] args) {
		AssetManager.defaultAssets = new FileAssetManager("assets_src", new FileAssetManager("assets", AssetManager.defaultAssets));
		LevelNames.load();
		
		if(args.length>0)
			WorldTime.setTimeOfDay(Float.parseFloat(args[0]));

		long seed = System.currentTimeMillis();
		generateRegion(seed);

		new AetherTown().run();
	}
}
