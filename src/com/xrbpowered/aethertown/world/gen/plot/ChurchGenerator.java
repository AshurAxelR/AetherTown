package com.xrbpowered.aethertown.world.gen.plot;

import java.util.Random;

import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.tiles.ChurchT;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;

public class ChurchGenerator extends HouseGeneratorBase {

	public String name;
	
	@Override
	public String getInfo() {
		return String.format("St. %s's Cathederal", name);
	}
	
	@Override
	protected boolean findSize(Random random) {
		setSize(1, 2, 5+random.nextInt(2));
		alignStraight = true;
		marginFront = 1;
		marginBack = 2;
		marginLeft = 1;
		marginRight = 1;
		return true;
	}

	@Override
	protected Tile placeAt(Token t, int i, int j, Random random) {
		TileTemplate temp;
		if(i==-left || i==right || j>fwd-marginBack)
			temp = Park.template;
		else if(j==0)
			temp = Plaza.template;
		else {
			Tile tile = ChurchT.template.createTile().makeSub(this, i, j);
			tile.place(t);
			HeightLimiter.updateAt(t, HeightLimiter.maxWall, 2, 3);
			return tile;
		}
		return temp.forceGenerate(t, random).makeSub(this, i, j);
	}
	
	private static String selectName(Level level, Random random) {
		String name = LevelNames.nextSaint(random);
		for(ChurchGenerator church : level.churches) {
			if(church.name.equals(name))
				return selectName(level, random);
		}
		return name;
	}
	
	@Override
	protected void place(Random random) {
		super.place(random);
		name = selectName(startToken.level, random);
		startToken.level.churches.add(this);
	}
	
	@Override
	public void remove() {
		super.remove();
		startToken.level.churches.remove(this);
	}

}
