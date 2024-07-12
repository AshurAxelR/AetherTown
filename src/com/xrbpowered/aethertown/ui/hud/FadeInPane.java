package com.xrbpowered.aethertown.ui.hud;

import java.awt.Color;

import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.gl.ui.pane.UITexture;
import com.xrbpowered.zoomui.UIContainer;

public class FadeInPane extends UITexture {

	private float duration = 1f;
	private float time = -1;
	
	public FadeInPane(UIContainer parent) {
		super(parent);
		pane.alpha = 0f;
	}
	
	@Override
	public boolean isInteractive() {
		return false;
	}
	
	@Override
	public void layout() {
		setPosition(0, 0);
		setSize(getParent().getWidth(), getParent().getHeight());
	}
	
	public void start(Color color, float duration) {
		if(duration<=0f)
			return;
		setTexture(TexColor.get(color));
		this.duration = duration;
		time = duration;
		pane.alpha = 1f;
	}

	@Override
	public void updateTime(float dt) {
		if(time>0f)
			time -= dt;
		pane.alpha = MathUtils.clamp(time / duration);
	}
}
