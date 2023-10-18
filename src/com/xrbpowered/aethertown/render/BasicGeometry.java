package com.xrbpowered.aethertown.render;

import org.joml.Vector3f;

import com.xrbpowered.gl.res.mesh.FastMeshBuilder;
import com.xrbpowered.gl.res.mesh.FastMeshBuilder.Vertex;
import com.xrbpowered.gl.res.mesh.MeshBuilder.Options;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.VertexInfo;

public abstract class BasicGeometry {

	public static StaticMesh wall(float size, float h, VertexInfo info, Options options) {
		float d = size / 2f;
		Vector3f norm = new Vector3f();
		FastMeshBuilder mb = new FastMeshBuilder(info, options, 4, 6);
		norm.set(0, 0, -1);
		mb.getVertex(0).setPosition(-d, 0, -d).setTexCoord(1, 1).setNormal(norm);
		mb.getVertex(1).setPosition(-d, h, -d).setTexCoord(1, 0).setNormal(norm);
		mb.getVertex(2).setPosition(d, h, -d).setTexCoord(0, 0).setNormal(norm);
		mb.getVertex(3).setPosition(d, 0, -d).setTexCoord(0, 1).setNormal(norm);
		mb.addQuad(0, 1, 2, 3);
		return mb.create();
	}
	
	public static StaticMesh slopeSideLeft(float size, float h, VertexInfo info, Options options) {
		float d = size / 2f;
		Vector3f norm = new Vector3f();
		FastMeshBuilder mb = new FastMeshBuilder(info, options, 4, 6);
		norm.set(0, 0, -1);
		mb.getVertex(0).setPosition(-d, 0, -d).setTexCoord(1, 1).setNormal(norm);
		mb.getVertex(1).setPosition(-d, h, -d).setTexCoord(0, 0).setNormal(norm);
		mb.getVertex(2).setPosition(d, 0, -d).setTexCoord(0, 1).setNormal(norm);
		mb.addTriangle(0, 1, 2);
		return mb.create();
	}
	
	public static StaticMesh slopeSideRight(float size, float h, VertexInfo info, Options options) {
		float d = size / 2f;
		Vector3f norm = new Vector3f();
		FastMeshBuilder mb = new FastMeshBuilder(info, options, 4, 6);
		norm.set(0, 0, -1);
		mb.getVertex(0).setPosition(-d, 0, -d).setTexCoord(1, 1).setNormal(norm);
		mb.getVertex(1).setPosition(d, h, -d).setTexCoord(0, 0).setNormal(norm);
		mb.getVertex(2).setPosition(d, 0, -d).setTexCoord(0, 1).setNormal(norm);
		mb.addTriangle(0, 1, 2);
		return mb.create();
	}

	public static StaticMesh slope(float size, float h, VertexInfo info, Options options) {
		float d = size / 2f;
		Vector3f norm = new Vector3f();
		FastMeshBuilder mb = new FastMeshBuilder(info, options, 4, 6);
		norm.set(0, d, -h).normalize();
		mb.getVertex(0).setPosition(-d, 0, -d).setTexCoord(0, 0).setNormal(norm);
		mb.getVertex(1).setPosition(-d, h, d).setTexCoord(0, 1).setNormal(norm);
		mb.getVertex(2).setPosition(d, h, d).setTexCoord(1, 1).setNormal(norm);
		mb.getVertex(3).setPosition(d, 0, -d).setTexCoord(1, 0).setNormal(norm);
		mb.addQuad(0, 1, 2, 3);
		return mb.create();
	}

	public static StaticMesh box(float size, float h, float pivotYRatio, VertexInfo info, Options options) {
		float d = size / 2f;
		Vector3f norm = new Vector3f();
		Vector3f tan = new Vector3f();
		FastMeshBuilder mb = new FastMeshBuilder(info, options, 4*4, 4*6);
		
		float y0 = -h/2f-pivotYRatio*h/2f;
		float y1 = h/2f-pivotYRatio*h/2f;
		
		norm.set(0, 0, -1);
		tan.set(-1, 0, 0);
		mb.getVertex(0).setPosition(-d, y0, -d).setTexCoord(1, 1).setNormal(norm);
		mb.getVertex(1).setPosition(-d, y1, -d).setTexCoord(1, 0).setNormal(norm);
		mb.getVertex(2).setPosition(d, y1, -d).setTexCoord(0, 0).setNormal(norm);
		mb.getVertex(3).setPosition(d, y0, -d).setTexCoord(0, 1).setNormal(norm);

		norm.set(0, 0, 1);
		tan.set(1, 0, 0);
		mb.getVertex(4).setPosition(d, y0, d).setTexCoord(1, 1).setNormal(norm);
		mb.getVertex(5).setPosition(d, y1, d).setTexCoord(1, 0).setNormal(norm);
		mb.getVertex(6).setPosition(-d, y1, d).setTexCoord(0, 0).setNormal(norm);
		mb.getVertex(7).setPosition(-d, y0, d).setTexCoord(0, 1).setNormal(norm);

		norm.set(-1, 0, 0);
		tan.set(0, 1, 0);
		mb.getVertex(8).setPosition(-d, y0, d).setTexCoord(1, 1).setNormal(norm);
		mb.getVertex(9).setPosition(-d, y1, d).setTexCoord(1, 0).setNormal(norm);
		mb.getVertex(10).setPosition(-d, y1, -d).setTexCoord(0, 0).setNormal(norm);
		mb.getVertex(11).setPosition(-d, y0, -d).setTexCoord(0, 1).setNormal(norm);

		norm.set(1, 0, 0);
		tan.set(0, 1, 0);
		mb.getVertex(12).setPosition(d, y0, -d).setTexCoord(1, 1).setNormal(norm);
		mb.getVertex(13).setPosition(d, y1, -d).setTexCoord(1, 0).setNormal(norm);
		mb.getVertex(14).setPosition(d, y1, d).setTexCoord(0, 0).setNormal(norm);
		mb.getVertex(15).setPosition(d, y0, d).setTexCoord(0, 1).setNormal(norm);
		
		for(int i=0; i<4*4; i+=4)
			mb.addQuad(i+0, i+1, i+2, i+3);
		
		return mb.create();
	}
	
	public static StaticMesh sphere(float r, int segm, float pivotYRatio, VertexInfo info) {
		int i, j;
		
		float[] sin = new float[segm*2+1];
		float[] cos = new float[segm*2+1];
		float ai;
		float da = (float) Math.PI / (float) segm;
		for(i=0, ai = 0; i<=segm*2; i++, ai += da) {
			sin[i] = (float) Math.sin(ai);
			cos[i] = (float) Math.cos(ai);
		}
		
		FastMeshBuilder mb = new FastMeshBuilder(info, null, (segm+1)*(segm*2+1), segm*segm*2*6);
		
		Vector3f v = new Vector3f();
		int index = 0;
		for(i=0; i<=segm*2; i++) {
			for(j=0; j<=segm; j++) {
				Vertex vertex = mb.getVertex(index);
				float r0 = r * sin[j];
				v.y = r * cos[j];
				v.x = -r0 * cos[i];
				v.z = r0 * sin[i];
				vertex.setPosition(v.x, v.y-pivotYRatio*r, v.z);
				vertex.setNormal(v.x, v.y, v.z);
				vertex.setTexCoord(i / (float) segm, j / (float) segm);
				index++;
			}
		}
		
		for(i=0; i<segm*2; i++) {
			for(j=0; j<segm; j++) {
				mb.addQuad(
					(i+0) * (segm+1) + (j+0),
					(i+0) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+1),
					(i+1) * (segm+1) + (j+0)
				);
			}
		}
		
		return mb.create();
	}

	public static StaticMesh cylinder(float r, int segm, float h, float pivotYRatio, VertexInfo info) {
		int i;
		
		float[] sin = new float[segm*2+1];
		float[] cos = new float[segm*2+1];
		float ai;
		float da = (float) Math.PI / (float) segm;
		for(i=0, ai = 0; i<=segm*2; i++, ai += da) {
			sin[i] = (float) Math.sin(ai);
			cos[i] = (float) Math.cos(ai);
		}
		
		FastMeshBuilder mb = new FastMeshBuilder(info, null, 2*(segm*2+1), segm*2*6);
		
		Vector3f v = new Vector3f();
		int index = 0;
		for(i=0; i<=segm*2; i++) {
			Vertex vertex = mb.getVertex(index);
			v.y = h/2f;
			v.x = -r * cos[i];
			v.z = r * sin[i];
			vertex.setPosition(v.x, v.y-pivotYRatio*h/2f, v.z);
			vertex.setNormal(v.x/r, 0, v.z/r);
			vertex.setTexCoord(i / (float) segm, 0);
			index++;
			
			vertex = mb.getVertex(index);
			v.y = -h/2f;
			v.x = -r * cos[i];
			v.z = r * sin[i];
			vertex.setPosition(v.x, v.y-pivotYRatio*h/2f, v.z);
			vertex.setNormal(v.x, 0, v.z);
			vertex.setTexCoord(i / (float) segm, 1);
			index++;
		}
		
		for(i=0; i<segm*2; i++) {
			mb.addQuad(
				(i+1) * 2 + 1,
				(i+1) * 2 + 0,
				(i+0) * 2 + 0,
				(i+0) * 2 + 1
			);
		}
		
		return mb.create();
	}
	
	public static StaticMesh doubleCone(float r, int segm, float h0, float h1, float h2, VertexInfo info) {
		int i;
		float h01 = h1-h0;
		float h02 = h2-h0;
		
		float[] sin = new float[segm*2+1];
		float[] cos = new float[segm*2+1];
		float ai;
		float da = (float) Math.PI / (float) segm;
		for(i=0, ai = 0; i<=segm*2; i++, ai += da) {
			sin[i] = (float) Math.sin(ai);
			cos[i] = (float) Math.cos(ai);
		}
		
		FastMeshBuilder mb = new FastMeshBuilder(info, null, 2*2*(segm*2+1), 2*segm*2*3);
		
		Vector3f v = new Vector3f();
		int index = 0;
		for(i=0; i<=segm*2; i++) {
			Vertex vertex = mb.getVertex(index);
			vertex.setPosition(0, h1, 0);
			vertex.setNormal(0, 1, 0);
			vertex.setTexCoord((i+0.5f) / (float) segm, 0);
			index++;

			vertex = mb.getVertex(index);
			v.y = h0;
			v.x = -r * cos[i];
			v.z = r * sin[i];
			vertex.setPosition(v.x, v.y, v.z);
			vertex.setNormal(-h01*cos[i], 0, h01*sin[i]);
			vertex.setTexCoord(i / (float) segm, 1);
			index++;
			
			vertex = mb.getVertex(index);
			vertex.setPosition(0, h2, 0);
			vertex.setNormal(0, -1, 0);
			vertex.setTexCoord((i+0.5f) / (float) segm, 0);
			index++;

			vertex = mb.getVertex(index);
			v.y = h0;
			v.x = -r * cos[i];
			v.z = r * sin[i];
			vertex.setPosition(v.x, v.y, v.z);
			vertex.setNormal(h02*cos[i], 0, -h02*sin[i]);
			vertex.setTexCoord(i / (float) segm, 1);
			index++;
		}
		
		for(i=0; i<segm*2; i++) {
			mb.addTriangle(
				(i+1) * 4 + 1,
				(i+0) * 4 + 0,
				(i+0) * 4 + 1
			);
			mb.addTriangle(
				(i+0) * 4 + 3,
				(i+0) * 4 + 2,
				(i+1) * 4 + 3
			);
		}
		
		return mb.create();
	}
}
