/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public abstract class LivingEntity
extends Entity {
    private static final UUID ATTR_SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final EntityAttributeModifier ATTR_SPRINTING_SPEED_BOOST = new EntityAttributeModifier(ATTR_SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", (double)0.3f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL).setSerialize(false);
    protected static final TrackedData<Byte> LIVING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Float> HEALTH = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> POTION_SWIRLS_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> POTION_SWIRLS_AMBIENT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> STUCK_ARROW_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<BlockPos>> SLEEPING_POSITION = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS);
    protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2f, 0.2f);
    private AbstractEntityAttributeContainer attributes;
    private final DamageTracker damageTracker = new DamageTracker(this);
    private final Map<StatusEffect, StatusEffectInstance> activeStatusEffects = Maps.newHashMap();
    private final DefaultedList<ItemStack> equippedHand = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> equippedArmor = DefaultedList.ofSize(4, ItemStack.EMPTY);
    public boolean isHandSwinging;
    public Hand preferredHand;
    public int handSwingTicks;
    public int stuckArrowTimer;
    public int hurtTime;
    public int field_6254;
    public float field_6271;
    public int deathTime;
    public float lastHandSwingProgress;
    public float handSwingProgress;
    protected int lastAttackedTicks;
    public float lastLimbDistance;
    public float limbDistance;
    public float limbAngle;
    public final int field_6269 = 20;
    public final float field_6244;
    public final float field_6262;
    public float field_6283;
    public float field_6220;
    public float headYaw;
    public float prevHeadYaw;
    public float field_6281 = 0.02f;
    protected PlayerEntity attackingPlayer;
    protected int playerHitTimer;
    protected boolean dead;
    protected int despawnCounter;
    protected float field_6217;
    protected float field_6233;
    protected float field_6255;
    protected float field_6275;
    protected float field_6215;
    protected int field_6232;
    protected float field_6253;
    protected boolean jumping;
    public float sidewaysSpeed;
    public float upwardSpeed;
    public float forwardSpeed;
    public float field_6267;
    protected int field_6210;
    protected double field_6224;
    protected double field_6245;
    protected double field_6263;
    protected double field_6284;
    protected double field_6221;
    protected double field_6242;
    protected int field_6265;
    private boolean field_6285 = true;
    @Nullable
    private LivingEntity attacker;
    private int lastAttackedTime;
    private LivingEntity attacking;
    private int lastAttackTime;
    private float movementSpeed;
    private int field_6228;
    private float absorptionAmount;
    protected ItemStack activeItemStack = ItemStack.EMPTY;
    protected int itemUseTimeLeft;
    protected int field_6239;
    private BlockPos lastBlockPos;
    private DamageSource field_6276;
    private long field_6226;
    protected int field_6261;
    private float field_6243;
    private float field_6264;
    protected Brain<?> brain;

    protected LivingEntity(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
        this.initAttributes();
        this.setHealth(this.getMaximumHealth());
        this.inanimate = true;
        this.field_6262 = (float)((Math.random() + 1.0) * (double)0.01f);
        this.updatePosition(this.x, this.y, this.z);
        this.field_6244 = (float)Math.random() * 12398.0f;
        this.headYaw = this.yaw = (float)(Math.random() * 6.2831854820251465);
        this.stepHeight = 0.6f;
        this.brain = this.deserializeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)new CompoundTag()));
    }

    public Brain<?> getBrain() {
        return this.brain;
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return new Brain((Collection<MemoryModuleType<?>>)ImmutableList.of(), ImmutableList.of(), dynamic);
    }

    @Override
    public void kill() {
        this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean canTarget(EntityType<?> type) {
        return true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(LIVING_FLAGS, (byte)0);
        this.dataTracker.startTracking(POTION_SWIRLS_COLOR, 0);
        this.dataTracker.startTracking(POTION_SWIRLS_AMBIENT, false);
        this.dataTracker.startTracking(STUCK_ARROW_COUNT, 0);
        this.dataTracker.startTracking(HEALTH, Float.valueOf(1.0f));
        this.dataTracker.startTracking(SLEEPING_POSITION, Optional.empty());
    }

    protected void initAttributes() {
        this.getAttributes().register(EntityAttributes.MAX_HEALTH);
        this.getAttributes().register(EntityAttributes.KNOCKBACK_RESISTANCE);
        this.getAttributes().register(EntityAttributes.MOVEMENT_SPEED);
        this.getAttributes().register(EntityAttributes.ARMOR);
        this.getAttributes().register(EntityAttributes.ARMOR_TOUGHNESS);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        if (!this.isTouchingWater()) {
            this.checkWaterState();
        }
        if (!this.world.isClient && this.fallDistance > 3.0f && onGround) {
            float f = MathHelper.ceil(this.fallDistance - 3.0f);
            if (!landedState.isAir()) {
                double d = Math.min((double)(0.2f + f / 15.0f), 2.5);
                int i = (int)(150.0 * d);
                ((ServerWorld)this.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, landedState), this.x, this.y, this.z, i, 0.0, 0.0, 0.0, 0.15f);
            }
        }
        super.fall(heightDifference, onGround, landedState, landedPosition);
    }

    public boolean canBreatheInWater() {
        return this.getGroup() == EntityGroup.UNDEAD;
    }

    @Environment(value=EnvType.CLIENT)
    public float method_6024(float f) {
        return MathHelper.lerp(f, this.field_6264, this.field_6243);
    }

    @Override
    public void baseTick() {
        boolean bl2;
        this.lastHandSwingProgress = this.handSwingProgress;
        if (this.firstUpdate) {
            this.getSleepingPosition().ifPresent(this::setPositionInBed);
        }
        super.baseTick();
        this.world.getProfiler().push("livingEntityBaseTick");
        boolean bl = this instanceof PlayerEntity;
        if (this.isAlive()) {
            double e;
            double d;
            if (this.isInsideWall()) {
                this.damage(DamageSource.IN_WALL, 1.0f);
            } else if (bl && !this.world.getWorldBorder().contains(this.getBoundingBox()) && (d = this.world.getWorldBorder().getDistanceInsideBorder(this) + this.world.getWorldBorder().getBuffer()) < 0.0 && (e = this.world.getWorldBorder().getDamagePerBlock()) > 0.0) {
                this.damage(DamageSource.IN_WALL, Math.max(1, MathHelper.floor(-d * e)));
            }
        }
        if (this.isFireImmune() || this.world.isClient) {
            this.extinguish();
        }
        boolean bl3 = bl2 = bl && ((PlayerEntity)this).abilities.invulnerable;
        if (this.isAlive()) {
            BlockPos blockPos;
            if (this.isInFluid(FluidTags.WATER) && this.world.getBlockState(new BlockPos(this.x, this.y + (double)this.getStandingEyeHeight(), this.z)).getBlock() != Blocks.BUBBLE_COLUMN) {
                if (!(this.canBreatheInWater() || StatusEffectUtil.hasWaterBreathing(this) || bl2)) {
                    this.setAir(this.getNextAirUnderwater(this.getAir()));
                    if (this.getAir() == -20) {
                        this.setAir(0);
                        Vec3d vec3d = this.getVelocity();
                        for (int i = 0; i < 8; ++i) {
                            float f = this.random.nextFloat() - this.random.nextFloat();
                            float g = this.random.nextFloat() - this.random.nextFloat();
                            float h = this.random.nextFloat() - this.random.nextFloat();
                            this.world.addParticle(ParticleTypes.BUBBLE, this.x + (double)f, this.y + (double)g, this.z + (double)h, vec3d.x, vec3d.y, vec3d.z);
                        }
                        this.damage(DamageSource.DROWN, 2.0f);
                    }
                }
                if (!this.world.isClient && this.hasVehicle() && this.getVehicle() != null && !this.getVehicle().canBeRiddenInWater()) {
                    this.stopRiding();
                }
            } else if (this.getAir() < this.getMaxAir()) {
                this.setAir(this.getNextAirOnLand(this.getAir()));
            }
            if (!this.world.isClient && !Objects.equal((Object)this.lastBlockPos, (Object)(blockPos = new BlockPos(this)))) {
                this.lastBlockPos = blockPos;
                this.applyFrostWalker(blockPos);
            }
        }
        if (this.isAlive() && this.isWet()) {
            this.extinguish();
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
            --this.timeUntilRegen;
        }
        if (this.getHealth() <= 0.0f) {
            this.updatePostDeath();
        }
        if (this.playerHitTimer > 0) {
            --this.playerHitTimer;
        } else {
            this.attackingPlayer = null;
        }
        if (this.attacking != null && !this.attacking.isAlive()) {
            this.attacking = null;
        }
        if (this.attacker != null) {
            if (!this.attacker.isAlive()) {
                this.setAttacker(null);
            } else if (this.age - this.lastAttackedTime > 100) {
                this.setAttacker(null);
            }
        }
        this.tickStatusEffects();
        this.field_6275 = this.field_6255;
        this.field_6220 = this.field_6283;
        this.prevHeadYaw = this.headYaw;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.world.getProfiler().pop();
    }

    protected void applyFrostWalker(BlockPos pos) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, this);
        if (i > 0) {
            FrostWalkerEnchantment.freezeWater(this, this.world, pos, i);
        }
    }

    public boolean isBaby() {
        return false;
    }

    public float getScaleFactor() {
        return this.isBaby() ? 0.5f : 1.0f;
    }

    @Override
    public boolean canBeRiddenInWater() {
        return false;
    }

    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            int i;
            if (!this.world.isClient && (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
                int j;
                for (i = this.getCurrentExperience(this.attackingPlayer); i > 0; i -= j) {
                    j = ExperienceOrbEntity.roundToOrbSize(i);
                    this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
                }
            }
            this.remove();
            for (i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.world.addParticle(ParticleTypes.POOF, this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), this.y + (double)(this.random.nextFloat() * this.getHeight()), this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), d, e, f);
            }
        }
    }

    protected boolean canDropLootAndXp() {
        return !this.isBaby();
    }

    protected int getNextAirUnderwater(int air) {
        int i = EnchantmentHelper.getRespiration(this);
        if (i > 0 && this.random.nextInt(i + 1) > 0) {
            return air;
        }
        return air - 1;
    }

    protected int getNextAirOnLand(int air) {
        return Math.min(air + 4, this.getMaxAir());
    }

    protected int getCurrentExperience(PlayerEntity player) {
        return 0;
    }

    protected boolean shouldAlwaysDropXp() {
        return false;
    }

    public Random getRandom() {
        return this.random;
    }

    @Nullable
    public LivingEntity getAttacker() {
        return this.attacker;
    }

    public int getLastAttackedTime() {
        return this.lastAttackedTime;
    }

    public void setAttacker(@Nullable LivingEntity attacker) {
        this.attacker = attacker;
        this.lastAttackedTime = this.age;
    }

    @Nullable
    public LivingEntity getAttacking() {
        return this.attacking;
    }

    public int getLastAttackTime() {
        return this.lastAttackTime;
    }

    public void onAttacking(Entity target) {
        this.attacking = target instanceof LivingEntity ? (LivingEntity)target : null;
        this.lastAttackTime = this.age;
    }

    public int getDespawnCounter() {
        return this.despawnCounter;
    }

    public void setDespawnCounter(int despawnCounter) {
        this.despawnCounter = despawnCounter;
    }

    protected void onEquipStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        SoundEvent soundEvent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        Item item = stack.getItem();
        if (item instanceof ArmorItem) {
            soundEvent = ((ArmorItem)item).getMaterial().getEquipSound();
        } else if (item == Items.ELYTRA) {
            soundEvent = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
        }
        this.playSound(soundEvent, 1.0f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        ItemStack itemStack;
        tag.putFloat("Health", this.getHealth());
        tag.putShort("HurtTime", (short)this.hurtTime);
        tag.putInt("HurtByTimestamp", this.lastAttackedTime);
        tag.putShort("DeathTime", (short)this.deathTime);
        tag.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            itemStack = this.getEquippedStack(equipmentSlot);
            if (itemStack.isEmpty()) continue;
            this.getAttributes().removeAll(itemStack.getAttributeModifiers(equipmentSlot));
        }
        tag.put("Attributes", EntityAttributes.toTag(this.getAttributes()));
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            itemStack = this.getEquippedStack(equipmentSlot);
            if (itemStack.isEmpty()) continue;
            this.getAttributes().replaceAll(itemStack.getAttributeModifiers(equipmentSlot));
        }
        if (!this.activeStatusEffects.isEmpty()) {
            ListTag listTag = new ListTag();
            for (StatusEffectInstance statusEffectInstance : this.activeStatusEffects.values()) {
                listTag.add(statusEffectInstance.toTag(new CompoundTag()));
            }
            tag.put("ActiveEffects", listTag);
        }
        tag.putBoolean("FallFlying", this.isFallFlying());
        this.getSleepingPosition().ifPresent(blockPos -> {
            tag.putInt("SleepingX", blockPos.getX());
            tag.putInt("SleepingY", blockPos.getY());
            tag.putInt("SleepingZ", blockPos.getZ());
        });
        tag.put("Brain", this.brain.serialize(NbtOps.INSTANCE));
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        this.setAbsorptionAmount(tag.getFloat("AbsorptionAmount"));
        if (tag.contains("Attributes", 9) && this.world != null && !this.world.isClient) {
            EntityAttributes.fromTag(this.getAttributes(), tag.getList("Attributes", 10));
        }
        if (tag.contains("ActiveEffects", 9)) {
            ListTag listTag = tag.getList("ActiveEffects", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromTag(compoundTag);
                if (statusEffectInstance == null) continue;
                this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
            }
        }
        if (tag.contains("Health", 99)) {
            this.setHealth(tag.getFloat("Health"));
        }
        this.hurtTime = tag.getShort("HurtTime");
        this.deathTime = tag.getShort("DeathTime");
        this.lastAttackedTime = tag.getInt("HurtByTimestamp");
        if (tag.contains("Team", 8)) {
            boolean bl;
            String string = tag.getString("Team");
            Team team = this.world.getScoreboard().getTeam(string);
            boolean bl2 = bl = team != null && this.world.getScoreboard().addPlayerToTeam(this.getUuidAsString(), team);
            if (!bl) {
                LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", (Object)string);
            }
        }
        if (tag.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }
        if (tag.contains("SleepingX", 99) && tag.contains("SleepingY", 99) && tag.contains("SleepingZ", 99)) {
            BlockPos blockPos = new BlockPos(tag.getInt("SleepingX"), tag.getInt("SleepingY"), tag.getInt("SleepingZ"));
            this.setSleepingPosition(blockPos);
            this.dataTracker.set(POSE, EntityPose.SLEEPING);
            if (!this.firstUpdate) {
                this.setPositionInBed(blockPos);
            }
        }
        if (tag.contains("Brain", 10)) {
            this.brain = this.deserializeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)tag.get("Brain")));
        }
    }

    protected void tickStatusEffects() {
        Iterator<StatusEffect> iterator = this.activeStatusEffects.keySet().iterator();
        try {
            while (iterator.hasNext()) {
                StatusEffect statusEffect = iterator.next();
                StatusEffectInstance statusEffectInstance = this.activeStatusEffects.get(statusEffect);
                if (!statusEffectInstance.update(this)) {
                    if (this.world.isClient) continue;
                    iterator.remove();
                    this.method_6129(statusEffectInstance);
                    continue;
                }
                if (statusEffectInstance.getDuration() % 600 != 0) continue;
                this.method_6009(statusEffectInstance, false);
            }
        }
        catch (ConcurrentModificationException statusEffect) {
            // empty catch block
        }
        if (this.field_6285) {
            if (!this.world.isClient) {
                this.updatePotionVisibility();
            }
            this.field_6285 = false;
        }
        int i = this.dataTracker.get(POTION_SWIRLS_COLOR);
        boolean bl = this.dataTracker.get(POTION_SWIRLS_AMBIENT);
        if (i > 0) {
            boolean bl2 = this.isInvisible() ? this.random.nextInt(15) == 0 : this.random.nextBoolean();
            if (bl) {
                bl2 &= this.random.nextInt(5) == 0;
            }
            if (bl2 && i > 0) {
                double d = (double)(i >> 16 & 0xFF) / 255.0;
                double e = (double)(i >> 8 & 0xFF) / 255.0;
                double f = (double)(i >> 0 & 0xFF) / 255.0;
                this.world.addParticle(bl ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), this.y + this.random.nextDouble() * (double)this.getHeight(), this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), d, e, f);
            }
        }
    }

    protected void updatePotionVisibility() {
        if (this.activeStatusEffects.isEmpty()) {
            this.clearPotionSwirls();
            this.setInvisible(false);
        } else {
            Collection<StatusEffectInstance> collection = this.activeStatusEffects.values();
            this.dataTracker.set(POTION_SWIRLS_AMBIENT, LivingEntity.containsOnlyAmbientEffects(collection));
            this.dataTracker.set(POTION_SWIRLS_COLOR, PotionUtil.getColor(collection));
            this.setInvisible(this.hasStatusEffect(StatusEffects.INVISIBILITY));
        }
    }

    public double getAttackDistanceScalingFactor(@Nullable Entity entity) {
        double d = 1.0;
        if (this.isSneaking()) {
            d *= 0.8;
        }
        if (this.isInvisible()) {
            float f = this.method_18396();
            if (f < 0.1f) {
                f = 0.1f;
            }
            d *= 0.7 * (double)f;
        }
        if (entity != null) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
            Item item = itemStack.getItem();
            EntityType<?> entityType = entity.getType();
            if (entityType == EntityType.SKELETON && item == Items.SKELETON_SKULL || entityType == EntityType.ZOMBIE && item == Items.ZOMBIE_HEAD || entityType == EntityType.CREEPER && item == Items.CREEPER_HEAD) {
                d *= 0.5;
            }
        }
        return d;
    }

    public boolean canTarget(LivingEntity target) {
        return true;
    }

    public boolean isTarget(LivingEntity entity, TargetPredicate predicate) {
        return predicate.test(this, entity);
    }

    public static boolean containsOnlyAmbientEffects(Collection<StatusEffectInstance> effects) {
        for (StatusEffectInstance statusEffectInstance : effects) {
            if (statusEffectInstance.isAmbient()) continue;
            return false;
        }
        return true;
    }

    protected void clearPotionSwirls() {
        this.dataTracker.set(POTION_SWIRLS_AMBIENT, false);
        this.dataTracker.set(POTION_SWIRLS_COLOR, 0);
    }

    public boolean clearStatusEffects() {
        if (this.world.isClient) {
            return false;
        }
        Iterator<StatusEffectInstance> iterator = this.activeStatusEffects.values().iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            this.method_6129(iterator.next());
            iterator.remove();
            bl = true;
        }
        return bl;
    }

    public Collection<StatusEffectInstance> getStatusEffects() {
        return this.activeStatusEffects.values();
    }

    public Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects() {
        return this.activeStatusEffects;
    }

    public boolean hasStatusEffect(StatusEffect effect) {
        return this.activeStatusEffects.containsKey(effect);
    }

    @Nullable
    public StatusEffectInstance getStatusEffect(StatusEffect effect) {
        return this.activeStatusEffects.get(effect);
    }

    public boolean addStatusEffect(StatusEffectInstance effect) {
        if (!this.canHaveStatusEffect(effect)) {
            return false;
        }
        StatusEffectInstance statusEffectInstance = this.activeStatusEffects.get(effect.getEffectType());
        if (statusEffectInstance == null) {
            this.activeStatusEffects.put(effect.getEffectType(), effect);
            this.method_6020(effect);
            return true;
        }
        if (statusEffectInstance.upgrade(effect)) {
            this.method_6009(statusEffectInstance, true);
            return true;
        }
        return false;
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        StatusEffect statusEffect;
        return this.getGroup() != EntityGroup.UNDEAD || (statusEffect = effect.getEffectType()) != StatusEffects.REGENERATION && statusEffect != StatusEffects.POISON;
    }

    public boolean isUndead() {
        return this.getGroup() == EntityGroup.UNDEAD;
    }

    @Nullable
    public StatusEffectInstance removeStatusEffectInternal(@Nullable StatusEffect type) {
        return this.activeStatusEffects.remove(type);
    }

    public boolean removeStatusEffect(StatusEffect type) {
        StatusEffectInstance statusEffectInstance = this.removeStatusEffectInternal(type);
        if (statusEffectInstance != null) {
            this.method_6129(statusEffectInstance);
            return true;
        }
        return false;
    }

    protected void method_6020(StatusEffectInstance statusEffectInstance) {
        this.field_6285 = true;
        if (!this.world.isClient) {
            statusEffectInstance.getEffectType().method_5555(this, this.getAttributes(), statusEffectInstance.getAmplifier());
        }
    }

    protected void method_6009(StatusEffectInstance statusEffectInstance, boolean bl) {
        this.field_6285 = true;
        if (bl && !this.world.isClient) {
            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            statusEffect.method_5562(this, this.getAttributes(), statusEffectInstance.getAmplifier());
            statusEffect.method_5555(this, this.getAttributes(), statusEffectInstance.getAmplifier());
        }
    }

    protected void method_6129(StatusEffectInstance statusEffectInstance) {
        this.field_6285 = true;
        if (!this.world.isClient) {
            statusEffectInstance.getEffectType().method_5562(this, this.getAttributes(), statusEffectInstance.getAmplifier());
        }
    }

    public void heal(float amount) {
        float f = this.getHealth();
        if (f > 0.0f) {
            this.setHealth(f + amount);
        }
    }

    public float getHealth() {
        return this.dataTracker.get(HEALTH).floatValue();
    }

    public void setHealth(float health) {
        this.dataTracker.set(HEALTH, Float.valueOf(MathHelper.clamp(health, 0.0f, this.getMaximumHealth())));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl3;
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.world.isClient) {
            return false;
        }
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        if (source.isFire() && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.isSleeping() && !this.world.isClient) {
            this.wakeUp();
        }
        this.despawnCounter = 0;
        float f = amount;
        if (!(source != DamageSource.ANVIL && source != DamageSource.FALLING_BLOCK || this.getEquippedStack(EquipmentSlot.HEAD).isEmpty())) {
            this.getEquippedStack(EquipmentSlot.HEAD).damage((int)(amount * 4.0f + this.random.nextFloat() * amount * 2.0f), this, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.HEAD));
            amount *= 0.75f;
        }
        boolean bl = false;
        float g = 0.0f;
        if (amount > 0.0f && this.method_6061(source)) {
            Entity entity;
            this.damageShield(amount);
            g = amount;
            amount = 0.0f;
            if (!source.isProjectile() && (entity = source.getSource()) instanceof LivingEntity) {
                this.takeShieldHit((LivingEntity)entity);
            }
            bl = true;
        }
        this.limbDistance = 1.5f;
        boolean bl2 = true;
        if ((float)this.timeUntilRegen > 10.0f) {
            if (amount <= this.field_6253) {
                return false;
            }
            this.applyDamage(source, amount - this.field_6253);
            this.field_6253 = amount;
            bl2 = false;
        } else {
            this.field_6253 = amount;
            this.timeUntilRegen = 20;
            this.applyDamage(source, amount);
            this.hurtTime = this.field_6254 = 10;
        }
        this.field_6271 = 0.0f;
        Entity entity2 = source.getAttacker();
        if (entity2 != null) {
            WolfEntity wolfEntity;
            if (entity2 instanceof LivingEntity) {
                this.setAttacker((LivingEntity)entity2);
            }
            if (entity2 instanceof PlayerEntity) {
                this.playerHitTimer = 100;
                this.attackingPlayer = (PlayerEntity)entity2;
            } else if (entity2 instanceof WolfEntity && (wolfEntity = (WolfEntity)entity2).isTamed()) {
                this.playerHitTimer = 100;
                LivingEntity livingEntity2 = wolfEntity.getOwner();
                this.attackingPlayer = livingEntity2 != null && livingEntity2.getType() == EntityType.PLAYER ? (PlayerEntity)livingEntity2 : null;
            }
        }
        if (bl2) {
            if (bl) {
                this.world.sendEntityStatus(this, (byte)29);
            } else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).method_5549()) {
                this.world.sendEntityStatus(this, (byte)33);
            } else {
                int b = source == DamageSource.DROWN ? 36 : (source.isFire() ? 37 : (source == DamageSource.SWEET_BERRY_BUSH ? 44 : 2));
                this.world.sendEntityStatus(this, (byte)b);
            }
            if (source != DamageSource.DROWN && (!bl || amount > 0.0f)) {
                this.scheduleVelocityUpdate();
            }
            if (entity2 != null) {
                double d = entity2.x - this.x;
                double e = entity2.z - this.z;
                while (d * d + e * e < 1.0E-4) {
                    d = (Math.random() - Math.random()) * 0.01;
                    e = (Math.random() - Math.random()) * 0.01;
                }
                this.field_6271 = (float)(MathHelper.atan2(e, d) * 57.2957763671875 - (double)this.yaw);
                this.takeKnockback(entity2, 0.4f, d, e);
            } else {
                this.field_6271 = (int)(Math.random() * 2.0) * 180;
            }
        }
        if (this.getHealth() <= 0.0f) {
            if (!this.method_6095(source)) {
                SoundEvent soundEvent = this.getDeathSound();
                if (bl2 && soundEvent != null) {
                    this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
                }
                this.onDeath(source);
            }
        } else if (bl2) {
            this.playHurtSound(source);
        }
        boolean bl4 = bl3 = !bl || amount > 0.0f;
        if (bl3) {
            this.field_6276 = source;
            this.field_6226 = this.world.getTime();
        }
        if (this instanceof ServerPlayerEntity) {
            Criterions.ENTITY_HURT_PLAYER.handle((ServerPlayerEntity)this, source, f, amount, bl);
            if (g > 0.0f && g < 3.4028235E37f) {
                ((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0f));
            }
        }
        if (entity2 instanceof ServerPlayerEntity) {
            Criterions.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity2, this, source, f, amount, bl);
        }
        return bl3;
    }

    protected void takeShieldHit(LivingEntity attacker) {
        attacker.knockback(this);
    }

    protected void knockback(LivingEntity target) {
        target.takeKnockback(this, 0.5f, target.x - this.x, target.z - this.z);
    }

    private boolean method_6095(DamageSource damageSource) {
        if (damageSource.isOutOfWorld()) {
            return false;
        }
        ItemStack itemStack = null;
        for (Hand hand : Hand.values()) {
            ItemStack itemStack2 = this.getStackInHand(hand);
            if (itemStack2.getItem() != Items.TOTEM_OF_UNDYING) continue;
            itemStack = itemStack2.copy();
            itemStack2.decrement(1);
            break;
        }
        if (itemStack != null) {
            if (this instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this;
                serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                Criterions.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
            }
            this.setHealth(1.0f);
            this.clearStatusEffects();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
            this.world.sendEntityStatus(this, (byte)35);
        }
        return itemStack != null;
    }

    @Nullable
    public DamageSource getRecentDamageSource() {
        if (this.world.getTime() - this.field_6226 > 40L) {
            this.field_6276 = null;
        }
        return this.field_6276;
    }

    protected void playHurtSound(DamageSource source) {
        SoundEvent soundEvent = this.getHurtSound(source);
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private boolean method_6061(DamageSource damageSource) {
        Vec3d vec3d;
        ProjectileEntity projectileEntity;
        Entity entity = damageSource.getSource();
        boolean bl = false;
        if (entity instanceof ProjectileEntity && (projectileEntity = (ProjectileEntity)entity).getPierceLevel() > 0) {
            bl = true;
        }
        if (!damageSource.bypassesArmor() && this.method_6039() && !bl && (vec3d = damageSource.method_5510()) != null) {
            Vec3d vec3d2 = this.getRotationVec(1.0f);
            Vec3d vec3d3 = vec3d.reverseSubtract(new Vec3d(this.x, this.y, this.z)).normalize();
            vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
            if (vec3d3.dotProduct(vec3d2) < 0.0) {
                return true;
            }
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    private void playEquipmentBreakEffects(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (!this.isSilent()) {
                this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_ITEM_BREAK, this.getSoundCategory(), 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f, false);
            }
            this.spawnItemParticles(stack, 5);
        }
    }

    public void onDeath(DamageSource source) {
        if (this.dead) {
            return;
        }
        Entity entity = source.getAttacker();
        LivingEntity livingEntity = this.method_6124();
        if (this.field_6232 >= 0 && livingEntity != null) {
            livingEntity.updateKilledAdvancementCriterion(this, this.field_6232, source);
        }
        if (entity != null) {
            entity.onKilledOther(this);
        }
        if (this.isSleeping()) {
            this.wakeUp();
        }
        this.dead = true;
        this.getDamageTracker().update();
        if (!this.world.isClient) {
            this.drop(source);
            boolean bl = false;
            if (livingEntity instanceof WitherEntity) {
                if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                    BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
                    BlockState blockState = Blocks.WITHER_ROSE.getDefaultState();
                    if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
                        this.world.setBlockState(blockPos, blockState, 3);
                        bl = true;
                    }
                }
                if (!bl) {
                    ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
                    this.world.spawnEntity(itemEntity);
                }
            }
        }
        this.world.sendEntityStatus(this, (byte)3);
        this.setPose(EntityPose.DYING);
    }

    protected void drop(DamageSource source) {
        boolean bl;
        Entity entity = source.getAttacker();
        int i = entity instanceof PlayerEntity ? EnchantmentHelper.getLooting((LivingEntity)entity) : 0;
        boolean bl2 = bl = this.playerHitTimer > 0;
        if (this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropLoot(source, bl);
            this.dropEquipment(source, i, bl);
        }
        this.dropInventory();
    }

    protected void dropInventory() {
    }

    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
    }

    public Identifier getLootTable() {
        return this.getType().getLootTableId();
    }

    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        Identifier identifier = this.getLootTable();
        LootTable lootTable = this.world.getServer().getLootManager().getSupplier(identifier);
        LootContext.Builder builder = this.getLootContextBuilder(causedByPlayer, source);
        lootTable.dropLimited(builder.build(LootContextTypes.ENTITY), this::dropStack);
    }

    protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
        LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world).setRandom(this.random).put(LootContextParameters.THIS_ENTITY, this).put(LootContextParameters.POSITION, new BlockPos(this)).put(LootContextParameters.DAMAGE_SOURCE, source).putNullable(LootContextParameters.KILLER_ENTITY, source.getAttacker()).putNullable(LootContextParameters.DIRECT_KILLER_ENTITY, source.getSource());
        if (causedByPlayer && this.attackingPlayer != null) {
            builder = builder.put(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).setLuck(this.attackingPlayer.getLuck());
        }
        return builder;
    }

    public void takeKnockback(Entity attacker, float speed, double xMovement, double zMovement) {
        if (this.random.nextDouble() < this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).getValue()) {
            return;
        }
        this.velocityDirty = true;
        Vec3d vec3d = this.getVelocity();
        Vec3d vec3d2 = new Vec3d(xMovement, 0.0, zMovement).normalize().multiply(speed);
        this.setVelocity(vec3d.x / 2.0 - vec3d2.x, this.onGround ? Math.min(0.4, vec3d.y / 2.0 + (double)speed) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_DEATH;
    }

    protected SoundEvent getFallSound(int distance) {
        if (distance > 4) {
            return SoundEvents.ENTITY_GENERIC_BIG_FALL;
        }
        return SoundEvents.ENTITY_GENERIC_SMALL_FALL;
    }

    protected SoundEvent getDrinkSound(ItemStack stack) {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    public SoundEvent getEatSound(ItemStack stack) {
        return SoundEvents.ENTITY_GENERIC_EAT;
    }

    public boolean isClimbing() {
        if (this.isSpectator()) {
            return false;
        }
        BlockState blockState = this.method_16212();
        Block block = blockState.getBlock();
        if (block == Blocks.LADDER || block == Blocks.VINE || block == Blocks.SCAFFOLDING) {
            return true;
        }
        return block instanceof TrapdoorBlock && this.method_6077(new BlockPos(this), blockState);
    }

    public BlockState method_16212() {
        return this.world.getBlockState(new BlockPos(this));
    }

    private boolean method_6077(BlockPos blockPos, BlockState blockState) {
        BlockState blockState2;
        return blockState.get(TrapdoorBlock.OPEN) != false && (blockState2 = this.world.getBlockState(blockPos.down())).getBlock() == Blocks.LADDER && blockState2.get(LadderBlock.FACING) == blockState.get(TrapdoorBlock.FACING);
    }

    @Override
    public boolean isAlive() {
        return !this.removed && this.getHealth() > 0.0f;
    }

    @Override
    public void handleFallDamage(float fallDistance, float damageMultiplier) {
        super.handleFallDamage(fallDistance, damageMultiplier);
        StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.JUMP_BOOST);
        float f = statusEffectInstance == null ? 0.0f : (float)(statusEffectInstance.getAmplifier() + 1);
        int i = MathHelper.ceil((fallDistance - 3.0f - f) * damageMultiplier);
        if (i > 0) {
            this.playSound(this.getFallSound(i), 1.0f, 1.0f);
            this.damage(DamageSource.FALL, i);
            int j = MathHelper.floor(this.x);
            int k = MathHelper.floor(this.y - (double)0.2f);
            int l = MathHelper.floor(this.z);
            BlockState blockState = this.world.getBlockState(new BlockPos(j, k, l));
            if (!blockState.isAir()) {
                BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
                this.playSound(blockSoundGroup.getFallSound(), blockSoundGroup.getVolume() * 0.5f, blockSoundGroup.getPitch() * 0.75f);
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void animateDamage() {
        this.hurtTime = this.field_6254 = 10;
        this.field_6271 = 0.0f;
    }

    public int getArmor() {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.ARMOR);
        return MathHelper.floor(entityAttributeInstance.getValue());
    }

    protected void damageArmor(float amount) {
    }

    protected void damageShield(float amount) {
    }

    protected float applyArmorToDamage(DamageSource source, float amount) {
        if (!source.bypassesArmor()) {
            this.damageArmor(amount);
            amount = DamageUtil.getDamageLeft(amount, this.getArmor(), (float)this.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue());
        }
        return amount;
    }

    protected float applyEnchantmentsToDamage(DamageSource source, float amount) {
        int i;
        int j;
        float f;
        float g;
        float h;
        if (source.isUnblockable()) {
            return amount;
        }
        if (this.hasStatusEffect(StatusEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD && (h = (g = amount) - (amount = Math.max((f = amount * (float)(j = 25 - (i = (this.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5))) / 25.0f, 0.0f))) > 0.0f && h < 3.4028235E37f) {
            if (this instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0f));
            } else if (source.getAttacker() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0f));
            }
        }
        if (amount <= 0.0f) {
            return 0.0f;
        }
        i = EnchantmentHelper.getProtectionAmount(this.getArmorItems(), source);
        if (i > 0) {
            amount = DamageUtil.getInflictedDamage(amount, i);
        }
        return amount;
    }

    protected void applyDamage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return;
        }
        amount = this.applyArmorToDamage(source, amount);
        float f = amount = this.applyEnchantmentsToDamage(source, amount);
        amount = Math.max(amount - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - amount));
        float g = f - amount;
        if (g > 0.0f && g < 3.4028235E37f && source.getAttacker() instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(g * 10.0f));
        }
        if (amount == 0.0f) {
            return;
        }
        float h = this.getHealth();
        this.setHealth(h - amount);
        this.getDamageTracker().onDamage(source, h, amount);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - amount);
    }

    public DamageTracker getDamageTracker() {
        return this.damageTracker;
    }

    @Nullable
    public LivingEntity method_6124() {
        if (this.damageTracker.getBiggestAttacker() != null) {
            return this.damageTracker.getBiggestAttacker();
        }
        if (this.attackingPlayer != null) {
            return this.attackingPlayer;
        }
        if (this.attacker != null) {
            return this.attacker;
        }
        return null;
    }

    public final float getMaximumHealth() {
        return (float)this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getValue();
    }

    public final int getStuckArrowCount() {
        return this.dataTracker.get(STUCK_ARROW_COUNT);
    }

    public final void setStuckArrowCount(int stuckArrowCount) {
        this.dataTracker.set(STUCK_ARROW_COUNT, stuckArrowCount);
    }

    private int getHandSwingDuration() {
        if (StatusEffectUtil.hasHaste(this)) {
            return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
        }
        if (this.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            return 6 + (1 + this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2;
        }
        return 6;
    }

    public void swingHand(Hand hand) {
        if (!this.isHandSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
            this.handSwingTicks = -1;
            this.isHandSwinging = true;
            this.preferredHand = hand;
            if (this.world instanceof ServerWorld) {
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAnimationS2CPacket(this, hand == Hand.MAIN_HAND ? 0 : 3));
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        switch (status) {
            case 2: 
            case 33: 
            case 36: 
            case 37: 
            case 44: {
                DamageSource damageSource;
                SoundEvent soundEvent;
                boolean bl = status == 33;
                boolean bl2 = status == 36;
                boolean bl3 = status == 37;
                boolean bl4 = status == 44;
                this.limbDistance = 1.5f;
                this.timeUntilRegen = 20;
                this.hurtTime = this.field_6254 = 10;
                this.field_6271 = 0.0f;
                if (bl) {
                    this.playSound(SoundEvents.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if ((soundEvent = this.getHurtSound(damageSource = bl3 ? DamageSource.ON_FIRE : (bl2 ? DamageSource.DROWN : (bl4 ? DamageSource.SWEET_BERRY_BUSH : DamageSource.GENERIC)))) != null) {
                    this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.damage(DamageSource.GENERIC, 0.0f);
                break;
            }
            case 3: {
                SoundEvent soundEvent2 = this.getDeathSound();
                if (soundEvent2 != null) {
                    this.playSound(soundEvent2, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.setHealth(0.0f);
                this.onDeath(DamageSource.GENERIC);
                break;
            }
            case 30: {
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8f + this.world.random.nextFloat() * 0.4f);
                break;
            }
            case 29: {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 0.8f + this.world.random.nextFloat() * 0.4f);
                break;
            }
            case 46: {
                int i = 128;
                for (int j = 0; j < 128; ++j) {
                    double d = (double)j / 127.0;
                    float f = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float g = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float h = (this.random.nextFloat() - 0.5f) * 0.2f;
                    double e = MathHelper.lerp(d, this.prevX, this.x) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
                    double k = MathHelper.lerp(d, this.prevY, this.y) + this.random.nextDouble() * (double)this.getHeight();
                    double l = MathHelper.lerp(d, this.prevZ, this.z) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
                    this.world.addParticle(ParticleTypes.PORTAL, e, k, l, f, g, h);
                }
                break;
            }
            case 47: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.MAINHAND));
                break;
            }
            case 48: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.OFFHAND));
                break;
            }
            case 49: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.HEAD));
                break;
            }
            case 50: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.CHEST));
                break;
            }
            case 51: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.LEGS));
                break;
            }
            case 52: {
                this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.FEET));
                break;
            }
            default: {
                super.handleStatus(status);
            }
        }
    }

    @Override
    protected void destroy() {
        this.damage(DamageSource.OUT_OF_WORLD, 4.0f);
    }

    protected void tickHandSwing() {
        int i = this.getHandSwingDuration();
        if (this.isHandSwinging) {
            ++this.handSwingTicks;
            if (this.handSwingTicks >= i) {
                this.handSwingTicks = 0;
                this.isHandSwinging = false;
            }
        } else {
            this.handSwingTicks = 0;
        }
        this.handSwingProgress = (float)this.handSwingTicks / (float)i;
    }

    public EntityAttributeInstance getAttributeInstance(EntityAttribute attribute) {
        return this.getAttributes().get(attribute);
    }

    public AbstractEntityAttributeContainer getAttributes() {
        if (this.attributes == null) {
            this.attributes = new EntityAttributeContainer();
        }
        return this.attributes;
    }

    public EntityGroup getGroup() {
        return EntityGroup.DEFAULT;
    }

    public ItemStack getMainHandStack() {
        return this.getEquippedStack(EquipmentSlot.MAINHAND);
    }

    public ItemStack getOffHandStack() {
        return this.getEquippedStack(EquipmentSlot.OFFHAND);
    }

    public ItemStack getStackInHand(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return this.getEquippedStack(EquipmentSlot.MAINHAND);
        }
        if (hand == Hand.OFF_HAND) {
            return this.getEquippedStack(EquipmentSlot.OFFHAND);
        }
        throw new IllegalArgumentException("Invalid hand " + (Object)((Object)hand));
    }

    public void setStackInHand(Hand hand, ItemStack stack) {
        if (hand == Hand.MAIN_HAND) {
            this.equipStack(EquipmentSlot.MAINHAND, stack);
        } else if (hand == Hand.OFF_HAND) {
            this.equipStack(EquipmentSlot.OFFHAND, stack);
        } else {
            throw new IllegalArgumentException("Invalid hand " + (Object)((Object)hand));
        }
    }

    public boolean hasStackEquipped(EquipmentSlot slot) {
        return !this.getEquippedStack(slot).isEmpty();
    }

    @Override
    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Override
    public abstract void equipStack(EquipmentSlot var1, ItemStack var2);

    public float method_18396() {
        Iterable<ItemStack> iterable = this.getArmorItems();
        int i = 0;
        int j = 0;
        for (ItemStack itemStack : iterable) {
            if (!itemStack.isEmpty()) {
                ++j;
            }
            ++i;
        }
        return i > 0 ? (float)j / (float)i : 0.0f;
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (entityAttributeInstance.getModifier(ATTR_SPRINTING_SPEED_BOOST_ID) != null) {
            entityAttributeInstance.removeModifier(ATTR_SPRINTING_SPEED_BOOST);
        }
        if (sprinting) {
            entityAttributeInstance.addModifier(ATTR_SPRINTING_SPEED_BOOST);
        }
    }

    protected float getSoundVolume() {
        return 1.0f;
    }

    protected float getSoundPitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.5f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }

    protected boolean isImmobile() {
        return this.getHealth() <= 0.0f;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (!this.isSleeping()) {
            super.pushAwayFrom(entity);
        }
    }

    public void method_6038(Entity entity) {
        if (entity instanceof BoatEntity || entity instanceof HorseBaseEntity) {
            double d = (double)(this.getWidth() / 2.0f + entity.getWidth() / 2.0f) + 0.4;
            float f = entity instanceof BoatEntity ? 0.0f : 1.5707964f * (float)(this.getMainArm() == Arm.RIGHT ? -1 : 1);
            float g = -MathHelper.sin(-this.yaw * ((float)Math.PI / 180) - (float)Math.PI + f);
            float h = -MathHelper.cos(-this.yaw * ((float)Math.PI / 180) - (float)Math.PI + f);
            double e = Math.abs(g) > Math.abs(h) ? d / (double)Math.abs(g) : d / (double)Math.abs(h);
            double i = this.x + (double)g * e;
            double j = this.z + (double)h * e;
            this.updatePosition(i, entity.y + (double)entity.getHeight() + 0.001, j);
            if (this.world.doesNotCollide(this, this.getBoundingBox().union(entity.getBoundingBox()))) {
                return;
            }
            this.updatePosition(i, entity.y + (double)entity.getHeight() + 1.001, j);
            if (this.world.doesNotCollide(this, this.getBoundingBox().union(entity.getBoundingBox()))) {
                return;
            }
            this.updatePosition(entity.x, entity.y + (double)this.getHeight() + 0.001, entity.z);
            return;
        }
        double k = entity.x;
        double l = entity.getBoundingBox().y1 + (double)entity.getHeight();
        double e = entity.z;
        Direction direction = entity.getMovementDirection();
        if (direction != null) {
            Direction direction2 = direction.rotateYClockwise();
            int[][] is = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
            double m = Math.floor(this.x) + 0.5;
            double n = Math.floor(this.z) + 0.5;
            double o = this.getBoundingBox().x2 - this.getBoundingBox().x1;
            double p = this.getBoundingBox().z2 - this.getBoundingBox().z1;
            Box box = new Box(m - o / 2.0, entity.getBoundingBox().y1, n - p / 2.0, m + o / 2.0, Math.floor(entity.getBoundingBox().y1) + (double)this.getHeight(), n + p / 2.0);
            for (int[] js : is) {
                BlockPos blockPos;
                double q = direction.getOffsetX() * js[0] + direction2.getOffsetX() * js[1];
                double r = direction.getOffsetZ() * js[0] + direction2.getOffsetZ() * js[1];
                double s = m + q;
                double t = n + r;
                Box box2 = box.offset(q, 0.0, r);
                if (this.world.doesNotCollide(this, box2)) {
                    blockPos = new BlockPos(s, this.y, t);
                    if (this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, this)) {
                        this.requestTeleport(s, this.y + 1.0, t);
                        return;
                    }
                    BlockPos blockPos2 = new BlockPos(s, this.y - 1.0, t);
                    if (!this.world.getBlockState(blockPos2).hasSolidTopSurface(this.world, blockPos2, this) && !this.world.getFluidState(blockPos2).matches(FluidTags.WATER)) continue;
                    k = s;
                    l = this.y + 1.0;
                    e = t;
                    continue;
                }
                blockPos = new BlockPos(s, this.y + 1.0, t);
                if (!this.world.doesNotCollide(this, box2.offset(0.0, 1.0, 0.0)) || !this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, this)) continue;
                k = s;
                l = this.y + 2.0;
                e = t;
            }
        }
        this.requestTeleport(k, l, e);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }

    protected float getJumpVelocity() {
        return 0.42f;
    }

    protected void jump() {
        float f = this.hasStatusEffect(StatusEffects.JUMP_BOOST) ? this.getJumpVelocity() + 0.1f * (float)(this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1) : this.getJumpVelocity();
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x, f, vec3d.z);
        if (this.isSprinting()) {
            float g = this.yaw * ((float)Math.PI / 180);
            this.setVelocity(this.getVelocity().add(-MathHelper.sin(g) * 0.2f, 0.0, MathHelper.cos(g) * 0.2f));
        }
        this.velocityDirty = true;
    }

    @Environment(value=EnvType.CLIENT)
    protected void method_6093() {
        this.setVelocity(this.getVelocity().add(0.0, -0.04f, 0.0));
    }

    protected void swimUpward(Tag<Fluid> fluid) {
        this.setVelocity(this.getVelocity().add(0.0, 0.04f, 0.0));
    }

    protected float getBaseMovementSpeedMultiplier() {
        return 0.8f;
    }

    public void travel(Vec3d movementInput) {
        double r;
        double d;
        if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
            boolean bl;
            d = 0.08;
            boolean bl2 = bl = this.getVelocity().y <= 0.0;
            if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                d = 0.01;
                this.fallDistance = 0.0f;
            }
            if (!(!this.isTouchingWater() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying)) {
                Vec3d vec3d2;
                double e = this.y;
                float f = this.isSprinting() ? 0.9f : this.getBaseMovementSpeedMultiplier();
                float g = 0.02f;
                float h = EnchantmentHelper.getDepthStrider(this);
                if (h > 3.0f) {
                    h = 3.0f;
                }
                if (!this.onGround) {
                    h *= 0.5f;
                }
                if (h > 0.0f) {
                    f += (0.54600006f - f) * h / 3.0f;
                    g += (this.getMovementSpeed() - g) * h / 3.0f;
                }
                if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                    f = 0.96f;
                }
                this.updateVelocity(g, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d = this.getVelocity();
                if (this.horizontalCollision && this.isClimbing()) {
                    vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
                }
                this.setVelocity(vec3d.multiply(f, 0.8f, f));
                if (!this.hasNoGravity() && !this.isSprinting()) {
                    vec3d2 = this.getVelocity();
                    double i = bl && Math.abs(vec3d2.y - 0.005) >= 0.003 && Math.abs(vec3d2.y - d / 16.0) < 0.003 ? -0.003 : vec3d2.y - d / 16.0;
                    this.setVelocity(vec3d2.x, i, vec3d2.z);
                }
                vec3d2 = this.getVelocity();
                if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + (double)0.6f - this.y + e, vec3d2.z)) {
                    this.setVelocity(vec3d2.x, 0.3f, vec3d2.z);
                }
            } else if (!(!this.isInLava() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying)) {
                double e = this.y;
                this.updateVelocity(0.02f, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5));
                if (!this.hasNoGravity()) {
                    this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
                }
                Vec3d vec3d3 = this.getVelocity();
                if (this.horizontalCollision && this.doesNotCollide(vec3d3.x, vec3d3.y + (double)0.6f - this.y + e, vec3d3.z)) {
                    this.setVelocity(vec3d3.x, 0.3f, vec3d3.z);
                }
            } else if (this.isFallFlying()) {
                double n;
                float o;
                double m;
                Vec3d vec3d4 = this.getVelocity();
                if (vec3d4.y > -0.5) {
                    this.fallDistance = 1.0f;
                }
                Vec3d vec3d5 = this.getRotationVector();
                float f = this.pitch * ((float)Math.PI / 180);
                double j = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
                double k = Math.sqrt(LivingEntity.squaredHorizontalLength(vec3d4));
                double i = vec3d5.length();
                float l = MathHelper.cos(f);
                l = (float)((double)l * ((double)l * Math.min(1.0, i / 0.4)));
                vec3d4 = this.getVelocity().add(0.0, d * (-1.0 + (double)l * 0.75), 0.0);
                if (vec3d4.y < 0.0 && j > 0.0) {
                    m = vec3d4.y * -0.1 * (double)l;
                    vec3d4 = vec3d4.add(vec3d5.x * m / j, m, vec3d5.z * m / j);
                }
                if (f < 0.0f && j > 0.0) {
                    m = k * (double)(-MathHelper.sin(f)) * 0.04;
                    vec3d4 = vec3d4.add(-vec3d5.x * m / j, m * 3.2, -vec3d5.z * m / j);
                }
                if (j > 0.0) {
                    vec3d4 = vec3d4.add((vec3d5.x / j * k - vec3d4.x) * 0.1, 0.0, (vec3d5.z / j * k - vec3d4.z) * 0.1);
                }
                this.setVelocity(vec3d4.multiply(0.99f, 0.98f, 0.99f));
                this.move(MovementType.SELF, this.getVelocity());
                if (this.horizontalCollision && !this.world.isClient && (o = (float)((n = k - (m = Math.sqrt(LivingEntity.squaredHorizontalLength(this.getVelocity())))) * 10.0 - 3.0)) > 0.0f) {
                    this.playSound(this.getFallSound((int)o), 1.0f, 1.0f);
                    this.damage(DamageSource.FLY_INTO_WALL, o);
                }
                if (this.onGround && !this.world.isClient) {
                    this.setFlag(7, false);
                }
            } else {
                BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().y1 - 1.0, this.z);
                float p = this.world.getBlockState(blockPos).getBlock().getSlipperiness();
                float f = this.onGround ? p * 0.91f : 0.91f;
                this.updateVelocity(this.method_18802(p), movementInput);
                this.setVelocity(this.method_18801(this.getVelocity()));
                this.move(MovementType.SELF, this.getVelocity());
                Vec3d vec3d6 = this.getVelocity();
                if ((this.horizontalCollision || this.jumping) && this.isClimbing()) {
                    vec3d6 = new Vec3d(vec3d6.x, 0.2, vec3d6.z);
                }
                double q = vec3d6.y;
                if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
                    q += (0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2;
                    this.fallDistance = 0.0f;
                } else if (!this.world.isClient || this.world.isBlockLoaded(blockPos)) {
                    if (!this.hasNoGravity()) {
                        q -= d;
                    }
                } else {
                    q = this.y > 0.0 ? -0.1 : 0.0;
                }
                this.setVelocity(vec3d6.x * (double)f, q * (double)0.98f, vec3d6.z * (double)f);
            }
        }
        this.lastLimbDistance = this.limbDistance;
        d = this.x - this.prevX;
        double s = this instanceof Flutterer ? this.y - this.prevY : 0.0;
        float g = MathHelper.sqrt(d * d + s * s + (r = this.z - this.prevZ) * r) * 4.0f;
        if (g > 1.0f) {
            g = 1.0f;
        }
        this.limbDistance += (g - this.limbDistance) * 0.4f;
        this.limbAngle += this.limbDistance;
    }

    private Vec3d method_18801(Vec3d vec3d) {
        if (this.isClimbing()) {
            this.fallDistance = 0.0f;
            float f = 0.15f;
            double d = MathHelper.clamp(vec3d.x, (double)-0.15f, (double)0.15f);
            double e = MathHelper.clamp(vec3d.z, (double)-0.15f, (double)0.15f);
            double g = Math.max(vec3d.y, (double)-0.15f);
            if (g < 0.0 && this.method_16212().getBlock() != Blocks.SCAFFOLDING && this.isSneaking() && this instanceof PlayerEntity) {
                g = 0.0;
            }
            vec3d = new Vec3d(d, g, e);
        }
        return vec3d;
    }

    private float method_18802(float f) {
        if (this.onGround) {
            return this.getMovementSpeed() * (0.21600002f / (f * f * f));
        }
        return this.field_6281;
    }

    public float getMovementSpeed() {
        return this.movementSpeed;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public boolean tryAttack(Entity target) {
        this.onAttacking(target);
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.method_6076();
        this.method_6072();
        if (!this.world.isClient) {
            int i = this.getStuckArrowCount();
            if (i > 0) {
                if (this.stuckArrowTimer <= 0) {
                    this.stuckArrowTimer = 20 * (30 - i);
                }
                --this.stuckArrowTimer;
                if (this.stuckArrowTimer <= 0) {
                    this.setStuckArrowCount(i - 1);
                }
            }
            block8: for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack;
                switch (equipmentSlot.getType()) {
                    case HAND: {
                        itemStack = this.equippedHand.get(equipmentSlot.getEntitySlotId());
                        break;
                    }
                    case ARMOR: {
                        itemStack = this.equippedArmor.get(equipmentSlot.getEntitySlotId());
                        break;
                    }
                    default: {
                        continue block8;
                    }
                }
                ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
                if (ItemStack.areEqualIgnoreDamage(itemStack2, itemStack)) continue;
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), equipmentSlot, itemStack2));
                if (!itemStack.isEmpty()) {
                    this.getAttributes().removeAll(itemStack.getAttributeModifiers(equipmentSlot));
                }
                if (!itemStack2.isEmpty()) {
                    this.getAttributes().replaceAll(itemStack2.getAttributeModifiers(equipmentSlot));
                }
                switch (equipmentSlot.getType()) {
                    case HAND: {
                        this.equippedHand.set(equipmentSlot.getEntitySlotId(), itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy());
                        continue block8;
                    }
                    case ARMOR: {
                        this.equippedArmor.set(equipmentSlot.getEntitySlotId(), itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy());
                    }
                }
            }
            if (this.age % 20 == 0) {
                this.getDamageTracker().update();
            }
            if (!this.glowing) {
                boolean bl = this.hasStatusEffect(StatusEffects.GLOWING);
                if (this.getFlag(6) != bl) {
                    this.setFlag(6, bl);
                }
            }
            if (this.isSleeping() && !this.isSleepingInBed()) {
                this.wakeUp();
            }
        }
        this.tickMovement();
        double d = this.x - this.prevX;
        double e = this.z - this.prevZ;
        float f = (float)(d * d + e * e);
        float g = this.field_6283;
        float h = 0.0f;
        this.field_6217 = this.field_6233;
        float j = 0.0f;
        if (f > 0.0025000002f) {
            j = 1.0f;
            h = (float)Math.sqrt(f) * 3.0f;
            float k = (float)MathHelper.atan2(e, d) * 57.295776f - 90.0f;
            float l = MathHelper.abs(MathHelper.wrapDegrees(this.yaw) - k);
            g = 95.0f < l && l < 265.0f ? k - 180.0f : k;
        }
        if (this.handSwingProgress > 0.0f) {
            g = this.yaw;
        }
        if (!this.onGround) {
            j = 0.0f;
        }
        this.field_6233 += (j - this.field_6233) * 0.3f;
        this.world.getProfiler().push("headTurn");
        h = this.turnHead(g, h);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("rangeChecks");
        while (this.yaw - this.prevYaw < -180.0f) {
            this.prevYaw -= 360.0f;
        }
        while (this.yaw - this.prevYaw >= 180.0f) {
            this.prevYaw += 360.0f;
        }
        while (this.field_6283 - this.field_6220 < -180.0f) {
            this.field_6220 -= 360.0f;
        }
        while (this.field_6283 - this.field_6220 >= 180.0f) {
            this.field_6220 += 360.0f;
        }
        while (this.pitch - this.prevPitch < -180.0f) {
            this.prevPitch -= 360.0f;
        }
        while (this.pitch - this.prevPitch >= 180.0f) {
            this.prevPitch += 360.0f;
        }
        while (this.headYaw - this.prevHeadYaw < -180.0f) {
            this.prevHeadYaw -= 360.0f;
        }
        while (this.headYaw - this.prevHeadYaw >= 180.0f) {
            this.prevHeadYaw += 360.0f;
        }
        this.world.getProfiler().pop();
        this.field_6255 += h;
        this.field_6239 = this.isFallFlying() ? ++this.field_6239 : 0;
        if (this.isSleeping()) {
            this.pitch = 0.0f;
        }
    }

    protected float turnHead(float bodyRotation, float headRotation) {
        boolean bl;
        float f = MathHelper.wrapDegrees(bodyRotation - this.field_6283);
        this.field_6283 += f * 0.3f;
        float g = MathHelper.wrapDegrees(this.yaw - this.field_6283);
        boolean bl2 = bl = g < -90.0f || g >= 90.0f;
        if (g < -75.0f) {
            g = -75.0f;
        }
        if (g >= 75.0f) {
            g = 75.0f;
        }
        this.field_6283 = this.yaw - g;
        if (g * g > 2500.0f) {
            this.field_6283 += g * 0.2f;
        }
        if (bl) {
            headRotation *= -1.0f;
        }
        return headRotation;
    }

    public void tickMovement() {
        if (this.field_6228 > 0) {
            --this.field_6228;
        }
        if (this.field_6210 > 0 && !this.isLogicalSideForUpdatingMovement()) {
            double d = this.x + (this.field_6224 - this.x) / (double)this.field_6210;
            double e = this.y + (this.field_6245 - this.y) / (double)this.field_6210;
            double f = this.z + (this.field_6263 - this.z) / (double)this.field_6210;
            double g = MathHelper.wrapDegrees(this.field_6284 - (double)this.yaw);
            this.yaw = (float)((double)this.yaw + g / (double)this.field_6210);
            this.pitch = (float)((double)this.pitch + (this.field_6221 - (double)this.pitch) / (double)this.field_6210);
            --this.field_6210;
            this.updatePosition(d, e, f);
            this.setRotation(this.yaw, this.pitch);
        } else if (!this.canMoveVoluntarily()) {
            this.setVelocity(this.getVelocity().multiply(0.98));
        }
        if (this.field_6265 > 0) {
            this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.field_6242 - (double)this.headYaw) / (double)this.field_6265);
            --this.field_6265;
        }
        Vec3d vec3d = this.getVelocity();
        double h = vec3d.x;
        double i = vec3d.y;
        double j = vec3d.z;
        if (Math.abs(vec3d.x) < 0.003) {
            h = 0.0;
        }
        if (Math.abs(vec3d.y) < 0.003) {
            i = 0.0;
        }
        if (Math.abs(vec3d.z) < 0.003) {
            j = 0.0;
        }
        this.setVelocity(h, i, j);
        this.world.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0f;
            this.forwardSpeed = 0.0f;
            this.field_6267 = 0.0f;
        } else if (this.canMoveVoluntarily()) {
            this.world.getProfiler().push("newAi");
            this.tickNewAi();
            this.world.getProfiler().pop();
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("jump");
        if (this.jumping) {
            if (this.waterHeight > 0.0 && (!this.onGround || this.waterHeight > 0.4)) {
                this.swimUpward(FluidTags.WATER);
            } else if (this.isInLava()) {
                this.swimUpward(FluidTags.LAVA);
            } else if ((this.onGround || this.waterHeight > 0.0 && this.waterHeight <= 0.4) && this.field_6228 == 0) {
                this.jump();
                this.field_6228 = 10;
            }
        } else {
            this.field_6228 = 0;
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("travel");
        this.sidewaysSpeed *= 0.98f;
        this.forwardSpeed *= 0.98f;
        this.field_6267 *= 0.9f;
        this.initAi();
        Box box = this.getBoundingBox();
        this.travel(new Vec3d(this.sidewaysSpeed, this.upwardSpeed, this.forwardSpeed));
        this.world.getProfiler().pop();
        this.world.getProfiler().push("push");
        if (this.field_6261 > 0) {
            --this.field_6261;
            this.method_6035(box, this.getBoundingBox());
        }
        this.tickCramming();
        this.world.getProfiler().pop();
    }

    private void initAi() {
        boolean bl = this.getFlag(7);
        if (bl && !this.onGround && !this.hasVehicle()) {
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemStack)) {
                bl = true;
                if (!this.world.isClient && (this.field_6239 + 1) % 20 == 0) {
                    itemStack.damage(1, this, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
                }
            } else {
                bl = false;
            }
        } else {
            bl = false;
        }
        if (!this.world.isClient) {
            this.setFlag(7, bl);
        }
    }

    protected void tickNewAi() {
    }

    protected void tickCramming() {
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            int j;
            int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                j = 0;
                for (int k = 0; k < list.size(); ++k) {
                    if (list.get(k).hasVehicle()) continue;
                    ++j;
                }
                if (j > i - 1) {
                    this.damage(DamageSource.CRAMMING, 6.0f);
                }
            }
            for (j = 0; j < list.size(); ++j) {
                Entity entity = list.get(j);
                this.pushAway(entity);
            }
        }
    }

    protected void method_6035(Box box, Box box2) {
        Box box3 = box.union(box2);
        List<Entity> list = this.world.getEntities(this, box3);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (!(entity instanceof LivingEntity)) continue;
                this.attackLivingEntity((LivingEntity)entity);
                this.field_6261 = 0;
                this.setVelocity(this.getVelocity().multiply(-0.2));
                break;
            }
        } else if (this.horizontalCollision) {
            this.field_6261 = 0;
        }
        if (!this.world.isClient && this.field_6261 <= 0) {
            this.setLivingFlag(4, false);
        }
    }

    protected void pushAway(Entity entity) {
        entity.pushAwayFrom(this);
    }

    protected void attackLivingEntity(LivingEntity target) {
    }

    public void method_6018(int i) {
        this.field_6261 = i;
        if (!this.world.isClient) {
            this.setLivingFlag(4, true);
        }
    }

    public boolean isUsingRiptide() {
        return (this.dataTracker.get(LIVING_FLAGS) & 4) != 0;
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();
        super.stopRiding();
        if (entity != null && entity != this.getVehicle() && !this.world.isClient) {
            this.method_6038(entity);
        }
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        this.field_6217 = this.field_6233;
        this.field_6233 = 0.0f;
        this.fallDistance = 0.0f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.field_6224 = x;
        this.field_6245 = y;
        this.field_6263 = z;
        this.field_6284 = yaw;
        this.field_6221 = pitch;
        this.field_6210 = interpolationSteps;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
        this.field_6242 = yaw;
        this.field_6265 = interpolationSteps;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public void sendPickup(Entity item, int count) {
        if (!item.removed && !this.world.isClient && (item instanceof ItemEntity || item instanceof ProjectileEntity || item instanceof ExperienceOrbEntity)) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(item, new ItemPickupAnimationS2CPacket(item.getEntityId(), this.getEntityId(), count));
        }
    }

    public boolean canSee(Entity entity) {
        Vec3d vec3d2;
        Vec3d vec3d = new Vec3d(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
        return this.world.rayTrace(new RayTraceContext(vec3d, vec3d2 = new Vec3d(entity.x, entity.y + (double)entity.getStandingEyeHeight(), entity.z), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public float getYaw(float tickDelta) {
        if (tickDelta == 1.0f) {
            return this.headYaw;
        }
        return MathHelper.lerp(tickDelta, this.prevHeadYaw, this.headYaw);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHandSwingProgress(float tickDelta) {
        float f = this.handSwingProgress - this.lastHandSwingProgress;
        if (f < 0.0f) {
            f += 1.0f;
        }
        return this.lastHandSwingProgress + f * tickDelta;
    }

    public boolean canMoveVoluntarily() {
        return !this.world.isClient;
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    public boolean isPushable() {
        return this.isAlive() && !this.isClimbing();
    }

    @Override
    protected void scheduleVelocityUpdate() {
        this.velocityModified = this.random.nextDouble() >= this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).getValue();
    }

    @Override
    public float getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public void setHeadYaw(float headYaw) {
        this.headYaw = headYaw;
    }

    @Override
    public void setYaw(float yaw) {
        this.field_6283 = yaw;
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float amount) {
        if (amount < 0.0f) {
            amount = 0.0f;
        }
        this.absorptionAmount = amount;
    }

    public void method_6000() {
    }

    public void method_6044() {
    }

    protected void method_6008() {
        this.field_6285 = true;
    }

    public abstract Arm getMainArm();

    public boolean isUsingItem() {
        return (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
    }

    public Hand getActiveHand() {
        return (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    private void method_6076() {
        if (this.isUsingItem()) {
            if (ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack)) {
                this.activeItemStack.usageTick(this.world, this, this.getItemUseTimeLeft());
                if (this.getItemUseTimeLeft() <= 25 && this.getItemUseTimeLeft() % 4 == 0) {
                    this.spawnConsumptionEffects(this.activeItemStack, 5);
                }
                if (--this.itemUseTimeLeft == 0 && !this.world.isClient && !this.activeItemStack.isUsedOnRelease()) {
                    this.method_6040();
                }
            } else {
                this.clearActiveItem();
            }
        }
    }

    private void method_6072() {
        this.field_6264 = this.field_6243;
        this.field_6243 = this.isInSwimmingPose() ? Math.min(1.0f, this.field_6243 + 0.09f) : Math.max(0.0f, this.field_6243 - 0.09f);
    }

    protected void setLivingFlag(int mask, boolean value) {
        int i = this.dataTracker.get(LIVING_FLAGS).byteValue();
        i = value ? (i |= mask) : (i &= ~mask);
        this.dataTracker.set(LIVING_FLAGS, (byte)i);
    }

    public void setCurrentHand(Hand hand) {
        ItemStack itemStack = this.getStackInHand(hand);
        if (itemStack.isEmpty() || this.isUsingItem()) {
            return;
        }
        this.activeItemStack = itemStack;
        this.itemUseTimeLeft = itemStack.getMaxUseTime();
        if (!this.world.isClient) {
            this.setLivingFlag(1, true);
            this.setLivingFlag(2, hand == Hand.OFF_HAND);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (SLEEPING_POSITION.equals(data)) {
            if (this.world.isClient) {
                this.getSleepingPosition().ifPresent(this::setPositionInBed);
            }
        } else if (LIVING_FLAGS.equals(data) && this.world.isClient) {
            if (this.isUsingItem() && this.activeItemStack.isEmpty()) {
                this.activeItemStack = this.getStackInHand(this.getActiveHand());
                if (!this.activeItemStack.isEmpty()) {
                    this.itemUseTimeLeft = this.activeItemStack.getMaxUseTime();
                }
            } else if (!this.isUsingItem() && !this.activeItemStack.isEmpty()) {
                this.activeItemStack = ItemStack.EMPTY;
                this.itemUseTimeLeft = 0;
            }
        }
    }

    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        super.lookAt(anchorPoint, target);
        this.prevHeadYaw = this.headYaw;
        this.field_6220 = this.field_6283 = this.headYaw;
    }

    protected void spawnConsumptionEffects(ItemStack stack, int particleCount) {
        if (stack.isEmpty() || !this.isUsingItem()) {
            return;
        }
        if (stack.getUseAction() == UseAction.DRINK) {
            this.playSound(this.getDrinkSound(stack), 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
        }
        if (stack.getUseAction() == UseAction.EAT) {
            this.spawnItemParticles(stack, particleCount);
            this.playSound(this.getEatSound(stack), 0.5f + 0.5f * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
    }

    private void spawnItemParticles(ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3d = vec3d.rotateX(-this.pitch * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(-this.yaw * ((float)Math.PI / 180));
            double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
            Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec3d2 = vec3d2.rotateX(-this.pitch * ((float)Math.PI / 180));
            vec3d2 = vec3d2.rotateY(-this.yaw * ((float)Math.PI / 180));
            vec3d2 = vec3d2.add(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
            this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
        }
    }

    protected void method_6040() {
        if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
            this.spawnConsumptionEffects(this.activeItemStack, 16);
            this.setStackInHand(this.getActiveHand(), this.activeItemStack.finishUsing(this.world, this));
            this.clearActiveItem();
        }
    }

    public ItemStack getActiveItem() {
        return this.activeItemStack;
    }

    public int getItemUseTimeLeft() {
        return this.itemUseTimeLeft;
    }

    public int getItemUseTime() {
        if (this.isUsingItem()) {
            return this.activeItemStack.getMaxUseTime() - this.getItemUseTimeLeft();
        }
        return 0;
    }

    public void stopUsingItem() {
        if (!this.activeItemStack.isEmpty()) {
            this.activeItemStack.onStoppedUsing(this.world, this, this.getItemUseTimeLeft());
            if (this.activeItemStack.isUsedOnRelease()) {
                this.method_6076();
            }
        }
        this.clearActiveItem();
    }

    public void clearActiveItem() {
        if (!this.world.isClient) {
            this.setLivingFlag(1, false);
        }
        this.activeItemStack = ItemStack.EMPTY;
        this.itemUseTimeLeft = 0;
    }

    public boolean method_6039() {
        if (!this.isUsingItem() || this.activeItemStack.isEmpty()) {
            return false;
        }
        Item item = this.activeItemStack.getItem();
        if (item.getUseAction(this.activeItemStack) != UseAction.BLOCK) {
            return false;
        }
        return item.getMaxUseTime(this.activeItemStack) - this.itemUseTimeLeft >= 5;
    }

    public boolean isFallFlying() {
        return this.getFlag(7);
    }

    @Override
    public boolean isInSwimmingPose() {
        return super.isInSwimmingPose() || !this.isFallFlying() && this.getPose() == EntityPose.FALL_FLYING;
    }

    @Environment(value=EnvType.CLIENT)
    public int method_6003() {
        return this.field_6239;
    }

    public boolean teleport(double x, double y, double z, boolean particleEffects) {
        double d = this.x;
        double e = this.y;
        double f = this.z;
        this.x = x;
        this.y = y;
        this.z = z;
        boolean bl = false;
        World world = this.world;
        BlockPos blockPos = new BlockPos(this);
        if (world.isBlockLoaded(blockPos)) {
            boolean bl2 = false;
            while (!bl2 && blockPos.getY() > 0) {
                BlockPos blockPos2 = blockPos.down();
                BlockState blockState = world.getBlockState(blockPos2);
                if (blockState.getMaterial().blocksMovement()) {
                    bl2 = true;
                    continue;
                }
                this.y -= 1.0;
                blockPos = blockPos2;
            }
            if (bl2) {
                this.requestTeleport(this.x, this.y, this.z);
                if (world.doesNotCollide(this) && !world.intersectsFluid(this.getBoundingBox())) {
                    bl = true;
                }
            }
        }
        if (!bl) {
            this.requestTeleport(d, e, f);
            return false;
        }
        if (particleEffects) {
            world.sendEntityStatus(this, (byte)46);
        }
        if (this instanceof MobEntityWithAi) {
            ((MobEntityWithAi)this).getNavigation().stop();
        }
        return true;
    }

    public boolean isAffectedBySplashPotions() {
        return true;
    }

    public boolean method_6102() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
    }

    public boolean canPickUp(ItemStack stack) {
        return false;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new MobSpawnS2CPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return pose == EntityPose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions(pose).scaled(this.getScaleFactor());
    }

    public Optional<BlockPos> getSleepingPosition() {
        return this.dataTracker.get(SLEEPING_POSITION);
    }

    public void setSleepingPosition(BlockPos pos) {
        this.dataTracker.set(SLEEPING_POSITION, Optional.of(pos));
    }

    public void clearSleepingPosition() {
        this.dataTracker.set(SLEEPING_POSITION, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getSleepingPosition().isPresent();
    }

    public void sleep(BlockPos pos) {
        BlockState blockState;
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        if ((blockState = this.world.getBlockState(pos)).getBlock() instanceof BedBlock) {
            this.world.setBlockState(pos, (BlockState)blockState.with(BedBlock.OCCUPIED, true), 3);
        }
        this.setPose(EntityPose.SLEEPING);
        this.setPositionInBed(pos);
        this.setSleepingPosition(pos);
        this.setVelocity(Vec3d.ZERO);
        this.velocityDirty = true;
    }

    private void setPositionInBed(BlockPos pos) {
        this.updatePosition((double)pos.getX() + 0.5, (float)pos.getY() + 0.6875f, (double)pos.getZ() + 0.5);
    }

    private boolean isSleepingInBed() {
        return this.getSleepingPosition().map(blockPos -> this.world.getBlockState((BlockPos)blockPos).getBlock() instanceof BedBlock).orElse(false);
    }

    public void wakeUp() {
        this.getSleepingPosition().filter(this.world::isBlockLoaded).ifPresent(blockPos -> {
            BlockState blockState = this.world.getBlockState((BlockPos)blockPos);
            if (blockState.getBlock() instanceof BedBlock) {
                this.world.setBlockState((BlockPos)blockPos, (BlockState)blockState.with(BedBlock.OCCUPIED, false), 3);
                Vec3d vec3d = BedBlock.findWakeUpPosition(this.getType(), this.world, blockPos, 0).orElseGet(() -> {
                    BlockPos blockPos2 = blockPos.up();
                    return new Vec3d((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.1, (double)blockPos2.getZ() + 0.5);
                });
                this.updatePosition(vec3d.x, vec3d.y, vec3d.z);
            }
        });
        this.setPose(EntityPose.STANDING);
        this.clearSleepingPosition();
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Direction getSleepingDirection() {
        BlockPos blockPos = this.getSleepingPosition().orElse(null);
        return blockPos != null ? BedBlock.getDirection(this.world, blockPos) : null;
    }

    @Override
    public boolean isInsideWall() {
        return !this.isSleeping() && super.isInsideWall();
    }

    @Override
    protected final float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return pose == EntityPose.SLEEPING ? 0.2f : this.getActiveEyeHeight(pose, dimensions);
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return super.getEyeHeight(pose, dimensions);
    }

    public ItemStack getArrowType(ItemStack itemStack) {
        return ItemStack.EMPTY;
    }

    public ItemStack eatFood(World world, ItemStack stack) {
        if (stack.isFood()) {
            world.playSound(null, this.x, this.y, this.z, this.getEatSound(stack), SoundCategory.NEUTRAL, 1.0f, 1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f);
            this.applyFoodEffects(stack, world, this);
            stack.decrement(1);
        }
        return stack;
    }

    private void applyFoodEffects(ItemStack stack, World world, LivingEntity targetEntity) {
        Item item = stack.getItem();
        if (item.isFood()) {
            List<Pair<StatusEffectInstance, Float>> list = item.getFoodComponent().getStatusEffects();
            for (Pair<StatusEffectInstance, Float> pair : list) {
                if (world.isClient || pair.getLeft() == null || !(world.random.nextFloat() < ((Float)pair.getRight()).floatValue())) continue;
                targetEntity.addStatusEffect(new StatusEffectInstance((StatusEffectInstance)pair.getLeft()));
            }
        }
    }

    private static byte getEquipmentBreakStatus(EquipmentSlot slot) {
        switch (slot) {
            case MAINHAND: {
                return 47;
            }
            case OFFHAND: {
                return 48;
            }
            case HEAD: {
                return 49;
            }
            case CHEST: {
                return 50;
            }
            case FEET: {
                return 52;
            }
            case LEGS: {
                return 51;
            }
        }
        return 47;
    }

    public void sendEquipmentBreakStatus(EquipmentSlot slot) {
        this.world.sendEntityStatus(this, LivingEntity.getEquipmentBreakStatus(slot));
    }

    public void sendToolBreakStatus(Hand hand) {
        this.sendEquipmentBreakStatus(hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }
}

