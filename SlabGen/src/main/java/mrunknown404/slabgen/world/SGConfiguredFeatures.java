package mrunknown404.slabgen.world;

import mrunknown404.slabgen.registries.SGFeatures;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class SGConfiguredFeatures {
	public static final Holder<ConfiguredFeature<NoneFeatureConfiguration, ?>> GROUND_SLABS = FeatureUtils.register("sg_ground_slabs", SGFeatures.GROUND_SLABS.get());
	public static final Holder<PlacedFeature> GROUND_SLABS_PLACEMENT = PlacementUtils.register("sg_ground_slabs", GROUND_SLABS, PlacementUtils.HEIGHTMAP_WORLD_SURFACE);
}
