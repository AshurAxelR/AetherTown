package com.xrbpowered.aethertown.world.stars.chart;

import static com.xrbpowered.zoomui.GraphAssist.CENTER;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.xrbpowered.aethertown.ui.StarChartImage.Palette;
import com.xrbpowered.zoomui.GraphAssist;

public interface Projection {

	public static final float baseScale = 0.35f;
	public static final float circleScale = 13f * baseScale; // 12.0f * 0.5f * 0.85f;
	public static final float dAsc = 690.0f * baseScale;
	public static final float dDecl = 45.5f * baseScale;
	public static final float margin = 40.0f;

	public float getPageWidth();
	public float getPageHeight();
	public double getTx();
	public double getTy();
	
	public void setClip(GraphAssist g);
	public void paintGrid(GraphAssist g, Palette palette);
	
	public Point2D.Double pos(double a, double d);
	public boolean isOutside(double a, double d);
	public Point2D.Double unpos(double x, double y);
	
	public void line(GraphAssist g, Point2D.Double pos1, Point2D.Double pos2, int level);
	public void vline(GraphAssist g, float a, float d1, float d2);
	public void hline(GraphAssist g, float a1, float a2, float d);
	
	public static void text(GraphAssist g, String s, float x, float y, Color color, Color bgColor) {
		FontMetrics fm = g.getFontMetrics();
		float w = fm.stringWidth(s);
		float h = fm.getAscent() - fm.getDescent();
		float tx = x - GraphAssist.align(w, CENTER);
		float ty = y + h - GraphAssist.align(h, CENTER);
		
		AffineTransform tr = AffineTransform.getTranslateInstance(tx, ty);
		Shape outline = new TextLayout(s, g.graph.getFont(), g.graph.getFontRenderContext()).getOutline(tr);
		if(bgColor!=null) {
			g.setStroke(2f);
			g.setColor(bgColor);
			g.graph.draw(outline);
		}
		g.setColor(color);
		g.graph.fill(outline);
	}

}
