package mrunknown404.slabgen.utils;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.GrassColors;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy {
	public static void clientSetup(@SuppressWarnings("unused") FMLClientSetupEvent e) {
		RenderTypeLookup.setRenderLayer(SGRegistry.GRASS_SLAB.get(), RenderType.cutoutMipped());
		
		Minecraft mc = Minecraft.getInstance();
		mc.getBlockColors().register((state, reader, pos, tintIndex) -> {
			// This is weird. Vanilla doesn't do this but this messes with particles otherwise??
			// I'm probably just missing something
			return reader instanceof ClientWorld ? -1 : mc.getBlockColors().getColor(Blocks.GRASS_BLOCK.defaultBlockState(), reader, pos, tintIndex);
		}, SGRegistry.GRASS_SLAB.get());
		
		mc.getItemColors().register((itemstack, tintIndex) -> {
			return GrassColors.get(0.5, 1);
		}, SGRegistry.GRASS_SLAB.get());
	}
}
