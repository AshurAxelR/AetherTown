package com.xrbpowered.aethertown.world.gen.plot.houses;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.actions.HouseTileAction;
import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.world.gen.plot.houses.ArchitectureTileSet.DoorInfo;

public class LocalShopRole extends HouseRole {
	
	private boolean addOffice;
	
	LocalShopRole(String title, Color color, HouseTileAction action, final DoorInfo door, boolean addOffice) {
		super(title, color, action,
			new ArchitectureStyle.BlankGroundNotFront(1, ArchitectureTileSet.shopSet).setIllum(IllumPattern.shop).setDoorInfo(door),
			new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.shop, (IllumPattern)null).setDoorInfo(door),
			new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.shop, (IllumPattern)null).setDoorInfo(door),
			new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop, IllumPattern.office).setDoorInfo(door)
		);
		this.addOffice = addOffice;
	}

	@Override
	public ArchitectureStyle arch(HouseGenerator house, Random random) {
		if(isPark(house))
			return arch[0];
		else {
			house.addRole =  addOffice ? addCityRole(house, random) : residential;
			if(house.addRole==residential)
				return house.getFootprint()<=4 || isCityHouse(house, random) ? arch[2] : arch[1];
			else
				return arch[3];
		}
	}
}