package gr.ionio.pijonmod.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, "pijon_mod");

    public static final RegistryObject<Potion> STINK_POTION = POTIONS.register("stink_potion",
            () -> new Potion(
                    new net.minecraft.world.effect.MobEffectInstance(ModEffects.STINK.getHolder().get(), -1, 0), // Βρώμα για πάντα
                    new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.POISON, 200, 0) // Poison για 10 δευτερόλεπτα
            ));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
