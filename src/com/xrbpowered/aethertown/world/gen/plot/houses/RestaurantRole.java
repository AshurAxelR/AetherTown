package com.xrbpowered.aethertown.world.gen.plot.houses;

import java.util.Random;

import com.xrbpowered.aethertown.actions.HouseTileAction;
import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.world.gen.plot.houses.ArchitectureTileSet.DoorInfo;

public class RestaurantRole extends HouseRole {
	
	RestaurantRole(String title, DoorInfo door) {
		super(title, colorFood, HouseTileAction.restaurant,
			new ArchitectureStyle.BlankGroundBack(1, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant).setDoorInfo(door),
			new ArchitectureStyle.BlankGroundBack(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant).setDoorInfo(door),
			new ArchitectureStyle.BlankGroundBack(2, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.restaurant, (IllumPattern)null).setDoorInfo(door),
			new ArchitectureStyle.BlankGroundBack(3, ArchitectureTileSet.officeSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant, IllumPattern.office).setDoorInfo(door)
		);
	}
	
	@Override
	public ArchitectureStyle arch(HouseGenerator house, Random random) {
		if(house.getFootprint()>=8 || house.getFootprint()>=6 && random.nextInt(3)>0) {
			if(!isPark(house)) {
				house.addRole = addCityRole(house, random);
				return house.addRole==residential ? arch[2] : arch[3];
			}
			else
				return arch[0];
		}
		else
			return arch[1];
	}
}