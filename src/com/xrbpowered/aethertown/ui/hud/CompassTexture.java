package com.xrbpowered.aethertown.ui.hud;

import java.awt.Color;
import java.awt.RenderingHints;

import com.xrbpowered.gl.res.texture.ImageBuffer;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;

public class CompassTexture {

	public static final int radius = 30;
	
	private CompassTexture() {}
	
	public static Texture create() {
		ImageBuffer img = new ImageBuffer(radius*2, radius*2, false);
		GraphAssist g = new GraphAssist(img.getGraphics());
		g.graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.graph.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.graph.setBackground(UIPane.transparent);
		g.graph.clearRect(0, 0, img.getWidth(), img.getHeight());
		g.translate(radius, radius);
		g.fillCircle(0, 0, radius, Hud.bgColor);
		g.resetStroke();
		g.drawCircle(0, 0, radius-2, new Color(0x77ffffff, true));
		int h = radius - 8;
		int w = radius/6;
		g.setStroke(1.5f);
		g.setColor(Color.WHITE);
		g.graph.drawPolygon(new int[] {0, w, 0, -w}, new int[] {-h, 0, h, 0}, 4);
		g.graph.fillPolygon(new int[] {0, w, 0, -w}, new int[] {-h, 0, -w, 0}, 4);
		g.fillCircle(0, 0, w/2);
		return new Texture(img, false, true);
	}

}
