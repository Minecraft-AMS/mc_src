/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CollisionView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class RabbitEntity
extends AnimalEntity {
    private static final TrackedData<Integer> RABBIT_TYPE = DataTracker.registerData(RabbitEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Identifier KILLER_BUNNY = new Identifier("killer_bunny");
    private int jumpTicks;
    private int jumpDuration;
    private boolean lastOnGround;
    private int ticksUntilJump;
    private int moreCarrotTicks;

    public RabbitEntity(EntityType<? extends RabbitEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.jumpControl = new RabbitJumpControl(this);
        this.moveControl = new RabbitMoveControl(this);
        this.setSpeed(0.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 2.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(3, new TemptGoal((MobEntityWithAi)this, 1.0, Ingredient.ofItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
        this.goalSelector.add(4, new FleeGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 2.2, 2.2));
        this.goalSelector.add(4, new FleeGoal<WolfEntity>(this, WolfEntity.class, 10.0f, 2.2, 2.2));
        this.goalSelector.add(4, new FleeGoal<HostileEntity>(this, HostileEntity.class, 4.0f, 2.2, 2.2));
        this.goalSelector.add(5, new EatCarrotCropGoal(this));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
    }

    @Override
    protected float getJumpVelocity() {
        if (this.horizontalCollision || this.moveControl.isMoving() && this.moveControl.getTargetY() > this.y + 0.5) {
            return 0.5f;
        }
        Path path = this.navigation.getCurrentPath();
        if (path != null && path.getCurrentNodeIndex() < path.getLength()) {
            Vec3d vec3d = path.getNodePosition(this);
            if (vec3d.y > this.y + 0.5) {
                return 0.5f;
            }
        }
        if (this.moveControl.getSpeed() <= 0.6) {
            return 0.2f;
        }
        return 0.3f;
    }

    @Override
    protected void jump() {
        double e;
        super.jump();
        double d = this.moveControl.getSpeed();
        if (d > 0.0 && (e = RabbitEntity.squaredHorizontalLength(this.getVelocity())) < 0.01) {
            this.updateVelocity(0.1f, new Vec3d(0.0, 0.0, 1.0));
        }
        if (!this.world.isClient) {
            this.world.sendEntityStatus(this, (byte)1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float method_6605(float f) {
        if (this.jumpDuration == 0) {
            return 0.0f;
        }
        return ((float)this.jumpTicks + f) / (float)this.jumpDuration;
    }

    public void setSpeed(double speed) {
        this.getNavigation().setSpeed(speed);
        this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), speed);
    }

    @Override
    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
        if (jumping) {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 0.8f);
        }
    }

    public void startJump() {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RABBIT_TYPE, 0);
    }

    @Override
    public void mobTick() {
        if (this.ticksUntilJump > 0) {
            --this.ticksUntilJump;
        }
        if (this.moreCarrotTicks > 0) {
            this.moreCarrotTicks -= this.random.nextInt(3);
            if (this.moreCarrotTicks < 0) {
                this.moreCarrotTicks = 0;
            }
        }
        if (this.onGround) {
            RabbitJumpControl rabbitJumpControl;
            LivingEntity livingEntity;
            if (!this.lastOnGround) {
                this.setJumping(false);
                this.method_6619();
            }
            if (this.getRabbitType() == 99 && this.ticksUntilJump == 0 && (livingEntity = this.getTarget()) != null && this.squaredDistanceTo(livingEntity) < 16.0) {
                this.lookTowards(livingEntity.x, livingEntity.z);
                this.moveControl.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, this.moveControl.getSpeed());
                this.startJump();
                this.lastOnGround = true;
            }
            if (!(rabbitJumpControl = (RabbitJumpControl)this.jumpControl).isActive()) {
                if (this.moveControl.isMoving() && this.ticksUntilJump == 0) {
                    Path path = this.navigation.getCurrentPath();
                    Vec3d vec3d = new Vec3d(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ());
                    if (path != null && path.getCurrentNodeIndex() < path.getLength()) {
                        vec3d = path.getNodePosition(this);
                    }
                    this.lookTowards(vec3d.x, vec3d.z);
                    this.startJump();
                }
            } else if (!rabbitJumpControl.method_6625()) {
                this.method_6611();
            }
        }
        this.lastOnGround = this.onGround;
    }

    @Override
    public void attemptSprintingParticles() {
    }

    private void lookTowards(double x, double z) {
        this.yaw = (float)(MathHelper.atan2(z - this.z, x - this.x) * 57.2957763671875) - 90.0f;
    }

    private void method_6611() {
        ((RabbitJumpControl)this.jumpControl).method_6623(true);
    }

    private void method_6621() {
        ((RabbitJumpControl)this.jumpControl).method_6623(false);
    }

    private void scheduleJump() {
        this.ticksUntilJump = this.moveControl.getSpeed() < 2.2 ? 10 : 1;
    }

    private void method_6619() {
        this.scheduleJump();
        this.method_6621();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.jumpTicks != this.jumpDuration) {
            ++this.jumpTicks;
        } else if (this.jumpDuration != 0) {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(3.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("RabbitType", this.getRabbitType());
        tag.putInt("MoreCarrotTicks", this.moreCarrotTicks);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setRabbitType(tag.getInt("RabbitType"));
        this.moreCarrotTicks = tag.getInt("MoreCarrotTicks");
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_RABBIT_JUMP;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (this.getRabbitType() == 99) {
            this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            return target.damage(DamageSource.mob(this), 8.0f);
        }
        return target.damage(DamageSource.mob(this), 3.0f);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        return super.damage(source, amount);
    }

    private boolean isBreedingItem(Item item) {
        return item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Blocks.DANDELION.asItem();
    }

    @Override
    public RabbitEntity createChild(PassiveEntity passiveEntity) {
        RabbitEntity rabbitEntity = EntityType.RABBIT.create(this.world);
        int i = this.chooseType(this.world);
        if (this.random.nextInt(20) != 0) {
            i = passiveEntity instanceof RabbitEntity && this.random.nextBoolean() ? ((RabbitEntity)passiveEntity).getRabbitType() : this.getRabbitType();
        }
        rabbitEntity.setRabbitType(i);
        return rabbitEntity;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return this.isBreedingItem(stack.getItem());
    }

    public int getRabbitType() {
        return this.dataTracker.get(RABBIT_TYPE);
    }

    public void setRabbitType(int rabbitType) {
        if (rabbitType == 99) {
            this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(8.0);
            this.goalSelector.add(4, new RabbitAttackGoal(this));
            this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
            this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
            this.targetSelector.add(2, new FollowTargetGoal<WolfEntity>((MobEntity)this, WolfEntity.class, true));
            if (!this.hasCustomName()) {
                this.setCustomName(new TranslatableText(Util.createTranslationKey("entity", KILLER_BUNNY), new Object[0]));
            }
        }
        this.dataTracker.set(RABBIT_TYPE, rabbitType);
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        entityData = super.initialize(world, difficulty, spawnType, entityData, entityTag);
        int i = this.chooseType(world);
        boolean bl = false;
        if (entityData instanceof RabbitEntityData) {
            i = ((RabbitEntityData)entityData).type;
            bl = true;
        } else {
            entityData = new RabbitEntityData(i);
        }
        this.setRabbitType(i);
        if (bl) {
            this.setBreedingAge(-24000);
        }
        return entityData;
    }

    private int chooseType(IWorld world) {
        Biome biome = world.getBiome(new BlockPos(this));
        int i = this.random.nextInt(100);
        if (biome.getPrecipitation() == Biome.Precipitation.SNOW) {
            return i < 80 ? 1 : 3;
        }
        if (biome.getCategory() == Biome.Category.DESERT) {
            return 4;
        }
        return i < 50 ? 0 : (i < 90 ? 5 : 2);
    }

    public static boolean method_20669(EntityType<RabbitEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        Block block = iWorld.getBlockState(blockPos.down()).getBlock();
        return (block == Blocks.GRASS_BLOCK || block == Blocks.SNOW || block == Blocks.SAND) && iWorld.getLightLevel(blockPos, 0) > 8;
    }

    private boolean wantsCarrots() {
        return this.moreCarrotTicks == 0;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 1) {
            this.spawnSprintingParticles();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity mate) {
        return this.createChild(mate);
    }

    static class RabbitAttackGoal
    extends MeleeAttackGoal {
        public RabbitAttackGoal(RabbitEntity rabbit) {
            super(rabbit, 1.4, true);
        }

        @Override
        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
            return 4.0f + entity.getWidth();
        }
    }

    static class EscapeDangerGoal
    extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
        private final RabbitEntity rabbit;

        public EscapeDangerGoal(RabbitEntity rabbit, double speed) {
            super(rabbit, speed);
            this.rabbit = rabbit;
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.setSpeed(this.speed);
        }
    }

    static class EatCarrotCropGoal
    extends MoveToTargetPosGoal {
        private final RabbitEntity rabbit;
        private boolean wantsCarrots;
        private boolean field_6861;

        public EatCarrotCropGoal(RabbitEntity rabbit) {
            super(rabbit, 0.7f, 16);
            this.rabbit = rabbit;
        }

        @Override
        public boolean canStart() {
            if (this.cooldown <= 0) {
                if (!this.rabbit.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                    return false;
                }
                this.field_6861 = false;
                this.wantsCarrots = this.rabbit.wantsCarrots();
                this.wantsCarrots = true;
            }
            return super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return this.field_6861 && super.shouldContinue();
        }

        @Override
        public void tick() {
            super.tick();
            this.rabbit.getLookControl().lookAt((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, 10.0f, this.rabbit.getLookPitchSpeed());
            if (this.hasReached()) {
                World world = this.rabbit.world;
                BlockPos blockPos = this.targetPos.up();
                BlockState blockState = world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (this.field_6861 && block instanceof CarrotsBlock) {
                    Integer integer = blockState.get(CarrotsBlock.AGE);
                    if (integer == 0) {
                        world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
                        world.breakBlock(blockPos, true);
                    } else {
                        world.setBlockState(blockPos, (BlockState)blockState.with(CarrotsBlock.AGE, integer - 1), 2);
                        world.playLevelEvent(2001, blockPos, Block.getRawIdFromState(blockState));
                    }
                    this.rabbit.moreCarrotTicks = 40;
                }
                this.field_6861 = false;
                this.cooldown = 10;
            }
        }

        @Override
        protected boolean isTargetPos(CollisionView world, BlockPos pos) {
            BlockState blockState;
            Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.FARMLAND && this.wantsCarrots && !this.field_6861 && (block = (blockState = world.getBlockState(pos = pos.up())).getBlock()) instanceof CarrotsBlock && ((CarrotsBlock)block).isMature(blockState)) {
                this.field_6861 = true;
                return true;
            }
            return false;
        }
    }

    static class FleeGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final RabbitEntity rabbit;

        public FleeGoal(RabbitEntity rabbit, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
            super(rabbit, fleeFromType, distance, slowSpeed, fastSpeed);
            this.rabbit = rabbit;
        }

        @Override
        public boolean canStart() {
            return this.rabbit.getRabbitType() != 99 && super.canStart();
        }
    }

    static class RabbitMoveControl
    extends MoveControl {
        private final RabbitEntity rabbit;
        private double field_6858;

        public RabbitMoveControl(RabbitEntity owner) {
            super(owner);
            this.rabbit = owner;
        }

        @Override
        public void tick() {
            if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
                this.rabbit.setSpeed(0.0);
            } else if (this.isMoving()) {
                this.rabbit.setSpeed(this.field_6858);
            }
            super.tick();
        }

        @Override
        public void moveTo(double x, double y, double z, double speed) {
            if (this.rabbit.isTouchingWater()) {
                speed = 1.5;
            }
            super.moveTo(x, y, z, speed);
            if (speed > 0.0) {
                this.field_6858 = speed;
            }
        }
    }

    public class RabbitJumpControl
    extends JumpControl {
        private final RabbitEntity rabbit;
        private boolean field_6856;

        public RabbitJumpControl(RabbitEntity owner) {
            super(owner);
            this.rabbit = owner;
        }

        public boolean isActive() {
            return this.active;
        }

        public boolean method_6625() {
            return this.field_6856;
        }

        public void method_6623(boolean bl) {
            this.field_6856 = bl;
        }

        @Override
        public void tick() {
            if (this.active) {
                this.rabbit.startJump();
                this.active = false;
            }
        }
    }

    public static class RabbitEntityData
    implements EntityData {
        public final int type;

        public RabbitEntityData(int type) {
            this.type = type;
        }
    }
}

