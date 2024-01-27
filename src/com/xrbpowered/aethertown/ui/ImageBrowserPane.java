package com.xrbpowered.aethertown.ui;

import static com.xrbpowered.aethertown.AetherTown.settings;

import java.awt.image.BufferedImage;

import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UITexture;
import com.xrbpowered.zoomui.DragActor;
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class ImageBrowserPane extends UINode {

	private DragActor panActor = new DragActor() {
		@Override
		public boolean notifyMouseDown(float x, float y, MouseInfo mouse) {
			return true;
		}

		@Override
		public boolean notifyMouseMove(float rx, float ry, float drx, float dry, MouseInfo mouse) {
			float pix = getPixelSize();
			pan(drx*pix, dry*pix);
			repaint();
			return true;
		}

		@Override
		public void notifyMouseUp(float rx, float ry, MouseInfo mouse, UIElement target) {
			// do nothing
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

		image.setPosition((w-iw)/2 - panX, (h-ih)/2 - panY);
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
	public DragActor acceptDrag(float x, float y, MouseInfo mouse) {
		if(panActor.notifyMouseDown(x, y, mouse))
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
	public boolean onMouseDown(float x, float y, MouseInfo mouse) {
		return true;
	}

}
