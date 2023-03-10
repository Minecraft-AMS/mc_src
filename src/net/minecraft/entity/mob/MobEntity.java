/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobVisibilityCache;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class MobEntity
extends LivingEntity {
    private static final TrackedData<Byte> MOB_FLAGS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final int AI_DISABLED_FLAG = 1;
    private static final int LEFT_HANDED_FLAG = 2;
    private static final int ATTACKING_FLAG = 4;
    public static final float BASE_SPAWN_EQUIPMENT_CHANCE = 0.15f;
    public static final float DEFAULT_CAN_PICKUP_LOOT_CHANCE = 0.55f;
    public static final float BASE_ENCHANTED_ARMOR_CHANCE = 0.5f;
    public static final float BASE_ENCHANTED_MAIN_HAND_EQUIPMENT_CHANCE = 0.25f;
    public static final String LEASH_KEY = "Leash";
    private static final int MINIMUM_DROPPED_XP_PER_EQUIPMENT = 1;
    public static final float DEFAULT_DROP_CHANCE = 0.085f;
    public static final int field_35039 = 2;
    public int ambientSoundChance;
    protected int experiencePoints;
    protected LookControl lookControl;
    protected MoveControl moveControl;
    protected JumpControl jumpControl;
    private final BodyControl bodyControl;
    protected EntityNavigation navigation;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    @Nullable
    private LivingEntity target;
    private final MobVisibilityCache visibilityCache;
    private final DefaultedList<ItemStack> handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
    protected final float[] handDropChances = new float[2];
    private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    protected final float[] armorDropChances = new float[4];
    private boolean canPickUpLoot;
    private boolean persistent;
    private final Map<PathNodeType, Float> pathfindingPenalties = Maps.newEnumMap(PathNodeType.class);
    @Nullable
    private Identifier lootTable;
    private long lootTableSeed;
    @Nullable
    private Entity holdingEntity;
    private int holdingEntityId;
    @Nullable
    private NbtCompound leashNbt;
    private BlockPos positionTarget = BlockPos.ORIGIN;
    private float positionTargetRange = -1.0f;

    protected MobEntity(EntityType<? extends MobEntity> entityType, World world) {
        super((EntityType<? extends LivingEntity>)entityType, world);
        this.goalSelector = new GoalSelector(world.getProfilerSupplier());
        this.targetSelector = new GoalSelector(world.getProfilerSupplier());
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

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
    }

    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world);
    }

    protected boolean movesIndependently() {
        return false;
    }

    public float getPathfindingPenalty(PathNodeType nodeType) {
        MobEntity mobEntity = this.getVehicle() instanceof MobEntity && ((MobEntity)this.getVehicle()).movesIndependently() ? (MobEntity)this.getVehicle() : this;
        Float float_ = mobEntity.pathfindingPenalties.get((Object)nodeType);
        return float_ == null ? nodeType.getDefaultPenalty() : float_.floatValue();
    }

    public void setPathfindingPenalty(PathNodeType nodeType, float penalty) {
        this.pathfindingPenalties.put(nodeType, Float.valueOf(penalty));
    }

    public boolean canJumpToNextPathNode(PathNodeType type) {
        return type != PathNodeType.DANGER_FIRE && type != PathNodeType.DANGER_CACTUS && type != PathNodeType.DANGER_OTHER && type != PathNodeType.WALKABLE_DOOR;
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

    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return false;
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
    protected int getXpToDrop(PlayerEntity player) {
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
                this.updateGoalControls();
            }
        }
    }

    protected void updateGoalControls() {
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        nbt.putBoolean("PersistenceRequired", this.persistent);
        NbtList nbtList = new NbtList();
        for (ItemStack itemStack : this.armorItems) {
            NbtCompound nbtCompound = new NbtCompound();
            if (!itemStack.isEmpty()) {
                itemStack.writeNbt(nbtCompound);
            }
            nbtList.add(nbtCompound);
        }
        nbt.put("ArmorItems", nbtList);
        NbtList nbtList2 = new NbtList();
        for (ItemStack itemStack : this.handItems) {
            NbtCompound nbtCompound2 = new NbtCompound();
            if (!itemStack.isEmpty()) {
                itemStack.writeNbt(nbtCompound2);
            }
            nbtList2.add(nbtCompound2);
        }
        nbt.put("HandItems", nbtList2);
        NbtList nbtList3 = new NbtList();
        for (float f : this.armorDropChances) {
            nbtList3.add(NbtFloat.of(f));
        }
        nbt.put("ArmorDropChances", nbtList3);
        NbtList nbtList4 = new NbtList();
        for (float g : this.handDropChances) {
            nbtList4.add(NbtFloat.of(g));
        }
        nbt.put("HandDropChances", nbtList4);
        if (this.holdingEntity != null) {
            Object nbtCompound2 = new NbtCompound();
            if (this.holdingEntity instanceof LivingEntity) {
                UUID uUID = this.holdingEntity.getUuid();
                ((NbtCompound)nbtCompound2).putUuid("UUID", uUID);
            } else if (this.holdingEntity instanceof AbstractDecorationEntity) {
                BlockPos blockPos = ((AbstractDecorationEntity)this.holdingEntity).getDecorationBlockPos();
                ((NbtCompound)nbtCompound2).putInt("X", blockPos.getX());
                ((NbtCompound)nbtCompound2).putInt("Y", blockPos.getY());
                ((NbtCompound)nbtCompound2).putInt("Z", blockPos.getZ());
            }
            nbt.put(LEASH_KEY, (NbtElement)nbtCompound2);
        } else if (this.leashNbt != null) {
            nbt.put(LEASH_KEY, this.leashNbt.copy());
        }
        nbt.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            nbt.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                nbt.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }
        if (this.isAiDisabled()) {
            nbt.putBoolean("NoAI", this.isAiDisabled());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        int i;
        NbtList nbtList;
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(nbt.getBoolean("CanPickUpLoot"));
        }
        this.persistent = nbt.getBoolean("PersistenceRequired");
        if (nbt.contains("ArmorItems", 9)) {
            nbtList = nbt.getList("ArmorItems", 10);
            for (i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.fromNbt(nbtList.getCompound(i)));
            }
        }
        if (nbt.contains("HandItems", 9)) {
            nbtList = nbt.getList("HandItems", 10);
            for (i = 0; i < this.handItems.size(); ++i) {
                this.handItems.set(i, ItemStack.fromNbt(nbtList.getCompound(i)));
            }
        }
        if (nbt.contains("ArmorDropChances", 9)) {
            nbtList = nbt.getList("ArmorDropChances", 5);
            for (i = 0; i < nbtList.size(); ++i) {
                this.armorDropChances[i] = nbtList.getFloat(i);
            }
        }
        if (nbt.contains("HandDropChances", 9)) {
            nbtList = nbt.getList("HandDropChances", 5);
            for (i = 0; i < nbtList.size(); ++i) {
                this.handDropChances[i] = nbtList.getFloat(i);
            }
        }
        if (nbt.contains(LEASH_KEY, 10)) {
            this.leashNbt = nbt.getCompound(LEASH_KEY);
        }
        this.setLeftHanded(nbt.getBoolean("LeftHanded"));
        if (nbt.contains("DeathLootTable", 8)) {
            this.lootTable = new Identifier(nbt.getString("DeathLootTable"));
            this.lootTableSeed = nbt.getLong("DeathLootTableSeed");
        }
        this.setAiDisabled(nbt.getBoolean("NoAI"));
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        this.lootTable = null;
    }

    @Override
    protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
        return super.getLootContextBuilder(causedByPlayer, source).random(this.lootTableSeed, this.random);
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

    public void setSidewaysSpeed(float sidewaysSpeed) {
        this.sidewaysSpeed = sidewaysSpeed;
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
        if (!this.world.isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            List<ItemEntity> list = this.world.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0));
            for (ItemEntity itemEntity : list) {
                if (itemEntity.isRemoved() || itemEntity.getStack().isEmpty() || itemEntity.cannotPickup() || !this.canGather(itemEntity.getStack())) continue;
                this.loot(itemEntity);
            }
        }
        this.world.getProfiler().pop();
    }

    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.tryEquip(itemStack)) {
            this.triggerItemPickedUpByEntityCriteria(item);
            this.sendPickup(item, itemStack.getCount());
            item.discard();
        }
    }

    public boolean tryEquip(ItemStack equipment) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(equipment);
        ItemStack itemStack = this.getEquippedStack(equipmentSlot);
        boolean bl = this.prefersNewEquipment(equipment, itemStack);
        if (bl && this.canPickupItem(equipment)) {
            double d = this.getDropChance(equipmentSlot);
            if (!itemStack.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1f, 0.0f) < d) {
                this.dropStack(itemStack);
            }
            this.equipLootStack(equipmentSlot, equipment);
            this.onEquipStack(equipment);
            return true;
        }
        return false;
    }

    protected void equipLootStack(EquipmentSlot slot, ItemStack stack) {
        this.equipStack(slot, stack);
        this.updateDropChances(slot);
        this.persistent = true;
    }

    public void updateDropChances(EquipmentSlot slot) {
        switch (slot.getType()) {
            case HAND: {
                this.handDropChances[slot.getEntitySlotId()] = 2.0f;
                break;
            }
            case ARMOR: {
                this.armorDropChances[slot.getEntitySlotId()] = 2.0f;
            }
        }
    }

    protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
        if (oldStack.isEmpty()) {
            return true;
        }
        if (newStack.getItem() instanceof SwordItem) {
            if (!(oldStack.getItem() instanceof SwordItem)) {
                return true;
            }
            SwordItem swordItem = (SwordItem)newStack.getItem();
            SwordItem swordItem2 = (SwordItem)oldStack.getItem();
            if (swordItem.getAttackDamage() != swordItem2.getAttackDamage()) {
                return swordItem.getAttackDamage() > swordItem2.getAttackDamage();
            }
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        if (newStack.getItem() instanceof BowItem && oldStack.getItem() instanceof BowItem) {
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        if (newStack.getItem() instanceof CrossbowItem && oldStack.getItem() instanceof CrossbowItem) {
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        if (newStack.getItem() instanceof ArmorItem) {
            if (EnchantmentHelper.hasBindingCurse(oldStack)) {
                return false;
            }
            if (!(oldStack.getItem() instanceof ArmorItem)) {
                return true;
            }
            ArmorItem armorItem = (ArmorItem)newStack.getItem();
            ArmorItem armorItem2 = (ArmorItem)oldStack.getItem();
            if (armorItem.getProtection() != armorItem2.getProtection()) {
                return armorItem.getProtection() > armorItem2.getProtection();
            }
            if (armorItem.getToughness() != armorItem2.getToughness()) {
                return armorItem.getToughness() > armorItem2.getToughness();
            }
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        if (newStack.getItem() instanceof MiningToolItem) {
            if (oldStack.getItem() instanceof BlockItem) {
                return true;
            }
            if (oldStack.getItem() instanceof MiningToolItem) {
                MiningToolItem miningToolItem = (MiningToolItem)newStack.getItem();
                MiningToolItem miningToolItem2 = (MiningToolItem)oldStack.getItem();
                if (miningToolItem.getAttackDamage() != miningToolItem2.getAttackDamage()) {
                    return miningToolItem.getAttackDamage() > miningToolItem2.getAttackDamage();
                }
                return this.prefersNewDamageableItem(newStack, oldStack);
            }
        }
        return false;
    }

    public boolean prefersNewDamageableItem(ItemStack newStack, ItemStack oldStack) {
        if (newStack.getDamage() < oldStack.getDamage() || newStack.hasNbt() && !oldStack.hasNbt()) {
            return true;
        }
        if (newStack.hasNbt() && oldStack.hasNbt()) {
            return newStack.getNbt().getKeys().stream().anyMatch(string -> !string.equals("Damage")) && !oldStack.getNbt().getKeys().stream().anyMatch(string -> !string.equals("Damage"));
        }
        return false;
    }

    public boolean canPickupItem(ItemStack stack) {
        return true;
    }

    public boolean canGather(ItemStack stack) {
        return this.canPickupItem(stack);
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return true;
    }

    public boolean cannotDespawn() {
        return this.hasVehicle();
    }

    protected boolean isDisallowedInPeaceful() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
            this.discard();
            return;
        }
        if (this.isPersistent() || this.cannotDespawn()) {
            this.despawnCounter = 0;
            return;
        }
        PlayerEntity entity = this.world.getClosestPlayer(this, -1.0);
        if (entity != null) {
            int i;
            int j;
            double d = entity.squaredDistanceTo(this);
            if (d > (double)(j = (i = this.getType().getSpawnGroup().getImmediateDespawnRange()) * i) && this.canImmediatelyDespawn(d)) {
                this.discard();
            }
            int k = this.getType().getSpawnGroup().getDespawnStartRange();
            int l = k * k;
            if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d > (double)l && this.canImmediatelyDespawn(d)) {
                this.discard();
            } else if (d < (double)l) {
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
        int i = this.world.getServer().getTicks() + this.getId();
        if (i % 2 == 0 || this.age <= 1) {
            this.world.getProfiler().push("targetSelector");
            this.targetSelector.tick();
            this.world.getProfiler().pop();
            this.world.getProfiler().push("goalSelector");
            this.goalSelector.tick();
            this.world.getProfiler().pop();
        } else {
            this.world.getProfiler().push("targetSelector");
            this.targetSelector.tickGoals(false);
            this.world.getProfiler().pop();
            this.world.getProfiler().push("goalSelector");
            this.goalSelector.tickGoals(false);
            this.world.getProfiler().pop();
        }
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

    public int getMaxLookPitchChange() {
        return 40;
    }

    public int getMaxHeadRotation() {
        return 75;
    }

    public int getMaxLookYawChange() {
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
            f = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0 - this.getEyeY();
        }
        double g = Math.sqrt(d * d + e * e);
        float h = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
        float i = (float)(-(MathHelper.atan2(f, g) * 57.2957763671875));
        this.setPitch(this.changeAngle(this.getPitch(), i, maxPitchChange));
        this.setYaw(this.changeAngle(this.getYaw(), h, maxYawChange));
    }

    private float changeAngle(float from, float to, float max) {
        float f = MathHelper.wrapDegrees(to - from);
        if (f > max) {
            f = max;
        }
        if (f < -max) {
            f = -max;
        }
        return from + f;
    }

    public static boolean canMobSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockPos blockPos = pos.down();
        return spawnReason == SpawnReason.SPAWNER || world.getBlockState(blockPos).allowsSpawning(world, blockPos, type);
    }

    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        return true;
    }

    public boolean canSpawn(WorldView world) {
        return !world.containsFluid(this.getBoundingBox()) && world.doesNotIntersectEntities(this);
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
        int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33f);
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
        this.processEquippedStack(stack);
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
            this.equipStack(equipmentSlot, ItemStack.EMPTY);
        }
    }

    protected float getDropChance(EquipmentSlot slot) {
        return switch (slot.getType()) {
            case EquipmentSlot.Type.HAND -> this.handDropChances[slot.getEntitySlotId()];
            case EquipmentSlot.Type.ARMOR -> this.armorDropChances[slot.getEntitySlotId()];
            default -> 0.0f;
        };
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
        this.enchantMainHandItem(f);
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() != EquipmentSlot.Type.ARMOR) continue;
            this.enchantEquipment(f, equipmentSlot);
        }
    }

    protected void enchantMainHandItem(float power) {
        if (!this.getMainHandStack().isEmpty() && this.random.nextFloat() < 0.25f * power) {
            this.equipStack(EquipmentSlot.MAINHAND, EnchantmentHelper.enchant(this.random, this.getMainHandStack(), (int)(5.0f + power * (float)this.random.nextInt(18)), false));
        }
    }

    protected void enchantEquipment(float power, EquipmentSlot slot) {
        ItemStack itemStack = this.getEquippedStack(slot);
        if (!itemStack.isEmpty() && this.random.nextFloat() < 0.5f * power) {
            this.equipStack(slot, EnchantmentHelper.enchant(this.random, itemStack, (int)(5.0f + power * (float)this.random.nextInt(18)), false));
        }
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, EntityAttributeModifier.Operation.MULTIPLY_BASE));
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

    public void setEquipmentDropChance(EquipmentSlot slot, float chance) {
        switch (slot.getType()) {
            case HAND: {
                this.handDropChances[slot.getEntitySlotId()] = chance;
                break;
            }
            case ARMOR: {
                this.armorDropChances[slot.getEntitySlotId()] = chance;
            }
        }
    }

    public boolean canPickUpLoot() {
        return this.canPickUpLoot;
    }

    public void setCanPickUpLoot(boolean canPickUpLoot) {
        this.canPickUpLoot = canPickUpLoot;
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        return this.getEquippedStack(equipmentSlot).isEmpty() && this.canPickUpLoot();
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    @Override
    public final ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.isAlive()) {
            return ActionResult.PASS;
        }
        if (this.getHoldingEntity() == player) {
            this.detachLeash(true, !player.getAbilities().creativeMode);
            return ActionResult.success(this.world.isClient);
        }
        ActionResult actionResult = this.interactWithItem(player, hand);
        if (actionResult.isAccepted()) {
            return actionResult;
        }
        actionResult = this.interactMob(player, hand);
        if (actionResult.isAccepted()) {
            return actionResult;
        }
        return super.interact(player, hand);
    }

    private ActionResult interactWithItem(PlayerEntity player, Hand hand) {
        ActionResult actionResult;
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.LEAD) && this.canBeLeashedBy(player)) {
            this.attachLeash(player, true);
            itemStack.decrement(1);
            return ActionResult.success(this.world.isClient);
        }
        if (itemStack.isOf(Items.NAME_TAG) && (actionResult = itemStack.useOnEntity(player, this, hand)).isAccepted()) {
            return actionResult;
        }
        if (itemStack.getItem() instanceof SpawnEggItem) {
            if (this.world instanceof ServerWorld) {
                SpawnEggItem spawnEggItem = (SpawnEggItem)itemStack.getItem();
                Optional<MobEntity> optional = spawnEggItem.spawnBaby(player, this, this.getType(), (ServerWorld)this.world, this.getPos(), itemStack);
                optional.ifPresent(entity -> this.onPlayerSpawnedChild(player, (MobEntity)entity));
                return optional.isPresent() ? ActionResult.SUCCESS : ActionResult.PASS;
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    protected void onPlayerSpawnedChild(PlayerEntity player, MobEntity child) {
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    public boolean isInWalkTargetRange() {
        return this.isInWalkTargetRange(this.getBlockPos());
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

    public void clearPositionTarget() {
        this.positionTargetRange = -1.0f;
    }

    public boolean hasPositionTarget() {
        return this.positionTargetRange != -1.0f;
    }

    @Nullable
    public <T extends MobEntity> T convertTo(EntityType<T> entityType, boolean keepEquipment) {
        if (this.isRemoved()) {
            return null;
        }
        MobEntity mobEntity = (MobEntity)entityType.create(this.world);
        mobEntity.copyPositionAndRotation(this);
        mobEntity.setBaby(this.isBaby());
        mobEntity.setAiDisabled(this.isAiDisabled());
        if (this.hasCustomName()) {
            mobEntity.setCustomName(this.getCustomName());
            mobEntity.setCustomNameVisible(this.isCustomNameVisible());
        }
        if (this.isPersistent()) {
            mobEntity.setPersistent();
        }
        mobEntity.setInvulnerable(this.isInvulnerable());
        if (keepEquipment) {
            mobEntity.setCanPickUpLoot(this.canPickUpLoot());
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack = this.getEquippedStack(equipmentSlot);
                if (itemStack.isEmpty()) continue;
                mobEntity.equipStack(equipmentSlot, itemStack.copy());
                mobEntity.setEquipmentDropChance(equipmentSlot, this.getDropChance(equipmentSlot));
                itemStack.setCount(0);
            }
        }
        this.world.spawnEntity(mobEntity);
        if (this.hasVehicle()) {
            Entity entity = this.getVehicle();
            this.stopRiding();
            mobEntity.startRiding(entity, true);
        }
        this.discard();
        return (T)mobEntity;
    }

    protected void updateLeash() {
        if (this.leashNbt != null) {
            this.readLeashNbt();
        }
        if (this.holdingEntity == null) {
            return;
        }
        if (!this.isAlive() || !this.holdingEntity.isAlive()) {
            this.detachLeash(true, true);
        }
    }

    public void detachLeash(boolean sendPacket, boolean dropItem) {
        if (this.holdingEntity != null) {
            this.holdingEntity = null;
            this.leashNbt = null;
            if (!this.world.isClient && dropItem) {
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

    public void attachLeash(Entity entity, boolean sendPacket) {
        this.holdingEntity = entity;
        this.leashNbt = null;
        if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.holdingEntity));
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }

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

    private void readLeashNbt() {
        if (this.leashNbt != null && this.world instanceof ServerWorld) {
            if (this.leashNbt.containsUuid("UUID")) {
                UUID uUID = this.leashNbt.getUuid("UUID");
                Entity entity = ((ServerWorld)this.world).getEntity(uUID);
                if (entity != null) {
                    this.attachLeash(entity, true);
                    return;
                }
            } else if (this.leashNbt.contains("X", 99) && this.leashNbt.contains("Y", 99) && this.leashNbt.contains("Z", 99)) {
                BlockPos blockPos = NbtHelper.toBlockPos(this.leashNbt);
                this.attachLeash(LeashKnotEntity.getOrCreate(this.world, blockPos), true);
                return;
            }
            if (this.age > 100) {
                this.dropItem(Items.LEAD);
                this.leashNbt = null;
            }
        }
    }

    @Override
    public boolean isLogicalSideForUpdatingMovement() {
        return this.canBeControlledByRider() && super.isLogicalSideForUpdatingMovement();
    }

    @Override
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && !this.isAiDisabled();
    }

    public void setAiDisabled(boolean aiDisabled) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, aiDisabled ? (byte)(b | 1) : (byte)(b & 0xFFFFFFFE));
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

    public void setBaby(boolean baby) {
    }

    @Override
    public Arm getMainArm() {
        return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
    }

    public double squaredAttackRange(LivingEntity target) {
        return this.getWidth() * 2.0f * (this.getWidth() * 2.0f) + target.getWidth();
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl;
        int i;
        float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float g = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
            g += (float)EnchantmentHelper.getKnockback(this);
        }
        if ((i = EnchantmentHelper.getFireAspect(this)) > 0) {
            target.setOnFireFor(i * 4);
        }
        if (bl = target.damage(DamageSource.mob(this), f)) {
            if (g > 0.0f && target instanceof LivingEntity) {
                ((LivingEntity)target).takeKnockback(g * 0.5f, MathHelper.sin(this.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }
            if (target instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)target;
                this.disablePlayerShield(playerEntity, this.getMainHandStack(), playerEntity.isUsingItem() ? playerEntity.getActiveItem() : ItemStack.EMPTY);
            }
            this.applyDamageEffects(this, target);
            this.onAttacking(target);
        }
        return bl;
    }

    private void disablePlayerShield(PlayerEntity player, ItemStack mobStack, ItemStack playerStack) {
        if (!mobStack.isEmpty() && !playerStack.isEmpty() && mobStack.getItem() instanceof AxeItem && playerStack.isOf(Items.SHIELD)) {
            float f = 0.25f + (float)EnchantmentHelper.getEfficiency(this) * 0.05f;
            if (this.random.nextFloat() < f) {
                player.getItemCooldownManager().set(Items.SHIELD, 100);
                this.world.sendEntityStatus(player, (byte)30);
            }
        }
    }

    protected boolean isAffectedByDaylight() {
        if (this.world.isDay() && !this.world.isClient) {
            boolean bl;
            float f = this.getBrightnessAtEyes();
            BlockPos blockPos = new BlockPos(this.getX(), this.getEyeY(), this.getZ());
            boolean bl2 = bl = this.isWet() || this.inPowderSnow || this.wasInPowderSnow;
            if (f > 0.5f && this.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && !bl && this.world.isSkyVisible(blockPos)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void swimUpward(TagKey<Fluid> fluid) {
        if (this.getNavigation().canSwim()) {
            super.swimUpward(fluid);
        } else {
            this.setVelocity(this.getVelocity().add(0.0, 0.3, 0.0));
        }
    }

    public void clearGoalsAndTasks() {
        this.goalSelector.clear();
        this.getBrain().clear();
    }

    @Override
    protected void removeFromDimension() {
        super.removeFromDimension();
        this.detachLeash(true, false);
        this.getItemsEquipped().forEach(stack -> stack.setCount(0));
    }

    @Override
    @Nullable
    public ItemStack getPickBlockStack() {
        SpawnEggItem spawnEggItem = SpawnEggItem.forEntity(this.getType());
        if (spawnEggItem == null) {
            return null;
        }
        return new ItemStack(spawnEggItem);
    }
}

