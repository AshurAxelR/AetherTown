package com.xrbpowered.aethertown.world.gen.plot.houses;

import java.util.Random;

import com.xrbpowered.aethertown.actions.HouseTileAction;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.IllumPattern;

class ShopRole extends HouseRole {
	
	ShopRole(String title, HouseTileAction action, IllumPattern illum) {
		super(title, colorShop, action,
			new ArchitectureStyle.BlankGroundNotFront(1, ArchitectureTileSet.shopSet).setIllum(illum, IllumLayer.shopping),
			new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(illum, IllumLayer.shopping),
			new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(illum, IllumLayer.shopping, null, null),
			new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(illum, IllumLayer.shopping, null, null),
			new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(illum, IllumLayer.shopping, IllumPattern.office, null)
		);
	}
	
	@Override
	public ArchitectureStyle arch(HouseGenerator house, Random random) {
		if(house.getFootprint()>=8) {
			if(!isPark(house)) {
				house.addRole = addCityRole(house, random);
				return house.addRole==residential ? arch[2] : arch[4];
			}
			else
				return arch[0];
		}
		if(house.getFootprint()>=6 && !isPark(house) && !isCityHouse(house, random)) {
			house.addRole = addCityRole(house, random);
			return house.addRole==residential ? arch[3] : arch[4];
		}
		else
			return arch[1];
	}
}