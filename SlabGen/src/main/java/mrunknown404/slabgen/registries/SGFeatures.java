package mrunknown404.slabgen.registries;

import java.util.function.Supplier;

import mrunknown404.slabgen.SlabGen;
import mrunknown404.slabgen.world.FeatureGroundSlabs;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SGFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, SlabGen.MOD_ID);
	
	public static final RegistryObject<Feature<NoneFeatureConfiguration>> GROUND_SLABS = feature("ground_slabs", () -> new FeatureGroundSlabs());
	
	static <T extends Feature<?>> RegistryObject<T> feature(String name, Supplier<T> o) {
		return FEATURES.register(name, o);
	}
}
