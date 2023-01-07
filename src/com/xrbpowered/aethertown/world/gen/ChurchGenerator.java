package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.world.HeightLimiter;
import com.xrbpowered.aethertown.world.TileTemplate;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.region.LevelNames;
import com.xrbpowered.aethertown.world.tiles.HouseT;
import com.xrbpowered.aethertown.world.tiles.Park;
import com.xrbpowered.aethertown.world.tiles.Plaza;

public class ChurchGenerator extends HouseGeneratorBase {

	public String name;
	
	@Override
	public String getInfo() {
		return String.format("St. %s Cathederal", name);
	}
	
	@Override
	protected boolean findSize(Random random) {
		setSize(1, 2, 5+random.nextInt(2));
		alignStraight = true;
		marginFront = 1;
		marginBack = 2;
		marginLeft = 1;
		marginRight = 1;
		illum = true;
		return true;
	}

	@Override
	protected void placeAt(Token t, int i, int j, Random random) {
		TileTemplate temp;
		if(i==-left || i==right || j>fwd-marginBack)
			temp = Park.template;
		else if(j==0)
			temp = Plaza.template;
		else { 
			HouseT.template.createTile().makeSub(this, i, j).place(t);
			HeightLimiter.updateAt(t, HeightLimiter.maxWall, 2, 3);
			return;
		}
		temp.forceGenerate(t, random).makeSub(this, i, j);
	}
	
	@Override
	protected void place(Random random) {
		super.place(random);
		startToken.level.churchCount++;
		name = LevelNames.nextSaint(random);
	}
	
	@Override
	public void remove() {
		super.remove();
		startToken.level.churchCount--;
	}

}
