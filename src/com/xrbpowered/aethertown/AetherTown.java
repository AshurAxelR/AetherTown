package com.xrbpowered.aethertown;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.env.DaytimeEnvironment;
import com.xrbpowered.aethertown.render.env.SkyRenderer;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.client.UIClient;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.res.buffer.OffscreenBuffer;
import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
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
import com.xrbpowered.zoomui.std.UIButton;

public class AetherTown extends UIClient {

	private static final boolean testFps = false;
	public static final float pawnHeight = 1.6f;

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
	private boolean showPointer = false;
	
	private Font uiFont = UIButton.font;
	private UINode uiRoot;
	private UITexture uiMinimap;
	private UIPane uiTime;

	public AetherTown() {
		super("Aether Town", 1f);
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
		windowedWidth = 1920;
		windowedHeight = 1080;
		if(testFps) {
			vsync = false;
			noVsyncSleep = 2;
		}
		
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
				
				renderer = new LevelRenderer(level, sky.buffer).setCamera(camera);
				Template.createAllComponents(); // FIXME should not depend on creating level renderer first
				renderer.createLevelGeometry();

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
				sky.render(target);
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
				uiMinimap.setLocation(getWidth()-uiMinimap.getWidth()-20, getHeight()-uiMinimap.getHeight()-20);
				uiTime.setLocation(20, getHeight()-uiTime.getHeight()-20);
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
			private Font font = uiFont.deriveFont(28f);
			@Override
			protected void paintSelf(GraphAssist g) {
				clear(g);
				g.setColor(Color.WHITE);
				g.setFont(font);
				String s = WorldTime.getFormattedTime();
				g.drawString(s, 0, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
			}
		};
		uiTime.setSize(120, 32);
	}
	
	private void updateWalkY() {
		hoverx = (int)((camera.position.x+Tile.size/2)/Tile.size);
		hoverz = (int)((camera.position.z+Tile.size/2)/Tile.size);
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
						Tile tile = level.map[hoverx][hoverz];
						System.out.printf("hover at [%d, %d]:\n", hoverx, hoverz);
						if(level.heightLimiter!=null)
							System.out.printf("\theightLimiter: %d, %d\n", level.heightLimiter.miny[hoverx][hoverz], level.heightLimiter.maxy[hoverx][hoverz]);
						if(tile!=null)
							System.out.printf("\tbasey=%d\n", tile.basey);
						else
							System.out.println("\tnull");
						int[] yloc = level.h.yloc(hoverx, hoverz);
						System.out.print("yloc: ");
						for(int i=0; i<4; i++)
							System.out.printf("%d; ", yloc[i]);
						System.out.println();
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
		seed = System.currentTimeMillis();
		System.out.println("Generating... "+seed);
		level = new Level(128);
		level.generate(new Random(seed));
		System.gc();
		System.out.println("Done.");

		new AetherTown().run();
	}
}
