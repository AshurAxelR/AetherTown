package com.xrbpowered.aethertown.world.region;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.gen.HouseGenerator;

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
		private Shuffle sh24 = new Shuffle(3);
		private WRandom w58 = new WRandom(3, 1, 1, 1, 0.2, 0.2, 1.5);
		private boolean hasMuseum = false;
		private int countConv = 0;
		private WRandom wConv = new WRandom(1.5, 1, 0.75);
		private int countComm = 0;
		private WRandom wComm = new WRandom(1, 0.5, 0.5, 0.3, 0.3, 0.2, 0.2);
		private int countPostOffice = 0;
		
		public Village(Level level) {
			super(level);
		}
		
		@Override
		public HouseRole assignNext(int index, Random random) {
			if(index==0)
				return HouseRole.civicCentre;
			else if(index==1)
				return HouseRole.hotel;
			else if(index<5) {
				switch(sh24.next(random)) {
					case 0: return HouseRole.supermarket;
					case 1: return HouseRole.restaurant;
					case 2: return HouseRole.clothesShop;
					default: throw new RuntimeException();
				}
			}
			else if(index<9) {
				switch(w58.next(random)) {
					case 0: return HouseRole.randomShop(random);
					case 1: return HouseRole.restaurant;
					case 2: return HouseRole.cafe;
					case 3: return HouseRole.fastfood;
					case 4: {
						if(hasMuseum)
							return assignNext(index, random);
						hasMuseum = true;
						return HouseRole.museum;
					}
					case 5: {
						if(hasMuseum)
							return assignNext(index, random);
						hasMuseum = true;
						return HouseRole.concertHall;
					}
					case 6: return HouseRole.office;
					default: throw new RuntimeException();
				}
			}
			else {
				if(countConv<countRes/4) {
					countConv++;
					switch(wConv.next(random)) {
						case 0: return HouseRole.convenience;
						case 1: return HouseRole.groceries;
						case 2: return HouseRole.fastfood;
						default: throw new RuntimeException();
					}
				}
				if(countComm<countRes/8) {
					countComm++;
					switch(wComm.next(random)) {
						case 0: return HouseRole.supermarket;
						case 1: return HouseRole.restaurant;
						case 2: return HouseRole.cafe;
						case 3: return HouseRole.hotel;
						case 4: return HouseRole.inn;
						case 5: return HouseRole.randomShop(random);
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
