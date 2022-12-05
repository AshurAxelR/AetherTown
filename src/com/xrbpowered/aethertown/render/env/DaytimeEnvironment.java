package com.xrbpowered.aethertown.render.env;

import org.joml.Vector4f;

public class DaytimeEnvironment extends ShaderEnvironment {

	public DaytimeEnvironment() {
	}
	
	public DaytimeEnvironment(ShaderEnvironment s) {
		super(s);
	}
	
	public void recalc(Vector4f sun) {
		float c = (float)Math.toDegrees(Math.atan(sun.y));
		if(c<-18f)
			copyFrom(clearNight);
		else if(c<-6f)
			blend(clearNight, dawn, (c+18f)/12f);
		else if(c<6f)
			blend(dawn, evening, (c+6f)/12f);
		else if(c<18f)
			blend(evening, clearDay, (c-6f)/12f);
		else
			copyFrom(clearDay);
		
		lightDirection.set(-sun.x, -Math.abs(sun.y), -sun.z);
		lightSkyDirection.set(-sun.x, -sun.y, -sun.z);
	}

}
