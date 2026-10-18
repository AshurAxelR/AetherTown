package com.xrbpowered.aethertools;

import static com.xrbpowered.aethertown.render.env.ShaderEnvironment.*;
import static com.xrbpowered.zoomui.GraphAssist.align;

import java.awt.Color;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.render.env.ShaderEnvironment;
import com.xrbpowered.aethertown.state.SaveState;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIModalWindow.ResultHandler;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.UIOptionBox;
import com.xrbpowered.zoomui.std.colors.UIColorBox;
import com.xrbpowered.zoomui.std.colors.UIColorBrowser;

public class TweakAetherTown extends AetherTown {

	private static class UIWidgetPane extends UIPane {
		private int halign, valign;
		private float margins;
		public UIWidgetPane(UINode parent, float width, float height, int halign, int valign, float margins) {
			super(parent, true);
			this.halign = halign;
			this.valign = valign;
			this.margins = margins;
			setSize(width, height);
		}
		@Override
		public void layout() {
			setPosition(alignIn(getParent().getWidth(), getWidth(), margins, halign),
					alignIn(getParent().getHeight(), getHeight(), margins, valign));
			super.layout();
		}
		@Override
		protected void paintBackground(GraphAssist g) {
			g.pixelBorder(this, 1, new Color(0xeeeeee), UIButton.colorBorder);
		}
		@Override
		public boolean onMouseDown(float x, float y, MouseInfo mouse) {
			getRoot().resetFocus();
			return true;
		}
	}
	
	private static float alignIn(float parentSize, float size, float margins, int align) {
		return align(parentSize, align) + margins - align(margins*2f, align) - align(size, align);
	}
	
	private static final ShaderEnvironment[] environments = {clearDay, evening, dawn, clearNight};
	private static final String[] environmentNames = {"day", "dawn", "twilight", "night"};
	private static final int numColors = 7;

	private UINode widgets;
	private ShaderEnvironment selectedEnvironment;
	private int currentColor = 0;
	
	private UIWidgetPane colorPickerPane;
	private UIColorBrowser colorPicker;
	private UIColorBox[] colorBoxes = new UIColorBox[numColors];
	
	public TweakAetherTown(LevelInfo startLevel) {
		super(startLevel);
		
		widgets = new UINode(getContainer()) {
			@Override
			public boolean isInteractive() {
				return false;
			}
		};
		
		UIContainer wpane = new UIWidgetPane(widgets, 296, 100, GraphAssist.CENTER, GraphAssist.BOTTOM, 8);
		UIOptionBox<String> optSelTexture = new UIOptionBox<String>(wpane, environmentNames) {
			@Override
			protected void onOptionSelected(String value) {
				selectEnvironment(environments[getSelectedIndex()]);
			}
		};
		optSelTexture.setSize(280, optSelTexture.getHeight());
		optSelTexture.setPosition(8, 8);
		for(int i=0; i<numColors; i++) {
			int colorIndex = i;
			UIColorBox cbox = new UIColorBox(wpane) {
				@Override
				public boolean onMouseDown(float x, float y, MouseInfo mouse) {
					if(mouse.isButtonDown(MouseInfo.LEFT)) {
						currentColor = colorIndex;
						color = getEnvColor(selectedEnvironment, colorIndex);
						colorPicker.setColor(color);
						colorPickerPane.setVisible(true);
						getRoot().repaint();
					}
					return true;
				}
			};
			cbox.setSize(40, 40);
			cbox.setPosition(8+i*cbox.getWidth(), optSelTexture.getHeight() + 16);
			colorBoxes[i] = cbox;
		}

		colorPickerPane = new UIWidgetPane(widgets, UIColorBrowser.defaultWidth,
				UIColorBrowser.defaultHeightAlpha, GraphAssist.CENTER, GraphAssist.BOTTOM, 116);
		ResultHandler<Color> colorResult = new ResultHandler<Color>() {
			@Override
			public void onResult(Color result) {
				colorBoxes[currentColor].color = result;
				setEnvColor(selectedEnvironment, currentColor, result);
				colorPickerPane.setVisible(false);
				getContainer().resetFocus();
				getContainer().repaint();
			}
			@Override
			public void onCancel() {
				colorPickerPane.setVisible(false);
				getContainer().resetFocus();
				getContainer().repaint();
			}
		};
		colorPicker = new UIColorBrowser(colorPickerPane, true, colorResult) {
			@Override
			public void layout() {
				setSize(getParent().getWidth(), getParent().getHeight());
				super.layout();
			}
		};
		colorPickerPane.setVisible(false);
		
		selectEnvironment(environments[0]);
	}
	
	private void selectEnvironment(ShaderEnvironment env) {
		selectedEnvironment = env;
		for(int i=0; i<numColors; i++) {
			colorBoxes[i].color = getEnvColor(env, i);
		}
	}
	
	private static Color getEnvColor(ShaderEnvironment env, int index) {
		switch(index) {
			case 0:
				return env.lightColor;
			case 1:
				return env.midColor;
			case 2:
				return env.shadowColor;
			case 3:
				return env.cloudColor;
			case 4:
				return env.horizonColor;
			case 5:
				return env.zenithColor;
			case 6:
				return env.lightSkyColor;
			default:
				throw new IllegalArgumentException("index "+index);
		}
	}
	
	private static void setEnvColor(ShaderEnvironment env, int index, Color color) {
		switch(index) {
			case 0:
				env.lightColor = color;
				break;
			case 1:
				env.midColor = color;
				break;
			case 2:
				env.shadowColor = color;
				break;
			case 3:
				env.cloudColor = color;
				break;
			case 4:
				env.horizonColor = color;
				break;
			case 5:
				env.zenithColor = color;
				break;
			case 6:
				env.lightSkyColor = color;
				break;
			default:
				throw new IllegalArgumentException("index "+index);
		}
	}
	
	@Override
	public String getDebugInfo() {
		String s = String.format("%.1f fps", getFps());
		s += "\n";

		float c = (float)Math.toDegrees(Math.atan(sky.sun.position.y));
		s += String.format("Sun: %.1f°", c);
		return s;
	}
	
	@Override
	public void keyPressed(char c, int code) {
		if(getContainer().getFocus()!=null) {
			if(getContainer().onKeyPressed(c, code, input.getInputInfo()))
				return;
		}
		super.keyPressed(c, code);
	}
	
	public static void main(String[] args) {
		System.out.printf("TweakAetherTown version %s\n", version);
		
		AssetManager.defaultAssets = new FileAssetManager("assets", AssetManager.defaultAssets);
		AssetManager.defaultAssets = new FileAssetManager("assets_src", AssetManager.defaultAssets);
		LevelNames.load();
		
		parseParams(args);
		settings.saveOnExit = false;
		settings.autosave = 0;
		
		SaveState save = new SaveState();
		if(!settings.startNew)
			save = save.load();
		
		LevelInfo startLevel = generateRegion(save);
		new TweakAetherTown(startLevel).run();
	}

}
