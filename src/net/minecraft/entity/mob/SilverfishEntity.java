/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InfestedBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CollisionView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SilverfishEntity
extends HostileEntity {
    private CallForHelpGoal callForHelpGoal;

    public SilverfishEntity(EntityType<? extends SilverfishEntity> entityType, World world) {
        super((EntityType<? extends HostileEntity>)entityType, world);
    }

    @Override
    protected void initGoals() {
        this.callForHelpGoal = new CallForHelpGoal(this);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(3, this.callForHelpGoal);
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(5, new WanderAndInfestGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
    }

    @Override
    public double getHeightOffset() {
        return 0.1;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.1f;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SILVERFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SILVERFISH_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if ((source instanceof EntityDamageSource || source == DamageSource.MAGIC) && this.callForHelpGoal != null) {
            this.callForHelpGoal.onHurt();
        }
        return super.damage(source, amount);
    }

    @Override
    public void tick() {
        this.field_6283 = this.yaw;
        super.tick();
    }

    @Override
    public void setYaw(float yaw) {
        this.yaw = yaw;
        super.setYaw(yaw);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, CollisionView world) {
        if (InfestedBlock.isInfestable(world.getBlockState(pos.down()))) {
            return 10.0f;
        }
        return super.getPathfindingFavor(pos, world);
    }

    public static boolean method_20684(EntityType<SilverfishEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        if (SilverfishEntity.method_20681(entityType, iWorld, spawnType, blockPos, random)) {
            PlayerEntity playerEntity = iWorld.getClosestPlayer((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 5.0, true);
            return playerEntity == null;
        }
        return false;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    static class WanderAndInfestGoal
    extends WanderAroundGoal {
        private Direction direction;
        private boolean canInfest;

        public WanderAndInfestGoal(SilverfishEntity silverfish) {
            super(silverfish, 1.0, 10);
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.mob.getTarget() != null) {
                return false;
            }
            if (!this.mob.getNavigation().isIdle()) {
                return false;
            }
            Random random = this.mob.getRandom();
            if (this.mob.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) && random.nextInt(10) == 0) {
                this.direction = Direction.random(random);
                BlockPos blockPos = new BlockPos(this.mob.x, this.mob.y + 0.5, this.mob.z).offset(this.direction);
                BlockState blockState = this.mob.world.getBlockState(blockPos);
                if (InfestedBlock.isInfestable(blockState)) {
                    this.canInfest = true;
                    return true;
                }
            }
            this.canInfest = false;
            return super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            if (this.canInfest) {
                return false;
            }
            return super.shouldContinue();
        }

        @Override
        public void start() {
            if (!this.canInfest) {
                super.start();
                return;
            }
            World iWorld = this.mob.world;
            BlockPos blockPos = new BlockPos(this.mob.x, this.mob.y + 0.5, this.mob.z).offset(this.direction);
            BlockState blockState = iWorld.getBlockState(blockPos);
            if (InfestedBlock.isInfestable(blockState)) {
                iWorld.setBlockState(blockPos, InfestedBlock.fromRegularBlock(blockState.getBlock()), 3);
                this.mob.playSpawnEffects();
                this.mob.remove();
            }
        }
    }

    static class CallForHelpGoal
    extends Goal {
        private final SilverfishEntity silverfish;
        private int delay;

        public CallForHelpGoal(SilverfishEntity silverfish) {
            this.silverfish = silverfish;
        }

        public void onHurt() {
            if (this.delay == 0) {
                this.delay = 20;
            }
        }

        @Override
        public boolean canStart() {
            return this.delay > 0;
        }

        @Override
        public void tick() {
            --this.delay;
            if (this.delay <= 0) {
                World world = this.silverfish.world;
                Random random = this.silverfish.getRandom();
                BlockPos blockPos = new BlockPos(this.silverfish);
                int i = 0;
                block0: while (i <= 5 && i >= -5) {
                    int j = 0;
                    while (j <= 10 && j >= -10) {
                        int k = 0;
                        while (k <= 10 && k >= -10) {
                            BlockPos blockPos2 = blockPos.add(j, i, k);
                            BlockState blockState = world.getBlockState(blockPos2);
                            Block block = blockState.getBlock();
                            if (block instanceof InfestedBlock) {
                                if (world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                                    world.breakBlock(blockPos2, true);
                                } else {
                                    world.setBlockState(blockPos2, ((InfestedBlock)block).getRegularBlock().getDefaultState(), 3);
                                }
                                if (random.nextBoolean()) break block0;
                            }
                            k = (k <= 0 ? 1 : 0) - k;
                        }
                        j = (j <= 0 ? 1 : 0) - j;
                    }
                    i = (i <= 0 ? 1 : 0) - i;
                }
            }
        }
    }
}
