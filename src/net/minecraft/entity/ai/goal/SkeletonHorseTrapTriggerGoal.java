/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;

public class SkeletonHorseTrapTriggerGoal
extends Goal {
    private final SkeletonHorseEntity skeletonHorse;

    public SkeletonHorseTrapTriggerGoal(SkeletonHorseEntity skeletonHorse) {
        this.skeletonHorse = skeletonHorse;
    }

    @Override
    public boolean canStart() {
        return this.skeletonHorse.world.isPlayerInRange(this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z, 10.0);
    }

    @Override
    public void tick() {
        LocalDifficulty localDifficulty = this.skeletonHorse.world.getLocalDifficulty(new BlockPos(this.skeletonHorse));
        this.skeletonHorse.setTrapped(false);
        this.skeletonHorse.setTame(true);
        this.skeletonHorse.setBreedingAge(0);
        ((ServerWorld)this.skeletonHorse.world).addLightning(new LightningEntity(this.skeletonHorse.world, this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z, true));
        SkeletonEntity skeletonEntity = this.getSkeleton(localDifficulty, this.skeletonHorse);
        skeletonEntity.startRiding(this.skeletonHorse);
        for (int i = 0; i < 3; ++i) {
            HorseBaseEntity horseBaseEntity = this.getHorse(localDifficulty);
            SkeletonEntity skeletonEntity2 = this.getSkeleton(localDifficulty, horseBaseEntity);
            skeletonEntity2.startRiding(horseBaseEntity);
            horseBaseEntity.addVelocity(this.skeletonHorse.getRandom().nextGaussian() * 0.5, 0.0, this.skeletonHorse.getRandom().nextGaussian() * 0.5);
        }
    }

    private HorseBaseEntity getHorse(LocalDifficulty localDifficulty) {
        SkeletonHorseEntity skeletonHorseEntity = EntityType.SKELETON_HORSE.create(this.skeletonHorse.world);
        skeletonHorseEntity.initialize(this.skeletonHorse.world, localDifficulty, SpawnType.TRIGGERED, null, null);
        skeletonHorseEntity.updatePosition(this.skeletonHorse.x, this.skeletonHorse.y, this.skeletonHorse.z);
        skeletonHorseEntity.timeUntilRegen = 60;
        skeletonHorseEntity.setPersistent();
        skeletonHorseEntity.setTame(true);
        skeletonHorseEntity.setBreedingAge(0);
        skeletonHorseEntity.world.spawnEntity(skeletonHorseEntity);
        return skeletonHorseEntity;
    }

    private SkeletonEntity getSkeleton(LocalDifficulty localDifficulty, HorseBaseEntity vehicle) {
        SkeletonEntity skeletonEntity = EntityType.SKELETON.create(vehicle.world);
        skeletonEntity.initialize(vehicle.world, localDifficulty, SpawnType.TRIGGERED, null, null);
        skeletonEntity.updatePosition(vehicle.x, vehicle.y, vehicle.z);
        skeletonEntity.timeUntilRegen = 60;
        skeletonEntity.setPersistent();
        if (skeletonEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            skeletonEntity.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }
        skeletonEntity.equipStack(EquipmentSlot.MAINHAND, EnchantmentHelper.enchant(skeletonEntity.getRandom(), skeletonEntity.getMainHandStack(), (int)(5.0f + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRandom().nextInt(18)), false));
        skeletonEntity.equipStack(EquipmentSlot.HEAD, EnchantmentHelper.enchant(skeletonEntity.getRandom(), skeletonEntity.getEquippedStack(EquipmentSlot.HEAD), (int)(5.0f + localDifficulty.getClampedLocalDifficulty() * (float)skeletonEntity.getRandom().nextInt(18)), false));
        skeletonEntity.world.spawnEntity(skeletonEntity);
        return skeletonEntity;
    }
}
