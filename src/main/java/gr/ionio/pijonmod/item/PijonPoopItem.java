package gr.ionio.pijonmod.item;

import gr.ionio.pijonmod.entity.PijonPoopEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class PijonPoopItem extends Item implements ProjectileItem {

    public PijonPoopItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        // Ο ήχος όταν το πετάς
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            // ΕΔΩ ΕΙΝΑΙ Η ΜΑΓΕΙΑ: Πλέον δημιουργεί το δικό σου Entity, όχι χιονόμπαλα!
            PijonPoopEntity poop = new PijonPoopEntity(gr.ionio.pijonmod.init.ModEntities.PIJON_POOP_PROJECTILE.get(), player, level);
            poop.setItem(itemStack);
            poop.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(poop);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        // Για όταν το ρίχνει το Dispenser
        PijonPoopEntity poop = new PijonPoopEntity(gr.ionio.pijonmod.init.ModEntities.PIJON_POOP_PROJECTILE.get(), position.x(), position.y(), position.z(), level);
        poop.setItem(itemStack);
        return poop;
    }
}