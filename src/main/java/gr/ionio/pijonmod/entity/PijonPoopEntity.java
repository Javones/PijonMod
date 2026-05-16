package gr.ionio.pijonmod.entity; // Βάλτο στον φάκελο entity

import gr.ionio.pijonmod.init.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.entity.Entity;

public class PijonPoopEntity extends ThrowableItemProjectile {

    // 1. Βασικός Κατασκευαστής (Τον χρειάζεται το παιχνίδι για να φορτώσει το Entity)
    public PijonPoopEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    // 2. Κατασκευαστής για τον Παίκτη/Mob (Όταν κάποιος το πετάει)
    public PijonPoopEntity(EntityType<? extends ThrowableItemProjectile> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    // 3. Κατασκευαστής για Dispenser (Πετάγεται από συγκεκριμένες συντεταγμένες X, Y, Z)
    public PijonPoopEntity(EntityType<? extends ThrowableItemProjectile> type, double x, double y, double z, Level level) {
        super(type, x, y, z, level);
    }

    // Η μέθοδος που ΣΕ ΥΠΟΧΡΕΩΝΕΙ η Java να γράψεις!
    // Εδώ της λες "Όσο πετάω στον αέρα, θέλω να φαίνομαι σαν την κουτσουλιά"
    @Override
    protected Item getDefaultItem() {
        return ModItems.PIJON_POOP.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity target = hitResult.getEntity();

        target.hurt(this.damageSources().thrown(this, this.getOwner()), 2.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}
