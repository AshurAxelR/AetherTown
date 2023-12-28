package com.xrbpowered.aethertown.ui;

import static com.xrbpowered.aethertown.AetherTown.settings;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.xrbpowered.zoomui.GraphAssist;

public abstract class ImageGenerator {

	public static final int margin = 16;
	public static final int marginTop = 64;
	public static final int lineSize = 24;

	public static final Color colorMargin = new Color(0x555555);
	public static final Color colorBg = new Color(0xfaf5ee);
	public static final Color colorGrid = new Color(0xeee5dd);
	public static final Color colorInfoBg = new Color(0xbbffffff, true);
	public static final Color colorInfoText = new Color(0x777777);
	public static final Color colorMarginText = Color.WHITE;
	public static final Color colorMarginTextDim = new Color(0x999999);

	public abstract BufferedImage create();
	
	protected BufferedImage create(int w, int h) {
		BufferedImage img = new BufferedImage((int)(w*settings.uiScaling), (int)(h*settings.uiScaling), BufferedImage.TYPE_INT_RGB);
		GraphAssist g = new GraphAssist(img.createGraphics());
		g.graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.graph.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g.scale(settings.uiScaling);
		paint(g, w, h);
		return img;
	}
	
	protected abstract void paint(GraphAssist g, int w, int h);

}
