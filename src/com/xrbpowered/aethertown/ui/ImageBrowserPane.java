package com.xrbpowered.aethertown.ui;

import static com.xrbpowered.aethertown.AetherTown.settings;

import java.awt.image.BufferedImage;

import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UITexture;
import com.xrbpowered.zoomui.DragActor;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class ImageBrowserPane extends UINode {

	private DragActor panActor = new DragActor() {
		@Override
		public boolean notifyMouseDown(float x, float y, Button button, int mods) {
			return true;
		}

		@Override
		public boolean notifyMouseMove(float dx, float dy) {
			float pix = getPixelScale();
			pan(dx*pix, dy*pix);
			repaint();
			return true;
		}

		@Override
		public boolean notifyMouseUp(float x, float y, Button button, int mods, UIElement target) {
			return true;
		}
	};
	
	public final UITexture image;
	
	protected float panX = 0;
	protected float panY = 0;
	
	public ImageBrowserPane(UIContainer parent) {
		super(parent);
		image = new UITexture(this);
	}
	
	private void updateImageLocation() {
		float w = getWidth();
		float iw = image.getWidth();
		float limX = iw>w ? (iw-w)/2 : 0;
		if(panX<-limX) panX = -limX;
		if(panX>limX) panX = limX;

		float h = getHeight();
		float ih = image.getHeight();
		float limY = ih>h ? (ih-h)/2 : 0;
		if(panY<-limY) panY = -limY;
		if(panY>limY) panY = limY;

		image.setLocation((w-iw)/2 - panX, (h-ih)/2 - panY);
	}
	
	public void resetPan() {
		panX = 0;
		panY = 0;
		updateImageLocation();
	}
	
	public void pan(float dx, float dy) {
		panX -= dx;
		panY -= dy;
		updateImageLocation();
	}
	
	@Override
	public void layout() {
		updateImageLocation();
	}
	
	@Override
	public DragActor acceptDrag(float x, float y, Button button, int mods) {
		if(panActor.notifyMouseDown(x, y, button, mods))
			return panActor;
		else
			return null;
	}
	
	public void setImage(BufferedImage img) {
		if(image.pane.getTexture()!=null)
			image.pane.getTexture().release();
		image.pane.setTexture(new Texture(img, false, false));
		image.setSize(img.getWidth() / settings.uiScaling, img.getHeight() / settings.uiScaling);
		resetPan();
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		return true;
	}

}
