package com.xrbpowered.aethertools;

import java.awt.Color;
import java.awt.Rectangle;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIZoomView;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class FollowTerrainTest extends UIContainer {

	private static final int tilex = 64;
	private static final int tiley = 8;
	private static final int yzero = 10;
	
	private static final Color tooltipColor = new Color(0xfafafa);
	private static final Color tooltipBorderColor = new Color(0xeeeeee);
	
	public static class LineData {
		public int offs;
		public int[] y;
		public Color color;
		
		public LineData(int offs, int[] y, Color color) {
			this.offs = offs;
			this.y = y;
			this.color = color;
		}
		
		public void paint(GraphAssist g) {
			int prevx = 0;
			int prevy = 0;
			for(int i=0; i<y.length; i++) {
				int cx = tilex*(offs+i);
				int cy = tiley*(yzero-y[i]);
				g.setColor(color);
				if(i>0)
					g.line(prevx, prevy, cx, cy);
				g.fillRect(cx-2, cy-2, 5, 5);
				g.setColor(tooltipColor);
				g.fillRect(cx-12, cy-24, 24, 16);
				g.setColor(tooltipBorderColor);
				g.drawRect(cx-12, cy-24, 24, 16);
				g.setColor(color);
				g.drawString(Integer.toString(y[i]), cx, cy-16, GraphAssist.CENTER, GraphAssist.CENTER);
				prevx = cx;
				prevy = cy;
			}
		}
	}
	
	private LineData guide;
	private LineData res = null;
	private LineData start = null;
	private LineData end = null;
	
	private static final int[] g = {0, 1, 0, -2, -3, 2, 4, 6, 5, 3, 1, 0, 2, 7, 8, 9};
	//private static final int[] g = {0, 1, 0, -12, -13, -3, 2, 4, 6, 5};
	//private static final int[] g = {0, 1, 0, 2, 5};
	//private static final int[] g = {0};
	private static final int sy = 0;
	private static final int sdy =0;
	private static final int ey = 12;
	private static final int edy = 0;
	
	public FollowTerrainTest(UIContainer parent) {
		super(new UIZoomView(parent) {
			@Override
			protected void paintSelf(GraphAssist g) {
				g.fill(this, Color.WHITE);
			}
		});
		guide = new LineData(0, g, new Color(0x77dd33));
		FollowTerrain ft = new FollowTerrain(true, sy, sdy, ey, edy, g);
		FollowTerrain.Result r = ft.compute();
		Color edgeColor = new Color(0x555555);
		if(r!=null) {
			res = new LineData(0, r.y, new Color(0xdd0000));
			start = new LineData(-2, new int[] {sy-sdy, sy, r.y[0]}, edgeColor);
			end = new LineData(r.y.length-1, new int[] {r.y[r.y.length-1], ey, ey+edy}, edgeColor);
		}
		else {
			start = new LineData(-2, new int[] {sy-sdy, sy}, edgeColor);
			end = new LineData(g.length, new int[] {ey, ey+edy}, edgeColor);
		}
	}
	
	@Override
	public boolean isVisible(Rectangle clip) {
		return true;
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		guide.paint(g);
		if(start!=null)
			start.paint(g);
		if(end!=null)
			end.paint(g);
		if(res!=null)
			res.paint(g);
	}
	
	public static void main(String[] args) {
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("FollowTerrainTest", 1200, 800);
		new FollowTerrainTest(frame.getContainer());
		frame.show();
		// frame.maximize();
	}

}
