package com.xrbpowered.aethertown.world.region;

import java.awt.Color;
import java.util.Random;

public class HouseRole {

	public static final Color colorResidential = new Color(0x007700);
	public static final Color colorCivic = new Color(0xdd7700);
	public static final Color colorHotel = new Color(0x77dd00);
	public static final Color colorFood = new Color(0x7777dd);
	public static final Color colorShopSmall = new Color(0x0077dd);
	public static final Color colorShop = new Color(0x0000dd);
	public static final Color colorShopLarge = new Color(0x000077);
	public static final Color colorChurch = new Color(0xffdd77);
	public static final Color colorCulture = new Color(0xdd7777);
	public static final Color colorOffice = new Color(0x77aaaa);
	
	public static final HouseRole residential = new HouseRole("Residential", colorResidential);

	public static final HouseRole postOffice = new HouseRole("Post Office", colorCivic);
	public static final HouseRole civicCentre = new HouseRole("Civic Centre", colorCivic);
	
	public static final HouseRole restaurant = new HouseRole("Restaurant", colorFood);
	public static final HouseRole cafe = new HouseRole("Cafe", colorFood);
	public static final HouseRole fastfood = new HouseRole("Fast Food", colorFood);

	public static final HouseRole hotel = new HouseRole("Hotel", colorHotel);
	public static final HouseRole inn = new HouseRole("Inn", colorHotel);
	
	public static final HouseRole groceries = new HouseRole("Groceries Shop", colorShopSmall);
	public static final HouseRole convenience = new HouseRole("Convenience Store", colorShopSmall);
	public static final HouseRole touristShop = new HouseRole("Tourist Shop", colorShopSmall);
	public static final HouseRole supermarket = new HouseRole("Supermarket", colorShop);
	
	public static final HouseRole clothesShop = new HouseRole("Clothes Shop", colorShop);
	public static final HouseRole homewareShop = new HouseRole("Homeware Shop", colorShop);
	public static final HouseRole techShop = new HouseRole("Tech Store", colorShop);
	public static final HouseRole hobbyShop = new HouseRole("Hobby Shop", colorShop);
	public static final HouseRole musicShop = new HouseRole("Music Shop", colorShop);
	public static final HouseRole artShop = new HouseRole("Art Shop", colorShop);
	
	public static final HouseRole mall = new HouseRole("Mall", colorShopLarge);
	
	// public static final HouseRole church = new HouseRole("Church", colorChurch);
	public static final HouseRole museum = new HouseRole("Museum", colorCulture);
	public static final HouseRole concertHall = new HouseRole("Concert Hall", colorCulture);
	public static final HouseRole theatre = new HouseRole("Theatre", colorCulture);
	// TODO library / book shop
	public static final HouseRole office = new HouseRole("Office", colorOffice);
	
	public final String title;
	public final Color previewColor;
	
	private HouseRole(String title, Color color) {
		this.title = title;
		this.previewColor = color;
	}
	
	public static final HouseRole[] shops = {
		groceries, supermarket, convenience, touristShop, clothesShop, homewareShop, techShop, hobbyShop, musicShop, artShop
	};
	
	public static HouseRole randomShop(Random random) {
		return shops[random.nextInt(shops.length)];
	}
	
}
