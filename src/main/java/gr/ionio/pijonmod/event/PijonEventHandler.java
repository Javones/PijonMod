package gr.ionio.pijonmod.event;

import gr.ionio.pijonmod.entity.Pijon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Συνδέουμε την κλάση απευθείας στο Forge Event Bus του παιχνιδιού
@Mod.EventBusSubscriber(modid = "pijonmod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PijonEventHandler {

    @SubscribeEvent
    public static void onPlayerAttackDragon(AttackEntityEvent event) {
        // Ελέγχουμε αν αυτό που χτύπησε ο παίκτης είναι κομμάτι (hitbox) του δράκου
        if (event.getTarget() instanceof EnderDragonPart dragonPart) {
            Player player = event.getEntity();

            // Το τρέχουμε μόνο στον Server για να μην έχουμε desync
            if (!player.level().isClientSide) {

                // Παίρνουμε την κύρια οντότητα του δράκου μέσω του κομματιού του
                EnderDragon dragon = dragonPart.parentMob;

                // Ψάχνουμε σε ακτίνα 64 blocks (ένα τεράστιο κουτί γύρω από τον παίκτη)
                AABB searchArea = player.getBoundingBox().inflate(64.0D);

                // Βρίσκουμε όλα τα περιστέρια σε αυτή την περιοχή
                for (Pijon pijon : player.level().getEntitiesOfClass(Pijon.class, searchArea)) {

                    // Αν το περιστέρι είναι δικό μας και ΔΕΝ του έχουμε πει να κάθεται
                    if (pijon.isTame() && pijon.isOwnedBy(player) && !pijon.isOrderedToSit()) {

                        // Παρακάμπτουμε το κανονικό AI και του θέτουμε τον δράκο ως στόχο!
                        pijon.setTarget(dragon);
                    }
                }
            }
        }
    }
}