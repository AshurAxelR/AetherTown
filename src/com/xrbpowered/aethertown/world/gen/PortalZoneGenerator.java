package com.xrbpowered.aethertown.world.gen;

import java.util.Random;

import com.xrbpowered.aethertown.utils.Dir;
import com.xrbpowered.aethertown.world.Generator;
import com.xrbpowered.aethertown.world.Token;
import com.xrbpowered.aethertown.world.tiles.Monument;
import com.xrbpowered.aethertown.world.tiles.Plaza;
import com.xrbpowered.aethertown.world.tiles.Street;

public class PortalZoneGenerator implements Generator {

	public static final int radius = 8;
	
	@Override
	public boolean generate(Token t, Random random) {
		Dir d0 = t.level.info.portal.d;
		for(Dir d : Dir.values()) {
			if(d==d0 || d==d0.flip()) {
				for(int i=1; i<=radius; i++)
					Street.template.forceGenerate(new Token(t.level, t.x+i*d.dx, t.y, t.z+i*d.dz, d), random);
			}
			else {
				Monument.template.forceGenerate(t.next(d, 0), random);
				for(int i=2; i<=radius; i++)
					Plaza.template.forceGenerate(new Token(t.level, t.x+i*d.dx, t.y, t.z+i*d.dz, d), random);
			}
		}
		for(int i=1; i<=radius; i++)
			for(int j=1; j<=radius; j++) {
				Plaza.template.forceGenerate(new Token(t.level, t.x+i, t.y, t.z+j, Dir.east), random);
				Plaza.template.forceGenerate(new Token(t.level, t.x+i, t.y, t.z-j, Dir.east), random);
				Plaza.template.forceGenerate(new Token(t.level, t.x-i, t.y, t.z+j, Dir.west), random);
				Plaza.template.forceGenerate(new Token(t.level, t.x-i, t.y, t.z-j, Dir.west), random);
			}
		return false;
	}

}
