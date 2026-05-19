package gr.ionio.pijonmod.entity;

import com.mojang.serialization.Codec;
import java.util.EnumSet;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import gr.ionio.pijonmod.init.ModEffects;
import gr.ionio.pijonmod.init.ModItems;
import gr.ionio.pijonmod.init.ModSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraftforge.event.ForgeEventFactory;

import static net.minecraft.tags.ItemTags.VILLAGER_PLANTABLE_SEEDS;

public class Pijon extends ShoulderRidingEntity implements VariantHolder<Pijon.Variant>, RangedAttackMob {
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(Pijon.class, EntityDataSerializers.INT);

    public float flap;
    //Variables for delivering letters
    public ItemStack carriedLetter = ItemStack.EMPTY;
    public String targetPlayerName = "";

    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    private float flapping = 1.0F;
    private float nextFlap = 1.0F;

    public int poopTime = this.random.nextInt(3600) + 3600;

    public Pijon(EntityType<? extends Pijon> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        //Getting Biome in which the pijon is spawning in
        net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome> biome = world.getBiome(this.blockPosition());

        //Cherry grove: White, dotted or rare purple (1 in 100 chance)
        if (biome.is(BiomeTags.IS_OVERWORLD) && biome.unwrapKey().isPresent() && biome.unwrapKey().get().location().getPath().equals("cherry_grove")) {
            if (this.random.nextInt(100) == 0) {
                this.setVariant(Variant.PURPLE);
            } else if (this.random.nextInt(7) == 0){
                this.setVariant(Variant.DOTTED);
            } else {
                this.setVariant(Variant.WHITE);
            }
        }
        //Mushroom fields: Only red
        else if (biome.is(BiomeTags.IS_OVERWORLD) && biome.unwrapKey().isPresent() && biome.unwrapKey().get().location().getPath().equals("mushroom_fields")) {
            this.setVariant(Variant.RED);
        }
        //Savanna: Only brown and brown-grey
        else if (biome.is(BiomeTags.IS_SAVANNA)) {
            this.setVariant(this.random.nextBoolean() ? Variant.BROWN : Variant.BROWN_GREY);
        }
        //Rest of the biomes: Grey, brown grey-brown, white, dotted
        else {
            int randomColor = this.random.nextInt(6);
            if (randomColor == 0) this.setVariant(Variant.GREY);
            else if (randomColor == 1) this.setVariant(Variant.BROWN);
            else if (randomColor == 2) this.setVariant(Variant.BROWN_GREY);
            else if (randomColor == 5) this.setVariant(Variant.DOTTED);
            else this.setVariant(Variant.WHITE);
        }

        return super.finalizeSpawn(world, difficulty, spawnType, spawnData == null ? new AgeableMob.AgeableMobGroupData(false) : spawnData);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected void registerGoals() {

        this.goalSelector.addGoal(1, new DeliverLetterGoal(this));

        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PanicGoal(this, 2.8D));

        //Fear from player
        this.goalSelector.addGoal(2,
                new AvoidEntityGoal<>(
                        this,
                        Player.class,
                        10.0F,
                        1.8D,
                        2.8D,
                        player -> {

                            boolean holdingSeeds =
                                    player.getMainHandItem().is(VILLAGER_PLANTABLE_SEEDS)
                                            || player.getOffhandItem().is(VILLAGER_PLANTABLE_SEEDS);

                            return !player.isShiftKeyDown()
                                    && !holdingSeeds
                                    && !this.isTame();
                        }
                )
        );

        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(VILLAGER_PLANTABLE_SEEDS), false) {
            @Override
            public boolean canUse() {
                return super.canUse() && !Pijon.this.isOrderedToSit();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !Pijon.this.isOrderedToSit();
            }
        });

        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.1D, 5.0F, 1.0F));
        this.goalSelector.addGoal(5, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new PijonShortFlyGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));

        this.goalSelector.addGoal(1, new PijonBombingGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6.0)
                .add(Attributes.FLYING_SPEED, 0.6F)
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.ATTACK_DAMAGE, 256.0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation navigation = new GroundPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.calculateFlapping();

        if (!this.level().isClientSide && !this.isTame()) {
            Player player = this.level().getNearestPlayer(this, 8.0D);

            if (player != null && !player.isCreative() && !player.isSpectator()) {
                boolean holdingSeeds =
                        player.getMainHandItem().is(VILLAGER_PLANTABLE_SEEDS) ||
                                player.getOffhandItem().is(VILLAGER_PLANTABLE_SEEDS);

                if (!player.isShiftKeyDown() && !holdingSeeds) {

                    double dx = this.getX() - player.getX();
                    double dz = this.getZ() - player.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    if (distance > 0.001D) {
                        this.pijonPanic(player.getX(), player.getZ());

                        for (Pijon nearbyPijon : this.level().getEntitiesOfClass(Pijon.class, this.getBoundingBox().inflate(5.0D))) {
                            if (!nearbyPijon.isTame()) {
                                nearbyPijon.pijonPanic(player.getX(), player.getZ());
                            }
                        }
                    }
                }
            }
        }

        //Dropping poops
        if (!this.level().isClientSide && this.isAlive()) {
            if (--this.poopTime <= 0) {
                this.spawnAtLocation(ModItems.PIJON_POOP.get());
                this.gameEvent(GameEvent.ENTITY_PLACE);
                this.poopTime = this.random.nextInt(3600) + 3600;
            }
        }

        if (!this.level().isClientSide && this.hasCustomName() && "jeb_".equalsIgnoreCase(this.getCustomName().getString())) {
            if (this.tickCount % 15 == 0) {
                int nextVariantId = (this.getVariant().getId() + 1) % 7;
                this.setVariant(Pijon.Variant.byId(nextVariantId));
            }
        }
    }

    private void pijonPanic(double sourceX, double sourceZ) {
        double dx = this.getX() - sourceX;
        double dz = this.getZ() - sourceZ;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0.001D) {
            dx /= distance;
            dz /= distance;

            float targetYaw = (float)(Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;

            this.setYRot(targetYaw);
            this.yBodyRot = targetYaw;
            this.yHeadRot = targetYaw;

            this.setDeltaMovement(dx * 0.3D, 0.25D, dz * 0.3D);
            this.hasImpulse = true;
        }
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed = this.flapSpeed + (float)(!this.onGround() && !this.isPassenger() ? 4 : -1) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();

        if (!this.onGround() && vec3.y < 0.0) {
            this.setDeltaMovement(vec3.multiply(1.0, 0.6, 1.0));
        }

        this.flap = this.flap + this.flapping * 2.0F;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        //Taming logic
        if (!this.isTame() && itemstack.is(net.minecraft.tags.ItemTags.VILLAGER_PLANTABLE_SEEDS)) {
            itemstack.consume(1, player);
            if (!this.isSilent()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if (!this.level().isClientSide) {
                if (this.random.nextInt(10) == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.level().broadcastEntityEvent(this, (byte)7);

                    if (player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.TAME_ANIMAL.trigger(serverPlayer, this);
                    }
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        //Logic for tamed pijones
        if (this.isTame() && this.isOwnedBy(player)) {

            if (itemstack.is(net.minecraft.tags.ItemTags.VILLAGER_PLANTABLE_SEEDS) && this.getHealth() < this.getMaxHealth()) {
                if (!this.level().isClientSide) {
                    itemstack.consume(1, player); // Τρώει 1 σπόρο
                    this.heal(1.0F); //Heals half a heart
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);

                    // ΕΛΕΓΧΟΣ ΜΕΤΑ ΤΟ ΦΑΓΗΤΟ:
                    if (this.getHealth() >= this.getMaxHealth()) {
                        // Αν αυτός ο σπόρος το φούλαρε, βγάζει Καρδούλες!
                        this.level().broadcastEntityEvent(this, (byte)7);
                    } else {
                        // Αν χρειάζεται κι άλλο φαγητό, βγάζει Πράσινα Αστεράκια
                        ((net.minecraft.server.level.ServerLevel) this.level()).sendParticles(
                                net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
                                this.getX(), this.getY() + 0.5D, this.getZ(),
                                7, 0.3D, 0.3D, 0.3D, 0.0D);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            //Accepts either signed book or paper (with name from anvil)
            boolean isWrittenBook = itemstack.is(net.minecraft.world.item.Items.WRITTEN_BOOK);
            boolean isNamedPaper = itemstack.is(net.minecraft.world.item.Items.PAPER) && itemstack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME);

            if (isWrittenBook || isNamedPaper) {
                if (!this.level().isClientSide) {
                    this.carriedLetter = itemstack.copyWithCount(1);
                    itemstack.shrink(1);

                    //Gets name either from title of the book either from the name of the paper
                    this.targetPlayerName = this.carriedLetter.getHoverName().getString().replaceAll("[^a-zA-Z0-9_]", "");

                    this.setOrderedToSit(false);
                    this.setInSittingPose(false);

                    //Sends message that the letter was received
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("Taking letter to " + this.targetPlayerName), false);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
                //Reversing state
                boolean isSitting = !this.isOrderedToSit();
                this.setOrderedToSit(isSitting);
                this.setInSittingPose(isSitting);

                //Stopping all other movement
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) { }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other) {
        return null;
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound() {
        return ModSounds.PIJON_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.playSound(ModSounds.PIJON_FLY.get(), 0.15F, 1.0F);
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    @Override
    public float getVoicePitch() {
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void doPush(Entity entity) {
        if (!(entity instanceof Player)) {
            super.doPush(entity);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        Entity attacker = source.getEntity();

        if (attacker != null && attacker.getType() == net.minecraft.world.entity.EntityType.ENDER_DRAGON) {
            return false;
        }

        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
            }
            return super.hurt(source, amount);
        }
    }

    public Pijon.Variant getVariant() {
        return Pijon.Variant.byId(this.entityData.get(DATA_VARIANT_ID));
    }

    public void setVariant(Pijon.Variant variant) {
        this.entityData.set(DATA_VARIANT_ID, variant.id);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT_ID, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant().id);
        compound.putInt("PoopLayTime", this.poopTime);

        if (!this.carriedLetter.isEmpty()) {
            compound.put("Letter", this.carriedLetter.saveOptional(this.registryAccess()));
            compound.putString("TargetPlayer", this.targetPlayerName);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(Pijon.Variant.byId(compound.getInt("Variant")));
        if (compound.contains("PoopLayTime")) {
            this.poopTime = compound.getInt("PoopLayTime");
        }

        if (compound.contains("Letter")) {
            this.carriedLetter = ItemStack.parseOptional(this.registryAccess(), compound.getCompound("Letter"));
            this.targetPlayerName = compound.getString("TargetPlayer");
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        //Drops the letter (if it exists)
        if (!this.carriedLetter.isEmpty()) {
            this.spawnAtLocation(this.carriedLetter);
        }

        //Checks colour and desides which feather it will drop
        ItemLike featherDrop = switch (this.getVariant()) {
            case GREY -> ModItems.GREY_FEATHER.get();
            case BROWN -> ModItems.BROWN_FEATHER.get();
            case BROWN_GREY -> ModItems.BROWN_GREY_FEATHER.get();
            case WHITE -> ModItems.WHITE_FEATHER.get();
            case PURPLE -> ModItems.PURPLE_FEATHER.get();
            case DOTTED -> ModItems.DOTTED_FEATHER.get();
            case RED -> ModItems.RED_FEATHER.get();
        };

        //Drops the feather
        this.spawnAtLocation(featherDrop);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    static class PijonShortFlyGoal extends Goal {
        private final Pijon pijon;

        public PijonShortFlyGoal(Pijon pijon) {
            this.pijon = pijon;
        }

        @Override
        public boolean canUse() {
            return !this.pijon.isOrderedToSit() && this.pijon.getRandom().nextInt(150) == 0 && this.pijon.onGround();
        }

        @Override
        public void start() {
            float yaw = this.pijon.getYRot() * ((float)Math.PI / 180F);

            double forwardX = (double)(-Mth.sin(yaw)) * 0.4;
            double forwardZ = (double)(Mth.cos(yaw)) * 0.4;

            this.pijon.setDeltaMovement(forwardX, 0.45, forwardZ);
        }
    }

    static class DeliverLetterGoal extends Goal {
        private final Pijon pijon;
        private Player targetPlayer;

        public DeliverLetterGoal(Pijon pijon) {
            this.pijon = pijon;
            this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !this.pijon.carriedLetter.isEmpty() && !this.pijon.targetPlayerName.isEmpty() && !this.pijon.isOrderedToSit();
        }

        @Override
        public void tick() {
            //Searches for player
            if (this.targetPlayer == null || !this.targetPlayer.getName().getString().replaceAll("[^a-zA-Z0-9_]", "").equalsIgnoreCase(this.pijon.targetPlayerName)) {
                for (Player p : this.pijon.level().players()) {
                    String pName = p.getName().getString().replaceAll("[^a-zA-Z0-9_]", "");
                    if (pName.equalsIgnoreCase(this.pijon.targetPlayerName)) {
                        this.targetPlayer = p;
                        break;
                    }
                }
            }

            //Flies if player was found
            if (this.targetPlayer != null) {
                this.pijon.getLookControl().setLookAt(this.targetPlayer, 10.0F, (float)this.pijon.getMaxHeadXRot());
                this.pijon.getNavigation().moveTo(this.targetPlayer, 1.5D);

                double dX = this.pijon.getX() - this.targetPlayer.getX();
                double dZ = this.pijon.getZ() - this.targetPlayer.getZ();
                double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);

                if (horizontalDistance < 3.0D) {

                    net.minecraft.world.entity.LivingEntity owner = this.pijon.getOwner();
                    if (owner instanceof Player) {
                        ((Player) owner).displayClientMessage(net.minecraft.network.chat.Component.literal("Letter delivered!"), false);
                    }

                    this.pijon.spawnAtLocation(this.pijon.carriedLetter.copy());

                    this.pijon.carriedLetter = ItemStack.EMPTY;
                    this.pijon.targetPlayerName = "";
                    this.targetPlayer = null;

                    this.pijon.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == ModEffects.STINK.getHolder().get()) {
            return false;
        }

        return super.canBeAffected(effectInstance);
    }

    public static enum Variant implements StringRepresentable {
        GREY(0, "grey"),
        BROWN(1, "brown"),
        BROWN_GREY(2, "brown_grey"),
        WHITE(3, "white"),
        PURPLE(4, "purple"),
        DOTTED(5, "dotted"),
        RED(6, "red");

        public static final Codec<Pijon.Variant> CODEC = StringRepresentable.fromEnum(Pijon.Variant::values);
        private static final IntFunction<Pijon.Variant> BY_ID = ByIdMap.continuous(Pijon.Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
        final int id;
        private final String name;

        Variant(final int id, final String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return this.id; }
        public static Pijon.Variant byId(int id) { return BY_ID.apply(id); }
        @Override
        public String getSerializedName() { return this.name; }
    }

    static class PijonBombingGoal extends Goal {
        private final Pijon pijon;
        private int attackCooldown = 0;

        public PijonBombingGoal(Pijon pijon) {
            this.pijon = pijon;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.pijon.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = this.pijon.getTarget();
            if (target == null) return;

            this.pijon.getLookControl().setLookAt(target, 30.0F, 30.0F);

            double targetX = target.getX();
            double targetY = target.getY() + 3.5D;
            double targetZ = target.getZ();

            double dx = targetX - this.pijon.getX();
            double dy = targetY - this.pijon.getY();
            double dz = targetZ - this.pijon.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.5D) {
                double speed = 0.25D;
                this.pijon.setDeltaMovement(dx / distance * speed, dy / distance * speed, dz / distance * speed);
            }

            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

            if (this.attackCooldown > 0) {
                this.attackCooldown--;
            }

            if (horizontalDistance < 2.5D && this.pijon.getY() > target.getY() + 1.5D && this.attackCooldown <= 0) {
                this.pijon.performRangedAttack(target, 1.0F);
                this.attackCooldown = 30;
            }
        }
    }

    @Nullable
    private void poop(LivingEntity target) {
        PijonPoopEntity poop = new PijonPoopEntity(gr.ionio.pijonmod.init.ModEntities.PIJON_POOP_PROJECTILE.get(), this.level());
        poop.setOwner(this);
        poop.setPos(this.getX(), this.getY() + 0.2D, this.getZ());
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333) - poop.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        poop.shoot(d0, d1 + d3, d2, 2.0F, 0.0F);
        if (!this.isSilent()) {
            this.level()
                    .playSound(
                            null,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            SoundEvents.LLAMA_SPIT,
                            this.getSoundSource(),
                            1.0F,
                            1.0F + (this.random.nextFloat() - this.random.nextFloat() * 0.2F)
                    );

            this.level().addFreshEntity(poop);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) { this.poop(target); }
}