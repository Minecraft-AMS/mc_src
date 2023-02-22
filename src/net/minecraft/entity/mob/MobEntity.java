/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.mob.MobVisibilityCache;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class MobEntity
extends LivingEntity {
    private static final TrackedData<Byte> MOB_FLAGS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
    public int ambientSoundChance;
    protected int experiencePoints;
    protected LookControl lookControl;
    protected MoveControl moveControl;
    protected JumpControl jumpControl;
    private final BodyControl bodyControl;
    protected EntityNavigation navigation;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    private LivingEntity target;
    private final MobVisibilityCache visibilityCache;
    private final DefaultedList<ItemStack> handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
    protected final float[] handDropChances = new float[2];
    private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    protected final float[] armorDropChances = new float[4];
    private boolean pickUpLoot;
    private boolean persistent;
    private final Map<PathNodeType, Float> pathfindingPenalties = Maps.newEnumMap(PathNodeType.class);
    private Identifier lootTable;
    private long lootTableSeed;
    @Nullable
    private Entity holdingEntity;
    private int holdingEntityId;
    @Nullable
    private CompoundTag leashTag;
    private BlockPos positionTarget = BlockPos.ORIGIN;
    private float positionTargetRange = -1.0f;

    protected MobEntity(EntityType<? extends MobEntity> type, World world) {
        super((EntityType<? extends LivingEntity>)type, world);
        this.goalSelector = new GoalSelector(world == null || world.getProfiler() == null ? null : world.getProfiler());
        this.targetSelector = new GoalSelector(world == null || world.getProfiler() == null ? null : world.getProfiler());
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.bodyControl = this.createBodyControl();
        this.navigation = this.createNavigation(world);
        this.visibilityCache = new MobVisibilityCache(this);
        Arrays.fill(this.armorDropChances, 0.085f);
        Arrays.fill(this.handDropChances, 0.085f);
        if (world != null && !world.isClient) {
            this.initGoals();
        }
    }

    protected void initGoals() {
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributes().register(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
        this.getAttributes().register(EntityAttributes.ATTACK_KNOCKBACK);
    }

    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world);
    }

    public float getPathfindingPenalty(PathNodeType nodeType) {
        Float float_ = this.pathfindingPenalties.get((Object)nodeType);
        return float_ == null ? nodeType.getDefaultPenalty() : float_.floatValue();
    }

    public void setPathfindingPenalty(PathNodeType nodeType, float penalty) {
        this.pathfindingPenalties.put(nodeType, Float.valueOf(penalty));
    }

    protected BodyControl createBodyControl() {
        return new BodyControl(this);
    }

    public LookControl getLookControl() {
        return this.lookControl;
    }

    public MoveControl getMoveControl() {
        if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)this.getVehicle();
            return mobEntity.getMoveControl();
        }
        return this.moveControl;
    }

    public JumpControl getJumpControl() {
        return this.jumpControl;
    }

    public EntityNavigation getNavigation() {
        if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)this.getVehicle();
            return mobEntity.getNavigation();
        }
        return this.navigation;
    }

    public MobVisibilityCache getVisibilityCache() {
        return this.visibilityCache;
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        return type != EntityType.GHAST;
    }

    public void onEatingGrass() {
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MOB_FLAGS, (byte)0);
    }

    public int getMinAmbientSoundDelay() {
        return 80;
    }

    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.world.getProfiler().push("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
            this.resetSoundDelay();
            this.playAmbientSound();
        }
        this.world.getProfiler().pop();
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.resetSoundDelay();
        super.playHurtSound(source);
    }

    private void resetSoundDelay() {
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    @Override
    protected int getCurrentExperience(PlayerEntity player) {
        if (this.experiencePoints > 0) {
            int j;
            int i = this.experiencePoints;
            for (j = 0; j < this.armorItems.size(); ++j) {
                if (this.armorItems.get(j).isEmpty() || !(this.armorDropChances[j] <= 1.0f)) continue;
                i += 1 + this.random.nextInt(3);
            }
            for (j = 0; j < this.handItems.size(); ++j) {
                if (this.handItems.get(j).isEmpty() || !(this.handDropChances[j] <= 1.0f)) continue;
                i += 1 + this.random.nextInt(3);
            }
            return i;
        }
        return this.experiencePoints;
    }

    public void playSpawnEffects() {
        if (this.world.isClient) {
            for (int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                double g = 10.0;
                this.world.addParticle(ParticleTypes.POOF, this.offsetX(1.0) - d * 10.0, this.getRandomBodyY() - e * 10.0, this.getParticleZ(1.0) - f * 10.0, d, e, f);
            }
        } else {
            this.world.sendEntityStatus(this, (byte)20);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 20) {
            this.playSpawnEffects();
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.updateLeash();
            if (this.age % 5 == 0) {
                this.method_20417();
            }
        }
    }

    protected void method_20417() {
        boolean bl = !(this.getPrimaryPassenger() instanceof MobEntity);
        boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
        this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
        this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
        this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
    }

    @Override
    protected float turnHead(float bodyRotation, float headRotation) {
        this.bodyControl.tick();
        return headRotation;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        tag.putBoolean("PersistenceRequired", this.persistent);
        ListTag listTag = new ListTag();
        for (ItemStack itemStack : this.armorItems) {
            CompoundTag compoundTag = new CompoundTag();
            if (!itemStack.isEmpty()) {
                itemStack.toTag(compoundTag);
            }
            listTag.add(compoundTag);
        }
        tag.put("ArmorItems", listTag);
        ListTag listTag2 = new ListTag();
        for (ItemStack itemStack : this.handItems) {
            CompoundTag compoundTag2 = new CompoundTag();
            if (!itemStack.isEmpty()) {
                itemStack.toTag(compoundTag2);
            }
            listTag2.add(compoundTag2);
        }
        tag.put("HandItems", listTag2);
        ListTag listTag3 = new ListTag();
        for (float f : this.armorDropChances) {
            listTag3.add(FloatTag.of(f));
        }
        tag.put("ArmorDropChances", listTag3);
        ListTag listTag4 = new ListTag();
        for (float g : this.handDropChances) {
            listTag4.add(FloatTag.of(g));
        }
        tag.put("HandDropChances", listTag4);
        if (this.holdingEntity != null) {
            Object compoundTag2 = new CompoundTag();
            if (this.holdingEntity instanceof LivingEntity) {
                UUID uUID = this.holdingEntity.getUuid();
                ((CompoundTag)compoundTag2).putUuid("UUID", uUID);
            } else if (this.holdingEntity instanceof AbstractDecorationEntity) {
                BlockPos blockPos = ((AbstractDecorationEntity)this.holdingEntity).getDecorationBlockPos();
                ((CompoundTag)compoundTag2).putInt("X", blockPos.getX());
                ((CompoundTag)compoundTag2).putInt("Y", blockPos.getY());
                ((CompoundTag)compoundTag2).putInt("Z", blockPos.getZ());
            }
            tag.put("Leash", (Tag)compoundTag2);
        } else if (this.leashTag != null) {
            tag.put("Leash", this.leashTag.copy());
        }
        tag.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            tag.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                tag.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }
        if (this.isAiDisabled()) {
            tag.putBoolean("NoAI", this.isAiDisabled());
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        int i;
        ListTag listTag;
        super.readCustomDataFromTag(tag);
        if (tag.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(tag.getBoolean("CanPickUpLoot"));
        }
        this.persistent = tag.getBoolean("PersistenceRequired");
        if (tag.contains("ArmorItems", 9)) {
            listTag = tag.getList("ArmorItems", 10);
            for (i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.fromTag(listTag.getCompound(i)));
            }
        }
        if (tag.contains("HandItems", 9)) {
            listTag = tag.getList("HandItems", 10);
            for (i = 0; i < this.handItems.size(); ++i) {
                this.handItems.set(i, ItemStack.fromTag(listTag.getCompound(i)));
            }
        }
        if (tag.contains("ArmorDropChances", 9)) {
            listTag = tag.getList("ArmorDropChances", 5);
            for (i = 0; i < listTag.size(); ++i) {
                this.armorDropChances[i] = listTag.getFloat(i);
            }
        }
        if (tag.contains("HandDropChances", 9)) {
            listTag = tag.getList("HandDropChances", 5);
            for (i = 0; i < listTag.size(); ++i) {
                this.handDropChances[i] = listTag.getFloat(i);
            }
        }
        if (tag.contains("Leash", 10)) {
            this.leashTag = tag.getCompound("Leash");
        }
        this.setLeftHanded(tag.getBoolean("LeftHanded"));
        if (tag.contains("DeathLootTable", 8)) {
            this.lootTable = new Identifier(tag.getString("DeathLootTable"));
            this.lootTableSeed = tag.getLong("DeathLootTableSeed");
        }
        this.setAiDisabled(tag.getBoolean("NoAI"));
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        this.lootTable = null;
    }

    @Override
    protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
        return super.getLootContextBuilder(causedByPlayer, source).setRandom(this.lootTableSeed, this.random);
    }

    @Override
    public final Identifier getLootTable() {
        return this.lootTable == null ? this.getLootTableId() : this.lootTable;
    }

    protected Identifier getLootTableId() {
        return super.getLootTable();
    }

    public void setForwardSpeed(float forwardSpeed) {
        this.forwardSpeed = forwardSpeed;
    }

    public void setUpwardSpeed(float upwardSpeed) {
        this.upwardSpeed = upwardSpeed;
    }

    public void setSidewaysSpeed(float sidewaysMovement) {
        this.sidewaysSpeed = sidewaysMovement;
    }

    @Override
    public void setMovementSpeed(float movementSpeed) {
        super.setMovementSpeed(movementSpeed);
        this.setForwardSpeed(movementSpeed);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.world.getProfiler().push("looting");
        if (!this.world.isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            List<ItemEntity> list = this.world.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0));
            for (ItemEntity itemEntity : list) {
                if (itemEntity.removed || itemEntity.getStack().isEmpty() || itemEntity.cannotPickup()) continue;
                this.loot(itemEntity);
            }
        }
        this.world.getProfiler().pop();
    }

    protected void loot(ItemEntity item) {
        EquipmentSlot equipmentSlot;
        ItemStack itemStack2;
        ItemStack itemStack = item.getStack();
        boolean bl = this.isBetterItemFor(itemStack, itemStack2 = this.getEquippedStack(equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack)), equipmentSlot);
        if (bl && this.canPickupItem(itemStack)) {
            double d = this.getDropChance(equipmentSlot);
            if (!itemStack2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1f, 0.0f) < d) {
                this.dropStack(itemStack2);
            }
            this.equipStack(equipmentSlot, itemStack);
            switch (equipmentSlot.getType()) {
                case HAND: {
                    this.handDropChances[equipmentSlot.getEntitySlotId()] = 2.0f;
                    break;
                }
                case ARMOR: {
                    this.armorDropChances[equipmentSlot.getEntitySlotId()] = 2.0f;
                }
            }
            this.persistent = true;
            this.sendPickup(item, itemStack.getCount());
            item.remove();
        }
    }

    protected boolean isBetterItemFor(ItemStack current, ItemStack previous, EquipmentSlot slot) {
        boolean bl = true;
        if (!previous.isEmpty()) {
            if (slot.getType() == EquipmentSlot.Type.HAND) {
                if (current.getItem() instanceof SwordItem && !(previous.getItem() instanceof SwordItem)) {
                    bl = true;
                } else if (current.getItem() instanceof SwordItem && previous.getItem() instanceof SwordItem) {
                    SwordItem swordItem = (SwordItem)current.getItem();
                    SwordItem swordItem2 = (SwordItem)previous.getItem();
                    bl = swordItem.getAttackDamage() == swordItem2.getAttackDamage() ? current.getDamage() < previous.getDamage() || current.hasTag() && !previous.hasTag() : swordItem.getAttackDamage() > swordItem2.getAttackDamage();
                } else {
                    bl = current.getItem() instanceof BowItem && previous.getItem() instanceof BowItem ? current.hasTag() && !previous.hasTag() : false;
                }
            } else if (current.getItem() instanceof ArmorItem && !(previous.getItem() instanceof ArmorItem)) {
                bl = true;
            } else if (current.getItem() instanceof ArmorItem && previous.getItem() instanceof ArmorItem && !EnchantmentHelper.hasBindingCurse(previous)) {
                ArmorItem armorItem = (ArmorItem)current.getItem();
                ArmorItem armorItem2 = (ArmorItem)previous.getItem();
                bl = armorItem.getProtection() == armorItem2.getProtection() ? current.getDamage() < previous.getDamage() || current.hasTag() && !previous.hasTag() : armorItem.getProtection() > armorItem2.getProtection();
            } else {
                bl = false;
            }
        }
        return bl;
    }

    protected boolean canPickupItem(ItemStack stack) {
        return true;
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return true;
    }

    public boolean cannotDespawn() {
        return false;
    }

    protected boolean method_23734() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.method_23734()) {
            this.remove();
            return;
        }
        if (this.isPersistent() || this.cannotDespawn()) {
            this.despawnCounter = 0;
            return;
        }
        PlayerEntity entity = this.world.getClosestPlayer(this, -1.0);
        if (entity != null) {
            double d = entity.squaredDistanceTo(this);
            if (d > 16384.0 && this.canImmediatelyDespawn(d)) {
                this.remove();
            }
            if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d > 1024.0 && this.canImmediatelyDespawn(d)) {
                this.remove();
            } else if (d < 1024.0) {
                this.despawnCounter = 0;
            }
        }
    }

    @Override
    protected final void tickNewAi() {
        ++this.despawnCounter;
        this.world.getProfiler().push("sensing");
        this.visibilityCache.clear();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("targetSelector");
        this.targetSelector.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("goalSelector");
        this.goalSelector.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("navigation");
        this.navigation.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("mob tick");
        this.mobTick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("controls");
        this.world.getProfiler().push("move");
        this.moveControl.tick();
        this.world.getProfiler().swap("look");
        this.lookControl.tick();
        this.world.getProfiler().swap("jump");
        this.jumpControl.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().pop();
        this.sendAiDebugData();
    }

    protected void sendAiDebugData() {
        DebugInfoSender.sendGoalSelector(this.world, this, this.goalSelector);
    }

    protected void mobTick() {
    }

    public int getLookPitchSpeed() {
        return 40;
    }

    public int getBodyYawSpeed() {
        return 75;
    }

    public int getLookYawSpeed() {
        return 10;
    }

    public void lookAtEntity(Entity targetEntity, float maxYawChange, float maxPitchChange) {
        double f;
        double d = targetEntity.getX() - this.getX();
        double e = targetEntity.getZ() - this.getZ();
        if (targetEntity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)targetEntity;
            f = livingEntity.getEyeY() - this.getEyeY();
        } else {
            f = (targetEntity.getBoundingBox().y1 + targetEntity.getBoundingBox().y2) / 2.0 - this.getEyeY();
        }
        double g = MathHelper.sqrt(d * d + e * e);
        float h = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
        float i = (float)(-(MathHelper.atan2(f, g) * 57.2957763671875));
        this.pitch = this.changeAngle(this.pitch, i, maxPitchChange);
        this.yaw = this.changeAngle(this.yaw, h, maxYawChange);
    }

    private float changeAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
        float f = MathHelper.wrapDegrees(newAngle - oldAngle);
        if (f > maxChangeInAngle) {
            f = maxChangeInAngle;
        }
        if (f < -maxChangeInAngle) {
            f = -maxChangeInAngle;
        }
        return oldAngle + f;
    }

    public static boolean canMobSpawn(EntityType<? extends MobEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
        BlockPos blockPos = pos.down();
        return spawnType == SpawnType.SPAWNER || world.getBlockState(blockPos).allowsSpawning(world, blockPos, type);
    }

    public boolean canSpawn(IWorld world, SpawnType spawnType) {
        return true;
    }

    public boolean canSpawn(WorldView world) {
        return !world.containsFluid(this.getBoundingBox()) && world.intersectsEntities(this);
    }

    public int getLimitPerChunk() {
        return 4;
    }

    public boolean spawnsTooManyForEachTry(int count) {
        return false;
    }

    @Override
    public int getSafeFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        int i = (int)(this.getHealth() - this.getMaximumHealth() * 0.33f);
        if ((i -= (3 - this.world.getDifficulty().getId()) * 4) < 0) {
            i = 0;
        }
        return i + 3;
    }

    @Override
    public Iterable<ItemStack> getItemsHand() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        switch (slot.getType()) {
            case HAND: {
                return this.handItems.get(slot.getEntitySlotId());
            }
            case ARMOR: {
                return this.armorItems.get(slot.getEntitySlotId());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        switch (slot.getType()) {
            case HAND: {
                this.handItems.set(slot.getEntitySlotId(), stack);
                break;
            }
            case ARMOR: {
                this.armorItems.set(slot.getEntitySlotId(), stack);
            }
        }
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            boolean bl;
            ItemStack itemStack = this.getEquippedStack(equipmentSlot);
            float f = this.getDropChance(equipmentSlot);
            boolean bl2 = bl = f > 1.0f;
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack) || !allowDrops && !bl || !(Math.max(this.random.nextFloat() - (float)lootingMultiplier * 0.01f, 0.0f) < f)) continue;
            if (!bl && itemStack.isDamageable()) {
                itemStack.setDamage(itemStack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
            }
            this.dropStack(itemStack);
        }
    }

    protected float getDropChance(EquipmentSlot equipmentSlot) {
        float f;
        switch (equipmentSlot.getType()) {
            case HAND: {
                f = this.handDropChances[equipmentSlot.getEntitySlotId()];
                break;
            }
            case ARMOR: {
                f = this.armorDropChances[equipmentSlot.getEntitySlotId()];
                break;
            }
            default: {
                f = 0.0f;
            }
        }
        return f;
    }

    protected void initEquipment(LocalDifficulty difficulty) {
        if (this.random.nextFloat() < 0.15f * difficulty.getClampedLocalDifficulty()) {
            float f;
            int i = this.random.nextInt(2);
            float f2 = f = this.world.getDifficulty() == Difficulty.HARD ? 0.1f : 0.25f;
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            boolean bl = true;
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                Item item;
                if (equipmentSlot.getType() != EquipmentSlot.Type.ARMOR) continue;
                ItemStack itemStack = this.getEquippedStack(equipmentSlot);
                if (!bl && this.random.nextFloat() < f) break;
                bl = false;
                if (!itemStack.isEmpty() || (item = MobEntity.getEquipmentForSlot(equipmentSlot, i)) == null) continue;
                this.equipStack(equipmentSlot, new ItemStack(item));
            }
        }
    }

    public static EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Blocks.CARVED_PUMPKIN.asItem() || item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            return EquipmentSlot.HEAD;
        }
        if (item instanceof ArmorItem) {
            return ((ArmorItem)item).getSlotType();
        }
        if (item == Items.ELYTRA) {
            return EquipmentSlot.CHEST;
        }
        if (item == Items.SHIELD) {
            return EquipmentSlot.OFFHAND;
        }
        return EquipmentSlot.MAINHAND;
    }

    @Nullable
    public static Item getEquipmentForSlot(EquipmentSlot equipmentSlot, int equipmentLevel) {
        switch (equipmentSlot) {
            case HEAD: {
                if (equipmentLevel == 0) {
                    return Items.LEATHER_HELMET;
                }
                if (equipmentLevel == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if (equipmentLevel == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if (equipmentLevel == 3) {
                    return Items.IRON_HELMET;
                }
                if (equipmentLevel == 4) {
                    return Items.DIAMOND_HELMET;
                }
            }
            case CHEST: {
                if (equipmentLevel == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if (equipmentLevel == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if (equipmentLevel == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if (equipmentLevel == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if (equipmentLevel == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            }
            case LEGS: {
                if (equipmentLevel == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if (equipmentLevel == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if (equipmentLevel == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if (equipmentLevel == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if (equipmentLevel == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            }
            case FEET: {
                if (equipmentLevel == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if (equipmentLevel == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if (equipmentLevel == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if (equipmentLevel == 3) {
                    return Items.IRON_BOOTS;
                }
                if (equipmentLevel != 4) break;
                return Items.DIAMOND_BOOTS;
            }
        }
        return null;
    }

    protected void updateEnchantments(LocalDifficulty difficulty) {
        float f = difficulty.getClampedLocalDifficulty();
        if (!this.getMainHandStack().isEmpty() && this.random.nextFloat() < 0.25f * f) {
            this.equipStack(EquipmentSlot.MAINHAND, EnchantmentHelper.enchant(this.random, this.getMainHandStack(), (int)(5.0f + f * (float)this.random.nextInt(18)), false));
        }
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack;
            if (equipmentSlot.getType() != EquipmentSlot.Type.ARMOR || (itemStack = this.getEquippedStack(equipmentSlot)).isEmpty() || !(this.random.nextFloat() < 0.5f * f)) continue;
            this.equipStack(equipmentSlot, EnchantmentHelper.enchant(this.random, itemStack, (int)(5.0f + f * (float)this.random.nextInt(18)), false));
        }
    }

    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).addModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        if (this.random.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        return entityData;
    }

    public boolean canBeControlledByRider() {
        return false;
    }

    public void setPersistent() {
        this.persistent = true;
    }

    public void setEquipmentDropChance(EquipmentSlot slot, float f) {
        switch (slot.getType()) {
            case HAND: {
                this.handDropChances[slot.getEntitySlotId()] = f;
                break;
            }
            case ARMOR: {
                this.armorDropChances[slot.getEntitySlotId()] = f;
            }
        }
    }

    public boolean canPickUpLoot() {
        return this.pickUpLoot;
    }

    public void setCanPickUpLoot(boolean bl) {
        this.pickUpLoot = bl;
    }

    @Override
    public boolean canPickUp(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        return this.getEquippedStack(equipmentSlot).isEmpty() && this.canPickUpLoot();
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    @Override
    public final boolean interact(PlayerEntity player, Hand hand) {
        if (!this.isAlive()) {
            return false;
        }
        if (this.getHoldingEntity() == player) {
            this.detachLeash(true, !player.abilities.creativeMode);
            return true;
        }
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() == Items.LEAD && this.canBeLeashedBy(player)) {
            this.attachLeash(player, true);
            itemStack.decrement(1);
            return true;
        }
        if (this.interactMob(player, hand)) {
            return true;
        }
        return super.interact(player, hand);
    }

    protected boolean interactMob(PlayerEntity player, Hand hand) {
        return false;
    }

    public boolean isInWalkTargetRange() {
        return this.isInWalkTargetRange(new BlockPos(this));
    }

    public boolean isInWalkTargetRange(BlockPos pos) {
        if (this.positionTargetRange == -1.0f) {
            return true;
        }
        return this.positionTarget.getSquaredDistance(pos) < (double)(this.positionTargetRange * this.positionTargetRange);
    }

    public void setPositionTarget(BlockPos target, int range) {
        this.positionTarget = target;
        this.positionTargetRange = range;
    }

    public BlockPos getPositionTarget() {
        return this.positionTarget;
    }

    public float getPositionTargetRange() {
        return this.positionTargetRange;
    }

    public boolean hasPositionTarget() {
        return this.positionTargetRange != -1.0f;
    }

    protected void updateLeash() {
        if (this.leashTag != null) {
            this.deserializeLeashTag();
        }
        if (this.holdingEntity == null) {
            return;
        }
        if (!this.isAlive() || !this.holdingEntity.isAlive()) {
            this.detachLeash(true, true);
        }
    }

    public void detachLeash(boolean sendPacket, boolean bl) {
        if (this.holdingEntity != null) {
            this.teleporting = false;
            if (!(this.holdingEntity instanceof PlayerEntity)) {
                this.holdingEntity.teleporting = false;
            }
            this.holdingEntity = null;
            if (!this.world.isClient && bl) {
                this.dropItem(Items.LEAD);
            }
            if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, null));
            }
        }
    }

    public boolean canBeLeashedBy(PlayerEntity player) {
        return !this.isLeashed() && !(this instanceof Monster);
    }

    public boolean isLeashed() {
        return this.holdingEntity != null;
    }

    @Nullable
    public Entity getHoldingEntity() {
        if (this.holdingEntity == null && this.holdingEntityId != 0 && this.world.isClient) {
            this.holdingEntity = this.world.getEntityById(this.holdingEntityId);
        }
        return this.holdingEntity;
    }

    public void attachLeash(Entity entity, boolean bl) {
        this.holdingEntity = entity;
        this.teleporting = true;
        if (!(this.holdingEntity instanceof PlayerEntity)) {
            this.holdingEntity.teleporting = true;
        }
        if (!this.world.isClient && bl && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.holdingEntity));
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void setHoldingEntityId(int id) {
        this.holdingEntityId = id;
        this.detachLeash(false, false);
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        boolean bl = super.startRiding(entity, force);
        if (bl && this.isLeashed()) {
            this.detachLeash(true, true);
        }
        return bl;
    }

    private void deserializeLeashTag() {
        if (this.leashTag != null && this.world instanceof ServerWorld) {
            if (this.leashTag.containsUuid("UUID")) {
                UUID uUID = this.leashTag.getUuid("UUID");
                Entity entity = ((ServerWorld)this.world).getEntity(uUID);
                if (entity != null) {
                    this.attachLeash(entity, true);
                }
            } else if (this.leashTag.contains("X", 99) && this.leashTag.contains("Y", 99) && this.leashTag.contains("Z", 99)) {
                BlockPos blockPos = new BlockPos(this.leashTag.getInt("X"), this.leashTag.getInt("Y"), this.leashTag.getInt("Z"));
                this.attachLeash(LeadKnotEntity.getOrCreate(this.world, blockPos), true);
            } else {
                this.detachLeash(false, true);
            }
            this.leashTag = null;
        }
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        EquipmentSlot equipmentSlot;
        if (slot == 98) {
            equipmentSlot = EquipmentSlot.MAINHAND;
        } else if (slot == 99) {
            equipmentSlot = EquipmentSlot.OFFHAND;
        } else if (slot == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.HEAD;
        } else if (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.CHEST;
        } else if (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.LEGS;
        } else if (slot == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            equipmentSlot = EquipmentSlot.FEET;
        } else {
            return false;
        }
        if (item.isEmpty() || MobEntity.canEquipmentSlotContain(equipmentSlot, item) || equipmentSlot == EquipmentSlot.HEAD) {
            this.equipStack(equipmentSlot, item);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLogicalSideForUpdatingMovement() {
        return this.canBeControlledByRider() && super.isLogicalSideForUpdatingMovement();
    }

    public static boolean canEquipmentSlotContain(EquipmentSlot slot, ItemStack item) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(item);
        return equipmentSlot == slot || equipmentSlot == EquipmentSlot.MAINHAND && slot == EquipmentSlot.OFFHAND || equipmentSlot == EquipmentSlot.OFFHAND && slot == EquipmentSlot.MAINHAND;
    }

    @Override
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && !this.isAiDisabled();
    }

    public void setAiDisabled(boolean bl) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, bl ? (byte)(b | 1) : (byte)(b & 0xFFFFFFFE));
    }

    public void setLeftHanded(boolean leftHanded) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, leftHanded ? (byte)(b | 2) : (byte)(b & 0xFFFFFFFD));
    }

    public void setAttacking(boolean attacking) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, attacking ? (byte)(b | 4) : (byte)(b & 0xFFFFFFFB));
    }

    public boolean isAiDisabled() {
        return (this.dataTracker.get(MOB_FLAGS) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return (this.dataTracker.get(MOB_FLAGS) & 2) != 0;
    }

    public boolean isAttacking() {
        return (this.dataTracker.get(MOB_FLAGS) & 4) != 0;
    }

    @Override
    public Arm getMainArm() {
        return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target.getType() == EntityType.PLAYER && ((PlayerEntity)target).abilities.invulnerable) {
            return false;
        }
        return super.canTarget(target);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl;
        int i;
        float f = (float)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue();
        float g = (float)this.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK).getValue();
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
            g += (float)EnchantmentHelper.getKnockback(this);
        }
        if ((i = EnchantmentHelper.getFireAspect(this)) > 0) {
            target.setOnFireFor(i * 4);
        }
        if (bl = target.damage(DamageSource.mob(this), f)) {
            if (g > 0.0f && target instanceof LivingEntity) {
                ((LivingEntity)target).takeKnockback(this, g * 0.5f, MathHelper.sin(this.yaw * ((float)Math.PI / 180)), -MathHelper.cos(this.yaw * ((float)Math.PI / 180)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }
            if (target instanceof PlayerEntity) {
                ItemStack itemStack2;
                PlayerEntity playerEntity = (PlayerEntity)target;
                ItemStack itemStack = this.getMainHandStack();
                ItemStack itemStack3 = itemStack2 = playerEntity.isUsingItem() ? playerEntity.getActiveItem() : ItemStack.EMPTY;
                if (!itemStack.isEmpty() && !itemStack2.isEmpty() && itemStack.getItem() instanceof AxeItem && itemStack2.getItem() == Items.SHIELD) {
                    float h = 0.25f + (float)EnchantmentHelper.getEfficiency(this) * 0.05f;
                    if (this.random.nextFloat() < h) {
                        playerEntity.getItemCooldownManager().set(Items.SHIELD, 100);
                        this.world.sendEntityStatus(playerEntity, (byte)30);
                    }
                }
            }
            this.dealDamage(this, target);
            this.onAttacking(target);
        }
        return bl;
    }

    protected boolean isInDaylight() {
        if (this.world.isDay() && !this.world.isClient) {
            BlockPos blockPos;
            float f = this.getBrightnessAtEyes();
            BlockPos blockPos2 = blockPos = this.getVehicle() instanceof BoatEntity ? new BlockPos(this.getX(), Math.round(this.getY()), this.getZ()).up() : new BlockPos(this.getX(), Math.round(this.getY()), this.getZ());
            if (f > 0.5f && this.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && this.world.isSkyVisible(blockPos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void swimUpward(net.minecraft.tag.Tag<Fluid> fluid) {
        if (this.getNavigation().canSwim()) {
            super.swimUpward(fluid);
        } else {
            this.setVelocity(this.getVelocity().add(0.0, 0.3, 0.0));
        }
    }

    public boolean isHolding(Item item) {
        return this.getMainHandStack().getItem() == item || this.getOffHandStack().getItem() == item;
    }
}

