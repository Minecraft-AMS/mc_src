/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.StepAndDestroyBlockGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ZombieEntity
extends HostileEntity {
    protected static final EntityAttribute SPAWN_REINFORCEMENTS = new ClampedEntityAttribute(null, "zombie.spawnReinforcements", 0.0, 0.0, 1.0).setName("Spawn Reinforcements Chance");
    private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final EntityAttributeModifier BABY_SPEED_BONUS = new EntityAttributeModifier(BABY_SPEED_ID, "Baby speed boost", 0.5, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> field_7427 = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CONVERTING_IN_WATER = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final Predicate<Difficulty> field_19015 = difficulty -> difficulty == Difficulty.HARD;
    private final BreakDoorGoal breakDoorsGoal = new BreakDoorGoal(this, field_19015);
    private boolean canBreakDoors;
    private int inWaterTime;
    private int ticksUntilWaterConversion;

    public ZombieEntity(EntityType<? extends ZombieEntity> type, World world) {
        super((EntityType<? extends HostileEntity>)type, world);
    }

    public ZombieEntity(World world) {
        this((EntityType<? extends ZombieEntity>)EntityType.ZOMBIE, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(4, new DestroyEggGoal((MobEntityWithAi)this, 1.0, 3));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(ZombiePigmanEntity.class));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new FollowTargetGoal<TurtleEntity>(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(35.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23f);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
        this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(2.0);
        this.getAttributes().register(SPAWN_REINFORCEMENTS).setBaseValue(this.random.nextDouble() * (double)0.1f);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(BABY, false);
        this.getDataTracker().startTracking(field_7427, 0);
        this.getDataTracker().startTracking(CONVERTING_IN_WATER, false);
    }

    public boolean isConvertingInWater() {
        return this.getDataTracker().get(CONVERTING_IN_WATER);
    }

    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        if (this.shouldBreakDoors()) {
            if (this.canBreakDoors != canBreakDoors) {
                this.canBreakDoors = canBreakDoors;
                ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(canBreakDoors);
                if (canBreakDoors) {
                    this.goalSelector.add(1, this.breakDoorsGoal);
                } else {
                    this.goalSelector.remove(this.breakDoorsGoal);
                }
            }
        } else if (this.canBreakDoors) {
            this.goalSelector.remove(this.breakDoorsGoal);
            this.canBreakDoors = false;
        }
    }

    protected boolean shouldBreakDoors() {
        return true;
    }

    @Override
    public boolean isBaby() {
        return this.getDataTracker().get(BABY);
    }

    @Override
    protected int getCurrentExperience(PlayerEntity player) {
        if (this.isBaby()) {
            this.experiencePoints = (int)((float)this.experiencePoints * 2.5f);
        }
        return super.getCurrentExperience(player);
    }

    public void setBaby(boolean baby) {
        this.getDataTracker().set(BABY, baby);
        if (this.world != null && !this.world.isClient) {
            EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
            entityAttributeInstance.removeModifier(BABY_SPEED_BONUS);
            if (baby) {
                entityAttributeInstance.addModifier(BABY_SPEED_BONUS);
            }
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (BABY.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    protected boolean canConvertInWater() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.world.isClient && this.isAlive()) {
            if (this.isConvertingInWater()) {
                --this.ticksUntilWaterConversion;
                if (this.ticksUntilWaterConversion < 0) {
                    this.convertInWater();
                }
            } else if (this.canConvertInWater()) {
                if (this.isInFluid(FluidTags.WATER)) {
                    ++this.inWaterTime;
                    if (this.inWaterTime >= 600) {
                        this.setTicksUntilWaterConversion(300);
                    }
                } else {
                    this.inWaterTime = -1;
                }
            }
        }
        super.tick();
    }

    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            boolean bl;
            boolean bl2 = bl = this.burnsInDaylight() && this.isInDaylight();
            if (bl) {
                ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }
                    bl = false;
                }
                if (bl) {
                    this.setOnFireFor(8);
                }
            }
        }
        super.tickMovement();
    }

    private void setTicksUntilWaterConversion(int ticksUntilWaterConversion) {
        this.ticksUntilWaterConversion = ticksUntilWaterConversion;
        this.getDataTracker().set(CONVERTING_IN_WATER, true);
    }

    protected void convertInWater() {
        this.convertTo(EntityType.DROWNED);
        this.world.playLevelEvent(null, 1040, new BlockPos(this), 0);
    }

    protected void convertTo(EntityType<? extends ZombieEntity> entityType) {
        if (this.removed) {
            return;
        }
        ZombieEntity zombieEntity = entityType.create(this.world);
        zombieEntity.copyPositionAndRotation(this);
        zombieEntity.setCanPickUpLoot(this.canPickUpLoot());
        zombieEntity.setCanBreakDoors(zombieEntity.shouldBreakDoors() && this.canBreakDoors());
        zombieEntity.method_7205(zombieEntity.world.getLocalDifficulty(new BlockPos(zombieEntity)).getClampedLocalDifficulty());
        zombieEntity.setBaby(this.isBaby());
        zombieEntity.setAiDisabled(this.isAiDisabled());
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            if (itemStack.isEmpty()) continue;
            zombieEntity.equipStack(equipmentSlot, itemStack.copy());
            zombieEntity.setEquipmentDropChance(equipmentSlot, this.getDropChance(equipmentSlot));
            itemStack.setCount(0);
        }
        if (this.hasCustomName()) {
            zombieEntity.setCustomName(this.getCustomName());
            zombieEntity.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.world.spawnEntity(zombieEntity);
        this.remove();
    }

    protected boolean burnsInDaylight() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (super.damage(source, amount)) {
            LivingEntity livingEntity = this.getTarget();
            if (livingEntity == null && source.getAttacker() instanceof LivingEntity) {
                livingEntity = (LivingEntity)source.getAttacker();
            }
            if (livingEntity != null && this.world.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttributeInstance(SPAWN_REINFORCEMENTS).getValue() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                int i = MathHelper.floor(this.x);
                int j = MathHelper.floor(this.y);
                int k = MathHelper.floor(this.z);
                ZombieEntity zombieEntity = new ZombieEntity(this.world);
                for (int l = 0; l < 50; ++l) {
                    int o;
                    int n;
                    int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    BlockPos blockPos = new BlockPos(m, (n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1)) - 1, o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1));
                    if (!this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, zombieEntity) || this.world.getLightLevel(new BlockPos(m, n, o)) >= 10) continue;
                    zombieEntity.updatePosition(m, n, o);
                    if (this.world.isPlayerInRange(m, n, o, 7.0) || !this.world.intersectsEntities(zombieEntity) || !this.world.doesNotCollide(zombieEntity) || this.world.intersectsFluid(zombieEntity.getBoundingBox())) continue;
                    this.world.spawnEntity(zombieEntity);
                    zombieEntity.setTarget(livingEntity);
                    zombieEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(zombieEntity)), SpawnType.REINFORCEMENT, null, null);
                    this.getAttributeInstance(SPAWN_REINFORCEMENTS).addModifier(new EntityAttributeModifier("Zombie reinforcement caller charge", -0.05f, EntityAttributeModifier.Operation.ADDITION));
                    zombieEntity.getAttributeInstance(SPAWN_REINFORCEMENTS).addModifier(new EntityAttributeModifier("Zombie reinforcement callee charge", -0.05f, EntityAttributeModifier.Operation.ADDITION));
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if (bl) {
            float f = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
            if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3f) {
                target.setOnFireFor(2 * (int)f);
            }
        }
        return bl;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        super.initEquipment(difficulty);
        float f = this.random.nextFloat();
        float f2 = this.world.getDifficulty() == Difficulty.HARD ? 0.05f : 0.01f;
        if (f < f2) {
            int i = this.random.nextInt(3);
            if (i == 0) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (this.isBaby()) {
            tag.putBoolean("IsBaby", true);
        }
        tag.putBoolean("CanBreakDoors", this.canBreakDoors());
        tag.putInt("InWaterTime", this.isTouchingWater() ? this.inWaterTime : -1);
        tag.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.getBoolean("IsBaby")) {
            this.setBaby(true);
        }
        this.setCanBreakDoors(tag.getBoolean("CanBreakDoors"));
        this.inWaterTime = tag.getInt("InWaterTime");
        if (tag.contains("DrownedConversionTime", 99) && tag.getInt("DrownedConversionTime") > -1) {
            this.setTicksUntilWaterConversion(tag.getInt("DrownedConversionTime"));
        }
    }

    @Override
    public void onKilledOther(LivingEntity other) {
        super.onKilledOther(other);
        if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) && other instanceof VillagerEntity) {
            if (this.world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return;
            }
            VillagerEntity villagerEntity = (VillagerEntity)other;
            ZombieVillagerEntity zombieVillagerEntity = EntityType.ZOMBIE_VILLAGER.create(this.world);
            zombieVillagerEntity.copyPositionAndRotation(villagerEntity);
            villagerEntity.remove();
            zombieVillagerEntity.initialize(this.world, this.world.getLocalDifficulty(new BlockPos(zombieVillagerEntity)), SpawnType.CONVERSION, new class_1644(false), null);
            zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
            zombieVillagerEntity.method_21649((Tag)villagerEntity.method_21651().serialize(NbtOps.INSTANCE).getValue());
            zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toTag());
            zombieVillagerEntity.setXp(villagerEntity.getExperience());
            zombieVillagerEntity.setBaby(villagerEntity.isBaby());
            zombieVillagerEntity.setAiDisabled(villagerEntity.isAiDisabled());
            if (villagerEntity.hasCustomName()) {
                zombieVillagerEntity.setCustomName(villagerEntity.getCustomName());
                zombieVillagerEntity.setCustomNameVisible(villagerEntity.isCustomNameVisible());
            }
            this.world.spawnEntity(zombieVillagerEntity);
            this.world.playLevelEvent(null, 1026, new BlockPos(this), 0);
        }
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return this.isBaby() ? 0.93f : 1.74f;
    }

    @Override
    protected boolean canPickupItem(ItemStack stack) {
        if (stack.getItem() == Items.EGG && this.isBaby() && this.hasVehicle()) {
            return false;
        }
        return super.canPickupItem(stack);
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        entityData = super.initialize(world, difficulty, spawnType, entityData, entityTag);
        float f = difficulty.getClampedLocalDifficulty();
        this.setCanPickUpLoot(this.random.nextFloat() < 0.55f * f);
        if (entityData == null) {
            entityData = new class_1644(world.getRandom().nextFloat() < 0.05f);
        }
        if (entityData instanceof class_1644) {
            class_1644 lv = (class_1644)entityData;
            if (lv.field_7439) {
                this.setBaby(true);
                if ((double)world.getRandom().nextFloat() < 0.05) {
                    List<Entity> list = world.getEntities(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
                    if (!list.isEmpty()) {
                        ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
                        chickenEntity.setHasJockey(true);
                        this.startRiding(chickenEntity);
                    }
                } else if ((double)world.getRandom().nextFloat() < 0.05) {
                    ChickenEntity chickenEntity2 = EntityType.CHICKEN.create(this.world);
                    chickenEntity2.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0f);
                    chickenEntity2.initialize(world, difficulty, SpawnType.JOCKEY, null, null);
                    chickenEntity2.setHasJockey(true);
                    world.spawnEntity(chickenEntity2);
                    this.startRiding(chickenEntity2);
                }
            }
            this.setCanBreakDoors(this.shouldBreakDoors() && this.random.nextFloat() < f * 0.1f);
            this.initEquipment(difficulty);
            this.updateEnchantments(difficulty);
        }
        if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && this.random.nextFloat() < 0.25f) {
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1f ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0f;
            }
        }
        this.method_7205(f);
        return entityData;
    }

    protected void method_7205(float f) {
        this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).addModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextDouble() * (double)0.05f, EntityAttributeModifier.Operation.ADDITION));
        double d = this.random.nextDouble() * 1.5 * (double)f;
        if (d > 1.0) {
            this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).addModifier(new EntityAttributeModifier("Random zombie-spawn bonus", d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        if (this.random.nextFloat() < f * 0.05f) {
            this.getAttributeInstance(SPAWN_REINFORCEMENTS).addModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.ADDITION));
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).addModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            this.setCanBreakDoors(this.shouldBreakDoors());
        }
    }

    @Override
    public double getHeightOffset() {
        return this.isBaby() ? 0.0 : -0.45;
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        CreeperEntity creeperEntity;
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        Entity entity = source.getAttacker();
        if (entity instanceof CreeperEntity && (creeperEntity = (CreeperEntity)entity).shouldDropHead()) {
            creeperEntity.onHeadDropped();
            ItemStack itemStack = this.getSkull();
            if (!itemStack.isEmpty()) {
                this.dropStack(itemStack);
            }
        }
    }

    protected ItemStack getSkull() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    class DestroyEggGoal
    extends StepAndDestroyBlockGoal {
        DestroyEggGoal(MobEntityWithAi mob, double speed, int i) {
            super(Blocks.TURTLE_EGG, mob, speed, i);
        }

        @Override
        public void tickStepping(IWorld world, BlockPos pos) {
            world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5f, 0.9f + ZombieEntity.this.random.nextFloat() * 0.2f);
        }

        @Override
        public void onDestroyBlock(World world, BlockPos pos) {
            world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7f, 0.9f + world.random.nextFloat() * 0.2f);
        }

        @Override
        public double getDesiredSquaredDistanceToTarget() {
            return 1.14;
        }
    }

    public class class_1644
    implements EntityData {
        public final boolean field_7439;

        private class_1644(boolean bl) {
            this.field_7439 = bl;
        }
    }
}

