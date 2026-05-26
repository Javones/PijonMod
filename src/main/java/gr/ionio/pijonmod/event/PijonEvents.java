package gr.ionio.pijonmod.event;

import gr.ionio.pijonmod.init.ModEffects;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "pijonmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PijonEvents {

    @SubscribeEvent
    public static void preventMilkClearingStink(MobEffectEvent.Remove event) {
        if (event.getEffectInstance() != null) {
            String effectBeingRemoved = event.getEffectInstance().getDescriptionId();
            String StinkEffect = ModEffects.STINK.get().getDescriptionId();

            if (effectBeingRemoved.equals(StinkEffect)) {
                if (event.getEntity().isInWaterOrRain()) {
                    return;
                }
                event.setCanceled(true);
            }
        }
    }
}