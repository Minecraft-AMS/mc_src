/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public class MeleeAttackTask
extends Task<MobEntity> {
    private final int interval;

    public MeleeAttackTask(int interval) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT), MemoryModuleType.ATTACK_COOLING_DOWN, (Object)((Object)MemoryModuleState.VALUE_ABSENT)));
        this.interval = interval;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
        LivingEntity livingEntity = this.method_25944(mobEntity);
        return !this.method_25942(mobEntity) && LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity) && LookTargetUtil.method_25941(mobEntity, livingEntity);
    }

    private boolean method_25942(MobEntity mobEntity) {
        return mobEntity.isHolding(item -> item instanceof RangedWeaponItem && mobEntity.canUseRangedWeapon((RangedWeaponItem)item));
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        LivingEntity livingEntity = this.method_25944(mobEntity);
        LookTargetUtil.lookAt(mobEntity, livingEntity);
        mobEntity.swingHand(Hand.MAIN_HAND);
        mobEntity.tryAttack(livingEntity);
        mobEntity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.interval);
    }

    private LivingEntity method_25944(MobEntity mobEntity) {
        return mobEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }
}
