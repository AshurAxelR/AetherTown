package com.xrbpowered.aethertools;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.joml.Vector3f;

public class ObjPrinter {

	public static final float d = 2f; 
	
	public static final int up = 0;
	public static final int fwd = 1;
	public static final String norms = "vn 0 1 0\nvn 0 0 -1\n";

	private PrintStream out;
	
	private StringBuilder pos = new StringBuilder();
	private StringBuilder tex = new StringBuilder();
	private StringBuilder norm = new StringBuilder(norms);
	private StringBuilder f = new StringBuilder();
	
	private int posIdx = 0;
	private int texIdx = 0;
	private int normIdx = 2;
	private int fIdx = 0;

	public ObjPrinter(PrintStream out) {
		this.out = out;
	}
	
	public ObjPrinter(String path) {
		try {
			this.out = new PrintStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void finish(String name) {
		out.println("o "+name);
		out.print(pos.toString());
		out.print(tex.toString());
		out.print(norm.toString());
		out.println("s off");
		out.print(f.toString());
	}
	
	private int texDummy() {
		tex.append("vt 0 0\n");
		texIdx++;
		return texIdx-1;
	}
	
	private int normLeft() {
		norm.append("vn 1 0 0\n");
		normIdx++;
		return normIdx-1;
	}

	private int normRight() {
		norm.append("vn -1 0 0\n");
		normIdx++;
		return normIdx-1;
	}

	private int pos(float x, float y, float z) {
		pos.append(String.format("v %.3f %.3f %.3f\n", x, y, z));
		posIdx++;
		return posIdx-1;
	}

	private int norm(Vector3f n) {
		n.normalize();
		norm.append(String.format("vn %.3f %.3f %.3f\n", n.x, n.y, n.z));
		normIdx++;
		return normIdx-1;
	}
	
	private int norm(float x, float y, float z) {
		return norm(new Vector3f(x, y, z));
	}
	
	private void fvert(int p, int t, int n) {
		f.append(String.format(" %d/%d/%d", p+1, t+1, n+1));
	}
	
	private int quad(int p1, int t1, int p2, int t2, int p3, int t3, int p4, int t4, int n) {
		f.append("f");
		fvert(p1, t1, n);
		fvert(p2, t2, n);
		fvert(p3, t3, n);
		fvert(p4, t4, n);
		f.append("\n");
		fIdx++;
		return fIdx-1;
	}
	
	public void printStairs(int h) {
		final int numSteps = h*3;
		final float steph = 0.175f;
		final float stepd = 0.25f;
		float db = d-0.5f;
		for(int i=0; i<=numSteps; i++) {
			pos(-db, i*steph, -d+i*stepd);
			pos(db, i*steph, -d+i*stepd);
			if(i<numSteps) {
				pos(-db, i*steph, -d+(i+1)*stepd);
				pos(db, i*steph, -d+(i+1)*stepd);
			}

			quad(i*4+1, 0, i*4+0, 0, i*4+2, 0, i*4+3, 0, up);
			if(i<numSteps) {
				quad(i*4+3, 0, i*4+2, 0, i*4+4, 0, i*4+5, 0, fwd);
			}
		}
		int left = normLeft();
		int right = normRight();
		quad(2, 0, 0, 0, numSteps*4, 0, numSteps*4-2, 0, left);
		quad(1, 0, 3, 0, numSteps*4-1, 0, numSteps*4+1, 0, right);
		
		int bottomPosIdx = pos(-d, 0, -d);
		pos(d, 0, -d);
		int topPosIdx = pos(-d, numSteps*steph, -d+numSteps*stepd);
		pos(d, numSteps*steph, -d+numSteps*stepd);
		pos(-d, numSteps*steph, d);
		pos(d, numSteps*steph, d);
		quad(topPosIdx+1, 0, topPosIdx, 0, topPosIdx+2, 0, topPosIdx+3, 0, up);
		
		int slopeNormIdx = norm(0, stepd, -steph);
		quad(0, 0, bottomPosIdx, 0, topPosIdx, 0, numSteps*4, 0, slopeNormIdx);
		quad(bottomPosIdx+1, 0, 1, 0, numSteps*4+1, 0, topPosIdx+1, 0, slopeNormIdx);
		
		texDummy();
		finish(String.format("Stairs%d", h));
	}

	public void printStairsSide(int h) {
		final int numSteps = h*3;
		final float steph = 0.175f;
		final float stepd = 0.25f;
		pos(-d, 0, -d);
		pos(d, 0, -d);
		pos(-d, 0, d);
		pos(d, 0, d);
		int topPosIdx = pos(-d, numSteps*steph, -d+numSteps*stepd);
		pos(d, numSteps*steph, -d+numSteps*stepd);
		pos(-d, numSteps*steph, d);
		pos(d, numSteps*steph, d);
		
		int left = normLeft();
		int right = normRight();
		quad(0, 0, 2, 0, topPosIdx+2, 0, topPosIdx, 0, right);
		quad(3, 0, 1, 0, topPosIdx+1, 0, topPosIdx+3, 0, left);
		
		texDummy();
		finish(String.format("Stairs%dside", h));
	}

	public static void main(String[] args) {
		// new ObjPrinter("assets/stairs2.obj").printStairs(2);
		// new ObjPrinter("assets/stairs2side.obj").printStairsSide(2);
	}

}
