package gr.ionio.pijonmod.init;

import gr.ionio.pijonmod.entity.Pijon;
import gr.ionio.pijonmod.entity.PijonPoopEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    // 1. Δημιουργία του Καταλόγου για τα Entities
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "pijonmod");

    // 2. Καταχώρηση του Περιστεριού (Pijon)
    public static final RegistryObject<EntityType<Pijon>> PIJON = ENTITIES.register("pijon",
            () -> EntityType.Builder.of(Pijon::new, MobCategory.CREATURE)
                    .sized(0.4F, 0.5F) // Τα μεγέθη του περιστεριού
                    .build("pijon"));

    // 3. Καταχώρηση της Κουτσουλιάς (PijonPoopEntity) με τη Lambda έκφραση
    public static final RegistryObject<EntityType<PijonPoopEntity>> PIJON_POOP_PROJECTILE = ENTITIES.register("pijon_poop_projectile",
            () -> EntityType.Builder.<PijonPoopEntity>of((type, level) -> new PijonPoopEntity(type, level), MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("pijon_poop_projectile"));

    // 4. Η μέθοδος register για να συνδεθεί με το Main class του Mod
    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}