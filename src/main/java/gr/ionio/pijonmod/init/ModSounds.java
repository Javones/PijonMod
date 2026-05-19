package gr.ionio.pijonmod.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "pijonmod");
    
    public static final RegistryObject<SoundEvent> PIJON_AMBIENT = registerSoundEvent("pijon_ambient");
    public static final RegistryObject<SoundEvent> PIJON_FLY = registerSoundEvent("pijon_fly");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("pijonmod", name)));
    }
}