package com.xrbpowered.aethertown;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.env.Seasons;
import com.xrbpowered.aethertown.render.env.ShaderEnvironment;
import com.xrbpowered.aethertown.render.env.SkyRenderer;
import com.xrbpowered.aethertown.render.tiles.LightTileComponent;
import com.xrbpowered.aethertown.render.tiles.LightTileObjectShader;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectShader;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Template;
import com.xrbpowered.aethertown.world.Tile;
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
import com.xrbpowered.gl.ui.pane.UITexture;
import com.xrbpowered.zoomui.UIElement;

public class AetherTown extends UIClient {
	
	public static final int season = Seasons.winter;
	private static int activeEnvironment = 0;
	private ShaderEnvironment environment = ShaderEnvironment.environments[activeEnvironment];

	private static final boolean testFps = false;
	
	public static final float pawnHeight = 1.6f;

	private CameraActor camera;
	private Controller flyController, walkController;
	private Controller activeController = null;
	private boolean controllerEnabled = false;
	
	private TileObjectShader shader;
	private LightTileObjectShader lightShader;
	private StaticMeshActor[] terrainActors;
	
	private SkyRenderer sky;
	private StaticMeshActor pointActor;
	private ObjectShader objShader;
	
	private static Level level;
	private int hoverx, hoverz;
	private boolean showPointer = false;
	
	private UINode uiRoot;
	private UITexture uiMinimap;

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
				sky.createBuffer(w, h);
				return super.createOffscreenBuffer(w, h);
			}
			
			@Override
			public void setupResources() {
				clearColor = environment.bgColor;
				camera = new CameraActor.Perspective().setRange(0.05f, environment.fogFar).setAspectRatio(getWidth(), getHeight());
				camera.position.x = Level.levelSize/2*Tile.size;
				camera.position.z = Level.levelSize/2*Tile.size;
				camera.rotation.y = (float)(-Math.PI*0.75);
				camera.updateTransform();
				
				walkController = new WalkController(input).setActor(camera);
				walkController.moveSpeed = 4.8f;
				flyController = new Controller(input).setActor(camera);
				flyController.moveSpeed = 24f;
				activeController = walkController;
				
				sky = new SkyRenderer();
				sky.getShader().setCamera(camera);
				
				objShader = new ObjectShader(sky);
				objShader.setCamera(camera);

				pointActor = StaticMeshActor.make(FastMeshBuilder.cube(0.5f, objShader.info, null), objShader, new Texture(Color.RED));
				
				shader = new TileObjectShader(sky);
				shader.setCamera(camera);
				lightShader = new LightTileObjectShader(sky);
				lightShader.setCamera(camera);

				TileComponent.createRenderer(shader);
				LightTileComponent.createRenderer(lightShader);
				Template.createAllComponents();
				
				TileComponent.renderer.startCreateInstances();
				LightTileComponent.renderer.startCreateInstances();
				TerrainBuilder terrainBuilder = new TerrainBuilder(level);
				level.createGeometry(terrainBuilder);
				terrainActors = terrainBuilder.createActors(objShader);
				TileComponent.renderer.finishCreateInstances();
				LightTileComponent.renderer.finishCreateInstances();

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
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				super.renderBuffer(target);
				sky.render(target);
				shader.bindSkyTexture();
				TileComponent.renderer.drawInstances();
				lightShader.bindSkyTexture();
				LightTileComponent.renderer.drawInstances();
				objShader.bindSkyTexture();
				for(StaticMeshActor actor : terrainActors)
					actor.draw();
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
				super.layout();
			}
		};
		
		uiMinimap = new UITexture(uiRoot) {
			@Override
			public void setupResources() {
				BufferedImage img = new BufferedImage(Level.levelSize*2, Level.levelSize*2, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D) img.getGraphics();
				level.drawMinimap(g, 2);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, img.getWidth()-1, img.getHeight()-1);
				setTexture(new Texture(img, false, false));
			}
		};
		uiMinimap.setSize(Level.levelSize*2, Level.levelSize*2);
	}
	
	private void updateWalkY() {
		hoverx = (int)((camera.position.x+Tile.size/2)/Tile.size);
		hoverz = (int)((camera.position.z+Tile.size/2)/Tile.size);
		pointActor.position.x = camera.position.x;
		pointActor.position.z = camera.position.z;
		pointActor.position.y = Level.isInside(hoverx, hoverz) ? level.gety(camera.position.x, camera.position.z) : 0;
		pointActor.updateTransform();
		
		if(activeController==walkController) {
			camera.position.y = pointActor.position.y+pawnHeight;
			camera.updateTransform();
		}
	}
	
	private void updateEnvironment() {
		environment.updateShader(sky.getShader());
		environment.updateShader(objShader);
		environment.updateShader(shader);
		environment.updateShader(lightShader);
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
					if(Level.isInside(hoverx, hoverz)) {
						Tile tile = level.map[hoverx][hoverz];
						System.out.printf("hover at [%d, %d]:\n", hoverx, hoverz);
						System.out.printf("\theightLimiter: %d, %d\n", level.heightLimiter.miny[hoverx][hoverz], level.heightLimiter.maxy[hoverx][hoverz]);
						if(tile!=null)
							System.out.printf("\tbasey=%d\n", tile.basey);
						else
							System.out.println("\tnull");
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
			case KeyEvent.VK_T:
				activeEnvironment = (activeEnvironment+1) % ShaderEnvironment.environments.length;
				environment = ShaderEnvironment.environments[activeEnvironment];
				updateEnvironment();
				break;
			default:
				super.keyPressed(c, code);
		}
	}
	
	public static void main(String[] args) {
		long seed = System.currentTimeMillis();
		System.out.println("Generating... "+seed);
		level = new Level();
		level.generate(new Random(seed));
		System.gc();
		System.out.println("Done.");

		new AetherTown().run();
	}
}
