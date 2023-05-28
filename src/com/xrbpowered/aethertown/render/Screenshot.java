package com.xrbpowered.aethertown.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.world.region.LevelInfo;
import com.xrbpowered.aethertown.world.stars.WorldTime;
import com.xrbpowered.gl.res.buffer.OffscreenBuffer;
import com.xrbpowered.zoomui.GraphAssist;

import static com.xrbpowered.aethertown.AetherTown.settings;

public class Screenshot {

	public static final File screenshotDir = new File("screenshots");
	public static final String imageFormat = "jpg";

	protected void decorate(GraphAssist g, int w, int h) {
	}
	
	public boolean make(OffscreenBuffer buffer) {
		BufferedImage img = scale(capture(buffer), settings.screenshotScale);
		GraphAssist g = new GraphAssist((Graphics2D) img.getGraphics());
		g.graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		decorate(g, img.getWidth(), img.getHeight());
		return saveImage(img);
	}
	
	public static BufferedImage capture(OffscreenBuffer buffer) {
		int w = buffer.getWidth();
		int h = buffer.getHeight();
		buffer.use();
		ByteBuffer pixels = ByteBuffer.allocateDirect(w*h*4);
		GL11.glReadPixels(0, 0, w, h, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixels);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		pixels.rewind();
		int[] rgb = new int[pixels.remaining()];
		for(int y=h-1; y>=0; y--) {
			int offs = y*w*3;
			for(int x=0; x<w; x++) {
				for(int d=0; d<3; d++)
					rgb[offs++] = (int) pixels.get() & 0xff;
			}
		}

		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = img.getRaster();
		raster.setPixels(0, 0, w, h, rgb);
		return img;
	}
	
	public static BufferedImage scale(BufferedImage img, float s) {
		int w = (int) (img.getWidth() * s);
		int h = (int) (img.getHeight() * s);
		BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = res.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(img, 0, 0, w, h, null);

		img = res;
		return res;
	}
	
	public static boolean saveImage(BufferedImage img) {
		try {
			screenshotDir.mkdirs();
			File f = new File(screenshotDir, String.format("%d.%s", System.currentTimeMillis(), imageFormat));
			ImageIO.write(img, imageFormat, f);
			System.out.println("Screenshot saved to: " + f.getPath());
			return true;
		} catch(IOException e) {
			return false;
		}
	}
	
	public static final Screenshot screenshot = new Screenshot() {
		@Override
		protected void decorate(GraphAssist g, int width, int height) {
			g.fillRect(width-300, height-70, 280, 50, AetherTown.bgColor);
			g.setColor(Color.WHITE);
			g.setFont(Fonts.small);
			
			int h = 18;
			int y = height - 55;
			String s = String.format("DAY %d, %s", WorldTime.getDay()+1, WorldTime.getFormattedTime());
			g.drawString(s, width-40, y, GraphAssist.RIGHT, GraphAssist.CENTER); y += h;
			if(AetherTown.level!=null) {
				LevelInfo level = AetherTown.level.info;
				s = String.format("%s [%d, %d]", level.name, level.x0, level.z0);
				g.drawString(s, width-40, y, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
		}
	};

}
