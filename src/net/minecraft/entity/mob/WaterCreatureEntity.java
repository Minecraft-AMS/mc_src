/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.CollisionView;
import net.minecraft.world.World;

public abstract class WaterCreatureEntity
extends MobEntityWithAi {
    protected WaterCreatureEntity(EntityType<? extends WaterCreatureEntity> type, World world) {
        super((EntityType<? extends MobEntityWithAi>)type, world);
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
    public boolean canSpawn(CollisionView world) {
        return world.intersectsEntities(this);
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }

    @Override
    protected int getCurrentExperience(PlayerEntity player) {
        return 1 + this.world.random.nextInt(3);
    }

    protected void tickWaterBreathingAir(int air) {
        if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
            this.setAir(air - 1);
            if (this.getAir() == -20) {
                this.setAir(0);
                this.damage(DamageSource.DROWN, 2.0f);
            }
        } else {
            this.setAir(300);
        }
    }

    @Override
    public void baseTick() {
        int i = this.getAir();
        super.baseTick();
        this.tickWaterBreathingAir(i);
    }

    @Override
    public boolean canFly() {
        return false;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }
}
