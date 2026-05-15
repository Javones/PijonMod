package gr.ionio.pijonmod.event;

import gr.ionio.pijonmod.entity.Pijon;
import gr.ionio.pijonmod.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "pijonmod")
public class PijonVillageSpawner {

    private static int tickDelay = 0;

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return;
        ServerLevel level = (ServerLevel) event.level;

        tickDelay++;
        if (tickDelay >= 200) {
            tickDelay = 0;

            for (Player player : level.players()) {
                AABB searchBox = player.getBoundingBox().inflate(64.0D);
                List<Villager> villagers = level.getEntitiesOfClass(Villager.class, searchBox);

                if (villagers.size() >= 2) {
                    List<Pijon> pigeons = level.getEntitiesOfClass(Pijon.class, searchBox);
                    int maxPigeons = villagers.size() * 2;

                    if (pigeons.size() < maxPigeons) {
                        Villager randomVillager = villagers.get(level.random.nextInt(villagers.size()));

                        int dx = level.random.nextInt(11) - 5;
                        int dz = level.random.nextInt(11) - 5;
                        BlockPos basePos = randomVillager.blockPosition().offset(dx, 0, dz);

                        BlockPos spawnPos = null;

                        for (int y = 5; y >= -5; y--) {
                            BlockPos checkPos = basePos.above(y);

                            boolean isSpaceEmpty = level.getBlockState(checkPos).getCollisionShape(level, checkPos).isEmpty();

                            boolean isGroundSolid = !level.getBlockState(checkPos.below()).isAir() && level.getFluidState(checkPos.below()).isEmpty();

                            if (isSpaceEmpty && isGroundSolid) {
                                spawnPos = checkPos;
                                break;
                            }
                        }

                        if (spawnPos != null) {
                            Pijon newPigeon = ModEntities.PIJON.get().create(level);
                            if (newPigeon != null) {
                                newPigeon.moveTo(spawnPos, 0.0F, 0.0F);
                                newPigeon.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.NATURAL, null);
                                level.addFreshEntity(newPigeon);
                                System.out.println("Success! Pigeon spawned at: " + spawnPos);
                            }
                        } else {
                            System.out.println("Failed: No suitable ground found.");
                        }
                    }
                }
            }
        }
    }
}