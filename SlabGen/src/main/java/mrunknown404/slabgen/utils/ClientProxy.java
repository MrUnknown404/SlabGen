package mrunknown404.slabgen.utils;

import mrunknown404.slabgen.registries.SGBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy {
	public static void clientSetup(@SuppressWarnings("unused") FMLClientSetupEvent e) {
		ItemBlockRenderTypes.setRenderLayer(SGBlocks.GRASS_SLAB.get(), RenderType.cutoutMipped());
		
		Minecraft mc = Minecraft.getInstance();
		mc.getBlockColors().register((state, reader, pos, tintIndex) -> {
			return mc.getBlockColors().getColor(Blocks.GRASS_BLOCK.defaultBlockState(), reader, pos, tintIndex);
		}, SGBlocks.GRASS_SLAB.get());
		
		mc.getItemColors().register((itemstack, tintIndex) -> {
			return GrassColor.get(0.5, 1);
		}, SGBlocks.GRASS_SLAB.get());
	}
}
