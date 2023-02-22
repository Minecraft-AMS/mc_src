/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PigEntity
extends AnimalEntity {
    private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> field_6815 = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private boolean field_6814;
    private int field_6812;
    private int field_6813;

    public PigEntity(EntityType<? extends PigEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new TemptGoal((MobEntityWithAi)this, 1.2, Ingredient.ofItems(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.add(4, new TemptGoal((MobEntityWithAi)this, 1.2, false, BREEDING_INGREDIENT));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        if (this.getPassengerList().isEmpty()) {
            return null;
        }
        return this.getPassengerList().get(0);
    }

    @Override
    public boolean canBeControlledByRider() {
        Entity entity = this.getPrimaryPassenger();
        if (!(entity instanceof PlayerEntity)) {
            return false;
        }
        PlayerEntity playerEntity = (PlayerEntity)entity;
        return playerEntity.getMainHandStack().getItem() == Items.CARROT_ON_A_STICK || playerEntity.getOffHandStack().getItem() == Items.CARROT_ON_A_STICK;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (field_6815.equals(data) && this.world.isClient) {
            this.field_6814 = true;
            this.field_6812 = 0;
            this.field_6813 = this.dataTracker.get(field_6815);
        }
        super.onTrackedDataSet(data);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SADDLED, false);
        this.dataTracker.startTracking(field_6815, 0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("Saddle", this.isSaddled());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setSaddled(tag.getBoolean("Saddle"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean interactMob(PlayerEntity player, Hand hand) {
        if (!super.interactMob(player, hand)) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.getItem() == Items.NAME_TAG) {
                itemStack.useOnEntity(player, this, hand);
                return true;
            }
            if (this.isSaddled() && !this.hasPassengers()) {
                if (!this.world.isClient) {
                    player.startRiding(this);
                }
                return true;
            }
            return itemStack.getItem() == Items.SADDLE && itemStack.useOnEntity(player, this, hand);
        }
        return true;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isSaddled()) {
            this.dropItem(Items.SADDLE);
        }
    }

    public boolean isSaddled() {
        return this.dataTracker.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        if (saddled) {
            this.dataTracker.set(SADDLED, true);
        } else {
            this.dataTracker.set(SADDLED, false);
        }
    }

    @Override
    public void onStruckByLightning(LightningEntity lightning) {
        ZombiePigmanEntity zombiePigmanEntity = EntityType.ZOMBIE_PIGMAN.create(this.world);
        zombiePigmanEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
        zombiePigmanEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
        zombiePigmanEntity.setAiDisabled(this.isAiDisabled());
        if (this.hasCustomName()) {
            zombiePigmanEntity.setCustomName(this.getCustomName());
            zombiePigmanEntity.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.world.spawnEntity(zombiePigmanEntity);
        this.remove();
    }

    @Override
    public void travel(Vec3d movementInput) {
        Entity entity;
        if (!this.isAlive()) {
            return;
        }
        Entity entity2 = entity = this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0);
        if (!this.hasPassengers() || !this.canBeControlledByRider()) {
            this.stepHeight = 0.5f;
            this.flyingSpeed = 0.02f;
            super.travel(movementInput);
            return;
        }
        this.prevYaw = this.yaw = entity.yaw;
        this.pitch = entity.pitch * 0.5f;
        this.setRotation(this.yaw, this.pitch);
        this.bodyYaw = this.yaw;
        this.headYaw = this.yaw;
        this.stepHeight = 1.0f;
        this.flyingSpeed = this.getMovementSpeed() * 0.1f;
        if (this.field_6814 && this.field_6812++ > this.field_6813) {
            this.field_6814 = false;
        }
        if (this.isLogicalSideForUpdatingMovement()) {
            float f = (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue() * 0.225f;
            if (this.field_6814) {
                f += f * 1.15f * MathHelper.sin((float)this.field_6812 / (float)this.field_6813 * (float)Math.PI);
            }
            this.setMovementSpeed(f);
            super.travel(new Vec3d(0.0, 0.0, 1.0));
            this.bodyTrackingIncrements = 0;
        } else {
            this.setVelocity(Vec3d.ZERO);
        }
        this.lastLimbDistance = this.limbDistance;
        double d = this.getX() - this.prevX;
        double e = this.getZ() - this.prevZ;
        float g = MathHelper.sqrt(d * d + e * e) * 4.0f;
        if (g > 1.0f) {
            g = 1.0f;
        }
        this.limbDistance += (g - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }

    public boolean method_6577() {
        if (this.field_6814) {
            return false;
        }
        this.field_6814 = true;
        this.field_6812 = 0;
        this.field_6813 = this.getRandom().nextInt(841) + 140;
        this.getDataTracker().set(field_6815, this.field_6813);
        return true;
    }

    @Override
    public PigEntity createChild(PassiveEntity passiveEntity) {
        return EntityType.PIG.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return BREEDING_INGREDIENT.test(stack);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity mate) {
        return this.createChild(mate);
    }
}

