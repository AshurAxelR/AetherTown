package com.xrbpowered.aethertown.ui;

import static com.xrbpowered.aethertown.AetherTown.settings;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.xrbpowered.zoomui.GraphAssist;

public abstract class GeneratedImage extends BufferedImage {

	public GeneratedImage(int w, int h) {
		super((int)(w*settings.uiScaling), (int)(h*settings.uiScaling), BufferedImage.TYPE_INT_RGB);
	}
	
	protected void paint() {
		GraphAssist g = new GraphAssist(createGraphics());
		g.graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.graph.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g.scale(settings.uiScaling);
		paint(g);
	}
	
	protected abstract void paint(GraphAssist g);

}
