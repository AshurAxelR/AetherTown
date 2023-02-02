package com.xrbpowered.aethertools;

import static com.xrbpowered.aethertown.world.stars.GateNetwork.base;

import java.awt.Color;
import java.awt.Cursor;
import java.util.Random;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.world.stars.GateNetwork;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class GateWalkTest extends UIElement {

	public static final String[] planetNames = {
		"Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"
	};
	public static final String[] zodiacNames = {
		"Aries", "Taurus", "Gemini", "Cancer",
		"Leo", "Virgo", "Libra", "Scorpio",
		"Sagittarius", "Capricorn", "Aquarius", "Pisces"
	};
	public static final String[] postcodeChars = {
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "X", "Y"
	};
	public static final String[] postcodeDigitNames = {
		"West Gate:", "East Gate:", "I.", "II.", "III.", "IV.", "V.", "VI.", "VII.", "Today:"
	};
	public static final String[] dayNames = {
			"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
	};
	public static final String[] monthNames = {
		"January", "February", "March", 
		"April", "May", "June",
		"July", "August", "September",
		"October", "November", "December", 
	};
	
	public static final Color colorBg = new Color(0xfafafa);
	public static final Color colorFg = new Color(0x555555);
	public static final int box = 32;

	public final GateNetwork gates;
	public long seed;
	public int time;
	
	int hover = -1;
	
	public GateWalkTest(UIContainer parent) {
		super(parent);
		Random random = new Random();
		this.gates = new GateNetwork(random.nextLong());
		this.seed = gates.randomTravel(random);
		this.time = random.nextInt(base);
	}

	private static void paintBox(GraphAssist g, int d, int row, int m, int hover) {
		int x = d*box+64;
		int w = box;
		String s;
		if(d==9) {
			w = box*2;
			if(row==0)
				s = Integer.toString(m+1);
			else
				s = String.format("%02d", m+1);
		}
		else if(row==1)
			s = postcodeChars[m];
		else
			s = Integer.toString(m);
		if(d<2 || d==9)
			g.setColor(d==hover ? new Color(0xeeffdd) : Color.WHITE);
		else
			g.setColor(new Color(0xf3f3f3));
		g.fillRect(x, row*box+64, w, box);
		g.setColor(colorFg);
		g.drawRect(x, row*box+64, w, box);
		g.drawString(s, x+w/2, row*box+64+box/2, GraphAssist.CENTER, GraphAssist.CENTER);
	}
	
	public static void paintPostcode(GraphAssist g, long seed, int time, int hover) {
		g.resetStroke();
		g.setFont(Fonts.large);
		for(int d=0; d<10; d++) {
			int n = (d==9) ? time : GateNetwork.getDigit(seed, d);
			int n0 = n%7;
			int n1 = n/7;
			String s = String.format("%s in %s", planetNames[n0], zodiacNames[n1]);
			if(d==9) {
				if(n0==0)
					n1 = (n1+11)%12;
				n0 = (n0+6) % 7;
				n1 = (n1+3) % 12;
				s = String.format("%s of %s (%s)", dayNames[n0], monthNames[n1], s);
			}
			else {
				s = String.format("%s %s", postcodeDigitNames[d], s);
			}
			paintBox(g, d, 0, n0, hover);
			paintBox(g, d, 1, n1, hover);
			if(d==hover) {
				g.setColor(colorFg);
				g.setFont(Fonts.small);
				g.drawString(s, 64, 128+box/2, GraphAssist.LEFT, GraphAssist.CENTER);
				g.setFont(Fonts.large);
			}
		}
		g.setColor(colorFg);
		g.drawString("W", 64+box/2, 64-box/2, GraphAssist.CENTER, GraphAssist.CENTER);
		g.drawString("E", 64+box+box/2, 64-box/2, GraphAssist.CENTER, GraphAssist.CENTER);
		g.setFont(Fonts.small);
		g.drawString("Today", 64+9*box+box/2+16, 64-box/2, GraphAssist.CENTER, GraphAssist.CENTER);
	}
	
	@Override
	public void paint(GraphAssist g) {
		g.fill(this, colorBg);
		paintPostcode(g, seed, time, hover);
	}

	@Override
	public void onMouseMoved(float x, float y, int mods) {
		if(y>=64 && y<=128)
			hover = (int)(x-64)/box;
		else
			hover = -1;
		if(hover==10)
			hover = 9;
		if(hover==0 || hover==1 || hover==9)
			getBase().getWindow().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else
			getBase().getWindow().setCursor(Cursor.getDefaultCursor());
		repaint();
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		if(hover==0)
			seed = gates.travelWest(seed, time);
		else if(hover==1)
			seed = gates.travelEast(seed, time);
		else if(hover==9)
			time = (time+1) % base;
		repaint();
		return true;
	}
	
	@Override
	public boolean onMouseScroll(float x, float y, float delta, int mods) {
		if(hover==9) {
			time = (time+base+(int)delta) % base;
			repaint();
		}
		return true;
	}
	
	public static void main(String[] args) {
		AssetManager.defaultAssets = new FileAssetManager("assets_src", new FileAssetManager("assets", AssetManager.defaultAssets));
		Fonts.load();
		
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("AetherTown: Gate walk", 480, 192, false);
		new GateWalkTest(frame.getContainer());
		frame.show();
	}

}
