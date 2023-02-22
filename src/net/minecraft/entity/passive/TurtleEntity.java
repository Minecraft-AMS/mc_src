/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TurtleEntity
extends AnimalEntity {
    private static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Boolean> HAS_EGG = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> DIGGING_SAND = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> TRAVEL_POS = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Boolean> LAND_BOUND = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ACTIVELY_TRAVELLING = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int sandDiggingCounter;
    public static final Predicate<LivingEntity> BABY_TURTLE_ON_LAND_FILTER = livingEntity -> livingEntity.isBaby() && !livingEntity.isTouchingWater();

    public TurtleEntity(EntityType<? extends TurtleEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.moveControl = new TurtleMoveControl(this);
        this.stepHeight = 1.0f;
    }

    public void setHomePos(BlockPos pos) {
        this.dataTracker.set(HOME_POS, pos);
    }

    private BlockPos getHomePos() {
        return this.dataTracker.get(HOME_POS);
    }

    private void setTravelPos(BlockPos pos) {
        this.dataTracker.set(TRAVEL_POS, pos);
    }

    private BlockPos getTravelPos() {
        return this.dataTracker.get(TRAVEL_POS);
    }

    public boolean hasEgg() {
        return this.dataTracker.get(HAS_EGG);
    }

    private void setHasEgg(boolean hasEgg) {
        this.dataTracker.set(HAS_EGG, hasEgg);
    }

    public boolean isDiggingSand() {
        return this.dataTracker.get(DIGGING_SAND);
    }

    private void setDiggingSand(boolean diggingSand) {
        this.sandDiggingCounter = diggingSand ? 1 : 0;
        this.dataTracker.set(DIGGING_SAND, diggingSand);
    }

    private boolean isLandBound() {
        return this.dataTracker.get(LAND_BOUND);
    }

    private void setLandBound(boolean landBound) {
        this.dataTracker.set(LAND_BOUND, landBound);
    }

    private boolean isActivelyTravelling() {
        return this.dataTracker.get(ACTIVELY_TRAVELLING);
    }

    private void setActivelyTravelling(boolean travelling) {
        this.dataTracker.set(ACTIVELY_TRAVELLING, travelling);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HOME_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(HAS_EGG, false);
        this.dataTracker.startTracking(TRAVEL_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(LAND_BOUND, false);
        this.dataTracker.startTracking(ACTIVELY_TRAVELLING, false);
        this.dataTracker.startTracking(DIGGING_SAND, false);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("HomePosX", this.getHomePos().getX());
        tag.putInt("HomePosY", this.getHomePos().getY());
        tag.putInt("HomePosZ", this.getHomePos().getZ());
        tag.putBoolean("HasEgg", this.hasEgg());
        tag.putInt("TravelPosX", this.getTravelPos().getX());
        tag.putInt("TravelPosY", this.getTravelPos().getY());
        tag.putInt("TravelPosZ", this.getTravelPos().getZ());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        int i = tag.getInt("HomePosX");
        int j = tag.getInt("HomePosY");
        int k = tag.getInt("HomePosZ");
        this.setHomePos(new BlockPos(i, j, k));
        super.readCustomDataFromTag(tag);
        this.setHasEgg(tag.getBoolean("HasEgg"));
        int l = tag.getInt("TravelPosX");
        int m = tag.getInt("TravelPosY");
        int n = tag.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos(l, m, n));
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.setHomePos(new BlockPos(this));
        this.setTravelPos(BlockPos.ORIGIN);
        return super.initialize(world, difficulty, spawnType, entityData, entityTag);
    }

    public static boolean method_20671(EntityType<TurtleEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        return blockPos.getY() < iWorld.getSeaLevel() + 4 && iWorld.getBlockState(blockPos.down()).getBlock() == Blocks.SAND && iWorld.getLightLevel(blockPos, 0) > 8;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new TurtleEscapeDangerGoal(this, 1.2));
        this.goalSelector.add(1, new MateGoal(this, 1.0));
        this.goalSelector.add(1, new LayEggGoal(this, 1.0));
        this.goalSelector.add(2, new ApproachFoodHoldingPlayerGoal(this, 1.1, Blocks.SEAGRASS.asItem()));
        this.goalSelector.add(3, new WanderInWaterGoal(this, 1.0));
        this.goalSelector.add(4, new GoHomeGoal(this, 1.0));
        this.goalSelector.add(7, new TravelGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(9, new WanderOnLandGoal(this, 1.0, 100));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public boolean canBreatheInWater() {
        return true;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.AQUATIC;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 200;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (!this.isTouchingWater() && this.onGround && !this.isBaby()) {
            return SoundEvents.ENTITY_TURTLE_AMBIENT_LAND;
        }
        return super.getAmbientSound();
    }

    @Override
    protected void playSwimSound(float volume) {
        super.playSwimSound(volume * 1.5f);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_TURTLE_SWIM;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        if (this.isBaby()) {
            return SoundEvents.ENTITY_TURTLE_HURT_BABY;
        }
        return SoundEvents.ENTITY_TURTLE_HURT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        if (this.isBaby()) {
            return SoundEvents.ENTITY_TURTLE_DEATH_BABY;
        }
        return SoundEvents.ENTITY_TURTLE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundEvent soundEvent = this.isBaby() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
        this.playSound(soundEvent, 0.15f, 1.0f);
    }

    @Override
    public boolean canEat() {
        return super.canEat() && !this.hasEgg();
    }

    @Override
    protected float calculateNextStepSoundDistance() {
        return this.distanceTraveled + 0.15f;
    }

    @Override
    public float getScaleFactor() {
        return this.isBaby() ? 0.3f : 1.0f;
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new TurtleSwimNavigation(this, world);
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity mate) {
        return EntityType.TURTLE.create(this.world);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Blocks.SEAGRASS.asItem();
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, CollisionView world) {
        if (!this.isLandBound() && world.getFluidState(pos).matches(FluidTags.WATER)) {
            return 10.0f;
        }
        if (world.getBlockState(pos.down()).getBlock() == Blocks.SAND) {
            return 10.0f;
        }
        return world.getBrightness(pos) - 0.5f;
    }

    @Override
    public void tickMovement() {
        BlockPos blockPos;
        super.tickMovement();
        if (this.isAlive() && this.isDiggingSand() && this.sandDiggingCounter >= 1 && this.sandDiggingCounter % 5 == 0 && this.world.getBlockState((blockPos = new BlockPos(this)).down()).getBlock() == Blocks.SAND) {
            this.world.playLevelEvent(2001, blockPos, Block.getRawIdFromState(Blocks.SAND.getDefaultState()));
        }
    }

    @Override
    protected void onGrowUp() {
        super.onGrowUp();
        if (!this.isBaby() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropItem(Items.SCUTE, 1);
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(0.1f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (!(this.getTarget() != null || this.isLandBound() && this.getHomePos().isWithinDistance(this.getPos(), 20.0))) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    public void onStruckByLightning(LightningEntity lightning) {
        this.damage(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }

    static class TurtleSwimNavigation
    extends SwimNavigation {
        TurtleSwimNavigation(TurtleEntity owner, World world) {
            super(owner, world);
        }

        @Override
        protected boolean isAtValidPosition() {
            return true;
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int i) {
            return new PathNodeNavigator(new AmphibiousPathNodeMaker(), i);
        }

        @Override
        public boolean isValidPosition(BlockPos pos) {
            TurtleEntity turtleEntity;
            if (this.entity instanceof TurtleEntity && (turtleEntity = (TurtleEntity)this.entity).isActivelyTravelling()) {
                return this.world.getBlockState(pos).getBlock() == Blocks.WATER;
            }
            return !this.world.getBlockState(pos.down()).isAir();
        }
    }

    static class TurtleMoveControl
    extends MoveControl {
        private final TurtleEntity turtle;

        TurtleMoveControl(TurtleEntity turtle) {
            super(turtle);
            this.turtle = turtle;
        }

        private void updateVelocity() {
            if (this.turtle.isTouchingWater()) {
                this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, 0.005, 0.0));
                if (!this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 16.0)) {
                    this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0f, 0.08f));
                }
                if (this.turtle.isBaby()) {
                    this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 3.0f, 0.06f));
                }
            } else if (this.turtle.onGround) {
                this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0f, 0.06f));
            }
        }

        @Override
        public void tick() {
            this.updateVelocity();
            if (this.state != MoveControl.State.MOVE_TO || this.turtle.getNavigation().isIdle()) {
                this.turtle.setMovementSpeed(0.0f);
                return;
            }
            double d = this.targetX - this.turtle.x;
            double e = this.targetY - this.turtle.y;
            double f = this.targetZ - this.turtle.z;
            double g = MathHelper.sqrt(d * d + e * e + f * f);
            float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f;
            this.turtle.field_6283 = this.turtle.yaw = this.changeAngle(this.turtle.yaw, h, 90.0f);
            float i = (float)(this.speed * this.turtle.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
            this.turtle.setMovementSpeed(MathHelper.lerp(0.125f, this.turtle.getMovementSpeed(), i));
            this.turtle.setVelocity(this.turtle.getVelocity().add(0.0, (double)this.turtle.getMovementSpeed() * (e /= g) * 0.1, 0.0));
        }
    }

    static class WanderInWaterGoal
    extends MoveToTargetPosGoal {
        private final TurtleEntity turtle;

        private WanderInWaterGoal(TurtleEntity turtle, double speed) {
            super(turtle, turtle.isBaby() ? 2.0 : speed, 24);
            this.turtle = turtle;
            this.lowestY = -1;
        }

        @Override
        public boolean shouldContinue() {
            return !this.turtle.isTouchingWater() && this.tryingTime <= 1200 && this.isTargetPos(this.turtle.world, this.targetPos);
        }

        @Override
        public boolean canStart() {
            if (this.turtle.isBaby() && !this.turtle.isTouchingWater()) {
                return super.canStart();
            }
            if (!(this.turtle.isLandBound() || this.turtle.isTouchingWater() || this.turtle.hasEgg())) {
                return super.canStart();
            }
            return false;
        }

        @Override
        public boolean shouldResetPath() {
            return this.tryingTime % 160 == 0;
        }

        @Override
        protected boolean isTargetPos(CollisionView world, BlockPos pos) {
            Block block = world.getBlockState(pos).getBlock();
            return block == Blocks.WATER;
        }
    }

    static class WanderOnLandGoal
    extends WanderAroundGoal {
        private final TurtleEntity turtle;

        private WanderOnLandGoal(TurtleEntity turtle, double speed, int chance) {
            super(turtle, speed, chance);
            this.turtle = turtle;
        }

        @Override
        public boolean canStart() {
            if (!(this.mob.isTouchingWater() || this.turtle.isLandBound() || this.turtle.hasEgg())) {
                return super.canStart();
            }
            return false;
        }
    }

    static class LayEggGoal
    extends MoveToTargetPosGoal {
        private final TurtleEntity turtle;

        LayEggGoal(TurtleEntity turtle, double speed) {
            super(turtle, speed, 16);
            this.turtle = turtle;
        }

        @Override
        public boolean canStart() {
            if (this.turtle.hasEgg() && this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 9.0)) {
                return super.canStart();
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.turtle.hasEgg() && this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 9.0);
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos blockPos = new BlockPos(this.turtle);
            if (!this.turtle.isTouchingWater() && this.hasReached()) {
                if (this.turtle.sandDiggingCounter < 1) {
                    this.turtle.setDiggingSand(true);
                } else if (this.turtle.sandDiggingCounter > 200) {
                    World world = this.turtle.world;
                    world.playSound(null, blockPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3f, 0.9f + world.random.nextFloat() * 0.2f);
                    world.setBlockState(this.targetPos.up(), (BlockState)Blocks.TURTLE_EGG.getDefaultState().with(TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setDiggingSand(false);
                    this.turtle.setLoveTicks(600);
                }
                if (this.turtle.isDiggingSand()) {
                    this.turtle.sandDiggingCounter++;
                }
            }
        }

        @Override
        protected boolean isTargetPos(CollisionView world, BlockPos pos) {
            if (!world.isAir(pos.up())) {
                return false;
            }
            Block block = world.getBlockState(pos).getBlock();
            return block == Blocks.SAND;
        }
    }

    static class MateGoal
    extends AnimalMateGoal {
        private final TurtleEntity turtle;

        MateGoal(TurtleEntity turtle, double speed) {
            super(turtle, speed);
            this.turtle = turtle;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !this.turtle.hasEgg();
        }

        @Override
        protected void breed() {
            ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
            if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
                serverPlayerEntity = this.mate.getLovingPlayer();
            }
            if (serverPlayerEntity != null) {
                serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
                Criterions.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.mate, null);
            }
            this.turtle.setHasEgg(true);
            this.animal.resetLoveTicks();
            this.mate.resetLoveTicks();
            Random random = this.animal.getRandom();
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.x, this.animal.y, this.animal.z, random.nextInt(7) + 1));
            }
        }
    }

    static class ApproachFoodHoldingPlayerGoal
    extends Goal {
        private static final TargetPredicate CLOSE_ENTITY_PREDICATE = new TargetPredicate().setBaseMaxDistance(10.0).includeTeammates().includeInvulnerable();
        private final TurtleEntity turtle;
        private final double speed;
        private PlayerEntity targetPlayer;
        private int cooldown;
        private final Set<Item> attractiveItems;

        ApproachFoodHoldingPlayerGoal(TurtleEntity turtle, double speed, Item attractiveItem) {
            this.turtle = turtle;
            this.speed = speed;
            this.attractiveItems = Sets.newHashSet((Object[])new Item[]{attractiveItem});
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            }
            this.targetPlayer = this.turtle.world.getClosestPlayer(CLOSE_ENTITY_PREDICATE, this.turtle);
            if (this.targetPlayer == null) {
                return false;
            }
            return this.isAttractive(this.targetPlayer.getMainHandStack()) || this.isAttractive(this.targetPlayer.getOffHandStack());
        }

        private boolean isAttractive(ItemStack stack) {
            return this.attractiveItems.contains(stack.getItem());
        }

        @Override
        public boolean shouldContinue() {
            return this.canStart();
        }

        @Override
        public void stop() {
            this.targetPlayer = null;
            this.turtle.getNavigation().stop();
            this.cooldown = 100;
        }

        @Override
        public void tick() {
            this.turtle.getLookControl().lookAt(this.targetPlayer, this.turtle.method_5986() + 20, this.turtle.getLookPitchSpeed());
            if (this.turtle.squaredDistanceTo(this.targetPlayer) < 6.25) {
                this.turtle.getNavigation().stop();
            } else {
                this.turtle.getNavigation().startMovingTo(this.targetPlayer, this.speed);
            }
        }
    }

    static class GoHomeGoal
    extends Goal {
        private final TurtleEntity turtle;
        private final double speed;
        private boolean noPath;
        private int homeReachingTryTicks;

        GoHomeGoal(TurtleEntity turtle, double speed) {
            this.turtle = turtle;
            this.speed = speed;
        }

        @Override
        public boolean canStart() {
            if (this.turtle.isBaby()) {
                return false;
            }
            if (this.turtle.hasEgg()) {
                return true;
            }
            if (this.turtle.getRandom().nextInt(700) != 0) {
                return false;
            }
            return !this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 64.0);
        }

        @Override
        public void start() {
            this.turtle.setLandBound(true);
            this.noPath = false;
            this.homeReachingTryTicks = 0;
        }

        @Override
        public void stop() {
            this.turtle.setLandBound(false);
        }

        @Override
        public boolean shouldContinue() {
            return !this.turtle.getHomePos().isWithinDistance(this.turtle.getPos(), 7.0) && !this.noPath && this.homeReachingTryTicks <= 600;
        }

        @Override
        public void tick() {
            BlockPos blockPos = this.turtle.getHomePos();
            boolean bl = blockPos.isWithinDistance(this.turtle.getPos(), 16.0);
            if (bl) {
                ++this.homeReachingTryTicks;
            }
            if (this.turtle.getNavigation().isIdle()) {
                Vec3d vec3d = TargetFinder.method_6377(this.turtle, 16, 3, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 0.3141592741012573);
                if (vec3d == null) {
                    vec3d = TargetFinder.method_6373(this.turtle, 8, 7, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                }
                if (vec3d != null && !bl && this.turtle.world.getBlockState(new BlockPos(vec3d)).getBlock() != Blocks.WATER) {
                    vec3d = TargetFinder.method_6373(this.turtle, 16, 5, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                }
                if (vec3d == null) {
                    this.noPath = true;
                    return;
                }
                this.turtle.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
            }
        }
    }

    static class TravelGoal
    extends Goal {
        private final TurtleEntity turtle;
        private final double speed;
        private boolean noPath;

        TravelGoal(TurtleEntity turtle, double speed) {
            this.turtle = turtle;
            this.speed = speed;
        }

        @Override
        public boolean canStart() {
            return !this.turtle.isLandBound() && !this.turtle.hasEgg() && this.turtle.isTouchingWater();
        }

        @Override
        public void start() {
            int i = 512;
            int j = 4;
            Random random = this.turtle.random;
            int k = random.nextInt(1025) - 512;
            int l = random.nextInt(9) - 4;
            int m = random.nextInt(1025) - 512;
            if ((double)l + this.turtle.y > (double)(this.turtle.world.getSeaLevel() - 1)) {
                l = 0;
            }
            BlockPos blockPos = new BlockPos((double)k + this.turtle.x, (double)l + this.turtle.y, (double)m + this.turtle.z);
            this.turtle.setTravelPos(blockPos);
            this.turtle.setActivelyTravelling(true);
            this.noPath = false;
        }

        @Override
        public void tick() {
            if (this.turtle.getNavigation().isIdle()) {
                BlockPos blockPos = this.turtle.getTravelPos();
                Vec3d vec3d = TargetFinder.method_6377(this.turtle, 16, 3, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 0.3141592741012573);
                if (vec3d == null) {
                    vec3d = TargetFinder.method_6373(this.turtle, 8, 7, new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                }
                if (vec3d != null) {
                    int i = MathHelper.floor(vec3d.x);
                    int j = MathHelper.floor(vec3d.z);
                    int k = 34;
                    if (!this.turtle.world.isAreaLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
                        vec3d = null;
                    }
                }
                if (vec3d == null) {
                    this.noPath = true;
                    return;
                }
                this.turtle.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
            }
        }

        @Override
        public boolean shouldContinue() {
            return !this.turtle.getNavigation().isIdle() && !this.noPath && !this.turtle.isLandBound() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }

        @Override
        public void stop() {
            this.turtle.setActivelyTravelling(false);
            super.stop();
        }
    }

    static class TurtleEscapeDangerGoal
    extends EscapeDangerGoal {
        TurtleEscapeDangerGoal(TurtleEntity turtle, double speed) {
            super(turtle, speed);
        }

        @Override
        public boolean canStart() {
            if (this.mob.getAttacker() == null && !this.mob.isOnFire()) {
                return false;
            }
            BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob, 7, 4);
            if (blockPos != null) {
                this.targetX = blockPos.getX();
                this.targetY = blockPos.getY();
                this.targetZ = blockPos.getZ();
                return true;
            }
            return this.findTarget();
        }
    }
}

