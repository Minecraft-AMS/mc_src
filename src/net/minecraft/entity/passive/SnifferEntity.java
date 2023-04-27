/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.entity.passive;

import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SnifferEntity
extends AnimalEntity {
    private static final int field_42656 = 1700;
    private static final int field_42657 = 6000;
    private static final int field_42658 = 30;
    private static final int field_42659 = 120;
    private static final int field_42661 = 48000;
    private static final TrackedData<State> STATE = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.SNIFFER_STATE);
    private static final TrackedData<Integer> FINISH_DIG_TIME = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public final AnimationState feelingHappyAnimationState = new AnimationState();
    public final AnimationState scentingAnimationState = new AnimationState();
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState diggingAnimationState = new AnimationState();
    public final AnimationState risingAnimationState = new AnimationState();
    public final AnimationState babyGrowthAnimationState = new AnimationState();

    public static DefaultAttributeContainer.Builder createSnifferAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1f).add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0);
    }

    public SnifferEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.dataTracker.startTracking(STATE, State.IDLING);
        this.dataTracker.startTracking(FINISH_DIG_TIME, 0);
        this.getNavigation().setCanSwim(true);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
        this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, -1.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_CAUTIOUS, -1.0f);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return this.getDimensions((EntityPose)pose).height * 0.6f;
    }

    @Override
    public void onStartPathfinding() {
        super.onStartPathfinding();
        if (this.isOnFire() || this.isTouchingWater()) {
            this.setPathfindingPenalty(PathNodeType.WATER, 0.0f);
        }
    }

    @Override
    public void onFinishPathfinding() {
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0f);
    }

    public boolean isPanicking() {
        return this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_PANICKING).isPresent();
    }

    public boolean isSearching() {
        return this.getState() == State.SEARCHING;
    }

    public boolean isTempted() {
        return this.brain.getOptionalRegisteredMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
    }

    public boolean canTryToDig() {
        return !this.isTempted() && !this.isPanicking() && !this.isTouchingWater() && !this.isInLove() && this.isOnGround() && !this.hasVehicle();
    }

    public boolean isDiggingOrSearching() {
        return this.getState() == State.DIGGING || this.getState() == State.SEARCHING;
    }

    private BlockPos getDigPos() {
        Vec3d vec3d = this.getDigLocation();
        return BlockPos.ofFloored(vec3d.getX(), this.getY() + (double)0.2f, vec3d.getZ());
    }

    private Vec3d getDigLocation() {
        return this.getPos().add(this.getRotationVecClient().multiply(2.25));
    }

    private State getState() {
        return this.dataTracker.get(STATE);
    }

    private SnifferEntity setState(State state) {
        this.dataTracker.set(STATE, state);
        return this;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (STATE.equals(data)) {
            State state = this.getState();
            this.stopAnimations();
            switch (state) {
                case SCENTING: {
                    this.scentingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case SNIFFING: {
                    this.sniffingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case DIGGING: {
                    this.diggingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case RISING: {
                    this.risingAnimationState.startIfNotRunning(this.age);
                    break;
                }
                case FEELING_HAPPY: {
                    this.feelingHappyAnimationState.startIfNotRunning(this.age);
                }
            }
        }
        super.onTrackedDataSet(data);
    }

    private void stopAnimations() {
        this.diggingAnimationState.stop();
        this.sniffingAnimationState.stop();
        this.risingAnimationState.stop();
        this.feelingHappyAnimationState.stop();
        this.scentingAnimationState.stop();
    }

    public SnifferEntity startState(State state) {
        switch (state) {
            case IDLING: {
                this.setState(State.IDLING);
                break;
            }
            case SCENTING: {
                this.setState(State.SCENTING).playScentingSound();
                break;
            }
            case SNIFFING: {
                this.playSound(SoundEvents.ENTITY_SNIFFER_SNIFFING, 1.0f, 1.0f);
                this.setState(State.SNIFFING);
                break;
            }
            case SEARCHING: {
                this.setState(State.SEARCHING);
                break;
            }
            case DIGGING: {
                this.setState(State.DIGGING).setDigging();
                break;
            }
            case RISING: {
                this.playSound(SoundEvents.ENTITY_SNIFFER_DIGGING_STOP, 1.0f, 1.0f);
                this.setState(State.RISING);
                break;
            }
            case FEELING_HAPPY: {
                this.playSound(SoundEvents.ENTITY_SNIFFER_HAPPY, 1.0f, 1.0f);
                this.setState(State.FEELING_HAPPY);
            }
        }
        return this;
    }

    private SnifferEntity playScentingSound() {
        this.playSound(SoundEvents.ENTITY_SNIFFER_SCENTING, 1.0f, this.isBaby() ? 1.3f : 1.0f);
        return this;
    }

    private SnifferEntity setDigging() {
        this.dataTracker.set(FINISH_DIG_TIME, this.age + 120);
        this.getWorld().sendEntityStatus(this, (byte)63);
        return this;
    }

    public SnifferEntity finishDigging(boolean explored) {
        if (explored) {
            this.addExploredPosition(this.getSteppingPos());
        }
        return this;
    }

    Optional<BlockPos> findSniffingTargetPos() {
        return IntStream.range(0, 5).mapToObj(i -> FuzzyTargeting.find(this, 10 + 2 * i, 3)).filter(Objects::nonNull).map(BlockPos::ofFloored).filter(pos -> this.getWorld().getWorldBorder().contains((BlockPos)pos)).map(BlockPos::down).filter(this::isDiggable).findFirst();
    }

    boolean canDig() {
        return !this.isPanicking() && !this.isTempted() && !this.isBaby() && !this.isTouchingWater() && this.isOnGround() && !this.hasVehicle() && this.isDiggable(this.getDigPos().down());
    }

    private boolean isDiggable(BlockPos pos) {
        return this.getWorld().getBlockState(pos).isIn(BlockTags.SNIFFER_DIGGABLE_BLOCK) && this.getExploredPositions().noneMatch(globalPos -> GlobalPos.create(this.getWorld().getRegistryKey(), pos).equals(globalPos)) && Optional.ofNullable(this.getNavigation().findPathTo(pos, 1)).map(Path::reachesTarget).orElse(false) != false;
    }

    private void dropSeeds() {
        if (this.getWorld().isClient() || this.dataTracker.get(FINISH_DIG_TIME) != this.age) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)this.getWorld();
        LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(LootTables.SNIFFER_DIGGING_GAMEPLAY);
        LootContext.Builder builder = new LootContext.Builder(serverWorld).parameter(LootContextParameters.ORIGIN, this.getDigLocation()).parameter(LootContextParameters.THIS_ENTITY, this).random(this.random);
        ObjectArrayList<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.GIFT));
        BlockPos blockPos = this.getDigPos();
        for (ItemStack itemStack : list) {
            ItemEntity itemEntity = new ItemEntity(serverWorld, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack);
            itemEntity.setToDefaultPickupDelay();
            serverWorld.spawnEntity(itemEntity);
        }
        this.playSound(SoundEvents.ENTITY_SNIFFER_DROP_SEED, 1.0f, 1.0f);
    }

    private SnifferEntity spawnDiggingParticles(AnimationState diggingAnimationState) {
        boolean bl;
        boolean bl2 = bl = diggingAnimationState.getTimeRunning() > 1700L && diggingAnimationState.getTimeRunning() < 6000L;
        if (bl) {
            BlockState blockState = this.getSteppingBlockState();
            BlockPos blockPos = this.getDigPos();
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                for (int i = 0; i < 30; ++i) {
                    Vec3d vec3d = Vec3d.ofCenter(blockPos).add(0.0, -0.65f, 0.0);
                    this.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
                }
                if (this.age % 10 == 0) {
                    this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), blockState.getSoundGroup().getHitSound(), this.getSoundCategory(), 0.5f, 0.5f, false);
                }
            }
        }
        if (this.age % 10 == 0) {
            this.getWorld().emitGameEvent(GameEvent.ENTITY_SHAKE, this.getDigPos(), GameEvent.Emitter.of(this));
        }
        return this;
    }

    private SnifferEntity addExploredPosition(BlockPos pos) {
        List list = this.getExploredPositions().limit(20L).collect(Collectors.toList());
        list.add(0, GlobalPos.create(this.getWorld().getRegistryKey(), pos));
        this.getBrain().remember(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, list);
        return this;
    }

    private Stream<GlobalPos> getExploredPositions() {
        return this.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
    }

    @Override
    protected void jump() {
        double e;
        super.jump();
        double d = this.moveControl.getSpeed();
        if (d > 0.0 && (e = this.getVelocity().horizontalLengthSquared()) < 0.01) {
            this.updateVelocity(0.1f, new Vec3d(0.0, 0.0, 1.0));
        }
    }

    @Override
    public void breed(ServerWorld world, AnimalEntity other) {
        ItemStack itemStack = new ItemStack(Items.SNIFFER_EGG);
        ItemEntity itemEntity = new ItemEntity(world, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), itemStack);
        itemEntity.setToDefaultPickupDelay();
        this.breed(world, other, null);
        this.playSound(SoundEvents.BLOCK_SNIFFER_EGG_PLOP, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 0.5f);
        world.spawnEntity(itemEntity);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.startState(State.IDLING);
        super.onDeath(damageSource);
    }

    @Override
    public void tick() {
        switch (this.getState()) {
            case DIGGING: {
                this.spawnDiggingParticles(this.diggingAnimationState).dropSeeds();
                break;
            }
            case SEARCHING: {
                this.playSearchingSound();
            }
        }
        this.babyGrowthAnimationState.setRunning(this.isBaby(), this.age);
        super.tick();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        boolean bl = this.isBreedingItem(itemStack);
        ActionResult actionResult = super.interactMob(player, hand);
        if (actionResult.isAccepted() && bl) {
            this.getWorld().playSoundFromEntity(null, this, this.getEatSound(itemStack), SoundCategory.NEUTRAL, 1.0f, MathHelper.nextBetween(this.getWorld().random, 0.8f, 1.2f));
        }
        return actionResult;
    }

    @Override
    public double getMountedHeightOffset() {
        return 1.8;
    }

    @Override
    public float getNameLabelHeight() {
        return super.getNameLabelHeight() + 0.3f;
    }

    private void playSearchingSound() {
        if (this.getWorld().isClient() && this.age % 20 == 0) {
            this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SNIFFER_SEARCHING, this.getSoundCategory(), 1.0f, 1.0f, false);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_SNIFFER_STEP, 0.15f, 1.0f);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return SoundEvents.ENTITY_SNIFFER_EAT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return Set.of(State.DIGGING, State.SEARCHING).contains((Object)this.getState()) ? null : SoundEvents.ENTITY_SNIFFER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SNIFFER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SNIFFER_DEATH;
    }

    @Override
    public int getMaxHeadRotation() {
        return 50;
    }

    @Override
    public void setBaby(boolean baby) {
        this.setBreedingAge(baby ? -48000 : 0);
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return EntityType.SNIFFER.create(world);
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other instanceof SnifferEntity) {
            SnifferEntity snifferEntity = (SnifferEntity)other;
            Set<State> set = Set.of(State.IDLING, State.SCENTING, State.FEELING_HAPPY);
            return set.contains((Object)this.getState()) && set.contains((Object)snifferEntity.getState()) && super.canBreedWith(other);
        }
        return false;
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return super.getVisibilityBoundingBox().expand(0.6f);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ItemTags.SNIFFER_FOOD);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return SnifferBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    public Brain<SnifferEntity> getBrain() {
        return super.getBrain();
    }

    protected Brain.Profile<SnifferEntity> createBrainProfile() {
        return Brain.createProfile(SnifferBrain.MEMORY_MODULES, SnifferBrain.SENSORS);
    }

    @Override
    protected void mobTick() {
        this.getWorld().getProfiler().push("snifferBrain");
        this.getBrain().tick((ServerWorld)this.getWorld(), this);
        this.getWorld().getProfiler().swap("snifferActivityUpdate");
        SnifferBrain.updateActivities(this);
        this.getWorld().getProfiler().pop();
        super.mobTick();
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public static final class State
    extends Enum<State> {
        public static final /* enum */ State IDLING = new State();
        public static final /* enum */ State FEELING_HAPPY = new State();
        public static final /* enum */ State SCENTING = new State();
        public static final /* enum */ State SNIFFING = new State();
        public static final /* enum */ State SEARCHING = new State();
        public static final /* enum */ State DIGGING = new State();
        public static final /* enum */ State RISING = new State();
        private static final /* synthetic */ State[] field_42672;

        public static State[] values() {
            return (State[])field_42672.clone();
        }

        public static State valueOf(String string) {
            return Enum.valueOf(State.class, string);
        }

        private static /* synthetic */ State[] method_49151() {
            return new State[]{IDLING, FEELING_HAPPY, SCENTING, SNIFFING, SEARCHING, DIGGING, RISING};
        }

        static {
            field_42672 = State.method_49151();
        }
    }
}

