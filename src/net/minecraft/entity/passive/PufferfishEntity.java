/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity
extends FishEntity {
    private static final TrackedData<Integer> PUFF_STATE = DataTracker.registerData(PufferfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private int field_6833;
    private int field_6832;
    private static final Predicate<LivingEntity> field_6834 = livingEntity -> {
        if (livingEntity == null) {
            return false;
        }
        if (livingEntity instanceof PlayerEntity && (livingEntity.isSpectator() || ((PlayerEntity)livingEntity).isCreative())) {
            return false;
        }
        return livingEntity.getGroup() != EntityGroup.AQUATIC;
    };

    public PufferfishEntity(EntityType<? extends PufferfishEntity> entityType, World world) {
        super((EntityType<? extends FishEntity>)entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PUFF_STATE, 0);
    }

    public int getPuffState() {
        return this.dataTracker.get(PUFF_STATE);
    }

    public void setPuffState(int puffState) {
        this.dataTracker.set(PUFF_STATE, puffState);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (PUFF_STATE.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("PuffState", this.getPuffState());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setPuffState(tag.getInt("PuffState"));
    }

    @Override
    protected ItemStack getFishBucketItem() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new class_1455(this));
    }

    @Override
    public void tick() {
        if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
            if (this.field_6833 > 0) {
                if (this.getPuffState() == 0) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(1);
                } else if (this.field_6833 > 40 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(2);
                }
                ++this.field_6833;
            } else if (this.getPuffState() != 0) {
                if (this.field_6832 > 60 && this.getPuffState() == 2) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(1);
                } else if (this.field_6832 > 100 && this.getPuffState() == 1) {
                    this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
                    this.setPuffState(0);
                }
                ++this.field_6832;
            }
        }
        super.tick();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.isAlive() && this.getPuffState() > 0) {
            List<LivingEntity> list = this.world.getEntities(MobEntity.class, this.getBoundingBox().expand(0.3), field_6834);
            for (MobEntity mobEntity : list) {
                if (!mobEntity.isAlive()) continue;
                this.sting(mobEntity);
            }
        }
    }

    private void sting(MobEntity mob) {
        int i = this.getPuffState();
        if (mob.damage(DamageSource.mob(this), 1 + i)) {
            mob.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0));
            this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0f, 1.0f);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        int i = this.getPuffState();
        if (player instanceof ServerPlayerEntity && i > 0 && player.damage(DamageSource.mob(this), 1 + i)) {
            ((ServerPlayerEntity)player).networkHandler.sendPacket(new GameStateChangeS2CPacket(9, 0.0f));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PUFFER_FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(PufferfishEntity.getScaleForPuffState(this.getPuffState()));
    }

    private static float getScaleForPuffState(int puffState) {
        switch (puffState) {
            case 1: {
                return 0.7f;
            }
            case 0: {
                return 0.5f;
            }
        }
        return 1.0f;
    }

    static class class_1455
    extends Goal {
        private final PufferfishEntity pufferfish;

        public class_1455(PufferfishEntity pufferfish) {
            this.pufferfish = pufferfish;
        }

        @Override
        public boolean canStart() {
            List<LivingEntity> list = this.pufferfish.world.getEntities(LivingEntity.class, this.pufferfish.getBoundingBox().expand(2.0), field_6834);
            return !list.isEmpty();
        }

        @Override
        public void start() {
            this.pufferfish.field_6833 = 1;
            this.pufferfish.field_6832 = 0;
        }

        @Override
        public void stop() {
            this.pufferfish.field_6833 = 0;
        }

        @Override
        public boolean shouldContinue() {
            List<LivingEntity> list = this.pufferfish.world.getEntities(LivingEntity.class, this.pufferfish.getBoundingBox().expand(2.0), field_6834);
            return !list.isEmpty();
        }
    }
}
