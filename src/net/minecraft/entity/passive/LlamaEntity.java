/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LlamaEntity
extends AbstractDonkeyEntity
implements RangedAttackMob {
    private static final TrackedData<Integer> ATTR_STRENGTH = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CARPET_COLOR = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ATTR_VARIANT = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private boolean spit;
    @Nullable
    private LlamaEntity following;
    @Nullable
    private LlamaEntity follower;

    public LlamaEntity(EntityType<? extends LlamaEntity> entityType, World world) {
        super((EntityType<? extends AbstractDonkeyEntity>)entityType, world);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isTrader() {
        return false;
    }

    private void setStrength(int strength) {
        this.dataTracker.set(ATTR_STRENGTH, Math.max(1, Math.min(5, strength)));
    }

    private void initializeStrength() {
        int i = this.random.nextFloat() < 0.04f ? 5 : 3;
        this.setStrength(1 + this.random.nextInt(i));
    }

    public int getStrength() {
        return this.dataTracker.get(ATTR_STRENGTH);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Variant", this.getVariant());
        tag.putInt("Strength", this.getStrength());
        if (!this.items.getInvStack(1).isEmpty()) {
            tag.put("DecorItem", this.items.getInvStack(1).toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        this.setStrength(tag.getInt("Strength"));
        super.readCustomDataFromTag(tag);
        this.setVariant(tag.getInt("Variant"));
        if (tag.contains("DecorItem", 10)) {
            this.items.setInvStack(1, ItemStack.fromTag(tag.getCompound("DecorItem")));
        }
        this.updateSaddle();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new FormCaravanGoal(this, 2.1f));
        this.goalSelector.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0f));
        this.goalSelector.add(3, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new SpitRevengeGoal(this));
        this.targetSelector.add(2, new ChaseWolvesGoal(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTR_STRENGTH, 0);
        this.dataTracker.startTracking(CARPET_COLOR, -1);
        this.dataTracker.startTracking(ATTR_VARIANT, 0);
    }

    public int getVariant() {
        return MathHelper.clamp(this.dataTracker.get(ATTR_VARIANT), 0, 3);
    }

    public void setVariant(int variant) {
        this.dataTracker.set(ATTR_VARIANT, variant);
    }

    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 2 + 3 * this.method_6702();
        }
        return super.getInventorySize();
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float f = MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180));
        float g = MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180));
        float h = 0.3f;
        passenger.updatePosition(this.getX() + (double)(0.3f * g), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.getZ() - (double)(0.3f * f));
    }

    @Override
    public double getMountedHeightOffset() {
        return (double)this.getHeight() * 0.67;
    }

    @Override
    public boolean canBeControlledByRider() {
        return false;
    }

    @Override
    protected boolean receiveFood(PlayerEntity player, ItemStack item) {
        int i = 0;
        int j = 0;
        float f = 0.0f;
        boolean bl = false;
        Item item2 = item.getItem();
        if (item2 == Items.WHEAT) {
            i = 10;
            j = 3;
            f = 2.0f;
        } else if (item2 == Blocks.HAY_BLOCK.asItem()) {
            i = 90;
            j = 6;
            f = 10.0f;
            if (this.isTame() && this.getBreedingAge() == 0 && this.canEat()) {
                bl = true;
                this.lovePlayer(player);
            }
        }
        if (this.getHealth() < this.getMaximumHealth() && f > 0.0f) {
            this.heal(f);
            bl = true;
        }
        if (this.isBaby() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(i);
            }
            bl = true;
        }
        if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            bl = true;
            if (!this.world.isClient) {
                this.addTemper(j);
            }
        }
        if (bl && !this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LLAMA_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return bl;
    }

    @Override
    protected boolean isImmobile() {
        return this.getHealth() <= 0.0f || this.isEatingGrass();
    }

    @Override
    @Nullable
    public net.minecraft.entity.EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable net.minecraft.entity.EntityData entityData, @Nullable CompoundTag entityTag) {
        int i;
        this.initializeStrength();
        if (entityData instanceof EntityData) {
            i = ((EntityData)entityData).variant;
        } else {
            i = this.random.nextInt(4);
            entityData = new EntityData(i);
        }
        this.setVariant(i);
        return super.initialize(world, difficulty, spawnType, entityData, entityTag);
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.ENTITY_LLAMA_ANGRY;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_LLAMA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_LLAMA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_LLAMA_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_LLAMA_STEP, 0.15f, 1.0f);
    }

    @Override
    protected void playAddChestSound() {
        this.playSound(SoundEvents.ENTITY_LLAMA_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    @Override
    public void playAngrySound() {
        SoundEvent soundEvent = this.getAngrySound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public int method_6702() {
        return this.getStrength();
    }

    @Override
    public boolean canEquip() {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack item) {
        Item item2 = item.getItem();
        return ItemTags.CARPETS.contains(item2);
    }

    @Override
    public boolean canBeSaddled() {
        return false;
    }

    @Override
    public void onInvChange(Inventory inventory) {
        DyeColor dyeColor = this.getCarpetColor();
        super.onInvChange(inventory);
        DyeColor dyeColor2 = this.getCarpetColor();
        if (this.age > 20 && dyeColor2 != null && dyeColor2 != dyeColor) {
            this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5f, 1.0f);
        }
    }

    @Override
    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        super.updateSaddle();
        this.setCarpetColor(LlamaEntity.getColorFromCarpet(this.items.getInvStack(1)));
    }

    private void setCarpetColor(@Nullable DyeColor color) {
        this.dataTracker.set(CARPET_COLOR, color == null ? -1 : color.getId());
    }

    @Nullable
    private static DyeColor getColorFromCarpet(ItemStack color) {
        Block block = Block.getBlockFromItem(color.getItem());
        if (block instanceof CarpetBlock) {
            return ((CarpetBlock)block).getColor();
        }
        return null;
    }

    @Nullable
    public DyeColor getCarpetColor() {
        int i = this.dataTracker.get(CARPET_COLOR);
        return i == -1 ? null : DyeColor.byId(i);
    }

    @Override
    public int getMaxTemper() {
        return 30;
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return other != this && other instanceof LlamaEntity && this.canBreed() && ((LlamaEntity)other).canBreed();
    }

    @Override
    public LlamaEntity createChild(PassiveEntity passiveEntity) {
        LlamaEntity llamaEntity = this.createChild();
        this.setChildAttributes(passiveEntity, llamaEntity);
        LlamaEntity llamaEntity2 = (LlamaEntity)passiveEntity;
        int i = this.random.nextInt(Math.max(this.getStrength(), llamaEntity2.getStrength())) + 1;
        if (this.random.nextFloat() < 0.03f) {
            ++i;
        }
        llamaEntity.setStrength(i);
        llamaEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : llamaEntity2.getVariant());
        return llamaEntity;
    }

    protected LlamaEntity createChild() {
        return EntityType.LLAMA.create(this.world);
    }

    private void spitAt(LivingEntity target) {
        LlamaSpitEntity llamaSpitEntity = new LlamaSpitEntity(this.world, this);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - llamaSpitEntity.getY();
        double f = target.getZ() - this.getZ();
        float g = MathHelper.sqrt(d * d + f * f) * 0.2f;
        llamaSpitEntity.setVelocity(d, e + (double)g, f, 1.5f, 10.0f);
        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        this.world.spawnEntity(llamaSpitEntity);
        this.spit = true;
    }

    private void setSpit(boolean spit) {
        this.spit = spit;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        int i = this.computeFallDamage(fallDistance, damageMultiplier);
        if (i <= 0) {
            return false;
        }
        if (fallDistance >= 6.0f) {
            this.damage(DamageSource.FALL, i);
            if (this.hasPassengers()) {
                for (Entity entity : this.getPassengersDeep()) {
                    entity.damage(DamageSource.FALL, i);
                }
            }
        }
        this.playBlockFallSound();
        return true;
    }

    public void stopFollowing() {
        if (this.following != null) {
            this.following.follower = null;
        }
        this.following = null;
    }

    public void follow(LlamaEntity llamaEntity) {
        this.following = llamaEntity;
        this.following.follower = this;
    }

    public boolean hasFollower() {
        return this.follower != null;
    }

    public boolean isFollowing() {
        return this.following != null;
    }

    @Nullable
    public LlamaEntity getFollowing() {
        return this.following;
    }

    @Override
    protected double getRunFromLeashSpeed() {
        return 2.0;
    }

    @Override
    protected void walkToParent() {
        if (!this.isFollowing() && this.isBaby()) {
            super.walkToParent();
        }
    }

    @Override
    public boolean eatsGrass() {
        return false;
    }

    @Override
    public void attack(LivingEntity target, float f) {
        this.spitAt(target);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity mate) {
        return this.createChild(mate);
    }

    static class ChaseWolvesGoal
    extends FollowTargetGoal<WolfEntity> {
        public ChaseWolvesGoal(LlamaEntity llama) {
            super(llama, WolfEntity.class, 16, false, true, livingEntity -> !((WolfEntity)livingEntity).isTamed());
        }

        @Override
        protected double getFollowRange() {
            return super.getFollowRange() * 0.25;
        }
    }

    static class SpitRevengeGoal
    extends RevengeGoal {
        public SpitRevengeGoal(LlamaEntity llamaEntity) {
            super(llamaEntity, new Class[0]);
        }

        @Override
        public boolean shouldContinue() {
            LlamaEntity llamaEntity;
            if (this.mob instanceof LlamaEntity && (llamaEntity = (LlamaEntity)this.mob).spit) {
                llamaEntity.setSpit(false);
                return false;
            }
            return super.shouldContinue();
        }
    }

    static class EntityData
    extends PassiveEntity.EntityData {
        public final int variant;

        private EntityData(int variant) {
            this.variant = variant;
        }
    }
}

