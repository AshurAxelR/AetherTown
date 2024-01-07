package com.xrbpowered.aethertown.world.region;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.utils.WRandom;
import com.xrbpowered.aethertown.world.Level;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;

public abstract class HouseAssignment {

	public final Level level;
	
	protected int countRes;
	protected int countResOnly;
	
	private HouseAssignment(Level level) {
		this.level = level;
	}
	
	protected void assignHouse(int index, HouseRole role, Random random) {
		HouseGenerator house = level.houses.get(index);
		HouseRole.assignRole(house, role, random);
		if(house.role==HouseRole.residential || house.addRole==HouseRole.residential)
			countRes++;
		if(house.role==HouseRole.residential)
			countResOnly++;
		house.illumTriggerOffs = random.nextFloat();
	}
	
	public abstract HouseRole assignNext(int index, Random random);
	
	public void assign(Random random) {
		countRes = 0;
		countResOnly = 0;
		for(int i=0; i<level.houseCount; i++)
			assignHouse(i, assignNext(i, random), random);
	}

	private static class Inn extends HouseAssignment {
		private WRandom wAdd = new WRandom(1.2, 0.5, 0.4, 0.3, 0.5, 0.1, 0.2, 0.2);
		private boolean hasGiftShop = false;
		private boolean hasSupermarket = false;
		private boolean hasMuseum = false;
		private boolean hasPostOffice = false;
		private boolean hasLibrary = false;
		
		public Inn(Level level) {
			super(level);
		}
		
		@Override
		public HouseRole assignNext(int index, Random random) {
			if(index==0)
				return HouseRole.inn;
			else {
				switch(wAdd.next(random)) {
					case 0: {
						if(hasGiftShop)
							return assignNext(index, random);
						hasGiftShop = true;
						return HouseRole.giftShop; 
					}
					case 1: {
						if(hasSupermarket)
							return assignNext(index, random);
						hasSupermarket = true;
						return HouseRole.supermarket;
					}
					case 2: return HouseRole.randomRestaurant(random);
					case 3: return HouseRole.randomFastFood(random);
					case 4: {
						if(hasMuseum)
							return assignNext(index, random);
						hasMuseum = true;
						return HouseRole.museum;
					}
					case 5: {
						if(hasPostOffice)
							return assignNext(index, random);
						hasPostOffice = true;
						return HouseRole.postOffice;
					}
					case 6: {
						if(hasLibrary)
							return assignNext(index, random);
						hasLibrary = true;
						return HouseRole.library;
					}
					case 7: return HouseRole.randomShop(random, countRes);
					default: throw new RuntimeException();
				}
			}
		}
	}

	private static class Outpost extends HouseAssignment {
		private Shuffle shInit = new Shuffle(3);
		private WRandom wInit = new WRandom(0.65, 0.85, 1.3, 0.3, 0.2, 0.4);
		private boolean hasMuseum = false;
		private int countConv = 0;
		private WRandom wConv = new WRandom(2.5, 1);
		
		public Outpost(Level level) {
			super(level);
		}
		
		@Override
		public HouseRole assignNext(int index, Random random) {
			if(index==0)
				return HouseRole.civicCentre;
			else if(index==1)
				return HouseRole.inn;
			else if(index<5) {
				switch(shInit.next(random)) {
					case 0: return HouseRole.supermarket;
					case 1: return HouseRole.randomRestaurant(random);
					case 2: return HouseRole.hospital;
					default: throw new RuntimeException();
				}
			}
			else if(index<8) {
				if(index<level.houseCount-2) {
					switch(wInit.next(random)) {
						case 0: return HouseRole.clothesShop;
						case 1: return HouseRole.randomShop(random, countRes);
						case 2: return HouseRole.randomFastFood(random);
						case 3: {
							if(hasMuseum)
								return assignNext(index, random);
							hasMuseum = true;
							return HouseRole.museum;
						}
						case 4: return HouseRole.library;
						case 5: return HouseRole.office;
						default: throw new RuntimeException();
					}
				}
				else
					return HouseRole.residential;
			}
			else {
				if(countConv<countRes/6) {
					countConv++;
					switch(wConv.next(random)) {
						case 0: return HouseRole.localShop;
						case 1: return HouseRole.randomFastFood(random);
						default: throw new RuntimeException();
					}
				}
				return HouseRole.residential;
			}
		}
	}

	private static class Village extends HouseAssignment {
		private Shuffle shInit = new Shuffle(6);
		private WRandom wDown = new WRandom(3, 2, 1, 0.2, 0.2, 1);
		private boolean hasMuseum = false;
		private int countConv = 0;
		private WRandom wConv = new WRandom(2.8, 0.2, 1);
		private int countComm = 0;
		private WRandom wComm = new WRandom(0.3, 0.5, 1.5, 0.2, 0.3, 1, 0.2, 0.5);
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
					case 0: return HouseRole.randomShop(random, countRes);
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
				if(countConv<countRes/5) {
					countConv++;
					switch(wConv.next(random)) {
						case 0: return HouseRole.localShop;
						case 1: return HouseRole.supermarket;
						case 2: return HouseRole.randomFastFood(random);
						default: throw new RuntimeException();
					}
				}
				if(countComm<countRes/6) {
					countComm++;
					switch(wComm.next(random)) {
						case 0: return HouseRole.supermarket;
						case 1: return HouseRole.randomFastFood(random);
						case 2: return HouseRole.randomRestaurant(random);
						case 3: return HouseRole.hotel;
						case 4: return HouseRole.inn;
						case 5: return HouseRole.randomShop(random, countRes);
						case 6: return HouseRole.library;
						case 7: return HouseRole.office;
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
	
	private static class Town extends HouseAssignment {
		private Shuffle shInit = new Shuffle(9);
		private WRandom wDown = new WRandom(2.5, 1, 1, 0.5, 2.5);
		private int countConv = 0;
		private WRandom wConv = new WRandom(2.5, 0.5, 1);
		private int countHub = 0;
		private int hubStart = -1;
		private Shuffle shHub = new Shuffle(4);
		private WRandom wHub = new WRandom(0.5, 1, 1.5, 0.5, 1, 0.5, 0.5);
		private boolean hasLibrary = false;
		private boolean hasMuseum = false;
		private boolean hasHospital = false;
		private int countComm = 0;
		private WRandom wComm = new WRandom(0.3, 0.6, 0.6, 0.1, 1.2, 0.2, 1, 0.2, 0.1);

		public Town(Level level) {
			super(level);
		}
		
		@Override
		public HouseRole assignNext(int index, Random random) {
			if(index==0)
				return HouseRole.civicCentre;
			else if(index<10) {
				switch(shInit.next(random)) {
					case 0: return HouseRole.supermarket;
					case 1: return HouseRole.randomRestaurant(random);
					case 2: return HouseRole.clothesShop;
					case 3: return HouseRole.concertHall;
					case 4: return HouseRole.hotel;
					case 5: return HouseRole.hospital;
					case 6: return HouseRole.museum;
					case 7: return HouseRole.randomShop(random, countRes);
					case 8: return HouseRole.library;
					default: throw new RuntimeException();
				}
			}
			else if(index<15) {
				switch(wDown.next(random)) {
					case 0: return HouseRole.randomShop(random, countRes);
					case 1: return HouseRole.randomRestaurant(random);
					case 2: return HouseRole.randomFastFood(random);
					case 3: return HouseRole.giftShop;
					case 4: return HouseRole.office;
					default: throw new RuntimeException();
				}
			}
			else if(hubStart>=0) {
				int hub = index-hubStart;
				if(hub==0)
					return HouseRole.postOffice;
				else if(hub<5) {
					switch(shHub.next(random)) {
						case 0: return HouseRole.supermarket;
						case 1: return HouseRole.randomFastFood(random);
						case 2: return HouseRole.hotel;
						case 3: return HouseRole.randomShop(random, countRes);
						default: throw new RuntimeException();
					}
				}
				else if(hub<10) {
					switch(wHub.next(random)) {
						case 0: return HouseRole.randomFastFood(random);
						case 1: return HouseRole.randomRestaurant(random);
						case 2: return HouseRole.randomShop(random, countRes);
						case 3: {
							if(hasLibrary)
								return assignNext(index, random);
							hasLibrary = true;
							return HouseRole.library;
						}
						case 4: return HouseRole.office;
						case 5: {
							if(hasMuseum)
								return assignNext(index, random);
							hasMuseum = true;
							return HouseRole.museum;
						}
						case 6: {
							if(hasHospital)
								return assignNext(index, random);
							hasHospital = true;
							return HouseRole.hospital;
						}
						default: throw new RuntimeException();
					}
				}
				else {
					hubStart = -1;
					return HouseRole.residential;
				}
			}
			else {
				if(countHub<countRes/15) {
					countHub++;
					hubStart = index;
					shHub.reset();
					hasLibrary = false;
					hasMuseum = false;
					hasHospital = false;
					return assignNext(index, random);
				}
				if(countConv<countRes/6) {
					countConv++;
					switch(wConv.next(random)) {
						case 0: return HouseRole.localShop;
						case 1: return HouseRole.supermarket;
						case 2: return HouseRole.randomFastFood(random);
						default: throw new RuntimeException();
					}
				}
				if(countComm<countRes/7) {
					countComm++;
					switch(wComm.next(random)) {
						case 0: return HouseRole.supermarket;
						case 1: return HouseRole.randomFastFood(random);
						case 2: return HouseRole.randomRestaurant(random);
						case 3: return HouseRole.inn;
						case 4: return HouseRole.randomShop(random, countRes);
						case 5: return HouseRole.library;
						case 6: return HouseRole.office;
						case 7: {
							if(hasMuseum)
								return assignNext(index, random);
							hasMuseum = true;
							return HouseRole.museum;
						}
						case 8: {
							if(hasMuseum)
								return assignNext(index, random);
							hasMuseum = true;
							return HouseRole.concertHall;
						}
						default: throw new RuntimeException();
					}
				}
				return HouseRole.residential;
			}
		}
	}
	
	public static void assignHouses(Level level, Random random) {
		if(level.houseCount==0)
			return;
		HouseRole.resetShuffle();
		HouseAssignment ha;
		switch(level.info.settlement) {
			case inn: ha = new Inn(level); break;
			case outpost: ha = new Outpost(level); break;
			case village: ha = new Village(level); break;
			default:
				ha = new Town(level);
		}
		ha.assign(random);
		System.out.printf("%d houses: %d (%d) res\n", level.houseCount, ha.countRes, ha.countResOnly);
	}
	
}
