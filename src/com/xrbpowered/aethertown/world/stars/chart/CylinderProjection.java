package com.xrbpowered.aethertown.world.stars.chart;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

import com.xrbpowered.aethertown.ui.StarChartImage.Palette;
import com.xrbpowered.zoomui.GraphAssist;

public class CylinderProjection implements Projection {
	
	private final int dspan;
	private final float w, h, hd, pw, ph, tx, ty;
	private Shape clip;

	public CylinderProjection(int dspan) {
		this.dspan = dspan;
		w = dAsc*24.0f;
		h = dDecl*dspan*2.0f;
		hd = h/2.0f;
		pw = w + margin*2.0f;
		ph = h + margin*2.0f;
		tx = margin;
		ty = ph/2.0f;
		clip = new Rectangle2D.Float(0, -hd, w, h);
	}

	@Override
	public float getPageWidth() {
		return pw;
	}
	
	@Override
	public float getPageHeight() {
		return ph;
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
		g.drawRect(0, -hd, w, h, palette.outerBorder);
		g.fillRect(0, -hd, w, h, palette.chart);
		for(int a=0; a<=24; a++) {
			float x = a*dAsc; 
			g.setColor(palette.grid);
			g.setStroke(0.5f);
			g.line(x, -hd, x, hd);
			String s = String.format("%dh", (24-a)%24);
			Projection.text(g, s, x, -hd - margin/2.0f, palette.outerText, null);
			Projection.text(g, s, x, hd + margin/2.0f, palette.outerText, null);
		}
		for(int d=-dspan; d<dspan; d+=10) {
			float y =d*dDecl; 
			if(d==0) {
				g.setColor(palette.gridEquator);
				g.resetStroke();
			}
			else {
				g.setColor(palette.grid);
				g.setStroke(0.5f);
			}
			g.line(0, y, w, y);
			String s = d==0 ? "0\u00B0" : String.format("%+d\u00B0", -d);
			Projection.text(g, s, -margin/2.0f, y, palette.outerText, null);
			Projection.text(g, s, w+margin/2.0f, y, palette.outerText, null);
		}
		g.setStroke(1.5f);
		g.setColor(palette.chartBorder);
		g.drawRect(0, -hd, w, h);
	}
	
	@Override
	public Point2D.Double pos(double a, double d) {
		return new Point2D.Double((24.0-a)*dAsc, -d*dDecl);
	}
	
	@Override
	public boolean isOutside(double a, double d) {
		return d<-dspan || d>dspan;
	}
	
	@Override
	public Double unpos(double x, double y) {
		double a = 24.0-x/dAsc;
		double d = -y/dDecl;
		if(a<0 || a>24 || d<-dspan || d>dspan)
			return null;
		else
			return new Point2D.Double(a, d);
	}
	
	@Override
	public void line(GraphAssist g, Point2D.Double pos1, Point2D.Double pos2, int level) {
		g.setStroke(level*0.5f);
		if(Math.abs(pos1.x-pos2.x)>w/2.0) {
			if(pos1.x>pos2.x) {
				g.line((float)pos1.x-w, (float)pos1.y, (float)pos2.x, (float)pos2.y);
				g.line((float)pos1.x, (float)pos1.y, (float)pos2.x+w, (float)pos2.y);
			}
			else {
				g.line((float)pos1.x+w, (float)pos1.y, (float)pos2.x, (float)pos2.y);
				g.line((float)pos1.x, (float)pos1.y, (float)pos2.x-w, (float)pos2.y);
			}
		}
		else {
			g.line((float)pos1.x, (float)pos1.y, (float)pos2.x, (float)pos2.y);
		}
	}
	
	@Override
	public void vline(GraphAssist g, float a, float d1, float d2) {
		float x = (24.0f-a)*dAsc;
		float y1 = -d1*dDecl;
		float y2 = -d2*dDecl;
		g.line(x, y1, x, y2);
	}
	
	@Override
	public void hline(GraphAssist g, float a1, float a2, float d) {
		float x1 = (24.0f-a1)*dAsc;
		float x2 = (24.0f-a2)*dAsc;
		float y = -d*dDecl;
		g.line(x1, y, x2, y);
	}

}
