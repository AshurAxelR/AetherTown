package com.xrbpowered.aethertown.world.region;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureStyle;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureTileSet;
import com.xrbpowered.aethertown.world.gen.plot.ArchitectureTileSet.DoorInfo;
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
		private boolean addOffice;
		private LocalShopRole(String title, final DoorInfo door, Color color, boolean addOffice) {
			super(title, color,
				new ArchitectureStyle.BlankGroundNotFront(1, ArchitectureTileSet.shopSet).setIllum(IllumPattern.shop).setDoorInfo(door),
				new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.shop, (IllumPattern)null).setDoorInfo(door),
				new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.shop, (IllumPattern)null).setDoorInfo(door),
				new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop, IllumPattern.office).setDoorInfo(door)
			);
			this.addOffice = addOffice;
		}
		private LocalShopRole(String title, Color color, boolean addOffice) {
			this(title, color==colorFoodSmall ? DoorInfo.fastFood :  null, color, addOffice);
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
	
	private static class ShopRole extends HouseRole {
		private ShopRole(String title, IllumPattern illum) {
			super(title, colorShop,
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

	private static class RestaurantRole extends HouseRole {
		private RestaurantRole(String title, DoorInfo door) {
			super(title, colorFood,
				new ArchitectureStyle.BlankGroundBack(1, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant).setDoorInfo(door),
				new ArchitectureStyle.BlankGroundBack(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant).setDoorInfo(door),
				new ArchitectureStyle.BlankGroundBack(2, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.restaurant, (IllumPattern)null).setDoorInfo(door),
				new ArchitectureStyle.BlankGroundBack(3, ArchitectureTileSet.officeSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant, IllumPattern.office).setDoorInfo(door)
			);
		}
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			if(house.getFootprint()>=8) {
				if(!isPark(house)) {
					house.addRole = addCityRole(house, random);
					return house.addRole==residential ? arch[1] : arch[2];
				}
				else
					return arch[0];
			}
			else
				return arch[1];
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
		@Override
		public IllumTileComponent getSign() {
			return ArchitectureTileSet.hotelSign;
		}
		@Override
		public float getSignY() {
			return 9f;
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

	public static final HouseRole postOffice = new HouseRole("Post Office", colorCivic,
			new ArchitectureStyle(1, ArchitectureTileSet.officeSet).setIllum(IllumPattern.officeLobby, IllumLayer.living, IllumPattern.office, IllumLayer.living),
			new ArchitectureStyle(2, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.officeLobby, IllumLayer.living, null, null),
			new ArchitectureStyle(3, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.officeLobby, IllumLayer.living, null, null)
		) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			if(isPark(house))
				return arch[0];
			else {
				house.addRole = residential;
				return house.getFootprint()<=4 || isCityHouse(house, random) ? arch[2] : arch[1];
			}
		}
		@Override
		public IllumTileComponent getSign() {
			return ArchitectureTileSet.postSign;
		}
	};
	public static final HouseRole civicCentre = new HouseRole("Civic Centre", colorCivic,
			new ArchitectureStyle(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.officeLobby, IllumLayer.living, IllumPattern.office, IllumLayer.living).setDoorInfo(DoorInfo.officeDouble),
			new ArchitectureStyle(3, ArchitectureTileSet.officeSet).setIllum(IllumPattern.officeLobby, IllumLayer.living, IllumPattern.office, IllumLayer.living).setDoorInfo(DoorInfo.officeDouble)
		) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return house.getFootprint()<8 && isCityHouse(house, random) ? arch[1] : arch[0];
		}
	};
	public static final HouseRole hospital = new HouseRole("Hospital", colorHospital,
			new ArchitectureStyle(2, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.hospital, IllumPattern.hospitalWards).setDoorInfo(DoorInfo.officeDouble),
			new ArchitectureStyle(3, ArchitectureTileSet.officeSet, ArchitectureTileSet.baseSet).setIllum(IllumPattern.hospital, IllumPattern.hospitalWards).setDoorInfo(DoorInfo.officeDouble)
		) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return house.getFootprint()<=4 || isCityHouse(house, random) ? arch[1] : arch[0];
		}
		@Override
		public IllumTileComponent getSign() {
			return ArchitectureTileSet.hospitalSign;
		}
		@Override
		public float getSignY() {
			return 6.5f;
		}
	};
	
	public static final HouseRole hotel = new HotelRole("Hotel");
	public static final HouseRole inn = new HotelRole("Inn");
	
	public static final HouseRole localShop = new LocalShopRole("Local Store", DoorInfo.localShop, colorShopSmall, false);
	public static final HouseRole supermarket = new HouseRole("Supermarket", colorShopLarge,
			new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop).setDoorInfo(DoorInfo.supermarket),
			new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop).setDoorInfo(DoorInfo.supermarket)
		) {
		@Override
		protected ArchitectureStyle arch(HouseGenerator house, Random random) {
			return !isPark(house) && (house.getFootprint()<6 || house.getFootprint()<8 && isCityHouse(house, random)) ? arch[1] : arch[0];
		}
	};
	public static final HouseRole clothesShop = new ShopRole("Fashion Shop", IllumPattern.shop);
	public static final HouseRole giftShop = new HouseRole("Gift Shop + Cafeteria", colorShop,
		new ArchitectureStyle.BlankBack(1, ArchitectureTileSet.shopSet).setIllum(IllumPattern.shop, IllumLayer.living).setDoorInfo(DoorInfo.coffeeShop)
	);
	
	public static final HouseRole museum = new HouseRole("Museum", colorCulture,
		new ArchitectureStyle(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.hotel, IllumLayer.officeHours)
	);
	public static final HouseRole library = new HouseRole("Library", colorCulture,
		new ArchitectureStyle(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.hotel, IllumLayer.officeHours)
	);
	public static final HouseRole concertHall = new HouseRole("Concert Hall", colorCulture,
		new ArchitectureStyle.BlankNotFront(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant, IllumLayer.leisure)
	);
	public static final HouseRole office = new HouseRole("Office", colorOffice,
		new ArchitectureStyle(3, ArchitectureTileSet.officeSet).setIllum(IllumPattern.officeLobby, IllumPattern.office)
	);
	
	public final String title;
	public final Color previewColor;
	protected final ArchitectureStyle[] arch;
	
	private HouseRole(String title, Color color, ArchitectureStyle... arch) {
		this.title = title;
		this.previewColor = color;
		this.arch = arch;
	}

	protected ArchitectureStyle arch(HouseGenerator house, Random random) {
		return arch[0];
	}
	
	public IllumTileComponent getSign() {
		return null;
	}
	
	public float getSignY() {
		return 5.5f;
	}
	
	public static void assignRole(HouseGenerator house, HouseRole role, Random random) {
		house.addRole = null;
		house.role = role;
		house.arch = role.arch(house, random);
	}
	
	protected static boolean isPark(HouseGenerator house) {
		return house.startToken.level.info.settlement==LevelSettlementType.inn;
	}

	protected static boolean isCityHouse(HouseGenerator house, Random random) {
		return (house.startToken.level.houseCount>=25 && random.nextInt(house.startToken.level.houseCount/5)>3);
	}
	
	protected static HouseRole addCityRole(HouseGenerator house, Random random) {
		return isCityHouse(house, random) ? office : residential;
	}
	
	private static final Shuffle.List<HouseRole> restaurantShuffle = new Shuffle.List<>(
		new RestaurantRole("Pub", DoorInfo.restaurantBlack),
		new RestaurantRole("Italian Restaurant", DoorInfo.restaurantRed),
		new RestaurantRole("Pizza Restaurant", DoorInfo.restaurantRed),
		new RestaurantRole("Asian Restaurant", DoorInfo.restaurantRed),
		new RestaurantRole("Chinese Buffet", DoorInfo.restaurantRed),
		new RestaurantRole("Thai Restaurant", DoorInfo.restaurantRed),
		new RestaurantRole("Mexican Restaurant", DoorInfo.restaurantRed),
		new RestaurantRole("Indian Restaurant", DoorInfo.restaurantRed),
		new RestaurantRole("European Restaurant", DoorInfo.restaurantBlack),
		new RestaurantRole("French Restaurant", DoorInfo.restaurantBlack),
		new RestaurantRole("Mediterranean Restaurant", DoorInfo.restaurantRed)
	);

	public static HouseRole randomRestaurant(Random random) {
		return restaurantShuffle.nextItem(random);
	}

	private static final Shuffle.List<HouseRole> fastFoodShuffle = new Shuffle.List<>(
			new LocalShopRole("Sandwich Bar", colorFoodSmall, true),
			new LocalShopRole("Burrito Bar", colorFoodSmall, true),
			new LocalShopRole("Burger Bar", colorFoodSmall, true),
			new LocalShopRole("Pizza Bar", colorFoodSmall, true),
			new LocalShopRole("Coffee Shop", DoorInfo.coffeeShop, colorFoodSmall, true),
			new LocalShopRole("Chinese Takeaway", colorFoodSmall, true),
			new LocalShopRole("Fish and Chips", colorFoodSmall, true),
			new LocalShopRole("Sushi Bar", colorFoodSmall, true),
			new LocalShopRole("Chicken Grill Bar", colorFoodSmall, true)
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
