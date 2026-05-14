package gr.ionio.pijonmod.init;

import gr.ionio.pijonmod.entity.Pijon;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "pijonmod");

    public static final RegistryObject<EntityType<Pijon>> PIJON = ENTITY_TYPES.register("pijon", () -> EntityType.Builder.of(Pijon::new, MobCategory.CREATURE).sized(0.5f, 0.9f).clientTrackingRange(8).build("pijon"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
