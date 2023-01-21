package com.xrbpowered.aethertown.world.region;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;

public abstract class HouseAssignment {

	public final Level level;
	
	protected int countRes;
	
	private HouseAssignment(Level level) {
		this.level = level;
	}
	
	protected void assignHouse(int index, HouseRole role) {
		HouseGenerator house = level.houses.get(index);
		house.role = role;
		if(role==HouseRole.residential) {
			countRes++;
			house.illum = false;
		}
		else {
			house.illum = true;
			// System.out.printf("%d: %s\n", index+1, role.title);
		}
	}
	
	public abstract HouseRole assignNext(int index, Random random);
	
	public void assign(Random random) {
		countRes = 0;
		for(int i=0; i<level.houseCount; i++)
			assignHouse(i, assignNext(i, random));
	}
	
	public static int levelRank(int numHouses) {
		if(numHouses<5)
			return 0;
		else if(numHouses<15)
			return 1;
		else if(numHouses<40)
			return 2;
		else if(numHouses<100)
			return 3;
		else
			return 4;
	}

	private static class Village extends HouseAssignment {
		private Shuffle shInit = new Shuffle(6);
		private WRandom wDown = new WRandom(3, 2, 1, 0.2, 0.2, 1);
		private boolean hasMuseum = false;
		private int countConv = 0;
		private WRandom wConv = new WRandom(3, 1);
		private int countComm = 0;
		private WRandom wComm = new WRandom(1, 1.5, 0.3, 0.3, 1, 0.2, 0.2);
		private int countPostOffice = 0;
		
		public Village(Level level) {
			super(level);
		}
		
		@Override
		public HouseRole assignNext(int index, Random random) {
			if(index==0)
				return HouseRole.civicCentre;
			else if(index<7) {
				switch(shInit.next(random)) {
					case 0: return HouseRole.supermarket;
					case 1: return HouseRole.randomRestaurant(random);
					case 2: return HouseRole.clothesShop;
					case 3: return HouseRole.library;
					case 4: return HouseRole.hotel;
					case 5: return HouseRole.hospital;
					default: throw new RuntimeException();
				}
			}
			else if(index<11) {
				switch(wDown.next(random)) {
					case 0: return HouseRole.randomShop(random);
					case 1: return HouseRole.randomRestaurant(random);
					case 2: return HouseRole.randomFastFood(random);
					case 3: {
						if(hasMuseum)
							return assignNext(index, random);
						hasMuseum = true;
						return HouseRole.museum;
					}
					case 4: {
						if(hasMuseum)
							return assignNext(index, random);
						hasMuseum = true;
						return HouseRole.concertHall;
					}
					case 5: return HouseRole.office;
					default: throw new RuntimeException();
				}
			}
			else {
				if(countConv<countRes/4) {
					countConv++;
					switch(wConv.next(random)) {
						case 0: return HouseRole.localShop;
						case 1: return HouseRole.randomFastFood(random);
						default: throw new RuntimeException();
					}
				}
				if(countComm<countRes/7) {
					countComm++;
					switch(wComm.next(random)) {
						case 0: return HouseRole.supermarket;
						case 1: return HouseRole.randomRestaurant(random);
						case 2: return HouseRole.hotel;
						case 3: return HouseRole.inn;
						case 4: return HouseRole.randomShop(random);
						case 5: return HouseRole.library;
						case 6: return HouseRole.office;
						default: throw new RuntimeException();
					}
				}
				if(countPostOffice<countRes/15) {
					countPostOffice++;
					return HouseRole.postOffice;
				}
				return HouseRole.residential;
			}
		}
	}
	
	public static void assignHouses(Level level, Random random) {
		new Village(level).assign(random);
	}
	
}