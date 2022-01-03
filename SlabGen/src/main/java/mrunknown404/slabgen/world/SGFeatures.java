package mrunknown404.slabgen.world;

import mrunknown404.slabgen.utils.SGRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features.Placements;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class SGFeatures {
	public static final ConfiguredFeature<?, ?> GROUND_SLABS = register("ground_slabs",
			SGRegistry.GROUND_SLABS.get().configured(new NoFeatureConfig()).decorated(Placements.HEIGHTMAP_WORLD_SURFACE));
	
	private static <FC extends IFeatureConfig> ConfiguredFeature<?, ?> register(String name, ConfiguredFeature<FC, ?> feature) {
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, "pr_" + name, feature);
	}
}
