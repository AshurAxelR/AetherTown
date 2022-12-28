package com.xrbpowered.aethertown.render;

import static com.xrbpowered.aethertown.render.TerrainBuilder.chunkSize;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.HeightMap;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder;
import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder.Edge;
import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder.Face;
import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder.Quad;
import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder.Triangle;
import com.xrbpowered.gl.res.mesh.AdvancedMeshBuilder.Vertex;
import com.xrbpowered.gl.res.mesh.StaticMesh;
import com.xrbpowered.gl.res.shader.ActorShader;
import com.xrbpowered.gl.res.texture.Texture;
import com.xrbpowered.gl.scene.StaticMeshActor;

public class TerrainChunkBuilder {

	private static final boolean enableSmooth = false;
	private static final boolean enableDebugNormals = false;
	
	public final Level level;
	public final int cx, cz;
	
	private AdvancedMeshBuilder grassBuilder, cliffBuilder, wallBuilder;
	private Vertex[][] grassVertexMap;
	private Vector3f[][] debugNormals;
	
	public BufferedImage color;
	
	public TerrainChunkBuilder(Level level, int cx, int cz) {
		this.level = level;
		this.cx = cx;
		this.cz = cz;
		
		color = new BufferedImage(chunkSize, chunkSize, BufferedImage.TYPE_INT_RGB);
		grassBuilder = new AdvancedMeshBuilder(ObjectShader.vertexInfo, null);
		cliffBuilder = new AdvancedMeshBuilder(ObjectShader.vertexInfo, null);
		wallBuilder = new AdvancedMeshBuilder(ObjectShader.vertexInfo, null);
		if(enableSmooth) {
			grassVertexMap = new Vertex[chunkSize+1][chunkSize+1];
			if(enableDebugNormals)
				debugNormals = new Vector3f[chunkSize+1][chunkSize+1];
		}
	}

	private static Vector3f pos(Vector3i p) {
		return new Vector3f(p.x*Tile.size-Tile.size/2, p.y*Tile.ysize, p.z*Tile.size-Tile.size/2);
	}
	
	private void initVertex(Vertex v, Vector3f p, Vector3f norm) {
		v.setPosition(p);
		v.setNormal(norm);
		v.setTexCoord(
			((p.x/Tile.size+0.5f) - cx*chunkSize)/(float)chunkSize,
			((p.z/Tile.size+0.5f) - cz*chunkSize)/(float)chunkSize
		);
	}
	
	private void addTriangleFlat(AdvancedMeshBuilder b, Vector3i p0, Vector3i p1, Vector3i p2) {
		Vector3f pos0 = pos(p0);
		Vector3f pos1 = pos(p1);
		Vector3f pos2 = pos(p2);
		Vector3f norm = new Vector3f(pos1).sub(pos0).cross(new Vector3f(pos2).sub(pos0)).normalize();
		Face face = new Triangle(b.addVertex(), b.addVertex(), b.addVertex());
		initVertex(face.vertices[0], pos0, norm);
		initVertex(face.vertices[1], pos1, norm);
		initVertex(face.vertices[2], pos2, norm);
		b.add(face);
	}

	private Vertex smoothVertex(HeightMap h, Vertex v, Vector3i p) {
		Vector3f norm = new Vector3f(0, 0, 0);
		if(p.x-1>=0 && Math.abs(h.y[p.x][p.z]-h.y[p.x-1][p.z])<10) {
			norm.x -= (h.y[p.x][p.z]-h.y[p.x-1][p.z])*Tile.ysize;
			norm.y += Tile.size;
		}
		if(p.x+1<=level.levelSize && Math.abs(h.y[p.x][p.z]-h.y[p.x+1][p.z])<10) {
			norm.x += (h.y[p.x][p.z]-h.y[p.x+1][p.z])*Tile.ysize;
			norm.y += Tile.size;
		}
		if(p.z-1>=0 && Math.abs(h.y[p.x][p.z]-h.y[p.x][p.z-1])<10) {
			norm.z -= (h.y[p.x][p.z]-h.y[p.x][p.z-1])*Tile.ysize;
			norm.y += Tile.size;
		}
		if(p.z+1<=level.levelSize && Math.abs(h.y[p.x][p.z]-h.y[p.x][p.z+1])<10) {
			norm.z += (h.y[p.x][p.z]-h.y[p.x][p.z+1])*Tile.ysize;
			norm.y += Tile.size;
		}
		norm.y *= 0.5f; // why normals need exaggeration?
		if(Math.abs(norm.y)<0.01f)
			norm.set(0, 1, 0);
		else
			norm.normalize();
		
		if(enableDebugNormals)
			debugNormals[p.x - cx*chunkSize][p.z - cz*chunkSize] = norm;
		
		v.setPosition(pos(p));
		v.setNormal(norm);
		v.setTexCoord(
				(p.x - cx*chunkSize)/(float)chunkSize,
				(p.z - cz*chunkSize)/(float)chunkSize
			);
		return v;
	}

	private Vertex smoothVertex(HeightMap h, AdvancedMeshBuilder b, Vertex[][] v, Vector3i p) {
		int x = p.x - cx*chunkSize;
		int z = p.z - cz*chunkSize;
		if(v[x][z]==null)
			v[x][z] = smoothVertex(h, b.addVertex(), p);
		return v[x][z];
	}
	
	private void addTriangleSmooth(HeightMap h, AdvancedMeshBuilder b, Vertex[][] v, Vector3i p0, Vector3i p1, Vector3i p2) {
		b.add(new Triangle(
			smoothVertex(h, b, v, p0),
			smoothVertex(h, b, v, p1),
			smoothVertex(h, b, v, p2)
		));
	}

	private void addHillTriangle(int d, Vector3i p0, Vector3i p1, Vector3i p2) {
		if(d>=10)
			addTriangleFlat(cliffBuilder, p0, p1, p2);
		else if(enableSmooth)
			addTriangleSmooth(level.h, grassBuilder, grassVertexMap, p0, p1, p2);
		else
			addTriangleFlat(grassBuilder, p0, p1, p2);
	}

	private void addHillTriangle(Vector3i p0, Vector3i p1, Vector3i p2) {
		addHillTriangle(MathUtils.maxDelta(p0.y, p1.y, p2.y), p0, p1, p2);
	}

	public void addHillTile(Color c, int x, int z, int y00, int y01, int y10, int y11, boolean diag) {
		Vector3i p00 = new Vector3i(x, y00, z);
		Vector3i p01 = new Vector3i(x, y01, z+1);
		Vector3i p10 = new Vector3i(x+1, y10, z);
		Vector3i p11 = new Vector3i(x+1, y11, z+1);
		if(diag) {
			addHillTriangle(p00, p01, p11);
			addHillTriangle(p00, p11, p10);
		}
		else {
			addHillTriangle(p00, p01, p10);
			addHillTriangle(p01, p11, p10);
		}
		color.setRGB(x - cx*chunkSize, z - cz*chunkSize, c.getRGB());
	}

	private void addTriangleVertical(AdvancedMeshBuilder b, Vector3i p0, Vector3i p1, Vector3i p2, Dir d) {
		Vector3f pos0 = pos(p0);
		Vector3f pos1 = pos(p1);
		Vector3f pos2 = pos(p2);
		Vector3f norm = new Vector3f(d.dx, 0, d.dz);
		Face face = new Triangle(b.addVertex(), b.addVertex(), b.addVertex());
		initVertex(face.vertices[0], pos0, norm); // FIXME vertical tex coord
		initVertex(face.vertices[1], pos1, norm);
		initVertex(face.vertices[2], pos2, norm);
		b.add(face);
	}
	
	private void addQuadVertical(AdvancedMeshBuilder b, Vector3i p0, Vector3i p1, Vector3i p2, Vector3i p3, Dir d) {
		Vector3f pos0 = pos(p0);
		Vector3f pos1 = pos(p1);
		Vector3f pos2 = pos(p2);
		Vector3f pos3 = pos(p3);
		Vector3f norm = new Vector3f(d.dx, 0, d.dz);
		Face face = new Quad(b.addVertex(), b.addVertex(), b.addVertex(), b.addVertex());
		initVertex(face.vertices[0], pos0, norm); // FIXME vertical tex coord
		initVertex(face.vertices[1], pos1, norm);
		initVertex(face.vertices[2], pos2, norm);
		initVertex(face.vertices[3], pos3, norm);
		b.add(face);
	}

	private void addWall(int x0, int z0, int x1, int z1, Dir d, int y00, int y01, int y10, int y11) {
		Vector3i p00 = new Vector3i(x0, y00, z0);
		Vector3i p01 = new Vector3i(x0, y01, z0);
		Vector3i p10 = new Vector3i(x1, y10, z1);
		Vector3i p11 = new Vector3i(x1, y11, z1);
		boolean e0 = y00<y01;
		boolean e1 = y10<y11;
		if(e0 && e1) {
			addQuadVertical(wallBuilder, p10, p11, p01, p00, d);
		}
		else if(e0) {
			addTriangleVertical(wallBuilder, p11, p01, p00, d);
		}
		else if(e1) {
			addTriangleVertical(wallBuilder, p10, p11, p01, d);
		}
	}
	
	public void addWall(int x, int z, Dir d, int y00, int y01, int y10, int y11) {
		Corner c0 = d.leftCorner();
		Corner c1 = d.rightCorner();
		int x0 = x+c0.tx+1;
		int z0 = z+c0.tz+1;
		int x1 = x+c1.tx+1;
		int z1 = z+c1.tz+1;
		addWall(x0, z0, x1, z1, d, y00, y01, y10, y11);
	}
	
	public void addWall(int x, int z, Dir d, int basey0, int basey1) {
		Corner c0 = d.leftCorner();
		Corner c1 = d.rightCorner();
		int x0 = x+c0.tx+1;
		int z0 = z+c0.tz+1;
		int x1 = x+c1.tx+1;
		int z1 = z+c1.tz+1;
		addWall(x0, z0, x1, z1, d, level.h.y[x0][z0], basey0, level.h.y[x1][z1], basey1);
	}

	private StaticMesh createDebugMesh(float debugVectorSize) {
		Vector3f up = new Vector3f(0, 1, 0);
		AdvancedMeshBuilder debug = new AdvancedMeshBuilder(ObjectShader.vertexInfo, null);
		for(int x=0; x<=level.levelSize; x++)
			for(int z=0; z<=level.levelSize; z++) {
				Vector3f o = pos(new Vector3i(x, level.h.y[x][z], z));
				Vector3f v = debugNormals[x][z];
				if(v!=null) {
					Edge e = new Edge(debug.addVertex(), debug.addVertex());
					e.vertices[0].setNormal(up).setPosition(o);
					e.vertices[1].setNormal(up).setPosition(new Vector3f(v).mul(debugVectorSize).add(o));
					debug.add(e);
				}
			}
		return debug.create();
	}

	public static StaticMeshActor makeActor(final StaticMesh mesh, final ActorShader shader, final Texture diffuse) {
		StaticMeshActor actor =  new StaticMeshActor() {
			@Override
			protected void bindTextures() {
				Texture.bindAll(2, textures);
			}
		};
		actor.setMesh(mesh);
		actor.setShader(shader);
		actor.setTextures(new Texture[] {diffuse});
		return actor;
	}
	
	public void createActors(ActorShader shader, ArrayList<StaticMeshActor> actors) {
		if(enableDebugNormals)
			actors.add(makeActor(createDebugMesh(Tile.size/2), shader, new Texture(Color.RED)));

		actors.add(makeActor(grassBuilder.create(), shader, new Texture(color, true, false)));
		actors.add(makeActor(cliffBuilder.create(), shader, new Texture(TerrainBuilder.cliffColor)));
		actors.add(makeActor(wallBuilder.create(), shader, new Texture(TerrainBuilder.wallColor)));
	}
}
