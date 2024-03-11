package com.xrbpowered.aethertown.ui;

import static com.xrbpowered.aethertown.AetherTown.bgColor;

import java.awt.Color;
import java.util.LinkedList;

import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class ToastPane extends UIPane {

	public static final float duration = 1.5f;
	public static final float fadeDuration = 1f;
	public static final float switchTime = 0.5f;
	
	public final LinkedList<String> queue = new LinkedList<>();
	
	private String msg = "";
	private float time = -1f;
	
	public ToastPane(UIContainer parent) {
		super(parent, false);
		setSize(330, 32);
	}
	
	@Override
	protected void paintBackground(GraphAssist g) {
		clear(g, bgColor);
		g.setColor(Color.WHITE);
		g.setFont(Fonts.small);
		g.drawString(msg, 20, getHeight()/2, GraphAssist.LEFT, GraphAssist.CENTER);
	}
	
	@Override
	public void updateTime(float dt) {
		if(time>0f)
			time -= dt;
		if(time<switchTime && !queue.isEmpty()) {
			msg = queue.pop();
			time = duration;
			repaint();
		}
		pane.alpha = MathUtils.clamp(time / fadeDuration);
	}

}