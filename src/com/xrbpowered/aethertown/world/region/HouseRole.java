package com.xrbpowered.aethertown.world.region;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureStyle;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureTileSet;
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
			super(title, color,
				new ArchitectureStyle.BlankGroundNotFront(1, ArchitectureTileSet.shopSet).setIllum(IllumPattern.shop),
				new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.shop, null),
				new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.shop, null)
			);
		}
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			if(isPark(house))
				return arch[0];
			else {
				house.addRole = residential;
				return isCityHouse(house, random) ? arch[1] : arch[2];
			}
		}
	}
	
	private static class ShopRole extends HouseRole {
		private ShopRole(String title, IllumPattern illum) {
			super(title, colorShop,
				new ArchitectureStyle.BlankGroundNotFront(1, ArchitectureTileSet.shopSet).setIllum(illum),
				new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(illum),
				new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(illum, null)
			);
		}
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			if(house.getFootprint()>=8) {
				if(!isPark(house)) {
					house.addRole = residential;
					return arch[2];
				}
				else
					return arch[0];
			}
			else
				return arch[1];
		}
	}

	private static class RestaurantRole extends HouseRole {
		private RestaurantRole(String title) {
			super(title, colorFood,
				new ArchitectureStyle(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant)
			);
		}
	}
	
	private static class HotelRole extends HouseRole {
		private HotelRole(String title) {
			super(title, colorHotel,
				new ArchitectureStyle(2, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.hotel, IllumPattern.hotelRooms),
				new ArchitectureStyle(3, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.hotel, IllumPattern.hotelRooms)
			);
		}
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return isCityHouse(house, random) ? arch[1] : arch[0];
		}
	}

	public static final HouseRole residential = new HouseRole("Residential", colorResidential,
			new ArchitectureStyle(2, ArchitectureTileSet.baseSet),
			new ArchitectureStyle(3, ArchitectureTileSet.baseSet)
		) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return isCityHouse(house, random) ? arch[1] : arch[0];
		}
	};

	public static final HouseRole postOffice = new HouseRole("Post Office", colorCivic);
	public static final HouseRole civicCentre = new HouseRole("Civic Centre", colorCivic);
	public static final HouseRole hospital = new HouseRole("Hospital", colorHospital,
			new ArchitectureStyle(2, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.hospital, IllumPattern.hospitalWards).setDoorInfo(ArchitectureTileSet.officeDoubleDoor),
			new ArchitectureStyle(3, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.hospital, IllumPattern.hospitalWards).setDoorInfo(ArchitectureTileSet.officeDoubleDoor)
		) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return house.getFootprint()<=4 || isCityHouse(house, random) ? arch[1] : arch[0];
		}
	};
	
	public static final HouseRole hotel = new HotelRole("Hotel");
	public static final HouseRole inn = new HotelRole("Inn");
	
	public static final HouseRole localShop = new LocalShopRole("Local Store", colorShopSmall);
	public static final HouseRole supermarket = new HouseRole("Supermarket", colorShopLarge,
			new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop),
			new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop)
		) {
		@Override
		protected ArchitectureStyle arch(HouseGenerator house, Random random) {
			return !isPark(house) && (house.getFootprint()<6 || house.getFootprint()<8 && isCityHouse(house, random)) ? arch[1] : arch[0];
		}
	};
	public static final HouseRole clothesShop = new ShopRole("Clothes Shop", IllumPattern.shop);
	public static final HouseRole giftShop = new HouseRole("Gift Shop + Cafeteria", colorShop,
		new ArchitectureStyle.BlankBack(1, ArchitectureTileSet.shopSet).setIllum(IllumPattern.shop)
	);
	
	public static final HouseRole museum = new HouseRole("Museum", colorCulture);
	public static final HouseRole concertHall = new HouseRole("Concert Hall", colorCulture);
	public static final HouseRole library = new HouseRole("Library", colorCulture);
	public static final HouseRole office = new HouseRole("Office", colorOffice,
		new ArchitectureStyle(3, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop, IllumPattern.office)
	);
	
	public final String title;
	public final Color previewColor;
	protected final ArchitectureStyle[] arch;
	
	private HouseRole(String title, Color color, ArchitectureStyle... arch) {
		this.title = title;
		this.previewColor = color;
		this.arch = arch;
	}

	private HouseRole(String title, Color color) {
		this(title, color, new ArchitectureStyle(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop)); // FIXME remove default
	}

	protected ArchitectureStyle arch(HouseGenerator house, Random random) {
		return arch[0];
	}
	
	public static void assignRole(HouseGenerator house, HouseRole role, Random random) {
		house.role = role;
		house.arch = role.arch(house, random);
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
			new ShopRole("Home Store", IllumPattern.shop),
			new ShopRole("Tech Store", IllumPattern.shop),
			new ShopRole("Book Shop", IllumPattern.hotel),
			new ShopRole("Art Shop", IllumPattern.hotel),
			new ShopRole("Music Shop", IllumPattern.restaurant)
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
