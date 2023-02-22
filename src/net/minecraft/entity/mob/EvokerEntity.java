/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.mob;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EvokerEntity
extends SpellcastingIllagerEntity {
    private SheepEntity wololoTarget;

    public EvokerEntity(EntityType<? extends EvokerEntity> entityType, World world) {
        super((EntityType<? extends SpellcastingIllagerEntity>)entityType, world);
        this.experiencePoints = 10;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtTargetOrWololoTarget());
        this.goalSelector.add(2, new FleeEntityGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 0.6, 1.0));
        this.goalSelector.add(4, new SummonVexGoal());
        this.goalSelector.add(5, new ConjureFangsGoal());
        this.goalSelector.add(6, new WololoGoal());
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, false).setMaxTimeWithoutVisibility(300));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, false));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(12.0);
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_EVOKER_CELEBRATE;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
    }

    @Override
    protected void mobTick() {
        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof VexEntity) {
            return this.isTeammate(((VexEntity)other).getOwner());
        }
        if (other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    private void setWololoTarget(@Nullable SheepEntity sheep) {
        this.wololoTarget = sheep;
    }

    @Nullable
    private SheepEntity getWololoTarget() {
        return this.wololoTarget;
    }

    @Override
    protected SoundEvent getCastSpellSound() {
        return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {
    }

    public class WololoGoal
    extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate purpleSheepPredicate;

        public WololoGoal() {
            super(EvokerEntity.this);
            this.purpleSheepPredicate = new TargetPredicate().setBaseMaxDistance(16.0).includeInvulnerable().setPredicate(livingEntity -> ((SheepEntity)livingEntity).getColor() == DyeColor.BLUE);
        }

        @Override
        public boolean canStart() {
            if (EvokerEntity.this.getTarget() != null) {
                return false;
            }
            if (EvokerEntity.this.isSpellcasting()) {
                return false;
            }
            if (EvokerEntity.this.age < this.startTime) {
                return false;
            }
            if (!EvokerEntity.this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                return false;
            }
            List<SheepEntity> list = EvokerEntity.this.world.getTargets(SheepEntity.class, this.purpleSheepPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0, 4.0, 16.0));
            if (list.isEmpty()) {
                return false;
            }
            EvokerEntity.this.setWololoTarget(list.get(EvokerEntity.this.random.nextInt(list.size())));
            return true;
        }

        @Override
        public boolean shouldContinue() {
            return EvokerEntity.this.getWololoTarget() != null && this.spellCooldown > 0;
        }

        @Override
        public void stop() {
            super.stop();
            EvokerEntity.this.setWololoTarget(null);
        }

        @Override
        protected void castSpell() {
            SheepEntity sheepEntity = EvokerEntity.this.getWololoTarget();
            if (sheepEntity != null && sheepEntity.isAlive()) {
                sheepEntity.setColor(DyeColor.RED);
            }
        }

        @Override
        protected int getInitialCooldown() {
            return 40;
        }

        @Override
        protected int getSpellTicks() {
            return 60;
        }

        @Override
        protected int startTimeDelay() {
            return 140;
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.WOLOLO;
        }
    }

    class SummonVexGoal
    extends SpellcastingIllagerEntity.CastSpellGoal {
        private final TargetPredicate closeVexPredicate;

        private SummonVexGoal() {
            super(EvokerEntity.this);
            this.closeVexPredicate = new TargetPredicate().setBaseMaxDistance(16.0).includeHidden().ignoreDistanceScalingFactor().includeInvulnerable().includeTeammates();
        }

        @Override
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            }
            int i = EvokerEntity.this.world.getTargets(VexEntity.class, this.closeVexPredicate, EvokerEntity.this, EvokerEntity.this.getBoundingBox().expand(16.0)).size();
            return EvokerEntity.this.random.nextInt(8) + 1 > i;
        }

        @Override
        protected int getSpellTicks() {
            return 100;
        }

        @Override
        protected int startTimeDelay() {
            return 340;
        }

        @Override
        protected void castSpell() {
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos = new BlockPos(EvokerEntity.this).add(-2 + EvokerEntity.this.random.nextInt(5), 1, -2 + EvokerEntity.this.random.nextInt(5));
                VexEntity vexEntity = EntityType.VEX.create(EvokerEntity.this.world);
                vexEntity.refreshPositionAndAngles(blockPos, 0.0f, 0.0f);
                vexEntity.initialize(EvokerEntity.this.world, EvokerEntity.this.world.getLocalDifficulty(blockPos), SpawnType.MOB_SUMMONED, null, null);
                vexEntity.setOwner(EvokerEntity.this);
                vexEntity.setBounds(blockPos);
                vexEntity.setLifeTicks(20 * (30 + EvokerEntity.this.random.nextInt(90)));
                EvokerEntity.this.world.spawnEntity(vexEntity);
            }
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.SUMMON_VEX;
        }
    }

    class ConjureFangsGoal
    extends SpellcastingIllagerEntity.CastSpellGoal {
        private ConjureFangsGoal() {
            super(EvokerEntity.this);
        }

        @Override
        protected int getSpellTicks() {
            return 40;
        }

        @Override
        protected int startTimeDelay() {
            return 100;
        }

        @Override
        protected void castSpell() {
            LivingEntity livingEntity = EvokerEntity.this.getTarget();
            double d = Math.min(livingEntity.y, EvokerEntity.this.y);
            double e = Math.max(livingEntity.y, EvokerEntity.this.y) + 1.0;
            float f = (float)MathHelper.atan2(livingEntity.z - EvokerEntity.this.z, livingEntity.x - EvokerEntity.this.x);
            if (EvokerEntity.this.squaredDistanceTo(livingEntity) < 9.0) {
                float g;
                int i;
                for (i = 0; i < 5; ++i) {
                    g = f + (float)i * (float)Math.PI * 0.4f;
                    this.conjureFangs(EvokerEntity.this.x + (double)MathHelper.cos(g) * 1.5, EvokerEntity.this.z + (double)MathHelper.sin(g) * 1.5, d, e, g, 0);
                }
                for (i = 0; i < 8; ++i) {
                    g = f + (float)i * (float)Math.PI * 2.0f / 8.0f + 1.2566371f;
                    this.conjureFangs(EvokerEntity.this.x + (double)MathHelper.cos(g) * 2.5, EvokerEntity.this.z + (double)MathHelper.sin(g) * 2.5, d, e, g, 3);
                }
            } else {
                for (int i = 0; i < 16; ++i) {
                    double h = 1.25 * (double)(i + 1);
                    int j = 1 * i;
                    this.conjureFangs(EvokerEntity.this.x + (double)MathHelper.cos(f) * h, EvokerEntity.this.z + (double)MathHelper.sin(f) * h, d, e, f, j);
                }
            }
        }

        private void conjureFangs(double x, double z, double maxY, double y, float f, int warmup) {
            BlockPos blockPos = new BlockPos(x, y, z);
            boolean bl = false;
            double d = 0.0;
            do {
                BlockState blockState2;
                VoxelShape voxelShape;
                BlockPos blockPos2;
                BlockState blockState;
                if (!(blockState = EvokerEntity.this.world.getBlockState(blockPos2 = blockPos.down())).isSideSolidFullSquare(EvokerEntity.this.world, blockPos2, Direction.UP)) continue;
                if (!EvokerEntity.this.world.isAir(blockPos) && !(voxelShape = (blockState2 = EvokerEntity.this.world.getBlockState(blockPos)).getCollisionShape(EvokerEntity.this.world, blockPos)).isEmpty()) {
                    d = voxelShape.getMaximum(Direction.Axis.Y);
                }
                bl = true;
                break;
            } while ((blockPos = blockPos.down()).getY() >= MathHelper.floor(maxY) - 1);
            if (bl) {
                EvokerEntity.this.world.spawnEntity(new EvokerFangsEntity(EvokerEntity.this.world, x, (double)blockPos.getY() + d, z, f, warmup, EvokerEntity.this));
            }
        }

        @Override
        protected SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellcastingIllagerEntity.Spell getSpell() {
            return SpellcastingIllagerEntity.Spell.FANGS;
        }
    }

    class LookAtTargetOrWololoTarget
    extends SpellcastingIllagerEntity.LookAtTargetGoal {
        private LookAtTargetOrWololoTarget() {
            super(EvokerEntity.this);
        }

        @Override
        public void tick() {
            if (EvokerEntity.this.getTarget() != null) {
                EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getTarget(), EvokerEntity.this.method_5986(), EvokerEntity.this.getLookPitchSpeed());
            } else if (EvokerEntity.this.getWololoTarget() != null) {
                EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getWololoTarget(), EvokerEntity.this.method_5986(), EvokerEntity.this.getLookPitchSpeed());
            }
        }
    }
}
