package com.xrbpowered.aethertown;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.env.DaytimeEnvironment;
import com.xrbpowered.aethertown.render.env.SkyRenderer;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.LevelNames;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
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
import com.xrbpowered.gl.scene.Controller;
import com.xrbpowered.gl.scene.StaticMeshActor;
import com.xrbpowered.gl.scene.WalkController;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.common.UIFpsOverlay;
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.gl.ui.pane.UITexture;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIElement;

public class AetherTown extends UIClient {

	private static final boolean testFps = false;
	public static final float pawnHeight = 1.6f;

	public static final Color bgColor = new Color(0x22000000, true);
	
	// private static int activeEnvironment = 0;
	private DaytimeEnvironment environment = new DaytimeEnvironment(); // ShaderEnvironment.environments[activeEnvironment];

	public static long seed;
	private static Level level;

	private CameraActor camera;
	private Controller flyController, walkController;
	private Controller activeController = null;
	private boolean controllerEnabled = false;
	
	private SkyRenderer sky;
	private LevelRenderer renderer;
	
	private StaticMeshActor pointActor;

	private int hoverx, hoverz;
	private String lookAtInfo = null;
	private boolean showPointer = false;
	
	private UINode uiRoot;
	private UITexture uiMinimap;
	private UIPane uiTime, uiCompass, uiLookInfo;

	public static Font fontSmall;
	public static Font fontLarge;
	
	private static void initFonts() {
		try {
			fontSmall = AssetManager.defaultAssets.loadFont("fonts/RobotoCondensed-Regular.ttf").deriveFont(16f);
			fontLarge = AssetManager.defaultAssets.loadFont("fonts/RobotoCondensed-Bold.ttf").deriveFont(24f);
		}
		catch(IOException e) {
			e.printStackTrace();
			fontSmall = null;
			fontLarge = null;
		}
	}
	
	public AetherTown() {
		super("Aether Town", 1f);
		windowedWidth = 1920;
		windowedHeight = 1080;
		if(testFps) {
			vsync = false;
			noVsyncSleep = 2;
		}
		
		initFonts();
		
		new UIOffscreen(getContainer()) {
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
				camera.position.x = level.getStartX()*Tile.size;
				camera.position.z = level.getStartZ()*Tile.size;
				camera.rotation.y = 0;
				camera.updateTransform();
				
				walkController = new WalkController(input).setActor(camera);
				walkController.moveSpeed = 4.8f;
				flyController = new Controller(input).setActor(camera);
				flyController.moveSpeed = 24f;
				activeController = walkController;
				
				sky = new SkyRenderer().setCamera(camera);
				sky.stars.createStars(seed);
				
				System.out.println("Creating components...");
				renderer = new LevelRenderer(level, sky.buffer).setCamera(camera);
				Template.createAllComponents(); // FIXME should not depend on creating level renderer first
				System.out.println("Building geometry...");
				renderer.createLevelGeometry();
				System.out.println("Done.");

				pointActor = StaticMeshActor.make(FastMeshBuilder.cube(0.5f, renderer.objShader.info, null), renderer.objShader, new Texture(Color.RED));

				updateEnvironment();
				updateWalkY();
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
				if(controllerEnabled) {
					if(input.isMouseDown(1)) {
						activeController.update(dt);
						updateWalkY();
					}
					else {
						activeController.setMouseLook(false);
						controllerEnabled = false;
					}
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
				sky.render(target, renderer);
				renderer.render(target);
				
				if(showPointer)
					pointActor.draw();
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
				uiMinimap.setLocation(getWidth()-uiMinimap.getWidth()-20, uiTime.getY()-uiMinimap.getHeight()-20);
				super.layout();
			}
		};
		
		uiMinimap = new UITexture(uiRoot) {
			@Override
			public void setupResources() {
				BufferedImage img = new BufferedImage(level.levelSize*2, level.levelSize*2, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D) img.getGraphics();
				level.drawMinimap(g, 2);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);
				setTexture(new Texture(img, false, false));
			}
		};
		uiMinimap.setSize(level.levelSize*2, level.levelSize*2);
		uiMinimap.setVisible(false);
		
		uiTime = new UIPane(uiRoot, false) {
			@Override
			protected void paintSelf(GraphAssist g) {
				clear(g, bgColor);
				g.setColor(Color.WHITE);
				g.setFont(fontLarge);
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
				g.setFont(fontLarge);
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
				g.setFont(fontSmall);
				g.drawString(lookAtInfo, getWidth()/2, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		};
		uiLookInfo.setSize(400, 32);
		uiLookInfo.setVisible(false);
	}
	
	private int compass = -1;
	
	private void updateWalkY() {
		hoverx = (int)((camera.position.x+Tile.size/2)/Tile.size);
		hoverz = (int)((camera.position.z+Tile.size/2)/Tile.size);
		
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
		pointActor.position.y = level.isInside(hoverx, hoverz) ? level.gety(camera.position.x, camera.position.z) : 0;
		pointActor.updateTransform();
		
		if(activeController==walkController) {
			camera.position.y = pointActor.position.y+pawnHeight;
			camera.updateTransform();
		}
	}
	
	private void updateEnvironment() {
		sky.updateEnvironment(environment);
		renderer.updateEnvironment(environment);
	}
	
	private static void printTileDebug(int hoverx, int hoverz, Tile tile) {
		System.out.printf("hover at [%d, %d]:\n", hoverx, hoverz);
		if(level.heightLimiter!=null)
			System.out.printf("\theightLimiter: %d, %d\n", level.heightLimiter.miny[hoverx][hoverz], level.heightLimiter.maxy[hoverx][hoverz]);
		if(tile!=null)
			System.out.printf("\tbasey=%d, ground=%d, d=%s\n", tile.basey, tile.getGroundY() , tile.d.name());
		else
			System.out.println("\tnull");
		int[] yloc = level.h.yloc(hoverx, hoverz);
		System.out.print("\tyloc: ");
		for(int i=0; i<4; i++)
			System.out.printf("%d; ", yloc[i]);
		System.out.println();
		System.out.print("\tfenceY: ");
		for(Corner c : Corner.values())
			System.out.printf("(%s)%d; ", c.name(), tile.t.getFenceY(tile, c));
		System.out.println();
	}
	
	@Override
	public void keyPressed(char c, int code) {
		switch(code) {
			case KeyEvent.VK_ESCAPE:
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
			/*case KeyEvent.VK_T:
				activeEnvironment = (activeEnvironment+1) % ShaderEnvironment.environments.length;
				environment = ShaderEnvironment.environments[activeEnvironment];
				updateEnvironment();
				break;*/
			case KeyEvent.VK_M:
				uiMinimap.setVisible(!uiMinimap.isVisible());
				uiMinimap.repaint();
				break;
			default:
				super.keyPressed(c, code);
		}
	}
	
	public static void main(String[] args) {
		AssetManager.defaultAssets = new FileAssetManager("assets_src", new FileAssetManager("assets", AssetManager.defaultAssets));
		LevelNames.load();

		seed = System.currentTimeMillis();
		System.out.printf("Generating... %dL\n", seed);
		level = new Level(128);
		level.generate(new Random(seed));
		System.gc();
		System.out.println("Done.");

		new AetherTown().run();
	}
}
