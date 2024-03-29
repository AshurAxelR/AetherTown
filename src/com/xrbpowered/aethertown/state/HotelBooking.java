package com.xrbpowered.aethertown.state;

import static com.xrbpowered.aethertown.AetherTown.player;

import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.aethertown.world.gen.plot.houses.HouseGenerator;
import com.xrbpowered.aethertown.world.stars.WorldTime;

public class HotelBooking {

	public static final int checkoutHour = 11;
	
	public final HouseTileRef hotel;
	public final double expires;
	
	public HotelBooking(HouseTileRef ref, double expires) {
		this.hotel = ref;
		this.expires = expires;
	}

	public static HotelBooking getBooking() {
		HotelBooking booking = player.hotelBooking;
		if(booking!=null && WorldTime.time>booking.expires) {
			player.hotelBooking = null;
			return null;
		}
		else
			return booking;
	}
	
	public static boolean hasBooking(Tile tile) {
		HotelBooking booking = getBooking();
		if(booking==null)
			return false;
		if(tile==null)
			return true;
		HouseGenerator hotel = (HouseGenerator) tile.sub.parent;
		return booking.hotel.isHouse(hotel);
	}
	
	public static void book(Tile tile) {
		HouseGenerator hotel = (HouseGenerator) tile.sub.parent;
		double expires = WorldTime.time + (1.0 - WorldTime.getTimeOfDay()) + checkoutHour*WorldTime.hour;
		player.hotelBooking = new HotelBooking(new HouseTileRef(hotel), expires);
		RegionVisits.visitTile(tile);
	}
	
	public static void cancel() {
		player.hotelBooking = null;
	}
	
}
