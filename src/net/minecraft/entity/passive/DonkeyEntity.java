/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class DonkeyEntity
extends AbstractDonkeyEntity {
    public DonkeyEntity(EntityType<? extends DonkeyEntity> entityType, World world) {
        super((EntityType<? extends AbstractDonkeyEntity>)entityType, world);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_DONKEY_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_DONKEY_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        super.getHurtSound(source);
        return SoundEvents.ENTITY_DONKEY_HURT;
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        }
        if (other instanceof DonkeyEntity || other instanceof HorseEntity) {
            return this.canBreed() && ((HorseBaseEntity)other).canBreed();
        }
        return false;
    }

    @Override
    public PassiveEntity createChild(PassiveEntity mate) {
        EntityType<AbstractDonkeyEntity> entityType = mate instanceof HorseEntity ? EntityType.MULE : EntityType.DONKEY;
        HorseBaseEntity horseBaseEntity = entityType.create(this.world);
        this.setChildAttributes(mate, horseBaseEntity);
        return horseBaseEntity;
    }
}

