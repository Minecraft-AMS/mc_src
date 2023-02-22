/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class VillagerHostilesSensor
extends Sensor<LivingEntity> {
    private static final ImmutableMap<EntityType<?>, Float> SQUARED_DISTANCES_FOR_DANGER = ImmutableMap.builder().put(EntityType.DROWNED, (Object)Float.valueOf(8.0f)).put(EntityType.EVOKER, (Object)Float.valueOf(12.0f)).put(EntityType.HUSK, (Object)Float.valueOf(8.0f)).put(EntityType.ILLUSIONER, (Object)Float.valueOf(12.0f)).put(EntityType.PILLAGER, (Object)Float.valueOf(15.0f)).put(EntityType.RAVAGER, (Object)Float.valueOf(12.0f)).put(EntityType.VEX, (Object)Float.valueOf(8.0f)).put(EntityType.VINDICATOR, (Object)Float.valueOf(10.0f)).put(EntityType.ZOGLIN, (Object)Float.valueOf(10.0f)).put(EntityType.ZOMBIE, (Object)Float.valueOf(8.0f)).put(EntityType.ZOMBIE_VILLAGER, (Object)Float.valueOf(8.0f)).build();

    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        entity.getBrain().remember(MemoryModuleType.NEAREST_HOSTILE, this.getNearestHostile(entity));
    }

    private Optional<LivingEntity> getNearestHostile(LivingEntity entity) {
        return this.getVisibleMobs(entity).flatMap(list -> list.stream().filter(this::isHostile).filter(livingEntity2 -> this.isCloseEnoughForDanger(entity, (LivingEntity)livingEntity2)).min((livingEntity2, livingEntity3) -> this.compareDistances(entity, (LivingEntity)livingEntity2, (LivingEntity)livingEntity3)));
    }

    private Optional<List<LivingEntity>> getVisibleMobs(LivingEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
    }

    private int compareDistances(LivingEntity entity, LivingEntity hostile1, LivingEntity hostile2) {
        return MathHelper.floor(hostile1.squaredDistanceTo(entity) - hostile2.squaredDistanceTo(entity));
    }

    private boolean isCloseEnoughForDanger(LivingEntity villager, LivingEntity target) {
        float f = ((Float)SQUARED_DISTANCES_FOR_DANGER.get(target.getType())).floatValue();
        return target.squaredDistanceTo(villager) <= (double)(f * f);
    }

    private boolean isHostile(LivingEntity entity) {
        return SQUARED_DISTANCES_FOR_DANGER.containsKey(entity.getType());
    }
}

