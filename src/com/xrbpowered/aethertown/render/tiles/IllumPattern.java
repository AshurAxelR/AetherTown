package com.xrbpowered.aethertown.render.tiles;

import java.util.Random;

import org.joml.Vector3f;

public class IllumPattern {

	public static final IllumPattern legacy = new IllumPattern(new Vector3f(0.5f, 0.8f, 0.8f), new Vector3f(1, 1, 1), false).setDim(0.35f, 0.1f);
	
	public static final IllumPattern shop = new IllumPattern(new Vector3f(1.0f, 0.9f, 0.8f), new Vector3f(1.0f, 1.0f, 0.9f), true);
	public static final IllumPattern office = new IllumPattern(new Vector3f(1.0f, 0.9f, 0.6f), new Vector3f(1.0f, 1.0f, 0.8f), true).setDim(0.65f, 0.3f);
	public static final IllumPattern restaurant = new IllumPattern(new Vector3f(0.5f, 0.8f, 0.8f), new Vector3f(0.9f, 0.85f, 0.9f), false);
	public static final IllumPattern hotel = new IllumPattern(new Vector3f(0.6f, 0.9f, 0.85f), new Vector3f(0.75f, 0.9f, 0.9f), false);
	public static final IllumPattern hotelRooms = new IllumPattern(new Vector3f(0.35f, 0.8f, 0.8f), new Vector3f(0.6f, 0.85f, 0.9f), false).setDim(0.65f, 0.15f);
	public static final IllumPattern hospital = new IllumPattern(new Vector3f(0.9f, 0.9f, 0.8f), new Vector3f(0.95f, 0.95f, 1.0f), true);
	public static final IllumPattern hospitalWards = new IllumPattern(new Vector3f(0.9f, 0.9f, 0.5f), new Vector3f(0.95f, 0.95f, 0.9f), true).setDim(0.65f, 0.4f);
	
	public IllumLayer layer = IllumLayer.placeholder;
	public Vector3f low, high;
	public boolean cool = false;
	public float dimRate = 0;
	public float dim = 0.05f;

	public IllumPattern(Vector3f low, Vector3f high, boolean cool) {
		this.low = low;
		this.high = high;
		this.cool = cool;
	}
	
	public IllumPattern setDim(float rate, float dim) {
		this.dimRate = rate;
		this.dim = dim;
		return this;
	}
	
	public static Vector3f calcMod(Random random, IllumPattern illum) {
		if(illum==null)
			return null;
		float r, g, b;
		if(illum.cool) {
			b = random.nextFloat()*(illum.high.z-illum.low.z)+illum.low.z;
			g = b*(random.nextFloat()*(illum.high.y-illum.low.y)+illum.low.y);
			r = g*(random.nextFloat()*(illum.high.x-illum.low.x)+illum.low.x);
		}
		else {
			r = random.nextFloat()*(illum.high.x-illum.low.x)+illum.low.x;
			g = r*(random.nextFloat()*(illum.high.y-illum.low.y)+illum.low.y);
			b = g*(random.nextFloat()*(illum.high.z-illum.low.z)+illum.low.z);
		}
		Vector3f mod = new Vector3f(r, g, b);
		if(random.nextFloat()<illum.dimRate)
			mod.mul(illum.dim);
		return mod;
	}
}
