package com.xrbpowered.aethertown.state;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.scene.CameraActor;

public class Player {

	public Vector3f cameraPosition = null;
	public Vector3f cameraRotation = null;
	
	public void fromSave(SaveState save) {
		if(save.defaultStart) {
			cameraPosition = null;
			cameraRotation = null;
		}
		else {
			cameraPosition = new Vector3f(save.cameraPosX, 0f, save.cameraPosZ);
			cameraRotation = new Vector3f(save.cameraLookX, save.cameraLookY, 0f);
		}
	}
	
	public void toSave(SaveState save) {
		save.defaultStart = false;
		save.cameraPosX = cameraPosition.x;
		save.cameraPosZ = cameraPosition.z;
		save.cameraLookX = cameraRotation.x;
		save.cameraLookY = cameraRotation.y;
	}
	
	public void initCamera(CameraActor camera, Level level, boolean resetPosition) {
		if(resetPosition || cameraPosition==null || cameraRotation==null) {
			camera.position.x = level.getStartX()*Tile.size;
			camera.position.z = level.getStartZ()*Tile.size;
			camera.rotation.x = 0;
			camera.rotation.y = 0;
		}
		else {
			camera.position.x = cameraPosition.x;
			camera.position.z = cameraPosition.z;
			camera.rotation.x = cameraRotation.x;
			camera.rotation.y = cameraRotation.y;
		}
		camera.position.y = (HeightLimiter.maxHeight+1) * Tile.ysize;
		updateCamera(camera);
	}
	
	public void updateCamera(CameraActor camera) {
		cameraPosition = camera.position;
		cameraRotation = camera.rotation;
	}

}
