package com.xrbpowered.aethertown.ui.hud;

import static com.xrbpowered.aethertown.ui.hud.Hud.bgColor;

import java.awt.Color;
import java.util.LinkedList;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class ToastPane extends UIPane {

	public static final float duration = 1.5f;
	public static final float fadeDuration = 1f;
	public static final float switchTime = 0.5f;
	
	private final LinkedList<String> queue = new LinkedList<>();
	
	private String msg = "";
	private float time = -1f;
	private boolean critical = false;
	
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
	
	public void push(String msg) {
		queue.addLast(msg);
	}
	
	public void showCritical(String msg) {
		this.msg = msg;
		critical = true;
		pane.alpha = 1f;
		repaint();
	}

	public void clearCritical(String msg) {
		time = duration;
		critical = false;
	}

	@Override
	public void updateTime(float dt) {
		if(critical)
			return;
		if(time>0f)
			time -= dt;
		if(time<switchTime && !queue.isEmpty()) {
			msg = queue.removeFirst();
			time = duration;
			repaint();
		}
		pane.alpha = MathUtils.clamp(time / fadeDuration);
	}

}
