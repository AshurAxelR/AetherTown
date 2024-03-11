package com.xrbpowered.aethertown.ui.dialogs;

import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class DialogManager extends UINode implements KeyInputHandler {

	public DialogManager(UIContainer parent) {
		super(parent);
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
			c.setPosition(0, 0);
			c.setSize(getWidth(), getHeight());
			c.layout();
		}
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		UIElement top = topChild();
		if(top==null || !(top instanceof KeyInputHandler))
			 return false;
		else
			return ((KeyInputHandler) top).onKeyPressed(c, code, input);
	}
	
}
