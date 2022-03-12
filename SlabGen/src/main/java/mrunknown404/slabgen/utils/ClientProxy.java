package mrunknown404.slabgen.utils;

import mrunknown404.slabgen.registries.SGBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy {
	public static void clientSetup(@SuppressWarnings("unused") FMLClientSetupEvent e) {
		ItemBlockRenderTypes.setRenderLayer(SGBlocks.GRASS_SLAB.get(), RenderType.cutoutMipped());
		
		Minecraft mc = Minecraft.getInstance();
		mc.getBlockColors().register((state, reader, pos, tintIndex) -> {
			return reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColor.get(0.5, 1);
		}, SGBlocks.GRASS_SLAB.get());
		
		mc.getItemColors().register((itemstack, tintIndex) -> {
			BlockState blockstate = ((BlockItem) itemstack.getItem()).getBlock().defaultBlockState();
			return mc.getBlockColors().getColor(blockstate, null, null, tintIndex);
		}, SGBlocks.GRASS_SLAB.get());
	}
}
