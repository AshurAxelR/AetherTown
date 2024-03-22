package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.xrbpowered.aethertown.actions.CooldownSettings;
import com.xrbpowered.aethertown.actions.TileAction;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public enum GlobalCooldowns {

	fastTravel,
	sleep,
	eat,
	drink,
	prayIns;
	
	private static final String formatId = "AetherTown.GlobalCooldowns.0";

	private static double[] cooldowns = new double[values().length];
	
	public boolean isOnCooldown() {
		return cooldowns[ordinal()] > WorldTime.time;
	}

	public double remaining() {
		return Math.max(0, cooldowns[ordinal()] - WorldTime.time); 
	}
	
	public String formatRemaining() {
		int rem = (int) Math.ceil(remaining() / WorldTime.minute);
		return "Cooldown remaining: "+TileAction.formatDelay(rem);
	}
	
	public void start(double duration) {
		cooldowns[ordinal()] = Math.max(cooldowns[ordinal()], WorldTime.time + duration);
	}

	public void startH(double hours) {
		start(hours*WorldTime.hour);
	}

	public void startDailyH(double hour) {
		start(1.0 - WorldTime.getTimeOfDay() + hour*WorldTime.hour);
	}

	public void pushBack(double duration) {
		cooldowns[ordinal()] = Math.max(cooldowns[ordinal()], WorldTime.time) + duration;
	}

	public void pushBackH(double hours) {
		pushBack(hours*WorldTime.hour);
	}

	public CooldownSettings hours(double h) {
		return new CooldownSettings(this, false, h);
	}

	public CooldownSettings daily(double h) {
		return new CooldownSettings(this, true, h);
	}

	private static void clear() {
		for(int i=0; i<cooldowns.length; i++)
			cooldowns[i] = 0.0;
	}
	
	public static boolean load(InputStream ins) {
		try {
			DataInputStream in = new DataInputStream(ins);
			
			if(!formatId.equals(in.readUTF()))
				throw new IOException("Bad file format");
			if(in.readInt()!=cooldowns.length)
				throw new IOException("Data length mismatch");
			for(int i=0; i<cooldowns.length; i++)
				cooldowns[i] = in.readDouble();
			
			System.out.println("Global cooldowns loaded");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't load global cooldowns");
			e.printStackTrace();
			clear();
			return false;
		}
	}
	
	public static boolean save(OutputStream outs) {
		try {
			DataOutputStream out = new DataOutputStream(outs);
			
			out.writeUTF(formatId);
			out.writeInt(cooldowns.length);
			for(int i=0; i<cooldowns.length; i++)
				out.writeDouble(cooldowns[i]);
			
			System.out.println("Global cooldowns saved");
			return true;
		}
		catch(Exception e) {
			System.err.println("Can't save global cooldowns");
			e.printStackTrace();
			return false;
		}
	}
}
