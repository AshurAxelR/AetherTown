package com.xrbpowered.aethertown.render.env;

import org.joml.Vector4f;

public class DaytimeEnvironment extends ShaderEnvironment {

	public DaytimeEnvironment() {
	}
	
	public DaytimeEnvironment(ShaderEnvironment s) {
		super(s);
	}
	
	@Override
	public void recalc(Vector4f sun) {
		float c = (float)Math.toDegrees(Math.atan(sun.y));
		if(c<-18f)
			copyFrom(clearNight);
		else if(c<-6f)
			blend(clearNight, dawn, (c+18f)/12f);
		else if(c<6f)
			blend(dawn, newDawn, (c+6f)/12f);
		else if(c<18f)
			blend(newDawn, newDay, (c-6f)/12f);
		else
			copyFrom(newDay);
		
		super.recalc(sun);
	}

}
