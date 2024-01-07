# House Role Assignment

{ a, b } - randomly pick from set; random weights are not shown here  
a; b - shuffled list, each element appears exactly once  
a^ - a is unique for the settlement  
a^X - X is a uniqueness tag; only one of X can appear in the settlement or hub  
/X - every X residential houses  
**N** - total house count  

## Inn (1-2)

* 0: inn
* 1+: { giftShop^, supermarket^, randomRestaurant, randomFastFood, museum^, postOffice^, library^, randomShop }

## Outpost (4-10)

* 0: civicCentre
* 1: inn
* 2-4: supermarket; randomRestaurant; hospital
* 5-7:
	* **N**-2: { clothesShop, randomShop, randomFastFood, museum, library, office }
	* _residential_
* 8+:
	* /6: { localShop, randomFastFood }
	* _residential_

## Village (10-30)

* 0: civicCentre
* 1-6: supermarket; randomRestaurant; clothesShop; library; hotel; hospital
* 7-10: { randomShop, randomRestaurant, randomFastFood, museum^M, concertHall^M, office }
* 11+:
	* /5: { localShop, supermarket, randomFastFood }
	* /6: { supermarket, randomFastFood, randomRestaurant, hotel, inn, randomShop, library, office }
	* /15: postOffice
	* _residential_

## Town (30-50-80)

* 0: civicCentre
* 1-9: supermarket; randomRestaurant; clothesShop; concertHall; hotel; hospital; museum; randomShop; library
* 10-14: { randomShop, randomRestaurant, randomFastFood, giftShop, office }
* **hub:**
	* 0: postOffice
	* 1-4: supermarket; randomRestaurant; hotel; randomShop
	* 5-9: { randomFastFood, randomRestaurant, randomShop, library, office, museum^M, hospital^ }
	* 10: _residential_
* 15+:
	* /15: **hub**
	* /6: { localShop, supermarket, randomFastFood }
	* /7: { supermarket, randomFastFood, randomRestaurant, inn, randomShop, library, office, museum^M, concertHall^M }
	* _residential_
