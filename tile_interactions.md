# Tile Interactions

## Player status

* CR - credits (cash), daily +30 CR if total below 1000
* INS - inspiration, determines speed of creative actions; min 0, max 100
* XP - knowledge, determines quality chance of creative actions. XP is permanent.
* items, hotkey **B**
	* **Rune** (level ref.) required for instant travel
	* **House Key** (house tile ref.) required for interacting with house tile
	* **Room Key** (hotel tile ref.) required for room actions in a hotel tile, expires at 11am
	* **Map** (level ref.) _use:_ view level map, hotkey **M**
	* **Region Map** (region ref.) _use:_ view region map, hotkey **N**
	* Trinket (level ref., timestamp) no use, just a memory
	* Groceries (BAG)
	* Snack (water, crisps, etc.) - food, consume: +5min
	* Cooked Food (cuisine type) - food, but must be stored at home
	* Takeaway Food (cuisine type) - food, consume: (4h gcd) +30min, +3 INS

BAG items go in their own slot instead of the inventory. There are 2 BAG slots.

## Home improvements

* Kitchenware - 10 CR
* Computer - 50 CR
* Board games - 5 CR
* Musical Instrument (guitar, keyboard) - 20 CR


## Actions and action types

* _Enter_ or (room): (use item?) UI +time?
	* (on enter event?)
	* ... actions
	* (storage? temporary?)
	* (inventory enabled?, disposing?, food allowed?)
* \> select item UI: type or { set of types }
* status action: (gcd? with action?) +time?, &plusmn;CR?, &plusmn;INS?, &plusmn;XP? (daily?)
* visit action: (once per tile) ... status action
* home action: req. home improvement ... status action
* buy (item): (cd until item expires?) +time?, -CR? first vs next? **+item**
* _Maps:_ UI
	* _View map:_ local or region
	* _Get map:_ local or region **+item**
* ... room actions
	* _Chill:_ +1h
	* _Shower:_ +15min, +1 INS
	* _Nap:_ (2h gcd: _Sleep_) +30min, +1 INS
	* _Short sleep:_ (3h gcd: _Sleep_) +4h, +3 INS
	* _Sleep:_ (10h gcd: _Sleep_) +6h, +10 INS


## Tiles

### Alcove / Fountain
* **E:** _Throw coin:_ (once per tile) -1 CR, +1 INS

### Bench / (Table) / Pavillion / Plaza?
* **E:** _Sit:_
	* (mouse look enabled)
	* (inventory enabled, food allowed)
	* (time-forwarding enabled)
	* **E / Esc:** leave

### Church
* **E:** _Enter:_ UI
	* _Pray:_ +10min, +5 INS (daily)
	* _Look around:_ (once per tile) +10min, +3 INS, +3 XP
	* _Shelter:_ +30min
		* _Wait:_ +30min
		* (inventory enabled, food allowed)

### Monument
* **E:** _Travel_ > select **Rune**: (2h gcd) fast travel to ref. level

### NavBox
* **E:** _View map:_ local

### HouseRole: residential
* **E:** _Enter:_ (use **House Key**) UI
	* (on enter, store items: Groceries)
	* _Kitchen:_
		* _Drink:_ (2h gcd) +15min, +1 INS
		* _Eat_ > select { Cooked Food, Takeaway Food }: (4h gcd) +30min, +3 INS, **-item:** selected
		* _Cook_ > (cuisine type): req. **Kitchenware** improvement, +40min, **-item:** Groceries, **+item:** 2&times;Cooked Food
	* _Living room:_
		* _Play board games:_ req. **Board games** improvement, +1h, +1 INS
		* _Play music:_ req. **Musical Instrument** improvement, +30min, +5 INS (daily)
		* _Watch movies:_ +1.5h, +2 INS (daily)
	* _Office:_
		* _Work_ > (product type): req. **Computer** improvement, +2h, -INS (up to 10: improves progress speed)  
			Completing a product grants +100 CR (+bonus for quality)
		* _Play games:_ req. **Computer** improvement, +40min, +1 INS
		* _Study:_ +1h, +1 XP
		* _Read:_ +30min
	* _Bedroom:_
		* ... room actions
	* (storage: Kitchen, Office, Bedroom)
	* (inventory enabled, disposing, food allowed)

### HouseRole: post office / civic centre
* **E:** _Enter:_ UI
	* _Maps:_ UI
	* _Get Rune:_ **+item: Rune**
	* _Collect Credits:_ +CR earned to this point, earned credits need to be collected at post offices
	* _Order goods_ > select home improvement: (local home owned) ... ?
	* **Civic Centre** _Residential Services:_
		* _Claim home > select address:_ (cd until abandoned) +10min, -50 CR &times; number of homes owned,  
			**+item: House Key**
		* _Recover key:_ (local home owned) +5min, **+item: House Key**
		* _Abandon home:_ (local home owned) +5min, **-item: House Key** (all), stored items disposed

### HouseRole: hospital
* view/get map

### HouseRole: hotel / inn
* **E:** _Enter:_ UI
	* _Reception:_
		* _Check in:_ (cd until item expires) +5min, -10 CR once per tile, -30 CR after that, **+item: Room Key**
		* _Check out:_ **-item: Room Key**, take items from room storage
		* _Maps:_ UI
		* _Get Rune:_ **+item: Rune**
		* (inventory enabled, disposing, food not allowed)
	* _Bar/Restaurant:_
		* _Hang out:_ +20min
		* _Drink:_ (2h gcd) +20min, -3 CR, +2 INS
		* **Inn** _Eat_: (4h gcd) +40min, -10 CR, +5 INS
		* (inventory enabled, disposing, food not allowed)
	* _Room:_ (use **Room Key**)
		* ... room actions
		* (storage, temporary until **Room Key** expires)
		* (inventory enabled, disposing, food allowed)

### HouseRole: local store / supermarket
* **E:** _Enter:_ UI
	* _Buy_ Groceries: -5CR **+item**
	* _Buy_ Snack (type): -1CR **+item**
	* **Supermarket** ... ?

### HouseRole: ... shop
* **E:** _Enter:_ UI
	* browse?

### HouseRole: museum
* wait 30min-1h (visit)

### HouseRole: library
* wait 30min-2h (read, study)

### HouseRole: concert hall
* $ wait 1h-2h (music, movie?)

### HouseRole: office
* +$ wait 1h-3h (work, play games)

### HouseRole: ... restaurant / fast food
* $ wait 20min-1h (food)
