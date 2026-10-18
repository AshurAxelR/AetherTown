package com.xrbpowered.aethertools;

import static com.xrbpowered.aethertown.render.env.ShaderEnvironment.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xrbpowered.aethertown.render.env.ShaderEnvironment;

public class RenderEnvironmentPalette {

	private static final int numColors = 7;
	private static final int swatchSize = 32;
	private static final String outputName = "environment_palette.png";

	public static ShaderEnvironment[] environments = {clearDay, evening, dawn, clearNight, cloudyNight};
	

	private static Color getEnvColor(ShaderEnvironment env, int index) {
		switch(index) {
			case 0:
				return env.lightColor;
			case 1:
				return env.midColor;
			case 2:
				return env.shadowColor;
			case 3:
				return env.cloudColor;
			case 4:
				return env.horizonColor;
			case 5:
				return env.zenithColor;
			case 6:
				return new Color(env.lightSkyColor.getRGB(), false);
			default:
				throw new IllegalArgumentException("index "+index);
		}
	}
	
	public static void main(String[] args) {
		BufferedImage image = new BufferedImage(swatchSize * environments.length, swatchSize * numColors, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		g2.setColor(Color.GRAY);
		g2.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		for(int i=0; i<environments.length; i++)
			for(int j=0; j<numColors; j++) {
				g2.setColor(getEnvColor(environments[i], j));
				g2.fillRect(swatchSize * i, swatchSize * j, swatchSize, swatchSize);
			}
		
		try {
			ImageIO.write(image, "PNG", new File(outputName));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

}
