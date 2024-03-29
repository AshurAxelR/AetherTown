package com.xrbpowered.aethertown.state;

import static com.xrbpowered.aethertown.AetherTown.player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.AetherTown;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.scene.CameraActor;

public class Player {

	private static final String formatId = "AetherTown.Player.1";

	public static final int maxInspiration = 100;
	
	public int cash = 0;
	public int ubiCollectedDay = -1;
	public int earnings = 0;
	
	private int inspiration = 0;
	private int xp = 0;
	
	public final Inventory backpack = new Inventory();
	
	public Vector3f cameraPosition = null;
	public Vector3f cameraRotation = null;
	
	public int getInspiration() {
		return inspiration;
	}
	
	public int getXP() {
		return xp;
	}

	public int addInspiration(int ins) {
		inspiration += ins;
		if(inspiration>maxInspiration) {
			int added = ins - (inspiration - maxInspiration);
			inspiration = maxInspiration;
			return added;
		}
		else if(inspiration<0) {
			int removed = ins - inspiration;
			inspiration = 0;
			return removed;
		}
		else
			return ins;
	}

	public int addXP(int xp) {
		this.xp += xp;
		return xp;
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

	public static void reset() {
		player = new Player();
	}
	
	public static boolean load(InputStream ins) {
		try {
			DataInputStream in = new DataInputStream(ins);
			
			if(!formatId.equals(in.readUTF()))
				throw new IOException("Bad file format");
			
			Player player = new Player();
			float cameraPosX = in.readFloat();
			float cameraPosZ = in.readFloat();
			float cameraLookX = in.readFloat();
			float cameraLookY = in.readFloat();
			player.cameraPosition = new Vector3f(cameraPosX, 0f, cameraPosZ);
			player.cameraRotation = new Vector3f(cameraLookX, cameraLookY, 0f);
			
			player.cash = in.readInt();
			player.ubiCollectedDay = in.readInt();
			player.earnings = in.readInt();
			player.inspiration = in.readInt();
			player.xp = in.readInt();
			player.backpack.loadItems(in);
			
			AetherTown.player = player;
			System.out.println("Player state loaded");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't load player state");
			e.printStackTrace();
			reset();
			return false;
		}
	}
	
	public static boolean save(OutputStream outs) {
		try {
			DataOutputStream out = new DataOutputStream(outs);
			
			out.writeUTF(formatId);
			
			out.writeFloat(player.cameraPosition.x);
			out.writeFloat(player.cameraPosition.z);
			out.writeFloat(player.cameraRotation.x);
			out.writeFloat(player.cameraRotation.y);

			out.writeInt(player.cash);
			out.writeInt(player.ubiCollectedDay);
			out.writeInt(player.earnings);
			out.writeInt(player.inspiration);
			out.writeInt(player.xp);
			player.backpack.saveItems(out);
			
			System.out.println("Player state saved");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't save player state");
			e.printStackTrace();
			return false;
		}
	}
}
