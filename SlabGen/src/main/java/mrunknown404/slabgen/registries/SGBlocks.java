package mrunknown404.slabgen.registries;

import java.util.function.Supplier;

import mrunknown404.slabgen.SlabGen;
import mrunknown404.slabgen.block.SlabDirt;
import mrunknown404.slabgen.block.SlabFallable;
import mrunknown404.slabgen.block.SlabGrass;
import mrunknown404.slabgen.block.SlabMycelium;
import mrunknown404.slabgen.block.SlabPath;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SGBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SlabGen.MOD_ID);
	
	public static final RegistryObject<SlabBlock> DIRT_SLAB = block("dirt_slab", () -> new SlabDirt());
	public static final RegistryObject<SlabBlock> GRASS_SLAB = block("grass_slab", () -> new SlabGrass());
	public static final RegistryObject<SlabBlock> MYCELIUM_SLAB = block("mycelium_slab", () -> new SlabMycelium());
	public static final RegistryObject<SlabBlock> COARSE_DIRT_SLAB = block("coarse_dirt_slab",
			() -> new SlabBlock(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.5f, 0.5f)));
	public static final RegistryObject<SlabBlock> PODZOL_SLAB = block("podzol_slab",
			() -> new SlabBlock(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.5f, 0.5f)));
	public static final RegistryObject<Block> PATH_SLAB = block("path_slab", () -> new SlabPath());
	public static final RegistryObject<Block> SAND_SLAB = block("sand_slab", () -> new SlabFallable(SoundType.SAND, 0.5f, 0.5f, 14406560));
	public static final RegistryObject<Block> RED_SAND_SLAB = block("red_sand_slab", () -> new SlabFallable(SoundType.SAND, 0.5f, 0.5f, 11098145));
	public static final RegistryObject<Block> GRAVEL_SLAB = block("gravel_slab", () -> new SlabFallable(SoundType.GRAVEL, 0.6f, 0.6f, -8356741));
	
	static <T extends Block> RegistryObject<T> block(String name, Supplier<T> o) {
		RegistryObject<T> reg = BLOCKS.register(name, o);
		SGItems.item(name, () -> new BlockItem(reg.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
		return reg;
	}
}
