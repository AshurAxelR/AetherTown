package com.xrbpowered.aethertown.ui.hud;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.xrbpowered.gl.res.buffer.RenderTarget;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class UIRotatedTexture extends UINode {

	private float radius = 0f;
	
	private Texture texture = null;
	private float angle = 0f;
	private float alpha = 1f;
	private boolean clip = false;

	private float tx;
	private float ty;
	private float tr;
	
	public UIRotatedTexture(UIContainer parent) {
		super(parent);
	}

	protected UIRotatedTexture setTexture(Texture texture) {
		this.texture = texture;
		return this;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public UIRotatedTexture setRadius(float radius) {
		this.radius = radius;
		return this;
	}

	public float getAngle() {
		return angle;
	}
	
	public UIRotatedTexture setAngle(float angle) {
		this.angle = angle;
		return this;
	}
	
	public UIRotatedTexture setAlpha(float alpha) {
		this.alpha = alpha;
		return this;
	}
	
	public UIRotatedTexture setClip(boolean clip) {
		this.clip = clip;
		return this;
	}
	
	protected void updatePaneBounds(GraphAssist g) {
		Point2D p = new Point2D.Float(0, 0);
		Point2D pr = new Point2D.Float(radius, 0);
		AffineTransform t = g.getTransform();
		t.transform(p, p);
		t.transform(pr, pr);
		tx = (float) p.getX();
		ty = (float) p.getY();
		tr = (float) (pr.getX()-p.getX());
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		updatePaneBounds(g);
	}
	
	@Override
	public boolean isVisible() {
		return super.isVisible() && texture!=null && radius>0;
	}
	
	public void draw(RenderTarget target) {
		if(alpha<=0f)
			return;
		RotatedPaneShader shader = RotatedPaneShader.getInstance();
		shader.use();
		shader.updateScreenSize(target);
		shader.updateUniforms(tx, ty, tr, angle, alpha, true, clip);
		texture.bind(0);
		shader.quad.draw();
		shader.unuse();
	}
	
	public void render(RenderTarget target) {
		if(isVisible()) {
			draw(target);
			super.render(target);
		}
	}
	
	@Override
	public void releaseResources() {
		if(texture!=null)
			texture.release();
		super.releaseResources();
	}

}
