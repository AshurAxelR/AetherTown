package com.xrbpowered.aethertown.world.tiles;

import java.util.Random;

import com.xrbpowered.aethertown.render.BasicGeometry;
import com.xrbpowered.aethertown.render.LevelRenderer;
import com.xrbpowered.aethertown.render.ObjectShader;
import com.xrbpowered.aethertown.render.TerrainBuilder;
import com.xrbpowered.aethertown.render.TexColor;
import com.xrbpowered.aethertown.render.tiles.TileComponent;
import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.utils.Corner;
import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.MathUtils;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.gen.Fences;
import com.xrbpowered.aethertown.world.gen.Fences.FenceType;
import com.xrbpowered.aethertown.world.gen.Tunnels;
import com.xrbpowered.aethertown.world.gen.Tunnels.TunnelInfo;
import com.xrbpowered.aethertown.world.tiles.Street.StreetTile;
import com.xrbpowered.gl.res.mesh.ObjMeshLoader;
import com.xrbpowered.gl.res.texture.Texture;

public class StreetSlope extends TileTemplate {

	public static final StreetSlope template1 = new StreetSlope(1);
	public static final StreetSlope template2 = new StreetSlope(2);
	public static final StreetSlope template4 = new StreetSlope(4);

	private static TileComponent stepsL, stepsR;
	
	public final int h;
	private TileComponent street, side, handrailL, handrailR;
	
	public StreetSlope(int h) {
		this.h = h;
	}
	
	@Override
	public Tile createTile() {
		return new StreetTile(this);
	}
	
	@Override
	public int getGroundY(Tile atile, Corner c) {
		StreetTile tile = (StreetTile) atile;
		if(tile.tunnel!=null)
			return tile.tunnel.getGroundY(c);
		else
			return c==null ? tile.basey : getNoTunnelGroundY(tile, c);
	}

	@Override
	public float getYIn(Tile atile, float sx, float sz, float prevy) {
		StreetTile tile = (StreetTile) atile;
		if(tile.tunnel!=null && Tunnels.isAbove(prevy, tile.tunnel.basey))
			return Tile.ysize*tile.tunnel.basey;
		if(tile.bridge && Bridge.isUnder(prevy, tile.basey-h))
			return tile.level.h.gety(tile.x, tile.z, sx, sz);

		float y0 = Tile.ysize*tile.basey;
		float y1 = Tile.ysize*(tile.basey-h);
		float s = sForXZ(sx, sz, tile.d);
		if(h==4) {
			s = MathUtils.clamp((s-0.25f)/0.75f);
			return MathUtils.lerp(y0, y1, s);
		}
		else if(h==2) {
			s = MathUtils.clamp((s-0.5f)/0.5f);
			return MathUtils.lerp(y0, y1, s);
		}
		else {
			return MathUtils.lerp(y0, y1, s);
		}
	}
	
	public float getYOut(Tile tile, Dir d, float sout, float sx, float sz, float prevy) {
		if(h==1 && tile.getFence(d)==FenceType.stepsOut) {
			float y0 = Tile.ysize*tile.basey;
			float y1 = Tile.ysize*(tile.basey-h);
			float y = Fences.getFenceYOut(tile.basey, sout) - y0;

			float s = sForXZ(sx, sz, tile.d);
			float ys = MathUtils.lerp(y0, y1, s);
			return ys + y;
		}
		else {
			return super.getYOut(tile, d, sout, sx, sz, prevy);
		}
	}
	
	public int getNoTunnelGroundY(Tile tile, Corner c) {
		c = c.rotate(tile.d); // wtf, Corner.rotate not working?
		if(tile.d==Dir.north || tile.d==Dir.south)  
			return (c==Corner.ne || c==Corner.nw) ? tile.basey-h : tile.basey;
		else
			return (c==Corner.ne || c==Corner.nw) ? tile.basey : tile.basey-h;
	}
	
	@Override
	public int getFenceY(Tile tile, Corner c) {
		return getGroundY(tile, c);
	}
	
	@Override
	public int getLightBlockY(Tile atile) {
		StreetTile tile = (StreetTile) atile;
		if(tile.tunnel!=null)
			return tile.tunnel.basey;
		else
			return super.getLightBlockY(tile);
	}

	@Override
	public boolean canExpandFill(Tile tile) {
		return h==1;
	}
	
	@Override
	public void createComponents() {
		Texture handrailTex = new Texture("models/fences/handrail.png", false, true, false);
		if(h==4) {
			street = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs4.obj", 0, 1f, ObjectShader.vertexInfo, null),
					TexColor.get(Street.streetColor));
			side = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs4side.obj", 0, 1f, ObjectShader.vertexInfo, null),
					TexColor.get(TerrainBuilder.wallColor));
			handrailL = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s4l.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
			handrailR = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s4r.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
		}
		else if(h==2) {
			street = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs2.obj", 0, 1f, ObjectShader.vertexInfo, null),
					TexColor.get(Street.streetColor));
			side = new TileComponent(
					ObjMeshLoader.loadObj("models/stairs/stairs2side.obj", 0, 1f, ObjectShader.vertexInfo, null),
					TexColor.get(TerrainBuilder.wallColor));
			handrailL = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s2l.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
			handrailR = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s2r.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
		}
		else {
			street = new TileComponent(
					BasicGeometry.slope(Tile.size, Tile.ysize*h, ObjectShader.vertexInfo, null),
					TexColor.get(Street.streetColor));
			handrailL = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s1l.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
			handrailR = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/handrail_s1r.obj", 0, 1f, ObjectShader.vertexInfo, null),
					handrailTex);
			stepsL = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/steps_out_s1l.obj", 0, 1f, ObjectShader.vertexInfo, null),
					TexColor.get(TerrainBuilder.wallColor));
			stepsR = new TileComponent(
					ObjMeshLoader.loadObj("models/fences/steps_out_s1r.obj", 0, 1f, ObjectShader.vertexInfo, null),
					TexColor.get(TerrainBuilder.wallColor));
		}
	}
	
	@Override
	public boolean finalizeTile(Tile atile, Random random) {
		StreetTile tile = (StreetTile) atile;
		if(h==1 || tile.sub!=null || tile.tunnel!=null)
			return false;
		
		
		final Dir[] sides = { tile.d.ccw(), tile.d.cw() };
		int check = 0;
		for(int i=2; check==0 && i<=4; i++) {
			for(Dir d : sides) {
				Tile adj = tile.getAdj(i*d.dx, i*d.dz);
				if(adj!=null && adj.d==tile.d && adj.t instanceof StreetSlope && ((StreetSlope) adj.t).h>1 && Math.abs(tile.basey-adj.basey)<i*4) {
					check = i;
					break;
				}
			}
		}
		if(check>0) {
			int maxDist = check*8;
			tile.level.map[tile.x][tile.z] = null;
			tile.level.walkingDist.calculate(tile.x+tile.d.dx, tile.z+tile.d.dz, maxDist);
			if(tile.level.walkingDist.map[tile.x-tile.d.dx][tile.z-tile.d.dz]<=maxDist) {
				final Dir[] ds = { tile.d, tile.d.flip() };
				for(Dir d : ds) {
					Tile t = tile.getAdj(d);
					while(t!=null && t.t instanceof StreetSlope) {
						tile.level.map[t.x][t.z] = null;
						t = t.getAdj(d);
					}
				}
				tile.level.heightLimiter.invalidate();
				return true;
			}
			tile.level.map[tile.x][tile.z] = tile;
		}
		return false;
	}
	
	public TunnelInfo checkTunnel(StreetTile tile) {
		if(Tunnels.tunnelWallCondition(tile, tile.d.cw(), h) && Tunnels.tunnelWallCondition(tile, tile.d.ccw(), h)) {
			tile.tunnel = new TunnelInfo(tile);
			return tile.tunnel;
		}
		return null;
	}

	@Override
	public void decorateTile(Tile atile, Random random) {
		StreetTile tile = (StreetTile) atile;
		if(tile.tunnel!=null)
			return;
		
		Street.template.autoAddHillBridge(tile, tile.basey-h);
		if(h==1)
			Street.template.addLamp(tile, random);
		
		Dir dl = tile.d.ccw();
		Dir dr = tile.d.cw();
		int hh = h>1 ? h : 0;
		tile.setFence(dl, Fences.getFenceType(tile, dl, -hh, 0, h));
		tile.setFence(dr, Fences.getFenceType(tile, dr, 0, -hh, h));
	}
	
	@Override
	public void createGeometry(Tile atile, LevelRenderer r) {
		StreetTile tile = (StreetTile) atile;
		Dir dl = tile.d.ccw();
		Dir dr = tile.d.cw();
		if(h>1) {
			side.addInstance(r, new TileObjectInfo(tile, 0, -h, 0));
			if(tile.bridge)
				Street.template.createHillBridge(r, tile, tile.basey-h);
			else
				r.terrain.addWalls(tile.x, tile.z, tile.basey-h);
			// FIXME when needed slope handrails 
			if(tile.getFence(dl)==FenceType.handrail) {
				handrailL.addInstance(r, new TileObjectInfo(tile).rotate(dl));
				Fences.createHandrailPoles(r, tile, dl, -h, 0);
				Fences.handrailPole.addInstance(r, new TileObjectInfo(tile, 0.5f*dl.dx+(0.5f-0.1875f*h)*tile.d.dx, 0, 0.5f*dl.dz+(0.5f-0.1875f*h)*tile.d.dz));
			}
			if(tile.getFence(dr)==FenceType.handrail) {
				handrailR.addInstance(r, new TileObjectInfo(tile).rotate(dr));
				Fences.createHandrailPoles(r, tile, dr, 0, -h);
				Fences.handrailPole.addInstance(r, new TileObjectInfo(tile, 0.5f*dr.dx+(0.5f-0.1875f*h)*tile.d.dx, 0, 0.5f*dr.dz+(0.5f-0.1875f*h)*tile.d.dz));
			}
		}
		else {
			if(tile.bridge) {
				Street.template.createHillBridge(r, tile, tile.basey-h);
				r.terrain.addWall(tile.x, tile.z, dr, tile.basey-h, tile.basey, tile.basey-h, tile.basey-h);
				r.terrain.addWall(tile.x, tile.z, dl, tile.basey-h, tile.basey-h, tile.basey-h, tile.basey);
			}
			else {
				r.terrain.addWall(tile.x, tile.z, dr, tile.basey, tile.basey-h);
				r.terrain.addWall(tile.x, tile.z, dl, tile.basey-h, tile.basey);
			}
			if(tile.getFence(dl)==FenceType.handrail) {
				handrailL.addInstance(r, new TileObjectInfo(tile).rotate(dl));
				Fences.createHandrailPoles(r, tile, dl, -h, 0);
			}
			else if(tile.getFence(dl)==FenceType.stepsOut) {
				stepsL.addInstance(r, new TileObjectInfo(tile).rotate(dl));
			}
			if(tile.getFence(dr)==FenceType.handrail) {
				handrailR.addInstance(r, new TileObjectInfo(tile).rotate(dr));
				Fences.createHandrailPoles(r, tile, dr, 0, -h);
			}
			else if(tile.getFence(dr)==FenceType.stepsOut) {
				stepsR.addInstance(r, new TileObjectInfo(tile).rotate(dr));
			}
			Street.template.createLamp(atile, r, -0.5f);
		}

		if(tile.tunnel!=null) {
			Tunnels.createTunnel(r, tile, tile.tunnel, tile.basey-h);
		}
		else {
			if(tile.getFence(dl)==FenceType.retainWall)
				Fences.retWall.addInstance(r, new TileObjectInfo(tile, 0, -h, 0).rotate(dl));
			if(tile.getFence(dr)==FenceType.retainWall)
				Fences.retWall.addInstance(r, new TileObjectInfo(tile, 0, -h, 0).rotate(dr));
		}
		
		street.addInstance(r, new TileObjectInfo(tile, 0, -h, 0));
	}
	
	public static TileTemplate getTemplate(int h) {
		switch(h) {
			case 0:
				return Street.template;
			case 1:
				return template1;
			case 2:
				return template2;
			case 4:
				return template4;
			default:
				throw new RuntimeException("No template for slope "+h);
		}
	}

}
