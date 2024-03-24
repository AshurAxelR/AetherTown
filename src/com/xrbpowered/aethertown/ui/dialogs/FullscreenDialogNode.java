package com.xrbpowered.aethertown.ui.dialogs;

import com.xrbpowered.aethertown.ui.controls.ClickButton;
import com.xrbpowered.aethertown.ui.controls.ControlPane;
import com.xrbpowered.gl.ui.UINode;
import com.xrbpowered.gl.ui.pane.UIPane;
import com.xrbpowered.zoomui.InputInfo;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.MouseInfo;
import com.xrbpowered.zoomui.UIContainer;

public class FullscreenDialogNode extends UINode implements KeyInputHandler {

	public final UINode content;
	
	private final UIPane closeButtonPane;
	
	public FullscreenDialogNode(UIContainer parent) {
		super(parent);
		content = new UINode(this);
		closeButtonPane = new ControlPane(this);
		closeButtonPane.setSize(40, 32);
		
		new ClickButton(closeButtonPane, "X") {
			@Override
			public void onAction() {
				close();
			}
		};
	}

	@Override
	public void layout() {
		setPosition(0, 0);
		setSize(getParent().getWidth(), getParent().getHeight());
		content.setPosition(0, 0);
		content.setSize(getWidth(), getHeight());
		content.layout();
		closeButtonPane.setPosition(getWidth()-closeButtonPane.getWidth()-20, 20);
		closeButtonPane.layout();
	}
	
	public void close() {
		DialogContainer.close(this);
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, InputInfo input) {
		if(DialogContainer.isCloseKey(this, code))
			close();
		return true;
	}

	@Override
	public boolean onMouseDown(float x, float y, MouseInfo mouse) {
		return true;
	}
}
