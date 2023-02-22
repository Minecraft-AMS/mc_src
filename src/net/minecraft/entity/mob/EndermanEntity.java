/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EndermanEntity
extends HostileEntity {
    private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", (double)0.15f, EntityAttributeModifier.Operation.ADDITION).setSerialize(false);
    private static final TrackedData<Optional<BlockState>> CARRIED_BLOCK = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);
    private static final TrackedData<Boolean> ANGRY = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final Predicate<LivingEntity> PLAYER_ENDERMITE_PREDICATE = livingEntity -> livingEntity instanceof EndermiteEntity && ((EndermiteEntity)livingEntity).isPlayerSpawned();
    private int lastAngrySoundAge;
    private int ageWhenTargetSet;

    public EndermanEntity(EntityType<? extends EndermanEntity> entityType, World world) {
        super((EntityType<? extends HostileEntity>)entityType, world);
        this.stepHeight = 1.0f;
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ChasePlayerGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal((MobEntityWithAi)this, 1.0, 0.0f));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.goalSelector.add(10, new PlaceBlockGoal(this));
        this.goalSelector.add(11, new PickUpBlockGoal(this));
        this.targetSelector.add(1, new TeleportTowardsPlayerGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new FollowTargetGoal<EndermiteEntity>(this, EndermiteEntity.class, 10, true, false, PLAYER_ENDERMITE_PREDICATE));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3f);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(64.0);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.dataTracker.set(ANGRY, false);
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            this.ageWhenTargetSet = this.age;
            this.dataTracker.set(ANGRY, true);
            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CARRIED_BLOCK, Optional.empty());
        this.dataTracker.startTracking(ANGRY, false);
    }

    public void playAngrySound() {
        if (this.age >= this.lastAngrySoundAge + 400) {
            this.lastAngrySoundAge = this.age;
            if (!this.isSilent()) {
                this.world.playSound(this.x, this.y + (double)this.getStandingEyeHeight(), this.z, SoundEvents.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5f, 1.0f, false);
            }
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (ANGRY.equals(data) && this.isAngry() && this.world.isClient) {
            this.playAngrySound();
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        BlockState blockState = this.getCarriedBlock();
        if (blockState != null) {
            tag.put("carriedBlockState", NbtHelper.fromBlockState(blockState));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        BlockState blockState = null;
        if (tag.contains("carriedBlockState", 10) && (blockState = NbtHelper.toBlockState(tag.getCompound("carriedBlockState"))).isAir()) {
            blockState = null;
        }
        this.setCarriedBlock(blockState);
    }

    private boolean isPlayerStaring(PlayerEntity player) {
        ItemStack itemStack = player.inventory.armor.get(3);
        if (itemStack.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            return false;
        }
        Vec3d vec3d = player.getRotationVec(1.0f).normalize();
        Vec3d vec3d2 = new Vec3d(this.x - player.x, this.getBoundingBox().y1 + (double)this.getStandingEyeHeight() - (player.y + (double)player.getStandingEyeHeight()), this.z - player.z);
        double d = vec3d2.length();
        double e = vec3d.dotProduct(vec3d2 = vec3d2.normalize());
        if (e > 1.0 - 0.025 / d) {
            return player.canSee(this);
        }
        return false;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.55f;
    }

    @Override
    public void tickMovement() {
        if (this.world.isClient) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.PORTAL, this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), this.y + this.random.nextDouble() * (double)this.getHeight() - 0.25, this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(), (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(), (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
        this.jumping = false;
        super.tickMovement();
    }

    @Override
    protected void mobTick() {
        float f;
        if (this.isWet()) {
            this.damage(DamageSource.DROWN, 1.0f);
        }
        if (this.world.isDay() && this.age >= this.ageWhenTargetSet + 600 && (f = this.getBrightnessAtEyes()) > 0.5f && this.world.isSkyVisible(new BlockPos(this)) && this.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f) {
            this.setTarget(null);
            this.teleportRandomly();
        }
        super.mobTick();
    }

    protected boolean teleportRandomly() {
        double d = this.x + (this.random.nextDouble() - 0.5) * 64.0;
        double e = this.y + (double)(this.random.nextInt(64) - 32);
        double f = this.z + (this.random.nextDouble() - 0.5) * 64.0;
        return this.teleport(d, e, f);
    }

    private boolean teleportTo(Entity targetEntity) {
        Vec3d vec3d = new Vec3d(this.x - targetEntity.x, this.getBoundingBox().y1 + (double)(this.getHeight() / 2.0f) - targetEntity.y + (double)targetEntity.getStandingEyeHeight(), this.z - targetEntity.z);
        vec3d = vec3d.normalize();
        double d = 16.0;
        double e = this.x + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.x * 16.0;
        double f = this.y + (double)(this.random.nextInt(16) - 8) - vec3d.y * 16.0;
        double g = this.z + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.z * 16.0;
        return this.teleport(e, f, g);
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);
        while (mutable.getY() > 0 && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
            mutable.setOffset(Direction.DOWN);
        }
        if (!this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
            return false;
        }
        boolean bl = this.teleport(x, y, z, true);
        if (bl) {
            this.world.playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0f, 1.0f);
            this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.isAngry() ? SoundEvents.ENTITY_ENDERMAN_SCREAM : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        BlockState blockState = this.getCarriedBlock();
        if (blockState != null) {
            this.dropItem(blockState.getBlock());
        }
    }

    public void setCarriedBlock(@Nullable BlockState blockState) {
        this.dataTracker.set(CARRIED_BLOCK, Optional.ofNullable(blockState));
    }

    @Nullable
    public BlockState getCarriedBlock() {
        return this.dataTracker.get(CARRIED_BLOCK).orElse(null);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source instanceof ProjectileDamageSource || source == DamageSource.FIREWORKS) {
            for (int i = 0; i < 64; ++i) {
                if (!this.teleportRandomly()) continue;
                return true;
            }
            return false;
        }
        boolean bl = super.damage(source, amount);
        if (source.bypassesArmor() && this.random.nextInt(10) != 0) {
            this.teleportRandomly();
        }
        return bl;
    }

    public boolean isAngry() {
        return this.dataTracker.get(ANGRY);
    }

    static class PickUpBlockGoal
    extends Goal {
        private final EndermanEntity enderman;

        public PickUpBlockGoal(EndermanEntity enderman) {
            this.enderman = enderman;
        }

        @Override
        public boolean canStart() {
            if (this.enderman.getCarriedBlock() != null) {
                return false;
            }
            if (!this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                return false;
            }
            return this.enderman.getRandom().nextInt(20) == 0;
        }

        @Override
        public void tick() {
            boolean bl;
            Vec3d vec3d2;
            Random random = this.enderman.getRandom();
            World world = this.enderman.world;
            int i = MathHelper.floor(this.enderman.x - 2.0 + random.nextDouble() * 4.0);
            int j = MathHelper.floor(this.enderman.y + random.nextDouble() * 3.0);
            int k = MathHelper.floor(this.enderman.z - 2.0 + random.nextDouble() * 4.0);
            BlockPos blockPos = new BlockPos(i, j, k);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            Vec3d vec3d = new Vec3d((double)MathHelper.floor(this.enderman.x) + 0.5, (double)j + 0.5, (double)MathHelper.floor(this.enderman.z) + 0.5);
            BlockHitResult blockHitResult = world.rayTrace(new RayTraceContext(vec3d, vec3d2 = new Vec3d((double)i + 0.5, (double)j + 0.5, (double)k + 0.5), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this.enderman));
            boolean bl2 = bl = blockHitResult.getType() != HitResult.Type.MISS && blockHitResult.getBlockPos().equals(blockPos);
            if (block.matches(BlockTags.ENDERMAN_HOLDABLE) && bl) {
                this.enderman.setCarriedBlock(blockState);
                world.removeBlock(blockPos, false);
            }
        }
    }

    static class PlaceBlockGoal
    extends Goal {
        private final EndermanEntity enderman;

        public PlaceBlockGoal(EndermanEntity enderman) {
            this.enderman = enderman;
        }

        @Override
        public boolean canStart() {
            if (this.enderman.getCarriedBlock() == null) {
                return false;
            }
            if (!this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                return false;
            }
            return this.enderman.getRandom().nextInt(2000) == 0;
        }

        @Override
        public void tick() {
            Random random = this.enderman.getRandom();
            World iWorld = this.enderman.world;
            int i = MathHelper.floor(this.enderman.x - 1.0 + random.nextDouble() * 2.0);
            int j = MathHelper.floor(this.enderman.y + random.nextDouble() * 2.0);
            int k = MathHelper.floor(this.enderman.z - 1.0 + random.nextDouble() * 2.0);
            BlockPos blockPos = new BlockPos(i, j, k);
            BlockState blockState = iWorld.getBlockState(blockPos);
            BlockPos blockPos2 = blockPos.down();
            BlockState blockState2 = iWorld.getBlockState(blockPos2);
            BlockState blockState3 = this.enderman.getCarriedBlock();
            if (blockState3 != null && this.method_7033(iWorld, blockPos, blockState3, blockState, blockState2, blockPos2)) {
                iWorld.setBlockState(blockPos, blockState3, 3);
                this.enderman.setCarriedBlock(null);
            }
        }

        private boolean method_7033(CollisionView collisionView, BlockPos blockPos, BlockState blockState, BlockState blockState2, BlockState blockState3, BlockPos blockPos2) {
            return blockState2.isAir() && !blockState3.isAir() && blockState3.method_21743(collisionView, blockPos2) && blockState.canPlaceAt(collisionView, blockPos);
        }
    }

    static class ChasePlayerGoal
    extends Goal {
        private final EndermanEntity enderman;

        public ChasePlayerGoal(EndermanEntity enderman) {
            this.enderman = enderman;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.enderman.getTarget();
            if (!(livingEntity instanceof PlayerEntity)) {
                return false;
            }
            double d = livingEntity.squaredDistanceTo(this.enderman);
            if (d > 256.0) {
                return false;
            }
            return this.enderman.isPlayerStaring((PlayerEntity)livingEntity);
        }

        @Override
        public void start() {
            this.enderman.getNavigation().stop();
        }
    }

    static class TeleportTowardsPlayerGoal
    extends FollowTargetGoal<PlayerEntity> {
        private final EndermanEntity enderman;
        private PlayerEntity targetPlayer;
        private int lookAtPlayerWarmup;
        private int ticksSinceUnseenTeleport;
        private final TargetPredicate staringPlayerPredicate;
        private final TargetPredicate validTargetPredicate = new TargetPredicate().includeHidden();

        public TeleportTowardsPlayerGoal(EndermanEntity enderman) {
            super((MobEntity)enderman, PlayerEntity.class, false);
            this.enderman = enderman;
            this.staringPlayerPredicate = new TargetPredicate().setBaseMaxDistance(this.getFollowRange()).setPredicate(playerEntity -> enderman.isPlayerStaring((PlayerEntity)playerEntity));
        }

        @Override
        public boolean canStart() {
            this.targetPlayer = this.enderman.world.getClosestPlayer(this.staringPlayerPredicate, this.enderman);
            return this.targetPlayer != null;
        }

        @Override
        public void start() {
            this.lookAtPlayerWarmup = 5;
            this.ticksSinceUnseenTeleport = 0;
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            super.stop();
        }

        @Override
        public boolean shouldContinue() {
            if (this.targetPlayer != null) {
                if (!this.enderman.isPlayerStaring(this.targetPlayer)) {
                    return false;
                }
                this.enderman.lookAtEntity(this.targetPlayer, 10.0f, 10.0f);
                return true;
            }
            if (this.targetEntity != null && this.validTargetPredicate.test(this.enderman, this.targetEntity)) {
                return true;
            }
            return super.shouldContinue();
        }

        @Override
        public void tick() {
            if (this.targetPlayer != null) {
                if (--this.lookAtPlayerWarmup <= 0) {
                    this.targetEntity = this.targetPlayer;
                    this.targetPlayer = null;
                    super.start();
                }
            } else {
                if (this.targetEntity != null && !this.enderman.hasVehicle()) {
                    if (this.enderman.isPlayerStaring((PlayerEntity)this.targetEntity)) {
                        if (this.targetEntity.squaredDistanceTo(this.enderman) < 16.0) {
                            this.enderman.teleportRandomly();
                        }
                        this.ticksSinceUnseenTeleport = 0;
                    } else if (this.targetEntity.squaredDistanceTo(this.enderman) > 256.0 && this.ticksSinceUnseenTeleport++ >= 30 && this.enderman.teleportTo(this.targetEntity)) {
                        this.ticksSinceUnseenTeleport = 0;
                    }
                }
                super.tick();
            }
        }
    }
}
