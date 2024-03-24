# Tile Interactions

## Player status

### Attributes
* CR, £ - credits (cash), daily £20+ if total below £500; earned credits need to be collected by mail
* INS - inspiration, determines speed of creative actions; min 0, max 100
* XP - knowledge, determines quality chance of creative actions. XP is permanent.

### Inventory itmes

Inventory hotkey **Q**  
Player inventory has 12 slots (2 _heavy_).

* **Travel Token** (level ref.) required for instant travel
* **House Key** (house tile ref.) required for interacting with house tile
* **Room Key** (hotel tile ref., expired - bool) required for room actions in a hotel tile, expires at 11am
* **Map** (level ref.) _View:_ view level map, hotkey **M**
* **Region Map** (region ref.) _View:_ view region map, hotkey **N**
* Trinket (level ref., timestamp) no use, just a memory
* Groceries (_heavy_)
* Food items:
	* Water bottle
	* Snack - food, _Eat:_ (consumed) +5min
	* Takeaway meal - food, _Eat:_ (consumed) (4h gcd) +30min, +3 INS
	* Home-cooked meal - food, but must be stored at home

### Other state variables

* Region visits - hash map: region ref.
	* Level visits - hash map: `ref.hash(region seed, x, z)`
		* Tile visits, for "once per tile" action tracking - hash map: `tile.hash(x, z)`; check: template hash
		* Owned home - bool
* All owned homes - list: house tile ref.
	* Improvements - array: boolean per enum
	* Storage - array: inventory data
* All room keys - list: **Room Key**, item location (inventory or storage ref.)
* Gloabl cooldowns (gcd) - list: action id, timestamp of gcd ending


## UI Layouts

### Inventory / Storage

* Character info:
	* CR info
	* INS info
	* \[button\] **STATS** > UI
		* XP, knowledge level?, statistics (number of created products by type, progress, etc.)
* Storage name or **Backpack**
* \[list\] Items (12 slots) - item name, **H** marks heavy > select item in item pane
* Item pane:
	* Item name and description
	* Blocking info if action is disabled, e.g. gcd remaining
	* \[button\] **USE** or action name > item action...
	* \[button\] red **X** for _Dispose_ if allowed > confirmation dialog
	* \[button\] **MOVE** if storage present > UI
		* \[list\] Storage list, **dot** marks current location
* \[list\] Storage list (if present) (horizontal)
* \[button\] **CLOSE**

### Action menu

* Character info...
* Tile type or name
	* Tile address
* \[list\] Actions - action name, cost, time > tile action...  
	No general confirmation. If action is disabled, a message box/toast is shown
* \[button\] **INVENTORY (N)** where N is the number of storages present, if any > inventory UI
* \[button\] **LEAVE** or **BACK**

## Home improvements

* Kitchenware - £7.50, 75%
* Board games - £5.50, 25%
* Musical instrument - £15, 10%
* TV - £30, 80%
* Computer - £50, 20%
* Art supplies - £15, 10%
* Book collection - £10, 90%

## Actions and action types

Tile action hotkey **E**, alternative (upstairs) **R**

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
	* in some tiles, _Get Travel Token:_ **+item: Travel Token**
* ... room actions
	* _Relax:_ +1h
	* _Shower:_ +10min, +2 INS (daily)
	* _Nap:_ (2h gcd: _Sleep_) +30min, +1 INS
	* _Short sleep:_ (3h gcd: _Sleep_) +4h, +3 INS
	* _Sleep:_ (10h gcd: _Sleep_) +6h, +10 INS


## Tiles

### Alcove / Fountain
* **E:** _Throw coin:_ (once per tile) £0.10, +1 INS

### Bench / (Table) / Pavillion / Plaza?
* **E:** _Sit:_
	* (mouse look enabled)
	* (inventory enabled, food allowed)
	* (time-forwarding enabled)
	* **E / Esc:** leave

### Church
* **E:** _Enter:_ UI
	* _Pray:_ +15min, +5 INS (daily)
	* _Look around:_ (once per tile) +10min, +3 INS, +3 XP
	* _Shelter:_ +30min
		* _Wait:_ +30min
		* (inventory enabled, food allowed)

### Monument
* **E:** _Travel_ > select **Travel Token**: (2h gcd) fast travel to ref. level

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
			Completing a product normally shoud take 5-10 days and grants £100+ (+bonus for quality)
		* _Play games:_ req. **Computer** improvement, +40min, +1 INS
		* _Study:_ +30min, +1 XP
		* _Read:_ +30min, +1 INS (daily)
	* _Bedroom:_
		* ... room actions
	* (storage: Kitchen, Office, Bedroom)
	* (inventory enabled, disposing, food allowed)

### HouseRole: post office / civic centre
* **E:** _Enter:_ UI
	* _Maps:_ UI (+Travel Token)
	* _Collect earnings:_ +CR earned to this point
	* _Order goods_ > select home improvement: (local home owned) ... ?
	* **Civic Centre** _Residential Services:_
		* _Claim home > select address:_ (no local home owned) +10min, £100 &times; number of homes owned (no crediting),  
			**+item: House Key**
		* _Recover key:_ (local home owned) +5min, **+item: House Key**
		* _Abandon home:_ (local home owned) +5min, **-item: House Key** (all), stored items disposed

### HouseRole: hospital
* _Wait:_ +20min
* view/get map

### HouseRole: hotel / inn
* **E:** _Enter:_ UI
	* _Reception:_
		* _Check in:_ (cd until item expires) +5min, £5 once per tile (crediting allowed), £25 CR after that (no crediting), **+item: Room Key**
		* _Check out:_ **-item: Room Key**, take items from room storage
		* _Maps:_ UI (+Travel Token)
		* _Collect earnings:_ +CR earned to this point
		* (inventory enabled, disposing, food not allowed)
	* _Bar/Restaurant:_
		* _Hang out:_ +20min
		* _Drink:_ (2h gcd) +20min, £2.50, +2 INS
		* **Inn**, _Eat_: (4h gcd) +40min, £7.50 CR, +5 INS
		* (inventory enabled, disposing, food not allowed)
	* _Room:_ (use **Room Key**)
		* ... room actions
		* (inventory enabled, disposing, food allowed)

### HouseRole: groceries
* **E:** _Enter:_ UI
	* _Buy_ Groceries: £5 **+item**
	* _Buy_ Bottle of water (type): £0.50 **+item**
	* _Buy_ Snack: £1.50 **+item**


### HouseRole: supermarket
* **E:** _Enter:_ UI
	* _Browse:_ (once per tile) + 15min, +1 INS
	* _Food isle:_ UI groceries
	* Buy new clothes?
	* **Kitchenware**

### HouseRole: ... shop
* _Fashion:_ +2 INS, Buy new clothes?
* _Gift shop:_ +3 INS, Buy **Trinket**, Cafeteria UI
* _Home goods:_ +1 INS, **Kitchenware**
* _Tech:_ +2 INS, **Computer**
* _Book:_ +3 INS, **Board games**, Play board games
* _Art:_ +5 INS
* _Music:_ +3 INS, **Musical instrument**, Play music

#### gift shop
* **E:** _Enter:_ UI
	* _Browse:_ (once per tile) + 15min, +3 INS
	* _Buy Trinket:_ **+item**, £2.50
	* _Cafeteria:_ UI food


### HouseRole: museum
* **E:** _Enter:_ UI
	* _View collection:_ (once per tile) +30min, +3 INS, +3 XP

### HouseRole: library
* **E:** _Enter:_ UI
	* _Study:_ +30min, +1 XP
	* _Read:_ +30min, +1 INS (daily)

### HouseRole: concert hall
* **E:** _Enter:_ UI
	* _Play music:_ +1h, +12 INS (daily)
	* _Watch movies:_ +1.5h, +5 INS (daily)

### HouseRole: office
* **E:** _Enter:_ UI
	* _Work_ > (product type): +2h, ... office grants bonus to productivity
	* _Play games:_ +40min, +1 INS

### HouseRole: ... restaurant / fast food
* UI food
