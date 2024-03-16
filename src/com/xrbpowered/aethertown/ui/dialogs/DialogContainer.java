package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.aether;
import static com.xrbpowered.aethertown.AetherTown.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class DialogContainer extends UINode implements KeyInputHandler {

	public static final Color bgColor = new Color(0x55000000, true);

	public DialogContainer(UIContainer parent) {
		super(parent);
	}

	public void hideTop() {
		UIElement top = topChild();
		if(top!=null)
			top.setVisible(false);
	}
	
	public void reveal() {
		UIElement top = topChild();
		if(top!=null) {
			aether.disableController();
			top.setVisible(true);
		}
		repaint();
	}
	
	public boolean isEmpty() {
		return children.isEmpty();
	}
	
	public UIElement topChild() {
		return children.isEmpty() ? null : children.get(children.size()-1);
	}
	
	@Override
	public void layout() {
		for(UIElement c : children) {
			c.setPosition(getWidth()/2 - c.getWidth()/2, getHeight()/2 - c.getHeight()/2);
			c.layout();
		}
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		UIElement top = topChild();
		if(top==null)
			return false;
		else if(!(top instanceof KeyInputHandler))
			 return true;
		else
			return ((KeyInputHandler) top).onKeyPressed(c, code, input);
	}
	
	@Override
	public boolean onMouseDown(float x, float y, MouseInfo mouse) {
		return !isEmpty();
	}
	
	public static void close(UINode dialog) {
		dialog.remove();
		ui.reveal();
	}
	
	public static void checkCloseKey(UINode dialog, int code) {
		switch(code) {
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_Q:
			case KeyEvent.VK_E:
			case KeyEvent.VK_R:
				close(dialog);
				break;
			default:
				break;
		}
	}
	
}
