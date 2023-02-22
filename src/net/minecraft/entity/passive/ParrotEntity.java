/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FlyOntoTreeGoal;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.FollowOwnerFlyingGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SitOnOwnerShoulderGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ParrotEntity
extends TameableShoulderEntity
implements Flutterer {
    private static final TrackedData<Integer> ATTR_VARIANT = DataTracker.registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Predicate<MobEntity> CAN_IMITATE = new Predicate<MobEntity>(){

        @Override
        public boolean test(@Nullable MobEntity mobEntity) {
            return mobEntity != null && MOB_SOUNDS.containsKey(mobEntity.getType());
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((MobEntity)object);
        }
    };
    private static final Item COOKIE = Items.COOKIE;
    private static final Set<Item> TAMING_INGREDIENTS = Sets.newHashSet((Object[])new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
    private static final Map<EntityType<?>, SoundEvent> MOB_SOUNDS = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(EntityType.BLAZE, SoundEvents.ENTITY_PARROT_IMITATE_BLAZE);
        hashMap.put(EntityType.CAVE_SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
        hashMap.put(EntityType.CREEPER, SoundEvents.ENTITY_PARROT_IMITATE_CREEPER);
        hashMap.put(EntityType.DROWNED, SoundEvents.ENTITY_PARROT_IMITATE_DROWNED);
        hashMap.put(EntityType.ELDER_GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
        hashMap.put(EntityType.ENDER_DRAGON, SoundEvents.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
        hashMap.put(EntityType.ENDERMAN, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMAN);
        hashMap.put(EntityType.ENDERMITE, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMITE);
        hashMap.put(EntityType.EVOKER, SoundEvents.ENTITY_PARROT_IMITATE_EVOKER);
        hashMap.put(EntityType.GHAST, SoundEvents.ENTITY_PARROT_IMITATE_GHAST);
        hashMap.put(EntityType.GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_GUARDIAN);
        hashMap.put(EntityType.HUSK, SoundEvents.ENTITY_PARROT_IMITATE_HUSK);
        hashMap.put(EntityType.ILLUSIONER, SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER);
        hashMap.put(EntityType.MAGMA_CUBE, SoundEvents.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
        hashMap.put(EntityType.ZOMBIE_PIGMAN, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_PIGMAN);
        hashMap.put(EntityType.PANDA, SoundEvents.ENTITY_PARROT_IMITATE_PANDA);
        hashMap.put(EntityType.PHANTOM, SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM);
        hashMap.put(EntityType.PILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_PILLAGER);
        hashMap.put(EntityType.POLAR_BEAR, SoundEvents.ENTITY_PARROT_IMITATE_POLAR_BEAR);
        hashMap.put(EntityType.RAVAGER, SoundEvents.ENTITY_PARROT_IMITATE_RAVAGER);
        hashMap.put(EntityType.SHULKER, SoundEvents.ENTITY_PARROT_IMITATE_SHULKER);
        hashMap.put(EntityType.SILVERFISH, SoundEvents.ENTITY_PARROT_IMITATE_SILVERFISH);
        hashMap.put(EntityType.SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_SKELETON);
        hashMap.put(EntityType.SLIME, SoundEvents.ENTITY_PARROT_IMITATE_SLIME);
        hashMap.put(EntityType.SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
        hashMap.put(EntityType.STRAY, SoundEvents.ENTITY_PARROT_IMITATE_STRAY);
        hashMap.put(EntityType.VEX, SoundEvents.ENTITY_PARROT_IMITATE_VEX);
        hashMap.put(EntityType.VINDICATOR, SoundEvents.ENTITY_PARROT_IMITATE_VINDICATOR);
        hashMap.put(EntityType.WITCH, SoundEvents.ENTITY_PARROT_IMITATE_WITCH);
        hashMap.put(EntityType.WITHER, SoundEvents.ENTITY_PARROT_IMITATE_WITHER);
        hashMap.put(EntityType.WITHER_SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
        hashMap.put(EntityType.WOLF, SoundEvents.ENTITY_PARROT_IMITATE_WOLF);
        hashMap.put(EntityType.ZOMBIE, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE);
        hashMap.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
    });
    public float field_6818;
    public float field_6819;
    public float field_6827;
    public float field_6829;
    public float field_6824 = 1.0f;
    private boolean songPlaying;
    private BlockPos songSource;

    public ParrotEntity(EntityType<? extends ParrotEntity> entityType, World world) {
        super((EntityType<? extends TameableShoulderEntity>)entityType, world);
        this.moveControl = new FlightMoveControl(this);
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.setVariant(this.random.nextInt(5));
        return super.initialize(world, difficulty, spawnType, entityData, entityTag);
    }

    @Override
    protected void initGoals() {
        this.sitGoal = new SitGoal(this);
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(2, this.sitGoal);
        this.goalSelector.add(2, new FollowOwnerFlyingGoal(this, 1.0, 5.0f, 1.0f));
        this.goalSelector.add(2, new FlyOntoTreeGoal(this, 1.0));
        this.goalSelector.add(3, new SitOnOwnerShoulderGoal(this));
        this.goalSelector.add(3, new FollowMobGoal(this, 1.0, 3.0f, 7.0f));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributes().register(EntityAttributes.FLYING_SPEED);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(6.0);
        this.getAttributeInstance(EntityAttributes.FLYING_SPEED).setBaseValue(0.4f);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2f);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.6f;
    }

    @Override
    public void tickMovement() {
        ParrotEntity.imitateNearbyMob(this.world, this);
        if (this.songSource == null || !this.songSource.isWithinDistance(this.getPos(), 3.46) || this.world.getBlockState(this.songSource).getBlock() != Blocks.JUKEBOX) {
            this.songPlaying = false;
            this.songSource = null;
        }
        super.tickMovement();
        this.method_6578();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
        this.songSource = songPosition;
        this.songPlaying = playing;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean getSongPlaying() {
        return this.songPlaying;
    }

    private void method_6578() {
        this.field_6829 = this.field_6818;
        this.field_6827 = this.field_6819;
        this.field_6819 = (float)((double)this.field_6819 + (double)(this.onGround || this.hasVehicle() ? -1 : 4) * 0.3);
        this.field_6819 = MathHelper.clamp(this.field_6819, 0.0f, 1.0f);
        if (!this.onGround && this.field_6824 < 1.0f) {
            this.field_6824 = 1.0f;
        }
        this.field_6824 = (float)((double)this.field_6824 * 0.9);
        Vec3d vec3d = this.getVelocity();
        if (!this.onGround && vec3d.y < 0.0) {
            this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
        }
        this.field_6818 += this.field_6824 * 2.0f;
    }

    private static boolean imitateNearbyMob(World world, Entity parrot) {
        MobEntity mobEntity;
        if (!parrot.isAlive() || parrot.isSilent() || world.random.nextInt(50) != 0) {
            return false;
        }
        List<MobEntity> list = world.getEntities(MobEntity.class, parrot.getBoundingBox().expand(20.0), CAN_IMITATE);
        if (!list.isEmpty() && !(mobEntity = list.get(world.random.nextInt(list.size()))).isSilent()) {
            SoundEvent soundEvent = ParrotEntity.getSound(mobEntity.getType());
            world.playSound(null, parrot.x, parrot.y, parrot.z, soundEvent, parrot.getSoundCategory(), 0.7f, ParrotEntity.getSoundPitch(world.random));
            return true;
        }
        return false;
    }

    @Override
    public boolean interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!this.isTamed() && TAMING_INGREDIENTS.contains(itemStack.getItem())) {
            if (!player.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            if (!this.isSilent()) {
                this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }
            if (!this.world.isClient) {
                if (this.random.nextInt(10) == 0) {
                    this.setOwner(player);
                    this.showEmoteParticle(true);
                    this.world.sendEntityStatus(this, (byte)7);
                } else {
                    this.showEmoteParticle(false);
                    this.world.sendEntityStatus(this, (byte)6);
                }
            }
            return true;
        }
        if (itemStack.getItem() == COOKIE) {
            if (!player.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 900));
            if (player.isCreative() || !this.isInvulnerable()) {
                this.damage(DamageSource.player(player), Float.MAX_VALUE);
            }
            return true;
        }
        if (!this.world.isClient && !this.isInAir() && this.isTamed() && this.isOwner(player)) {
            this.sitGoal.setEnabledWithOwner(!this.isSitting());
        }
        return super.interactMob(player, hand);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    public static boolean method_20667(EntityType<ParrotEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        Block block = iWorld.getBlockState(blockPos.down()).getBlock();
        return (block.matches(BlockTags.LEAVES) || block == Blocks.GRASS_BLOCK || block instanceof LogBlock || block == Blocks.AIR) && iWorld.getLightLevel(blockPos, 0) > 8;
    }

    @Override
    public void handleFallDamage(float fallDistance, float damageMultiplier) {
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        return false;
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity mate) {
        return null;
    }

    public static void playMobSound(World world, Entity parrot) {
        if (!parrot.isSilent() && !ParrotEntity.imitateNearbyMob(world, parrot) && world.random.nextInt(200) == 0) {
            world.playSound(null, parrot.x, parrot.y, parrot.z, ParrotEntity.getRandomSound(world.random), parrot.getSoundCategory(), 1.0f, ParrotEntity.getSoundPitch(world.random));
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        return target.damage(DamageSource.mob(this), 3.0f);
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound() {
        return ParrotEntity.getRandomSound(this.random);
    }

    private static SoundEvent getRandomSound(Random random) {
        if (random.nextInt(1000) == 0) {
            ArrayList list = Lists.newArrayList(MOB_SOUNDS.keySet());
            return ParrotEntity.getSound((EntityType)list.get(random.nextInt(list.size())));
        }
        return SoundEvents.ENTITY_PARROT_AMBIENT;
    }

    public static SoundEvent getSound(EntityType<?> imitate) {
        return MOB_SOUNDS.getOrDefault(imitate, SoundEvents.ENTITY_PARROT_AMBIENT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PARROT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15f, 1.0f);
    }

    @Override
    protected float playFlySound(float distance) {
        this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15f, 1.0f);
        return distance + this.field_6819 / 2.0f;
    }

    @Override
    protected boolean method_5776() {
        return true;
    }

    @Override
    protected float getSoundPitch() {
        return ParrotEntity.getSoundPitch(this.random);
    }

    private static float getSoundPitch(Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void pushAway(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return;
        }
        super.pushAway(entity);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.sitGoal != null) {
            this.sitGoal.setEnabledWithOwner(false);
        }
        return super.damage(source, amount);
    }

    public int getVariant() {
        return MathHelper.clamp(this.dataTracker.get(ATTR_VARIANT), 0, 4);
    }

    public void setVariant(int i) {
        this.dataTracker.set(ATTR_VARIANT, i);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTR_VARIANT, 0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setVariant(tag.getInt("Variant"));
    }

    public boolean isInAir() {
        return !this.onGround;
    }
}
