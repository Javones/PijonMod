package gr.ionio.pijonmod.entity;

import gr.ionio.pijonmod.init.ModEffects;
import gr.ionio.pijonmod.init.ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.Entity;

public class PijonPoopEntity extends ThrowableItemProjectile {

    public PijonPoopEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public PijonPoopEntity(EntityType<? extends ThrowableItemProjectile> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    public PijonPoopEntity(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, Level level) {
        super(type, x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.PIJON_POOP.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity target = result.getEntity();
        Entity shooter = this.getOwner();
        Player playerOwner = null;
        
        if (shooter instanceof Pijon pijon && pijon.isTame()) {
            if (pijon.getOwner() instanceof Player p) {
                playerOwner = p;
            }
        } else if (shooter instanceof Player p) {
            playerOwner = p;
        }

        if (playerOwner != null) {
            if (target == playerOwner && shooter instanceof gr.ionio.pijonmod.entity.Pijon) {
                return;
            }
            if (target instanceof TamableAnimal tamableTarget) {
                if (tamableTarget.isOwnedBy(playerOwner)) {
                    return;
                }
            }
        }

        float damage = 2.0F;

        if (target instanceof EnderDragon || target instanceof EnderDragonPart) {
            if (playerOwner instanceof ServerPlayer serverPlayer) {
                AdvancementHolder advancement = serverPlayer.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath("pijonmod", "gotta_catch_em_all"));

                if (advancement != null && serverPlayer.getAdvancements().getOrStartProgress(advancement).isDone()) {
                    damage = 20.0F;
                }
            }
        }

        net.minecraft.resources.ResourceKey<net.minecraft.world.damagesource.DamageType> POOPED_DAMAGE =
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("pijonmod", "pooped"));

        net.minecraft.world.damagesource.DamageSource poopSource =
                new net.minecraft.world.damagesource.DamageSource(this.level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(POOPED_DAMAGE), this, shooter);

        target.hurt(poopSource, damage);

        if (target instanceof LivingEntity livingTarget) {
            MobEffectInstance stinkInstance = new MobEffectInstance(ModEffects.STINK.getHolder().get(), MobEffectInstance.INFINITE_DURATION, 0);

            livingTarget.addEffect(stinkInstance);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}