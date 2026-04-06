package com.xrbpowered.aethertown.ui.dialogs;

import static com.xrbpowered.aethertown.AetherTown.ui;

import com.xrbpowered.aethertown.ui.ImageBrowserPane;
import com.xrbpowered.aethertown.ui.StarChartImage;
import com.xrbpowered.aethertown.world.region.Region;
import com.xrbpowered.aethertown.world.stars.chart.CylinderProjection;
import com.xrbpowered.aethertown.world.stars.chart.HemisphereProjection;
import com.xrbpowered.aethertown.world.stars.chart.Projection;
import com.xrbpowered.zoomui.UIContainer;

public class StarChartDialog extends FullscreenDialogNode {

	public static final Projection nhemi = new HemisphereProjection(1, 90);
	public static final Projection shemi = new HemisphereProjection(-1, 90);
	public static final Projection cylinder = new CylinderProjection(70);

	private final ImageBrowserPane image;
	
	private StarChartDialog(UIContainer parent, Region region, Projection proj) {
		super(parent);
		
		image = new ImageBrowserPane(content);
		image.setImage(new StarChartImage(region, proj).create());
	}
	
	public static void show(Region region, Projection proj) {
		ui.hideTop();
		new StarChartDialog(ui, region, proj);
		ui.reveal();
	}

}
