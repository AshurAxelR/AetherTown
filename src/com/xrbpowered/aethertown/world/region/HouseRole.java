package com.xrbpowered.aethertown.world.region;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureStyle;
import com.xrbpowered.aethertown.world.gen.plot.HouseGenerator;

public class HouseRole {

	public static final Color colorResidential = new Color(0x007700);
	public static final Color colorCivic = new Color(0xdd7700);
	public static final Color colorHospital = new Color(0xdd0000);
	public static final Color colorHotel = new Color(0x77dd00);
	public static final Color colorFoodSmall = new Color(0x9999ff);
	public static final Color colorFood = new Color(0x7777dd);
	public static final Color colorShopSmall = new Color(0x0077dd);
	public static final Color colorShop = new Color(0x0000dd);
	public static final Color colorShopLarge = new Color(0x005599);
	public static final Color colorChurch = new Color(0xffdd77);
	public static final Color colorCulture = new Color(0xdd77bb);
	public static final Color colorOffice = new Color(0x77aaaa);
	
	private static class LocalShopRole extends HouseRole {
		private LocalShopRole(String title, Color color) {
			super(title, color);
		}
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			if(isPark(house))
				return ArchitectureStyle.shop1;
			else
				return isCityHouse(house, random) ? ArchitectureStyle.local3 : ArchitectureStyle.local2;
		}
		@Override
		public IllumPattern illum(int floor, ArchitectureStyle arch) {
			return floor==0 ? IllumPattern.shop : null;
		}
	}
	
	private static class ShopRole extends HouseRole {
		private ShopRole(String title) {
			super(title, colorShop);
		}
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			if(house.getFootprint()>=8)
				return !isPark(house) ? ArchitectureStyle.local2 : ArchitectureStyle.shop1;
			else
				return ArchitectureStyle.shop2;
		}
		@Override
		public IllumPattern illum(int floor, ArchitectureStyle arch) {
			return floor==0 || arch==ArchitectureStyle.shop2 ? IllumPattern.shop : null;
		}
	}

	private static class RestaurantRole extends HouseRole {
		private RestaurantRole(String title) {
			super(title, colorFood);
		}
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return ArchitectureStyle.residential2;
		}
		@Override
		public IllumPattern illum(int floor, ArchitectureStyle arch) {
			return IllumPattern.restaurant;
		}
	}

	public static final HouseRole residential = new HouseRole("Residential", colorResidential) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return isCityHouse(house, random) ? ArchitectureStyle.residential3 : ArchitectureStyle.residential2;
		}
		@Override
		public IllumPattern illum(int floor, ArchitectureStyle arch) {
			return null;
		}
	};

	public static final HouseRole postOffice = new HouseRole("Post Office", colorCivic);
	public static final HouseRole civicCentre = new HouseRole("Civic Centre", colorCivic);
	public static final HouseRole hospital = new HouseRole("Hospital", colorHospital);
	
	public static final HouseRole hotel = new HouseRole("Hotel", colorHotel);
	public static final HouseRole inn = new HouseRole("Inn", colorHotel);
	
	public static final HouseRole localShop = new LocalShopRole("Local Store", colorShopSmall);
	public static final HouseRole supermarket = new HouseRole("Supermarket", colorShopLarge) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return !isPark(house) && (house.getFootprint()<6 || house.getFootprint()<8 && isCityHouse(house, random)) ? ArchitectureStyle.shop3 : ArchitectureStyle.shop2;
		}
		@Override
		public int doorType() {
			return 1;
		}
	};
	public static final HouseRole clothesShop = new ShopRole("Clothes Shop");
	public static final HouseRole giftShop = new HouseRole("Gift Shop", colorShop) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return ArchitectureStyle.openShop1;
		}
	};
	
	public static final HouseRole museum = new HouseRole("Museum", colorCulture);
	public static final HouseRole concertHall = new HouseRole("Concert Hall", colorCulture);
	public static final HouseRole library = new HouseRole("Library", colorCulture);
	public static final HouseRole office = new HouseRole("Office", colorOffice) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return ArchitectureStyle.residential3;
		}
		@Override
		public IllumPattern illum(int floor, ArchitectureStyle arch) {
			return IllumPattern.office;
		}
	};
	
	public final String title;
	public final Color previewColor;
	
	private HouseRole(String title, Color color) {
		this.title = title;
		this.previewColor = color;
	}
	
	public ArchitectureStyle arch(HouseGenerator house, Random random) {
		return ArchitectureStyle.residential2;
	}
	
	public IllumPattern illum(int floor, ArchitectureStyle arch) {
		return IllumPattern.shop;
	}
	
	public int doorType() {
		return 0;
	}

	protected static boolean isPark(HouseGenerator house) {
		return house.startToken.level.info.settlement==LevelSettlementType.inn;
	}

	protected static boolean isCityHouse(HouseGenerator house, Random random) {
		return (house.startToken.level.houseCount>=25 && random.nextInt(house.startToken.level.houseCount/5)>3);
	}
	
	private static final Shuffle.List<HouseRole> restaurantShuffle = new Shuffle.List<>(
		new RestaurantRole("Pub"),
		new RestaurantRole("Italian Restaurant"),
		new RestaurantRole("Pizza Restaurant"),
		new RestaurantRole("Central-Asian Restaurant"),
		new RestaurantRole("Chinese Buffet"),
		new RestaurantRole("Thai Restaurant"),
		new RestaurantRole("Mexican Restaurant"),
		new RestaurantRole("Indian Restaurant"),
		new RestaurantRole("Eastern-European Restaurant"),
		new RestaurantRole("French Restaurant"),
		new RestaurantRole("Middle-Eastern Restaurant")
	);

	public static HouseRole randomRestaurant(Random random) {
		return restaurantShuffle.nextItem(random);
	}

	private static final Shuffle.List<HouseRole> fastFoodShuffle = new Shuffle.List<>(
			new LocalShopRole("Sandwich Bar", colorFoodSmall),
			new LocalShopRole("Burrito Bar", colorFoodSmall),
			new LocalShopRole("Burger Bar", colorFoodSmall),
			new LocalShopRole("Pizza Bar", colorFoodSmall),
			new LocalShopRole("Coffee Shop", colorFoodSmall),
			new LocalShopRole("Chinese Takeaway", colorFoodSmall),
			new LocalShopRole("Fish and Chips", colorFoodSmall),
			new LocalShopRole("Sushi Bar", colorFoodSmall),
			new LocalShopRole("Chicken Grill Bar", colorFoodSmall)
	);

	public static HouseRole randomFastFood(Random random) {
		return fastFoodShuffle.nextItem(random);
	}

	private static final Shuffle.List<HouseRole> shopShuffle = new Shuffle.List<>(
			clothesShop,
			giftShop,
			new ShopRole("Home Store"),
			new ShopRole("Tech Store"),
			new ShopRole("Hobby Shop"),
			new ShopRole("Music Shop"),
			new ShopRole("Art Shop")
	);
	
	public static HouseRole randomShop(Random random) {
		return shopShuffle.nextItem(random);
	}
	
	public static void resetShuffle() {
		restaurantShuffle.reset();
		fastFoodShuffle.reset();
		shopShuffle.reset();
	}
	
}
