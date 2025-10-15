package com.xrbpowered.aethertools.ui;

import java.util.Random;

import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.text.UITextBox;

public class UISeedControls extends UIContainer {

	public static final int buttonWidth = 40;

	private class UIBaseToggle extends UIToggleButton {
		public final int baseOption;
		
		public UIBaseToggle(String label, int base) {
			super(UISeedControls.this, label);
			this.baseOption = base;
			setSize(buttonWidth, defaultHeight);
		}
		
		@Override
		public boolean isToggled() {
			return base==this.baseOption;
		}
		
		@Override
		public void toggle() {
			base = this.baseOption;
		}
	}
	
	public final UITextBox txtSeed;
	public final UIToggleButton hex;
	public final UIToggleButton dec;
	public final UIToggleButton oct;
	public final UIToggleButton legacy;
	public final UIButton load;
	public final UIButton random;

	private Random rand = new Random();
	private int base = 10;
	
	public UISeedControls(UIContainer parent) {
		super(parent);
		
		float x = 8;
		txtSeed = new UITextBox(this);
		txtSeed.setSize(240, txtSeed.getHeight());
		txtSeed.setPosition(x, 8);
		x += txtSeed.getWidth();
		
		hex = new UIBaseToggle("Hex", 16);
		hex.setPosition(x, 8);
		x += buttonWidth;
		dec = new UIBaseToggle("Dec", 10);
		dec.setSize(buttonWidth, UIButton.defaultHeight);
		dec.setPosition(x, 8);
		x += buttonWidth;
		oct = new UIBaseToggle("Oct", 8);
		oct.setSize(buttonWidth, UIButton.defaultHeight);
		oct.setPosition(x, 8);
		x += buttonWidth;
		legacy = new UIToggleButton(this, "Legacy");
		legacy.setPosition(x, 8);
		x += legacy.getWidth();
		
		load = new UIButton(this, "Load") {
			@Override
			public void onAction() {
				long seed;
				try {
					String s = txtSeed.editor.getText();
					if(s.endsWith("L"))
						s = s.substring(0, s.length()-1);
					s = s.replaceAll("\\s", "");
					if(s.isEmpty())
						return;
					seed = Long.parseUnsignedLong(s, base);
				}
				catch(NumberFormatException e) {
					return;
				}
				apply(seed);
			}
		};
		load.setPosition(x, 8);
		x += load.getWidth() + 8;

		random = new UIButton(this, "Random") {
			@Override
			public void onAction() {
				base = 10;
				long seed = rand.nextLong() & 0x0fff_ffff_ffff_ffffL;
				txtSeed.editor.setText(Long.toString(seed));
				apply(seed);
			}
		};
		random.setPosition(x, 8);
		x += random.getWidth();

		setSize(x+8, UIButton.defaultHeight+16);
	}
	
	public void apply(long seed) {
		// does nothing, override
	}

}
