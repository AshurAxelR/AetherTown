package com.xrbpowered.aethertown.state;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;

public class TileVisit extends TileRef {
	
	public final int templateHash;
	
	public TileVisit(int x, int z, int templateHash) {
		super(x, z);
		this.templateHash = templateHash;
	}
	
	public TileVisit(Tile tile) {
		super(tile);
		this.templateHash = calcTileTemplateHash(tile.t);
	}
	
	public boolean isValid(Level level) {
		if(!level.isInside(x, z))
			return false;
		Tile tile = level.map[x][z];
		if(TileRef.calcTileTemplateHash(tile.t)!=templateHash)
			return false;
		return true;
	}
	
	public static TileVisit load(DataInputStream in) throws IOException {
		int x = in.readShort();
		int z = in.readShort();
		int hash = in.readInt();
		return new TileVisit(x, z, hash);
	}

	public static void save(DataOutputStream out, TileVisit ref) throws IOException {
		out.writeShort(ref.x);
		out.writeShort(ref.z);
		out.writeInt(ref.templateHash);
	}
	
	public static TileVisit loadNullable(DataInputStream in) throws IOException {
		if(in.readBoolean())
			return load(in);
		else
			return null;
	}
	
	public static void saveNullable(DataOutputStream out, TileVisit ref) throws IOException {
		if(ref!=null) {
			out.writeBoolean(true);
			save(out, ref);
		}
		else
			out.writeBoolean(false);
	}

}