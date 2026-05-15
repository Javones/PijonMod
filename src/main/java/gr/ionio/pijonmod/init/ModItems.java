package gr.ionio.pijonmod.init;

import gr.ionio.pijonmod.item.PijonPoopItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "pijonmod");

    public static final RegistryObject<Item> PIJON_SPAWN_EGG = ITEMS.register("pijon_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.PIJON, 0x808080, 0x00FF00, new Item.Properties()));

    public static final RegistryObject<Item> PIJON_POOP = ITEMS.register("pijon_poop", () -> new PijonPoopItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
