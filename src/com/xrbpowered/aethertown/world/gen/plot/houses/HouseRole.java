package com.xrbpowered.aethertown.world.gen.plot.houses;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.aethertown.actions.EnterHomeAction;
import com.xrbpowered.aethertown.actions.HouseTileAction;
import com.xrbpowered.aethertown.actions.menus.ShopActionMenu;
import com.xrbpowered.aethertown.render.tiles.IllumLayer;
import com.xrbpowered.aethertown.render.tiles.IllumPattern;
import com.xrbpowered.aethertown.render.tiles.IllumTileComponent;
import com.xrbpowered.aethertown.state.items.FoodItem.FoodItemType;
import com.xrbpowered.aethertown.utils.Shuffle;
import com.xrbpowered.aethertown.world.gen.plot.houses.ArchitectureTileSet.DoorInfo;
import com.xrbpowered.aethertown.world.region.LevelSettlementType;

public class HouseRole {

	public static final Color colorResidential = new Color(0x007700);
	public static final Color colorCivic = new Color(0xdd7700);
	public static final Color colorHospital = new Color(0xdd0000);
	public static final Color colorHotel = new Color(0x77dd00);
	public static final Color colorFoodSmall = new Color(0x9999ff);
	public static final Color colorFood = new Color(0x7777dd);
	public static final Color colorShopSmall = new Color(0x5599ff);
	public static final Color colorShop = new Color(0x0000dd);
	public static final Color colorShopLarge = new Color(0x0077cc);
	public static final Color colorChurch = new Color(0xffdd77);
	public static final Color colorCulture = new Color(0xdd77bb);
	public static final Color colorOffice = new Color(0x77aaaa);
	
	public static final HouseRole residential = new HouseRole("Residential", colorResidential, EnterHomeAction.action,
			new ArchitectureStyle(2, ArchitectureTileSet.baseSet),
			new ArchitectureStyle(3, ArchitectureTileSet.baseSet)
		) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			return isCityHouse(house, random) ? arch[1] : arch[0];
		}
	};

	public static final HouseRole postOffice = new HouseRole("Post Office", colorCivic, HouseTileAction.postOffice,
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
	
	public static final HouseRole civicCentre = new HouseRole("Civic Centre", colorCivic, HouseTileAction.civicCentre,
			new ArchitectureStyle(1, ArchitectureTileSet.officeSet).setIllum(IllumPattern.officeLobby, IllumLayer.living, IllumPattern.office, IllumLayer.living).setDoorInfo(DoorInfo.officeDouble),
			new ArchitectureStyle(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.officeLobby, IllumLayer.living, IllumPattern.office, IllumLayer.living).setDoorInfo(DoorInfo.officeDouble),
			new ArchitectureStyle(3, ArchitectureTileSet.officeSet).setIllum(IllumPattern.officeLobby, IllumLayer.living, IllumPattern.office, IllumLayer.living).setDoorInfo(DoorInfo.officeDouble)
		) {
		@Override
		public ArchitectureStyle arch(HouseGenerator house, Random random) {
			if(house.startToken.level.info.settlement==LevelSettlementType.outpost)
				return house.getFootprint()<6 ? arch[1] : arch[0];
			else
				return house.getFootprint()<8 && isCityHouse(house, random) ? arch[2] : arch[1];
		}
		@Override
		public IllumTileComponent getSign() {
			return ArchitectureTileSet.postSign;
		}
	};
	
	public static final HouseRole hospital = new HouseRole("Hospital", colorHospital, HouseTileAction.hospital,
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
	
	public static final HouseRole hotel = new HotelRole("Hotel", HouseTileAction.hotel);
	public static final HouseRole inn = new HotelRole("Inn", HouseTileAction.inn);
	
	public static final HouseRole localShop = new LocalShopRole("Groceries", colorShopSmall, HouseTileAction.localShop, DoorInfo.localShop, false);
	
	public static final HouseRole supermarket = new HouseRole("Supermarket", colorShopLarge, HouseTileAction.supermarket,
			new ArchitectureStyle.BlankGroundNotFront(2, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop).setDoorInfo(DoorInfo.supermarket),
			new ArchitectureStyle.BlankGroundNotFront(3, ArchitectureTileSet.shopSet, ArchitectureTileSet.officeSet).setIllum(IllumPattern.shop).setDoorInfo(DoorInfo.supermarket)
		) {
		@Override
		protected ArchitectureStyle arch(HouseGenerator house, Random random) {
			return !isPark(house) && (house.getFootprint()<6 || house.getFootprint()<8 && isCityHouse(house, random)) ? arch[1] : arch[0];
		}
	};
	
	public static final HouseRole clothesShop = new ShopRole("Fashion Shop", new HouseTileAction(ShopActionMenu.clothesShop), IllumPattern.shop);
	public static final HouseRole homeShop = new ShopRole("Home Goods", new HouseTileAction(ShopActionMenu.homeShop), IllumPattern.shop);
	
	public static final HouseRole giftShop = new HouseRole("Gift Shop + Cafeteria", colorShop, HouseTileAction.giftShop,
		new ArchitectureStyle.BlankBack(1, ArchitectureTileSet.shopSet).setIllum(IllumPattern.shop, IllumLayer.living).setDoorInfo(DoorInfo.coffeeShop)
	);
	
	public static final HouseRole museum = new HouseRole("Museum", colorCulture, HouseTileAction.museum,
		new ArchitectureStyle(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.library)
	);
	public static final HouseRole library = new HouseRole("Library", colorCulture, HouseTileAction.library,
		new ArchitectureStyle(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.library)
	);
	public static final HouseRole concertHall = new HouseRole("Concert Hall", colorCulture, HouseTileAction.concertHall,
		new ArchitectureStyle.BlankNotFront(2, ArchitectureTileSet.officeSet).setIllum(IllumPattern.restaurant, IllumLayer.leisure)
	);
	public static final HouseRole office = new HouseRole("Office", colorOffice, null, // TODO office action
		new ArchitectureStyle(3, ArchitectureTileSet.officeSet).setIllum(IllumPattern.officeLobby, IllumPattern.office)
	);
	
	public final String title;
	public final Color previewColor;
	public final HouseTileAction action;
	protected final ArchitectureStyle[] arch;
	
	HouseRole(String title, Color color, HouseTileAction action, ArchitectureStyle... arch) {
		this.title = title;
		this.previewColor = color;
		this.action = action;
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
		new RestaurantRole("Mexican Restaurant", DoorInfo.restaurantRed),
		new RestaurantRole("Indian Restaurant", DoorInfo.restaurantRed),
		new RestaurantRole("European Restaurant", DoorInfo.restaurantBlack),
		new RestaurantRole("Mediterranean Restaurant", DoorInfo.restaurantRed)
	);

	public static HouseRole randomRestaurant(Random random) {
		return restaurantShuffle.nextItem(random);
	}

	private static final Shuffle.List<HouseRole> fastFoodShuffle = new Shuffle.List<>(
			new FastFoodRole("Sandwich Bar", FoodItemType.takeawaySandwich),
			new FastFoodRole("Burrito Bar", FoodItemType.takeawayBurrito),
			new FastFoodRole("Burger Bar", FoodItemType.takeawayBurger),
			new FastFoodRole("Pizza Bar", FoodItemType.takeawayPizza),
			new FastFoodRole("Coffee Shop", DoorInfo.coffeeShop, HouseTileAction.coffeeShop),
			new FastFoodRole("Chinese Takeaway", FoodItemType.takeawayChinese),
			new FastFoodRole("Chicken Grill Bar", FoodItemType.takeawayChicken)
	);

	public static HouseRole randomFastFood(Random random) {
		return fastFoodShuffle.nextItem(random);
	}

	private static final Shuffle.List<HouseRole> shopShuffle = new Shuffle.List<>(
			clothesShop,
			giftShop,
			homeShop,
			new ShopRole("Tech Store", new HouseTileAction(ShopActionMenu.techShop), IllumPattern.shop),
			new ShopRole("Book Shop", new HouseTileAction(ShopActionMenu.bookShop), IllumPattern.hotel),
			new ShopRole("Art Shop", new HouseTileAction(ShopActionMenu.artShop), IllumPattern.hotel),
			new ShopRole("Music Shop", new HouseTileAction(ShopActionMenu.musicShop), IllumPattern.restaurant)
	);
	
	public static HouseRole randomShop(Random random, int countRes) {
		HouseRole shop = shopShuffle.nextItem(random);
		if(shop==homeShop && countRes<5)
			return randomShop(random, countRes);
		else
			return shop;
	}
	
	public static void resetShuffle() {
		restaurantShuffle.reset();
		fastFoodShuffle.reset();
		shopShuffle.reset();
	}
	
}
