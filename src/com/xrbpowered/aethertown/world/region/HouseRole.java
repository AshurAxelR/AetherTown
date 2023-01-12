package com.xrbpowered.aethertown.world.region;

import java.awt.Color;
import java.util.Random;

public class HouseRole {

	public static final Color colorResidential = new Color(0x007700);
	public static final Color colorCivic = new Color(0xdd7700);
	public static final Color colorHospital = new Color(0xdd0000);
	public static final Color colorHotel = new Color(0x77dd00);
	public static final Color colorFoodSmall = new Color(0x9999ff);
	public static final Color colorFood = new Color(0x7777dd);
	public static final Color colorShopSmall = new Color(0x0077dd);
	public static final Color colorShop = new Color(0x0000dd);
	public static final Color colorShopLarge = new Color(0x000077);
	public static final Color colorChurch = new Color(0xffdd77);
	public static final Color colorCulture = new Color(0xdd77bb);
	public static final Color colorOffice = new Color(0x77aaaa);
	
	public static final HouseRole residential = new HouseRole("Residential", colorResidential);

	public static final HouseRole postOffice = new HouseRole("Post Office", colorCivic);
	public static final HouseRole civicCentre = new HouseRole("Civic Centre", colorCivic);
	public static final HouseRole hospital = new HouseRole("Hospital", colorHospital);
	
	public static final HouseRole hotel = new HouseRole("Hotel", colorHotel);
	public static final HouseRole inn = new HouseRole("Inn", colorHotel);
	
	public static final HouseRole localShop = new HouseRole("Local Store", colorShopSmall);
	public static final HouseRole supermarket = new HouseRole("Supermarket", colorShop);
	public static final HouseRole clothesShop = new HouseRole("Clothes Shop", colorShop);
	
	// public static final HouseRole mall = new HouseRole("Mall", colorShopLarge);
	public static final HouseRole museum = new HouseRole("Museum", colorCulture);
	public static final HouseRole concertHall = new HouseRole("Concert Hall", colorCulture);
	public static final HouseRole library = new HouseRole("Library", colorCulture);
	public static final HouseRole office = new HouseRole("Office", colorOffice);
	
	public final String title;
	public final Color previewColor;
	
	private HouseRole(String title, Color color) {
		this.title = title;
		this.previewColor = color;
	}
	
	private static final HouseRole[] restaurantList = {
		new HouseRole("Pub", colorFood),
		new HouseRole("Italian Restaurant", colorFood),
		new HouseRole("Pizza Restaurant", colorFood),
		new HouseRole("Thai Restaurant", colorFood),
		new HouseRole("Chinese Buffet", colorFood),
		new HouseRole("Mexican Restaurant", colorFood),
		new HouseRole("Indian Restaurant", colorFood),
		new HouseRole("Western-European Restaurant", colorFood),
		new HouseRole("Eastern-European Restaurant", colorFood),
		new HouseRole("Spanish Tapas", colorFood),
		new HouseRole("Turkish Restaurant", colorFood),
	};

	public static HouseRole randomRestaurant(Random random) {
		return restaurantList[random.nextInt(restaurantList.length)];
	}

	private static final HouseRole[] fastFoodList = {
		new HouseRole("Sandwich Bar", colorFoodSmall),
		new HouseRole("Burrito Bar", colorFoodSmall),
		new HouseRole("Burger Bar", colorFoodSmall),
		new HouseRole("Pizza Bar", colorFoodSmall),
		new HouseRole("Coffee Shop", colorFoodSmall),
		new HouseRole("Chinese Takeaway", colorFoodSmall),
		new HouseRole("Fish and Chips", colorFoodSmall),
		new HouseRole("Chicken Grill Bar", colorFoodSmall),
	};

	public static HouseRole randomFastFood(Random random) {
		return fastFoodList[random.nextInt(fastFoodList.length)];
	}

	private static final HouseRole[] shopList = {
		supermarket,
		clothesShop,
		new HouseRole("Book Store", colorShop),
		new HouseRole("DIY and Homeware", colorShop),
		new HouseRole("Tech Store", colorShop),
		new HouseRole("Hobby Shop", colorShop),
		new HouseRole("Music Shop", colorShop),
		new HouseRole("Art Shop", colorShop),
	};
	
	public static HouseRole randomShop(Random random) {
		return shopList[random.nextInt(shopList.length)];
	}
	
}
