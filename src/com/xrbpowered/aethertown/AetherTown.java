package com.xrbpowered.aethertown;

import static com.xrbpowered.zoomui.MouseInfo.RIGHT;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.render.LevelCache;
import com.xrbpowered.aethertown.render.Screenshot;
import com.xrbpowered.aethertown.render.TerrainChunkBuilder.TerrainMeshActor;
import com.xrbpowered.aethertown.render.env.DaytimeEnvironment;
import com.xrbpowered.aethertown.render.env.SkyRenderer;
import com.xrbpowered.aethertown.render.tiles.ComponentLibrary;
import com.xrbpowered.aethertown.render.tiles.TileRenderer;
import com.xrbpowered.aethertown.state.Player;
import com.xrbpowered.aethertown.state.RegionVisits;
import com.xrbpowered.aethertown.state.SaveState;
import com.xrbpowered.aethertown.ui.BookmarkPane;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.ui.ToastPane;
import com.xrbpowered.aethertown.ui.dialogs.DialogContainer;
import com.xrbpowered.aethertown.ui.dialogs.InventoryDialog;
import com.xrbpowered.aethertown.ui.dialogs.LevelMapDialog;
import com.xrbpowered.aethertown.ui.dialogs.RegionMapDialog;
import com.xrbpowered.aethertown.utils.AbstractConfig;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.utils.ParseParams;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TunnelTileTemplate.TunnelTile;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelInfo;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.region.PortalSystem;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.region.RegionCache;
import com.xrbpowered.aethertown.world.region.RegionMode;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.aethertown.world.tiles.Hill.HillTile;
import com.xrbpowered.gl.client.ClientInput;
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
import com.xrbpowered.gl.ui.pane.UIOffscreen;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.MouseInfo;

public class AetherTown extends UIClient {

	public static final Color bgColor = new Color(0x22000000, true);
	public static final float pawnHeight = 1.55f;
	
	private static final boolean useDebugAssets = false; 
	
	public static class ClientConfig extends AbstractConfig {
		public boolean fullscreen = false;
		public int windowedWidth = 1920;
		public int windowedHeight = 1080;
		public float uiScaling = 1;
		public int renderScaling = 1;
		public int fov = 75;
		public boolean vsync = true;
		public int noVsyncSleep = 2;
		public boolean showFps = false;
		public float mouseSensitivity = 0.002f;
		public float walkSpeed = 4.8f; // meters per second. Human: 1.35
		public float flySpeed = 24f;
		
		public float startTime = 0.25f;
		public float startSeason = 0.75f; // 0f - spring equinox, 0.25f - summer solstice, 0.5f - autumn equinox, 0.75f - winter solstice
		public float timeSpeed = 20f;
		public float timeSpeedUp = 100f;
		
		public float screenshotScale = 1f;
		
		public RegionMode regionMode = RegionMode.defaultMode;
		public long regionSeed = -1L;
		public boolean residentialLighting = true;
		public boolean revealRegion = false;
		public boolean markAdjVisited = false;
		
		public boolean allowFlying = false;
		public boolean allowTimeControl = false;
		public boolean allowBookmaks = false;
		public boolean nosave = false;
		
		public ClientConfig() {
			super("./client.cfg", true);
		}
		
		@Override
		public ClientConfig reset() {
			return new ClientConfig();
		}
		
		@Override
		protected Object parseValue(String name, String value, Class<?> type) {
			if(name.equals("startTime"))
				return WorldTime.parseTime(value);
			else if(name.equals("startSeason"))
				return WorldTime.parseDate(value);
			else if(name.equals("regionMode"))
				return RegionMode.parseValue(value);
			else
				return super.parseValue(name, value, type);
		}
		
		@Override
		protected String formatValue(String name, Object obj) {
			if(name.equals("startTime"))
				return WorldTime.getFormattedTime((Float) obj);
			else if(name.equals("regionMode"))
				return ((RegionMode) obj).formatValue();
			else
				return super.formatValue(name, obj);
		}
	}
	
	public static ClientConfig settings = new ClientConfig();
	
	private DaytimeEnvironment environment = new DaytimeEnvironment();

	public static AetherTown aether;
	public static Player player = new Player();
	public static RegionCache regionCache;
	public static Region region;
	public static LevelCache levelCache;
	public static Level level = null;
	public static LevelInfo levelInfo = null;
	public static DialogContainer ui = null;

	private CameraActor camera;
	private Controller flyController, walkController;
	private Controller activeController = null;
	private boolean controllerEnabled = false;
	private boolean autoWalk = false;
	
	private SkyRenderer sky;
	private TileRenderer tiles;
	
	private StaticMeshActor pointActor;

	private int hoverx, hoverz;
	private Tile lookAtTile = null;
	private String lookAtInfo = null;
	private TileAction lookAtAction = null;
	private boolean showPointer = false;
	
	private UIOffscreen uiRender;
	private UINode uiHud;
	private UIPane uiTime, uiLookInfo, uiActionInfo, uiDebugInfo;
	private ToastPane uiToast;
	
	public AetherTown(final LevelInfo startLevel) {
		super("Aether Town", settings.uiScaling);
		aether = this;
		
		fullscreen = settings.fullscreen;
		windowedWidth = settings.windowedWidth;
		windowedHeight = settings.windowedHeight;
		vsync = settings.vsync;
		noVsyncSleep = settings.noVsyncSleep;
		
		uiRender = new UIOffscreen(getContainer(), settings.renderScaling) {
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

				Fonts.load();
				
				Shader.resolveIncludes = true;
				Shader.xunifyDefs = true;
				
				clearColor = environment.bgColor;
				camera = new CameraActor.Perspective().setFov(settings.fov).setRange(0.05f, environment.fogFar).setAspectRatio(getWidth(), getHeight());
				
				walkController = new WalkController(input) {
					@Override
					protected void updateMove(org.joml.Vector3f move) {
						super.updateMove(move);
						if(autoWalk) {
							if(move.length()>0)
								autoWalk = false;
							else
								move.set(0, 0, 1);
						}
					}
				};
				walkController.setActor(camera);
				walkController.moveSpeed = settings.walkSpeed;
				walkController.mouseSensitivity = settings.mouseSensitivity;
				flyController = new Controller(input).setActor(camera);
				flyController.moveSpeed = settings.flySpeed;
				flyController.mouseSensitivity = settings.mouseSensitivity;
				activeController = walkController;
				
				sky = new SkyRenderer(bufferScale).setCamera(camera);
				sky.stars.updateStars(region.seed);
				// updateEnvironment();
				
				tiles = new TileRenderer().setCamera(camera);
				System.out.println("Creating components...");
				ComponentLibrary.createAllComponents();

				pointActor = new TerrainMeshActor();
				pointActor.setMesh(FastMeshBuilder.cube(0.5f, tiles.objShader.info, null));
				pointActor.setShader(tiles.objShader);
				pointActor.setTextures(new Texture[] {new Texture(Color.RED)});

				activateLevel(startLevel);
				player.initCamera(camera, level, false);
				updateWalkY();

				// setup child resources
				super.setupResources();
			}
			
			@Override
			public boolean onMouseDown(float x, float y, MouseInfo mouse) {
				if(mouse.eventButton==RIGHT) {
					controllerEnabled = true;
					getRoot().resetFocus();
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
				
				boolean blockUpdatePortals = false;
				if(settings.allowTimeControl) {
					if(input.isKeyDown(KeyEvent.VK_MINUS)) {
						WorldTime.shiftTimeOfYear(-dt);
						blockUpdatePortals = true;
					}
					else if(input.isKeyDown(KeyEvent.VK_EQUALS)) {
						WorldTime.shiftTimeOfYear(dt);
						blockUpdatePortals = true;
					}
				}
				
				float dtDay = dt;
				if(settings.allowTimeControl && input.isKeyDown(KeyEvent.VK_OPEN_BRACKET))
					dtDay = -settings.timeSpeedUp*dt;
				if(settings.allowTimeControl && input.isKeyDown(KeyEvent.VK_CLOSE_BRACKET)) // TODO time-forwarding allowed by actions
					dtDay = settings.timeSpeedUp*dt; 
				sky.updateTime(dtDay);
				
				uiTime.repaint();
				environment.recalc(sky.sun.position);
				updateEnvironment();
				if(!blockUpdatePortals)
					regionCache.portals.updateTime();
				
				super.updateTime(dt);
			}
			
			@Override
			protected void renderBuffer(RenderTarget target) {
				super.renderBuffer(target);
				if(levelCache.isMissingRenderers())
					levelCache.createRenderers(sky.buffer, tiles);
				sky.render(target, levelCache.activeLevelRenderer());
				levelCache.renderAll(target, (Perspective)camera, region);
				
				if(showPointer) {
					tiles.objShader.level = levelCache.activeLevelRenderer();
					pointActor.draw();
				}
			}
		};
		
		ui = new DialogContainer(getContainer());
		
		uiHud = new UINode(getContainer()) {
			@Override
			public boolean isInside(float px, float py) {
				return false;
			}
			@Override
			public void layout() {
				uiTime.setPosition(20, getHeight()-uiTime.getHeight()-20);
				uiLookInfo.setPosition(getWidth()/2-uiLookInfo.getWidth()/2, uiTime.getY());
				uiActionInfo.setPosition(getWidth()/2-uiActionInfo.getWidth()/2, uiTime.getY()-uiActionInfo.getHeight()-40);
				uiToast.setPosition(20, getHeight()/2-uiToast.getHeight()/2);
				super.layout();
			}
		};
		
		if(settings.showFps) {
			uiDebugInfo = new UIPane(uiHud, false) {
				@Override
				protected void paintBackground(GraphAssist g) {
					clear(g, bgColor);
					g.setColor(Color.WHITE);
					g.setFont(Fonts.small);
					paintDebugInfo(g);
				}
				@Override
				public void updateTime(float dt) {
					repaint();
				}
			};
			uiDebugInfo.setSize(180, 50);
			uiDebugInfo.setPosition(20, 20);
		}
		
		uiTime = new UIPane(uiHud, false) {
			@Override
			protected void paintBackground(GraphAssist g) {
				clear(g, bgColor);
				g.setColor(Color.WHITE);
				g.setFont(Fonts.large);
				g.drawString(WorldTime.getFormattedTime(), 50, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
				g.setFont(Fonts.small);
				g.drawString(String.format("DAY %d, %s", WorldTime.getDay()+1, WorldTime.getFormattedDate()), 100, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
			}
		};
		uiTime.setSize(220, 32);
		
		uiLookInfo = new UIPane(uiHud, false) {
			@Override
			public boolean isVisible() {
				return super.isVisible() && ui.isEmpty();
			}
			@Override
			protected void paintBackground(GraphAssist g) {
				clear(g, bgColor);
				g.setColor(Color.WHITE);
				g.setFont(Fonts.small);
				g.drawString(lookAtInfo, getWidth()/2, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		};
		uiLookInfo.setSize(600, 32);
		uiLookInfo.setVisible(false);

		uiActionInfo = new UIPane(uiHud, false) {
			@Override
			public boolean isVisible() {
				return super.isVisible() && ui.isEmpty();
			}
			@Override
			protected void paintBackground(GraphAssist g) {
				clear(g, bgColor);
				if(lookAtAction!=null) {
					g.setColor(Color.WHITE);
					g.setFont(Fonts.small);
					g.drawString(String.format("[ E ]   %s", lookAtAction.getName()),
							getWidth()/2, getHeight()/2, GraphAssist.CENTER, GraphAssist.CENTER);
				}
			}
		};
		uiActionInfo.setSize(200, 32);
		uiActionInfo.setVisible(false);

		uiToast = new ToastPane(uiHud);
	}
	
	private int compass = -1;
	
	private void updateWalkY() {
		boolean inside = Level.hoverInside(level.levelSize, camera.position.x, camera.position.z);
		if(!inside)
			checkLevelChange();

		hoverx = Level.hover(camera.position.x);
		hoverz = Level.hover(camera.position.z);
		
		String info = "";
		TileAction action = null;
		if(activeController==walkController) {
			Dir d = Dir.values()[(int)Math.round(-camera.rotation.y*2.0/Math.PI) & 0x03];
			lookAtTile = level.getAdj(hoverx, hoverz, d);
			if(lookAtTile!=null) {
				info = lookAtTile.t.getTileInfo(lookAtTile);
				action = lookAtTile.t.getTileAction(lookAtTile);
			}
		}
		boolean hudRepaint = false;
		if(!info.equals(lookAtInfo)) { // FIXME can change by time and need repaint
			lookAtInfo = info;
			if(info.isEmpty())
				uiLookInfo.setVisible(false);
			else {
				uiLookInfo.setVisible(true);
				hudRepaint = true;
			}
		}
		if(action!=lookAtAction) { // FIXME can change by time and need repaint
			lookAtAction = action;
			if(lookAtAction==null)
				uiActionInfo.setVisible(false);
			else {
				uiActionInfo.setVisible(true);
				hudRepaint = true;
			}
		}
		if(hudRepaint)
			uiHud.repaint();
		
		int comp = (int)Math.round(-camera.rotation.y*4.0/Math.PI) & 0x07;
		if(comp!=compass) {
			compass = comp;
		}
		
		pointActor.position.x = camera.position.x;
		pointActor.position.z = camera.position.z;
		pointActor.position.y = level.isInside(hoverx, hoverz) ? level.gety(camera.position.x, camera.position.y-pawnHeight, camera.position.z) : 0;
		pointActor.updateTransform();
		
		if(activeController==walkController) {
			camera.position.y = pointActor.position.y+pawnHeight;
			camera.updateTransform();
		}
		
		if(levelInfo.isPortal())
			regionCache.portals.updateWalk(hoverx, hoverz);
	}
	
	private void updateEnvironment() {
		sky.updateEnvironment(environment);
		tiles.updateEnvironment(environment);
	}
	
	public void teleportTo(LevelInfo info) {
		activateLevel(info);
		player.initCamera(camera, level, true);
		activeController = walkController;
		updateWalkY();
	}
	
	public void activatePortal() {
		if(levelInfo.isPortal()) {
			LevelInfo other = regionCache.portals.otherLevel;
			activateLevel(other);
		}
	}
	
	private void activateLevel(LevelInfo info) {
		if(region!=info.region) {
			region = info.region;
			sky.stars.updateStars(region.seed);
			System.out.printf("\nREGION switched to *%04dL\n\n", region.seed%10000L);
		}
		levelCache.addAllAdj(info, true);
		level = levelCache.setActive(info, true);
		levelInfo = info;
		regionCache.portals.updateLevel();
		// levelCache.createRenderers(sky.buffer, tiles);
		
		RegionVisits.visit(info);
		
		System.out.printf("Level switched to *%04dL:[%d, %d]\n", info.region.seed%10000L, info.x0, info.z0);
		System.out.printf("Level cache storage: %d blocks\n", levelCache.getStoredBlocks());
	}
	
	private void checkLevelChange() {
		Level l = levelCache.findHover(region, camera.position.x, camera.position.z);
		if(l==null || l==level)
			return;
		LevelCache.adjustCameraPosition(level.info, l.info, camera.position);
		camera.updateTransform();
		activateLevel(l.info);
	}

	private static void printTileDebug(int hoverx, int hoverz, Tile tile) {
		System.out.printf("Rendering %d levels\n", levelCache.renderedLevels);
		System.out.printf("hover at [%d, %d]:\n", hoverx, hoverz);
		if(level.heightGuide!=null)
			System.out.printf("\theightGuide: %d (h=%d)\n", level.heightGuide.gety(hoverx, hoverz), level.h.y[hoverx][hoverz]);
		if(level.heightLimiter!=null)
			System.out.printf("\theightLimiter: %d, %d\n", level.heightLimiter.miny[hoverx][hoverz], level.heightLimiter.maxy[hoverx][hoverz]);
		if(tile!=null) {
			System.out.printf("\t%s: basey=%d, ground=%d, block=%d, d=%s\n", tile.t.getClass().getSimpleName(), tile.basey, tile.getGroundY(), tile.t.getBlockY(tile), tile.d.name());
			if(tile.sub!=null)
				System.out.printf("\t%s: [%d, %d]\n", tile.sub.parent.getClass().getSimpleName(), tile.sub.i, tile.sub.j);
			System.out.print("\tfenceY: ");
			for(Corner c : Corner.values())
				System.out.printf("(%s)%d; ", c.name(), tile.t.getFenceY(tile, c));
			System.out.println();
			if(tile instanceof TunnelTile) {
				TunnelInfo tunnel = ((TunnelTile) tile).tunnel;
				if(tunnel==null)
					System.out.println("\tno tunnel");
				else
					System.out.printf("\ttunnel(%s) rank=%d, depth=%d, basey=%d, topy=%d\n", tunnel.type.name(), tunnel.rank, tunnel.depth, tunnel.basey, tunnel.topy);
			}
		}
		else
			System.out.println("\tnull");
		int[] yloc = level.h.yloc(hoverx, hoverz);
		System.out.print("\tyloc: ");
		for(int i=0; i<4; i++)
			System.out.printf("%d; ", yloc[i]);
		System.out.println();
		
		if(tile!=null && tile instanceof HillTile) {
			HillTile ht = (HillTile) tile;
			System.out.printf("miny=%d, maxDelta=%d\n\n", MathUtils.min(yloc), ht.maxDelta);
		}
	}
	
	private void paintDebugInfo(GraphAssist g) {
		float y = 10;
		String s = String.format("%.1f fps", getFps());
		Color c = environment.lightColor;
		float illum = c.getRed()/255f + c.getGreen()/255f + c.getBlue()/255f;
		s += String.format(" illum:%.2f", illum);
		y = g.drawString(s, 10, y, GraphAssist.LEFT, GraphAssist.TOP);
		
		// float a = 90f - (float)Math.toDegrees(Math.acos(sky.sun.position.dot(0, 1, 0, 0)));
		// y = g.drawString(String.format("Sun angle: %.1f\u00b0", a), 10, y, GraphAssist.LEFT, GraphAssist.TOP);
		s = String.format("[%d, %d] %s", hoverx, hoverz, Dir8.values()[compass].name().toUpperCase());
		if(level!=null && level.isInside(hoverx, hoverz) && level.map[hoverx][hoverz]!=null)
			s += String.format(" y:%d", level.map[hoverx][hoverz].basey);
		y = g.drawString(s, 10, y, GraphAssist.LEFT, GraphAssist.TOP);
	}
	
	public void disableController() {
		if(controllerEnabled) {
			activeController.setMouseLook(false);
			autoWalk = false;
			controllerEnabled = false;
		}
	}
	
	@Override
	public void mouseDown(float x, float y, int button) {
		if(controllerEnabled && ClientInput.getMouseButton(button)==RIGHT)
			disableController();
		else if(controllerEnabled && activeController==walkController && button==3)
			autoWalk = !autoWalk;
		else
			super.mouseDown(x, y, button);
	}
	
	@Override
	public void keyPressed(char c, int code) {
		if(ui.onKeyPressed(c, code, input.getInputInfo()))
			return;
		
		switch(code) {
			case KeyEvent.VK_ESCAPE:
				requestExit();
				break;
			case KeyEvent.VK_BACK_SPACE:
				camera.rotation.y += Math.PI;
				break;
			case KeyEvent.VK_TAB:
				if(settings.allowFlying && controllerEnabled) {
					autoWalk = false;
					activeController.setMouseLook(false);
					if(activeController==flyController) {
						activeController = walkController;
						updateWalkY();
						showToast("Flying OFF");
					}
					else {
						activeController = flyController;
						flyController.moveSpeed = settings.flySpeed;
						showToast("Flying ON");
					}
					activeController.setMouseLook(true);
				}
				break;
			case KeyEvent.VK_F1: {
					if(level!=null && level.isInside(hoverx, hoverz)) {
						printTileDebug(hoverx, hoverz, level.map[hoverx][hoverz]);
					}
					else {
						System.out.println("Outside level");
					}
				}
				break;
			case KeyEvent.VK_F2:
				showPointer = !showPointer;
				showToast("Pointer "+(showPointer ? "ON" : "OFF"));
				break;
			case KeyEvent.VK_F3:
				if(activeController==flyController) {
					if(flyController.moveSpeed==settings.flySpeed)
						flyController.moveSpeed = settings.flySpeed * 5f;
					else
						flyController.moveSpeed = settings.flySpeed;
				}
				break;
			case KeyEvent.VK_F5:
				saveState();
				showToast("Saved");
				break;
			case KeyEvent.VK_F10:
				Screenshot.screenshot.make(uiRender.pane.getBuffer());
				break;
			case KeyEvent.VK_B:
				if(level!=null && settings.allowBookmaks) {
					new BookmarkPane(ui);
					ui.reveal();
				}
				break;
			case KeyEvent.VK_N:
				if(level!=null)
					RegionMapDialog.show(level);
				break;
			case KeyEvent.VK_M:
				if(level!=null)
					LevelMapDialog.show(level, true);
				break;
			case KeyEvent.VK_E:
				if(lookAtAction!=null)
					lookAtAction.performAt(lookAtTile);
				break;
			case KeyEvent.VK_Q:
				InventoryDialog.show();
				break;
			default:
				break;
		}
	}
	
	public static void showToast(String msg) {
		aether.uiToast.queue.push(msg);
		System.out.printf("> %s\n", msg);
	}
	
	public static void saveState() {
		new SaveState().update().save();
	}
	
	@Override
	public void destroyWindow() {
		if(!settings.nosave)
			saveState();
		super.destroyWindow();
	}
	
	public static LevelInfo generateRegion(SaveState save) {
		WorldTime.setTime(save.startSeason, save.day, save.time);
		player.fromSave(save);
		
		long seed = PortalSystem.getRegionSeed(save.regionSeed);
		System.out.printf("Region seed: %dL\n", seed);
		regionCache = new RegionCache(save.regionMode);
		region = regionCache.get(seed);

		levelCache = new LevelCache();
		LevelInfo info = save.getLevel(region);
		levelCache.addAllAdj(info, true);
		return info;
	}
	
	public static void main(String[] args) {
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
		if(useDebugAssets)
			AssetManager.defaultAssets = new FileAssetManager("assets_src", AssetManager.defaultAssets);
		
		LevelNames.load();
		settings.load();
		// settings.save();

		ParseParams params = new ParseParams();
		params.addFlagParam("-nosave", x -> { settings.nosave = x; }, "ignore save file");
		params.parseParams(args);

		SaveState save = new SaveState();
		if(!settings.nosave)
			save.load();
		
		LevelInfo startLevel = generateRegion(save);
		new AetherTown(startLevel).run();
	}
}
