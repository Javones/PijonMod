package gr.ionio.pijonmod.init;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "pijonmod");

    public static final RegistryObject<MobEffect> STINK = MOB_EFFECTS.register("stink",
            () -> new MobEffect(MobEffectCategory.HARMFUL, 0x556B2F) {
                @Override
                public java.util.List<ItemStack> getCurativeItems() {
                    return java.util.Collections.emptyList();
                }
    });

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
