package com.xrbpowered.aethertools;

import java.awt.Color;

import com.xrbpowered.aethertown.utils.Dir8;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class BlockLightTest extends UIElement {

	public static final int size = 64;
	public static final int pixelSize = 16;
	public static final float deltav = 0.075f;
	public static final int maxr = (int)Math.ceil(1/deltav)+1;
	
	public class Block {
		public float r = 0f;
		public float g = 0f;
		public float b = 0f;
	}
	
	public Block[][] map;
	
	public BlockLightTest(UIContainer parent) {
		super(parent);
		setSize(size*pixelSize, size*pixelSize);
		
		map = new Block[size][size];
		init();
		addLight(20, 20, Color.WHITE);
		addLight(35, 25, new Color(0xffdd99));
		addLight(15, 35, new Color(0x886622));
		addLight(40, 40, new Color(0xddeeff));
		addLight(50, 15, new Color(0xdd7700));
	}
	
	public void init() {
		for(int x=0; x<size; x++)
			for(int z=0; z<size; z++) {
				map[x][z] = new Block();
			}
	}
	
	public void addLight(int x0, int z0, float v, float[][] vmap) {
		if(vmap[x0][z0]>=v)
			return;
		vmap[x0][z0] = v;
		for(Dir8 d : Dir8.values()) {
			int x = x0+d.dx;
			int z = z0+d.dz;
			float dv = deltav*d.len;
			addLight(x, z, v-dv, vmap);
		}
	}

	public void addLight(int x0, int z0, Color color) {
		float[][] vmap = new float[maxr*2][maxr*2];
		addLight(maxr, maxr, 1f, vmap);
		for(int x=0; x<maxr*2; x++)
			for(int z=0; z<maxr*2; z++) {
				int mx = x0-maxr+x;
				int mz = z0-maxr+z;
				if(mx<0 || mx>=size || mz<0 || mz>=size)
					continue;
				Block b = map[mx][mz];
				float v = vmap[x][z];
				v = v*v;
				b.r += v*color.getRed()/255f;
				if(b.r>1f) b.r = 1f;
				b.g += v*color.getGreen()/255f;
				if(b.g>1f) b.g = 1f;
				b.b += v*color.getBlue()/255f;
				if(b.b>1f) b.b = 1f;
			}
	}

	@Override
	public void paint(GraphAssist g) {
		for(int x=0; x<size; x++)
			for(int z=0; z<size; z++) {
				Block b = map[x][z];
				Color col = new Color(b.r, b.g, b.b);
				g.fillRect(x*pixelSize, z*pixelSize, pixelSize, pixelSize, col);
			}
	}

	public static void main(String[] args) {
		SwingFrame frame = SwingWindowFactory.use(1f).createFrame("BlockLightTest", size*pixelSize, size*pixelSize);
		new BlockLightTest(frame.getContainer());
		frame.show();
	}

}
