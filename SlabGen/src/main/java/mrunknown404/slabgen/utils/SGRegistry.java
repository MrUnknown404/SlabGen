package mrunknown404.slabgen.utils;

import java.util.function.Supplier;

import mrunknown404.slabgen.SlabGen;
import mrunknown404.slabgen.block.SlabDirt;
import mrunknown404.slabgen.block.SlabFallable;
import mrunknown404.slabgen.block.SlabGrass;
import mrunknown404.slabgen.block.SlabMycelium;
import mrunknown404.slabgen.block.SlabPath;
import mrunknown404.slabgen.world.FeatureGroundSlabs;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SGRegistry {
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SlabGen.MOD_ID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SlabGen.MOD_ID);
	private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, SlabGen.MOD_ID);
	
	public static final RegistryObject<Feature<NoFeatureConfig>> GROUND_SLABS = feature("ground_slabs", () -> new FeatureGroundSlabs());
	
	public static final RegistryObject<SlabBlock> DIRT_SLAB = block("dirt_slab", new SlabDirt());
	public static final RegistryObject<SlabBlock> GRASS_SLAB = block("grass_slab", new SlabGrass());
	public static final RegistryObject<SlabBlock> MYCELIUM_SLAB = block("mycelium_slab", new SlabMycelium());
	public static final RegistryObject<SlabBlock> COARSE_DIRT_SLAB = block("coarse_dirt_slab",
			new SlabBlock(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL).strength(0.5f, 0.5f)));
	public static final RegistryObject<SlabBlock> PODZOL_SLAB = block("podzol_slab",
			new SlabBlock(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL).strength(0.5f, 0.5f)));
	public static final RegistryObject<Block> PATH_SLAB = block("path_slab", new SlabPath());
	public static final RegistryObject<Block> SAND_SLAB = block("sand_slab", new SlabFallable(SoundType.SAND, 0.5f, 0.5f, 14406560));
	public static final RegistryObject<Block> RED_SAND_SLAB = block("red_sand_slab", new SlabFallable(SoundType.SAND, 0.5f, 0.5f, 11098145));
	public static final RegistryObject<Block> GRAVEL_SLAB = block("gravel_slab", new SlabFallable(SoundType.GRAVEL, 0.6f, 0.6f, -8356741));
	
	private static <T extends Block> RegistryObject<T> block(String name, T o) {
		RegistryObject<T> reg = BLOCKS.register(name, () -> o);
		item(name, new BlockItem(o, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
		return reg;
	}
	
	private static <T extends Item> RegistryObject<T> item(String name, T o) {
		return ITEMS.register(name, () -> o);
	}
	
	private static <T extends Feature<?>> RegistryObject<T> feature(String name, Supplier<T> o) {
		return FEATURES.register(name, o);
	}
	
	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
		ITEMS.register(bus);
		FEATURES.register(bus);
	}
}
