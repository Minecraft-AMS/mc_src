/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;

public class WardenAttackablesSensor
extends NearestLivingEntitiesSensor<WardenEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.copyOf((Iterable)Iterables.concat(super.getOutputMemoryModules(), List.of(MemoryModuleType.NEAREST_ATTACKABLE)));
    }

    @Override
    protected void sense(ServerWorld serverWorld, WardenEntity wardenEntity) {
        super.sense(serverWorld, wardenEntity);
        WardenAttackablesSensor.findNearestTarget(wardenEntity, entity -> entity.getType() == EntityType.PLAYER).or(() -> WardenAttackablesSensor.findNearestTarget(wardenEntity, livingEntity -> livingEntity.getType() != EntityType.PLAYER)).ifPresentOrElse(entity -> wardenEntity.getBrain().remember(MemoryModuleType.NEAREST_ATTACKABLE, entity), () -> wardenEntity.getBrain().forget(MemoryModuleType.NEAREST_ATTACKABLE));
    }

    private static Optional<LivingEntity> findNearestTarget(WardenEntity warden, Predicate<LivingEntity> targetPredicate) {
        return warden.getBrain().getOptionalMemory(MemoryModuleType.MOBS).stream().flatMap(Collection::stream).filter(warden::isValidTarget).filter(targetPredicate).findFirst();
    }

    @Override
    protected int getHorizontalExpansion() {
        return 24;
    }

    @Override
    protected int getHeightExpansion() {
        return 24;
    }
}
