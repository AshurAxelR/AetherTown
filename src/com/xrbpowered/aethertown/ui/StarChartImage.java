package com.xrbpowered.aethertown.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.stars.chart.ChartStar;
import com.xrbpowered.aethertown.world.stars.chart.Projection;
import com.xrbpowered.zoomui.GraphAssist;

public class StarChartImage extends ImageGenerator {

	public static final float scale = 0.75f;
	
	public static class Palette {
		public Color page;
		public Color chart;
		public Color chartBorder;
		public Color outerBorder;
		public Color text;
		public Color outerText;
		public Color grid;
		public Color gridEquator;
		public Color star;
	}
	
	public static final Palette palette = new Palette() {{
		page = new Color(0xe7d7c5);
		chart = Color.getHSBColor(0.65f, 0.65f, 0.25f);
		chartBorder = Color.WHITE;
		outerBorder = Color.BLACK;
		text = Color.WHITE;
		outerText = Color.BLACK;
		grid = Color.getHSBColor(0.65f, 0.5f, 0.5f);
		gridEquator = Color.getHSBColor(0.65f, 0.25f, 0.7f);
		star = Color.WHITE;
	}};

	private Projection proj;
	private ArrayList<ChartStar> stars;

	public StarChartImage(Region region, Projection proj) {
		this.proj = proj;
		this.stars = region.getStarChartData();
	}

	@Override
	public BufferedImage create() {
		return create((int)(proj.getPageWidth()*scale), (int)(proj.getPageHeight()*scale));
	}

	@Override
	protected void paint(GraphAssist g, int w, int h) {
		g.fillRect(0, 0, w, h, palette.page);
		g.setFont(Fonts.small);
		g.scale(scale);
		
		Rectangle saveClip = g.getClip();
		g.pushTx();
		g.translate(proj.getTx(), proj.getTy());
		
		proj.paintGrid(g, palette);
		proj.setClip(g);
		
		g.setStroke(2f);
		for(ChartStar s : stars) {
			if(proj.isOutside(s.ra, s.de))
				continue;
			
			if(s.shape==null)
				s.shape = new Ellipse2D.Double(-s.r, -s.r, s.r*2.0, s.r*2.0);
			
			g.pushTx();
			Point2D.Double pos = proj.pos(s.ra, s.de);
			g.translate(pos.x, pos.y);
			g.setColor(palette.chart);
			g.graph.draw(s.shape);
			g.setColor(palette.star);
			g.graph.fill(s.shape);
			g.popTx();
		}

		g.popTx();
		g.setClip(saveClip);
	}

}
