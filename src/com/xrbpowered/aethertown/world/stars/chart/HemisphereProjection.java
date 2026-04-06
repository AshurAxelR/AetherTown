package com.xrbpowered.aethertown.world.stars.chart;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import com.xrbpowered.aethertown.ui.StarChartImage.Palette;
import com.xrbpowered.zoomui.GraphAssist;

public class HemisphereProjection implements Projection {

	private final int dspan;
	private final int dir;
	private final float size, sized, psize, tx, ty;
	private Shape clip;
	
	public HemisphereProjection(int dir, int dspan) {
		this.dspan = dspan;
		this.dir = dir;
		size = dDecl*dspan*2.0f;
		sized = size/2.0f;
		psize = size + margin*2.0f;
		tx = psize/2.0f;
		ty = psize/2.0f;
		clip = new Ellipse2D.Float(-sized, -sized, sized*2, sized*2);
	}

	@Override
	public float getPageWidth() {
		return psize;
	}
	
	@Override
	public float getPageHeight() {
		return psize;
	}
	
	@Override
	public double getTx() {
		return tx;
	}
	
	@Override
	public double getTy() {
		return ty;
	}
	
	@Override
	public void setClip(GraphAssist g) {
		g.graph.setClip(clip);
	}
	
	@Override
	public void paintGrid(GraphAssist g, Palette palette) {
		g.setStroke(4f);
		g.drawCircle(0, 0, sized, palette.outerBorder);
		g.fillCircle(0, 0, sized, palette.chart);
		for(int a=0; a<24; a++) {
			float aa = a * (float)Math.PI / 12.0f;
			float sinaa = (float)Math.sin(aa);
			float cosaa = (float)Math.cos(aa);
			float x = sized*sinaa;
			float y = -sized*cosaa;
			g.setColor(palette.grid);
			g.setStroke(0.5f);
			g.line(0, 0, x, y);
			x = (sized+margin/2.0f)*sinaa;
			y = -(sized+margin/2.0f)*cosaa;
			String s = String.format("%dh", (24+a*dir)%24);
			Projection.text(g, s, x, y, palette.outerText, null);
		}
		for(int d=90; d>90-dspan; d-=10) {
			float r =(90-d)*dDecl;
			if(d<90) {
				g.setColor(palette.grid);
				g.setStroke(0.5f);
				g.drawCircle(0, 0, r);
				String s = String.format("%+d\u00B0", d*dir);
				Projection.text(g, s, 0, -r, palette.text, palette.chart);
				Projection.text(g, s, 0, r, palette.text, palette.chart);
				Projection.text(g, s, -r, 0, palette.text, palette.chart);
				Projection.text(g, s, r, 0, palette.text, palette.chart);
			}
		}
		g.setStroke(1.5f);
		g.setColor(palette.chartBorder);
		g.drawCircle(0, 0, sized);
	}
	
	@Override
	public Point2D.Double pos(double a, double d) {
		double r = (90-d*dir)*dDecl;
		double aa = dir * a * Math.PI / 12.0;
		return new Point2D.Double(
			r*Math.sin(aa),
			-r*Math.cos(aa)
		);
	}
	
	@Override
	public boolean isOutside(double a, double d) {
		return d*dir<90-dspan;
	}
	
	@Override
	public Double unpos(double x, double y) {
		double aa = Math.atan2(x, -y);
		if(aa<0) aa += Math.PI * 2;
		double r = Math.sqrt(x*x + y*y);
		double d = (90 - r/dDecl) * dir;
		if(d*dir<0)
			return null;
		else
			return new Point2D.Double(aa *12.0 * dir / Math.PI, d);
	}
	
	@Override
	public void line(GraphAssist g, Point2D.Double pos1, Point2D.Double pos2, int level) {
		g.setStroke(level*0.5f);
		g.line((float)pos1.x, (float)pos1.y, (float)pos2.x, (float)pos2.y);
	}
	
	@Override
	public void vline(GraphAssist g, float a, float d1, float d2) {
		float aa = dir* a * (float)Math.PI / 12.0f;
		float sinaa = (float)Math.sin(aa);
		float cosaa = (float)Math.cos(aa);
		float r = (90-d1*dir)*dDecl;
		float x1 = r*sinaa;
		float y1 = -r*cosaa;
		r = (90-d2*dir)*dDecl;
		float x2 = r*sinaa;
		float y2 = -r*cosaa;
		g.line(x1, y1, x2, y2);
	}
	
	@Override
	public void hline(GraphAssist g, float a1, float a2, float d) {
		double r = (90-d*dir)*dDecl;
		float aa1 = dir* a1 * (float)Math.PI / 12.0f;
		float aa2 = dir* a2 * (float)Math.PI / 12.0f;
		g.graph.draw(new Arc2D.Double(-r, -r, r*2, r*2, -Math.toDegrees(aa1)+90, Math.toDegrees(aa1-aa2), Arc2D.OPEN));
	}

}
