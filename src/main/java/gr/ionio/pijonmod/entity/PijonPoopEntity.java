package gr.ionio.pijonmod.entity; // Βάλτο στον φάκελο entity

import gr.ionio.pijonmod.init.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

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

    // --------------------------------------------------------
    // ΑΠΟ ΕΔΩ ΚΑΙ ΚΑΤΩ ΕΙΝΑΙ Η ΣΕΙΡΑ ΣΟΥ!
    // Εδώ θα γράψεις την onHit() ή onHitEntity()
    // για να βάλεις το effect της βρώμας που συζητήσαμε!
    // --------------------------------------------------------

}