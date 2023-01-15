package com.xrbpowered.aethertown.world.gen.plot;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.Tile.SubInfo;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.region.HouseRole;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.Street;

public class HouseGenerator extends HouseGeneratorBase {

	private static final int[][] sizes = {
		{0, 1, 1}, {0, 1, 2}, {0, 1, 3}, {1, 0, 1}, {1, 0, 2}, {1, 0, 3},
		{1, 1, 1}, {1, 2, 1}, {2, 1, 1}
	};
	private static final WRandom sizesw = new WRandom(
		2, 0.75, 0.1, 2, 0.75, 0.1,
		1.5, 0.25, 0.25
	);
	
	public int index = -1;
	public HouseRole role = null;
	
	public static HouseGeneratorBase select(Level level) {
		if(level.churches.size()<(level.houseCount+26)/30)
			return new ChurchGenerator();
		else
			return new HouseGenerator();
	}
	
	@Override
	public String getInfo() {
		return String.format("%d, %s: %s", index+1, startToken.level.name, role.title);
	}
	
	@Override
	protected boolean findSize(Random random) {
		int[] s = sizes[sizesw.next(random)];
		setSize(s[0], s[1], s[2]);
		int w = left+right;
		if(w==fwd)
			alignStraight = random.nextBoolean();
		else
			alignStraight = (fwd>w);
		return true;
	}
	
	@Override
	protected Tile placeAt(Token t, int i, int j, Random random) {
		Tile tile = HouseT.template.createTile().makeSub(this, i, j);
		tile.place(t);
		HeightLimiter.updateAt(t, HeightLimiter.maxWall, 2, 3);
		return tile;
	}
	
	@Override
	protected void place(Random random) {
		super.place(random);
		startToken.level.houseCount++;
	}
	
	@Override
	public void remove() {
		super.remove();
		startToken.level.houseCount--;
	}

	private static void listHousesRec(Level level, Random random, int x, int z, boolean[][] visited, ArrayList<HouseGenerator> houses) {
		if(!level.isInside(x, z) || visited[x][z])
			return;
		visited[x][z] = true;
		Tile tile = level.map[x][z];
		if(tile==null)
			return;
		if(Street.isAnyPath(tile.t)) {
			for(Dir d : Dir.shuffle(random)) {
				Tile adj = tile.getAdj(d);
				if(adj!=null && adj.t==HouseT.template && adj.d==d && (adj.sub.parent instanceof HouseGenerator)) {
					SubInfo st = adj.sub;
					HouseGenerator house = (HouseGenerator)st.parent;
					if(st.i==0 && st.j==0 && house.index<0) {
						house.index = houses.size();
						houses.add(house);
					}
				}
			}
			for(Dir d : Dir.values()) {
				listHousesRec(level, random, x+d.dx, z+d.dz, visited, houses);
			}
		}
	}
	
	public static ArrayList<HouseGenerator> listHouses(Level level, Random random) {
		ArrayList<HouseGenerator> houses = new ArrayList<>();
		boolean[][] visited = new boolean[level.levelSize][level.levelSize];
		listHousesRec(level, random, level.getStartX(), level.getStartZ(), visited, houses);
		System.out.printf("%d houses\n", houses.size());
		return houses;
	}
	
}
