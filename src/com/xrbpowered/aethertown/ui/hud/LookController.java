package com.xrbpowered.aethertown.ui.hud;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.xrbpowered.gl.client.ClientInput;
import com.xrbpowered.gl.scene.WalkController;

public class LookController extends WalkController {

	public float centerY = 0;
	public boolean limitY;
	
	public LookController(ClientInput input, boolean limitY) {
		super(input);
		this.limitY = limitY;
	}

	@Override
	protected void updateMove(Vector3f move) {
	}
	
	@Override
	protected void applyRotation(Vector2f turn) {
		super.applyRotation(turn);
		if(limitY) {
			if(actor.rotation.y<centerY-lookLimiter)
				actor.rotation.y = centerY-lookLimiter;
			if(actor.rotation.y>centerY+lookLimiter)
				actor.rotation.y = centerY+lookLimiter;
		}
	}

}
