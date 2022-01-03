package mrunknown404.slabgen.utils;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.world.GrassColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy {
	public static void clientSetup(@SuppressWarnings("unused") FMLClientSetupEvent e) {
		RenderTypeLookup.setRenderLayer(SGRegistry.GRASS_SLAB.get(), RenderType.cutoutMipped());
		
		Minecraft mc = Minecraft.getInstance();
		mc.getBlockColors().register((state, reader, pos, tintIndex) -> {
			return reader != null && pos != null ? BiomeColors.getAverageGrassColor(reader, pos) : GrassColors.get(0.5, 1);
		}, SGRegistry.GRASS_SLAB.get());
		
		mc.getItemColors().register((itemstack, tintIndex) -> {
			BlockState blockstate = ((BlockItem) itemstack.getItem()).getBlock().defaultBlockState();
			return mc.getBlockColors().getColor(blockstate, null, null, tintIndex);
		}, SGRegistry.GRASS_SLAB.get());
	}
}
