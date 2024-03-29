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
		ZoomableCss css = new ZoomableCss(
			"p{text-align:left} "+
			"td.v{text-align:right} "+
			".w{color:#ffffff} .d{color:#555555}"
		);
		css.addZoomRule("p", "margin-bottom", 10);
		css.addZoomRule("td.v", "padding-left", 30);
		css.addZoomRule("tr.total td", "padding-top", 10);
		htmlKit.zoomableCss = css;
	}

}
