package mrunknown404.slabgen;

import mrunknown404.slabgen.utils.ClientProxy;
import mrunknown404.slabgen.utils.SGRegistry;
import mrunknown404.slabgen.utils.SlabGenConfig;
import mrunknown404.slabgen.world.SGFeatures;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SlabGen.MOD_ID)
public class SlabGen {
	public static final String MOD_ID = "slabgen";
	
	public SlabGen() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(ClientProxy::clientSetup);
		
		MinecraftForge.EVENT_BUS.register(this);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SlabGenConfig.COMMON_SPEC);
		SGRegistry.register(bus);
	}
	
	@SubscribeEvent
	public void biomeLoad(BiomeLoadingEvent e) {
		boolean isWhitelist = SlabGenConfig.COMMON.biomeWhiteList.get(), contains = SlabGenConfig.COMMON.biomeList.get().contains(e.getName().toString());
		
		if ((isWhitelist && contains) || (!isWhitelist && !contains)) {
			e.getGeneration().addFeature(Decoration.TOP_LAYER_MODIFICATION.ordinal(), () -> SGFeatures.GROUND_SLABS);
		}
	}
}
