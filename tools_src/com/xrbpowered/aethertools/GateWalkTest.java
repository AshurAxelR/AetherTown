package com.xrbpowered.aethertools;

import java.awt.Color;
import java.awt.Cursor;
import java.util.Random;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.gl.res.asset.AssetManager;
import com.xrbpowered.gl.res.asset.FileAssetManager;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class GateWalkTest extends UIElement {

	public static final int base = 7*12; // 84
	public static final long maxSeed = 208215748530929664L; // 84^9
	
	public static final String[] planetNames = {
		"Sun", "Moon", "Mars", "Mercury", "Jupiter", "Venus", "Saturn"
	};
	public static final String[] zodiacNames = {
		"Aries", "Taurus", "Gemini", "Cancer",
		"Leo", "Virgo", "Libra", "Scorpio",
		"Sagittarius", "Capricorn", "Aquarius", "Pisces"
	};
	public static final String[] keyChars = {
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "X", "Y"
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
	public static final String[] digitNames = {
		"West Gate:", "East Gate:", "I.", "II.", "III.", "IV.", "V.", "VI.", "VII.", "Today:"
	};
	
	public static final Color colorBg = new Color(0xfafafa);
	public static final Color colorFg = new Color(0x555555);
	public static final int box = 32;

	public final long salt;
	public final int innerSalt;
	
	public final int[] timeMap;
	public long seed;
	public int time;
	
	public static int[] createTimeMap(long salt) {
		Shuffle sh = new Shuffle(base);
		int[] out = new int[base];
		Random random = new Random(salt);
		for(int i=0; i<base; i++)
			out[i] = sh.next(random);
		return out;
	}
	
	public static long hashSeed(long seed, long add) {
		// Multiply by Knuth's Random (Linear congruential generator) and add offset
		seed *= seed * 6364136223846793005L + 1442695040888963407L;
		seed += add;
		return seed;
	}

	public static int dmask(int x, int m, int base) {
		return (m - x + base) % base;
	}

	public static int dmask(int x, int m) {
		return dmask(x%7, m%7, 7) + dmask(x/7, m/7, 12)*7;
	}
	
	public static long dmaskAll(long x, long m, int digits) {
		long res = 0;
		long mul = 1;
		for(int i=0; i<digits; i++) {
			res = res + mul*dmask((int)(x%base), (int)(m%base));
			x /= base;
			m /= base;
			mul *= base;
		}
		return res;
	}
	
	public static int zmask(int x, int base) {
		return (x*2) % base;
	}
	
	public static int zmask(int x) {
		return zmask(x%7, 7) + zmask(x/7, 12)*7;
	}
	
	public static long zmaskAll(long x, int digits) {
		long res = 0;
		long mul = 1;
		for(int i=0; i<digits; i++) {
			res = res + mul*zmask((int)(x%base));
			x /= base;
			mul *= base;
		}
		return res;
	}
	
	public static long zmaskSet(long x, int[] m) {
		long res = 0;
		long mul = 1;
		for(int i=0; i<m.length; i++) {
			int v = zmask((int)(x%base));
			if(m[i]>0)
				v = m[i]-1;
			res = res + mul*v;
			x /= base;
			mul *= base;
		}
		return res;
	}
	
	public static int westGate(long seed) {
		return (int)(seed % (long)base);
	}

	public static int eastGate(long seed) {
		return (int)((seed / (long) base) % (long)base);
	}
	
	public static int makeGateKey(int outerWest, int inner, int outerEast) {
		return outerEast*base*base + outerWest*base + inner;
	}
	
	public static long makeSeedMask(long salt, int gateKey, long seed) {
		long gateSeed = hashSeed(hashSeed(salt, gateKey), gateKey);
		// return gateSeed & (maxSeed-1L);
		Random random = new Random(gateSeed);
		int[] m = new int[7];
		int d0 = random.nextInt(7);
		m[d0] = random.nextInt(base)+1;
		int d1;
		do {
			d1 = random.nextInt(7);
		} while(d0==d1);
		m[d1] = random.nextInt(base)+1;
		return zmaskSet(seed, m);
	}
	
	public static long makeSeed(long salt, long prevSeed, int gateKey, int eastGate, int westGate) {
		final long base2 = base*base;
		long seed = prevSeed/base2;
		long mask = makeSeedMask(salt, gateKey, seed);
		seed = dmaskAll(seed, mask, 7);
		seed *= base2;
		seed += eastGate*base + westGate;
		return seed;
	}

	public int outerWest(int outerEast, int inner, int time) {
		return dmask(dmask(outerEast, timeMap[time]), dmask(inner, innerSalt));
	}

	public int outerEast(int outerWest, int inner, int time) {
		return dmask(dmask(outerWest, dmask(inner, innerSalt)), timeMap[time]);
	}

	public long travelWest() {
		int e = eastGate(seed);
		int w = westGate(seed);
		int o = outerWest(e, w, time);
		int gateKey = makeGateKey(o, w, e);
		return makeSeed(salt, seed, gateKey, w, o);
	}
	
	public long travelEast() {
		int e = eastGate(seed);
		int w = westGate(seed);
		int o = outerEast(w, e, time);
		int gateKey = makeGateKey(w, e, o);
		return makeSeed(salt, seed, gateKey, o, e);
	}
	
	int hover = -1;
	
	public GateWalkTest(UIContainer parent) {
		super(parent);
		Random random = new Random();
		this.salt = random.nextLong();
		this.seed = random.nextLong() & (maxSeed-1L);
		this.time = random.nextInt(base);
		
		this.timeMap = createTimeMap(salt);
		random = new Random(salt);
		this.innerSalt = random.nextInt(base);
	}

	private void paintBox(GraphAssist g, int d, int row, int m) {
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
			s = keyChars[m];
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
	
	@Override
	public void paint(GraphAssist g) {
		g.fill(this, colorBg);
		g.resetStroke();
		g.setFont(Fonts.large);
		long num = seed;
		for(int d=0; d<10; d++) {
			int n = (d==9) ? time : (int)(num % (long)base);
			int n0 = n%7;
			int n1 = n/7;
			String s = String.format("%s %s in %s", digitNames[d], planetNames[n0], zodiacNames[n1]);
			if(d==9) {
				if(n0==0)
					n1 = (n1+11)%12;
				n0 = (n0+6) % 7;
				n1 = (n1+3) % 12;
				s = String.format("%s of %s (%s)", dayNames[n0], monthNames[n1], s);
			}
			paintBox(g, d, 0, n0);
			paintBox(g, d, 1, n1);
			if(d==hover) {
				g.setColor(colorFg);
				g.setFont(Fonts.small);
				g.drawString(s, 64, 128+box/2, GraphAssist.LEFT, GraphAssist.CENTER);
				g.setFont(Fonts.large);
			}
			num /= (long)base;
		}
		g.setColor(colorFg);
		g.drawString("W", 64+box/2, 64-box/2, GraphAssist.CENTER, GraphAssist.CENTER);
		g.drawString("E", 64+box+box/2, 64-box/2, GraphAssist.CENTER, GraphAssist.CENTER);
		g.setFont(Fonts.small);
		g.drawString("Today", 64+9*box+box/2+16, 64-box/2, GraphAssist.CENTER, GraphAssist.CENTER);
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
			seed = travelWest();
		else if(hover==1)
			seed = travelEast();
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
