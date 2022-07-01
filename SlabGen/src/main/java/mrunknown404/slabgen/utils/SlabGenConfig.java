package mrunknown404.slabgen.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class SlabGenConfig {
	public static class Common {
		public final ConfigValue<Boolean> biomeWhiteList;
		public final ConfigValue<List<String>> biomeList;
		
		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("slabgen");
			this.biomeWhiteList = builder.comment("Wheather or not the biome list is whitelist or blacklist. false for blacklist, true for whitelist").define("biome whitelist",
					false);
			this.biomeList = builder.comment("Biomes that should include/exclude slab generation. Should be <modid:biomeid>. Example: minecraft:forest").define("biome list",
					new ArrayList<String>());
			builder.pop();
		}
	}
	
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	
	static {
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();
	}
}
