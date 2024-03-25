package com.xrbpowered.aethertown.ui.controls;

import java.awt.Color;

import com.xrbpowered.aethertown.ui.Fonts;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.std.UIFormattedLabel;

public class InfoBox extends UIFormattedLabel {

	public static final Color textColor = new Color(0x999999);
	public static final Color darkColor = new Color(0x555555);
	
	public InfoBox(UIContainer parent, String html) {
		super(parent, html);
	}

	public InfoBox(UIContainer parent) {
		this(parent, "");
	}
	
	@Override
	public void setupHtmlKit() {
		htmlKit.defaultFont = Fonts.small;
		htmlKit.defaultColor = textColor;
		htmlKit.zoomableCss = new ZoomableCss(
			"p{text-align:left;margin-bottom:10} "+
			"span.w{color:#ffffff} span.d{color:#"+Integer.toHexString(darkColor.getRGB())+"}"
		);
	}

}
