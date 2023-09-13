package com.xrbpowered.aethertools;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import com.xrbpowered.aethertown.world.gen.FollowTerrain;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIZoomView;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class FollowTerrainView extends UIContainer {

	private static final int tilex = 64;
	private static final int tiley = 8;
	private static final int yzero = 10;
	
	private static final Color tooltipColor = new Color(0xfafafa);
	private static final Color tooltipBorderColor = new Color(0xeeeeee);
	
	private static final Color guideColor = new Color(0x77dd33);
	private static final Color edgeColor = new Color(0x555555);
	private static final Color resColor = new Color(0xdd0000);
	private static final Color joinColor = new Color(0xffaaaa);
	// private static final Color debugColor = new Color(0xeeeeee);

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
	private ArrayList<LineData> lines = new ArrayList<>();
	private LineData start = null;
	private LineData end = null;
	
	// private static final int sy=0, sdy=0, ey = 4, edy = 0;
	// private static final int[] g = {0, 1, 0, -2, -3, 2, 4, 6, 5, 8, 10, 9, -2, -7, -5, -5, -3, 5, 7, 8, 10, 10, 11, 10, 12, 14, 13};
	private static final int sy=29, sdy=0, ey=32, edy=0;
	private static final int[] g = {30, 31, 33, 33, 30, 30, 32, 34, 34};
	
	private void createLines(FollowTerrain.Result r) {
		if(r.subS!=null && r.subE!=null) {
			lines.add(new LineData(r.subE.x-3, new int[] {r.subS.y[r.subS.y.length-1], r.my, r.my, r.subE.y[0]}, joinColor));
			createLines(r.subS);
			createLines(r.subE);
		}
		else {
			lines.add(new LineData(r.x, r.y, resColor));
		}
	}
	
	public FollowTerrainView(UIContainer parent) {
		super(new UIZoomView(parent) {
			@Override
			protected void paintSelf(GraphAssist g) {
				g.fill(this, Color.WHITE);
			}
		});
		guide = new LineData(0, g, guideColor);
		FollowTerrain ft = new FollowTerrain(sy, sdy, ey, edy, g); // .useDir(false);
		FollowTerrain.Result r = ft.compute();
		if(r!=null) {
			System.out.printf("Computed in %d iterations, cost=%d\n", ft.iterations, r.cost);
			createLines(r);
			start = new LineData(-2, new int[] {sy-sdy, sy, r.y[0]}, edgeColor);
			end = new LineData(g.length-1, new int[] {r.y[r.y.length-1], ey, ey+edy}, edgeColor);
		}
		else {
			System.out.printf("No solution in %d iterations\n", ft.iterations);
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
		for(LineData line : lines)
			line.paint(g);
	}
	
	public static void main(String[] args) {
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("FollowTerrain", 1200, 800);
		new FollowTerrainView(frame.getContainer());
		frame.show();
		// frame.maximize();
	}

}
