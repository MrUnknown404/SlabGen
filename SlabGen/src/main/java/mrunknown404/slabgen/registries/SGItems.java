package mrunknown404.slabgen.registries;

import java.util.function.Supplier;

import mrunknown404.slabgen.SlabGen;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SGItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SlabGen.MOD_ID);
	
	static <T extends Item> RegistryObject<T> item(String name, Supplier<T> o) {
		return ITEMS.register(name, o);
	}
}
