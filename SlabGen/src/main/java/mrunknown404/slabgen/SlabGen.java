package mrunknown404.slabgen;

import mrunknown404.slabgen.registries.SGBlocks;
import mrunknown404.slabgen.registries.SGFeatures;
import mrunknown404.slabgen.registries.SGItems;
import mrunknown404.slabgen.utils.ClientProxy;
import mrunknown404.slabgen.utils.SlabGenConfig;
import mrunknown404.slabgen.world.SGConfiguredFeatures;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SlabGen.MOD_ID)
public class SlabGen {
	public static final String MOD_ID = "slabgen";
	
	public SlabGen() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(ClientProxy::clientSetup);
		
		MinecraftForge.EVENT_BUS.register(this);
		SGBlocks.BLOCKS.register(bus);
		SGItems.ITEMS.register(bus);
		SGFeatures.FEATURES.register(bus);
	}
	
	@SubscribeEvent
	public void biomeLoad(BiomeLoadingEvent e) {
		boolean isWhitelist = SlabGenConfig.COMMON.biomeWhiteList.get(), contains = SlabGenConfig.COMMON.biomeList.get().contains(e.getName().toString());
		
		if ((isWhitelist && contains) || (!isWhitelist && !contains)) {
			e.getGeneration().addFeature(Decoration.TOP_LAYER_MODIFICATION.ordinal(), SGConfiguredFeatures.GROUND_SLABS_PLACEMENT);
		}
	}
}
