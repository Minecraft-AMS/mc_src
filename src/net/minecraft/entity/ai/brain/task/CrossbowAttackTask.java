/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class CrossbowAttackTask<E extends MobEntity, T extends LivingEntity>
extends Task<E> {
    private static final int RUN_TIME = 1200;
    private int chargingCooldown;
    private CrossbowState state = CrossbowState.UNCHARGED;

    public CrossbowAttackTask() {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), 1200);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
        LivingEntity livingEntity = CrossbowAttackTask.getAttackTarget(mobEntity);
        return ((LivingEntity)mobEntity).isHolding(Items.CROSSBOW) && LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity) && LookTargetUtil.isTargetWithinAttackRange(mobEntity, livingEntity, 0);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, E mobEntity, long l) {
        return ((LivingEntity)mobEntity).getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.shouldRun(serverWorld, mobEntity);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, E mobEntity, long l) {
        LivingEntity livingEntity = CrossbowAttackTask.getAttackTarget(mobEntity);
        this.setLookTarget((MobEntity)mobEntity, livingEntity);
        this.tickState(mobEntity, livingEntity);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, E mobEntity, long l) {
        if (((LivingEntity)mobEntity).isUsingItem()) {
            ((LivingEntity)mobEntity).clearActiveItem();
        }
        if (((LivingEntity)mobEntity).isHolding(Items.CROSSBOW)) {
            ((CrossbowUser)mobEntity).setCharging(false);
            CrossbowItem.setCharged(((LivingEntity)mobEntity).getActiveItem(), false);
        }
    }

    private void tickState(E entity, LivingEntity target) {
        if (this.state == CrossbowState.UNCHARGED) {
            ((LivingEntity)entity).setCurrentHand(ProjectileUtil.getHandPossiblyHolding(entity, Items.CROSSBOW));
            this.state = CrossbowState.CHARGING;
            ((CrossbowUser)entity).setCharging(true);
        } else if (this.state == CrossbowState.CHARGING) {
            ItemStack itemStack;
            int i;
            if (!((LivingEntity)entity).isUsingItem()) {
                this.state = CrossbowState.UNCHARGED;
            }
            if ((i = ((LivingEntity)entity).getItemUseTime()) >= CrossbowItem.getPullTime(itemStack = ((LivingEntity)entity).getActiveItem())) {
                ((LivingEntity)entity).stopUsingItem();
                this.state = CrossbowState.CHARGED;
                this.chargingCooldown = 20 + ((LivingEntity)entity).getRandom().nextInt(20);
                ((CrossbowUser)entity).setCharging(false);
            }
        } else if (this.state == CrossbowState.CHARGED) {
            --this.chargingCooldown;
            if (this.chargingCooldown == 0) {
                this.state = CrossbowState.READY_TO_ATTACK;
            }
        } else if (this.state == CrossbowState.READY_TO_ATTACK) {
            ((RangedAttackMob)entity).attack(target, 1.0f);
            ItemStack itemStack2 = ((LivingEntity)entity).getStackInHand(ProjectileUtil.getHandPossiblyHolding(entity, Items.CROSSBOW));
            CrossbowItem.setCharged(itemStack2, false);
            this.state = CrossbowState.UNCHARGED;
        }
    }

    private void setLookTarget(MobEntity entity, LivingEntity target) {
        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
    }

    private static LivingEntity getAttackTarget(LivingEntity entity) {
        return entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (E)((MobEntity)entity), time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (E)((MobEntity)entity), time);
    }

    static final class CrossbowState
    extends Enum<CrossbowState> {
        public static final /* enum */ CrossbowState UNCHARGED = new CrossbowState();
        public static final /* enum */ CrossbowState CHARGING = new CrossbowState();
        public static final /* enum */ CrossbowState CHARGED = new CrossbowState();
        public static final /* enum */ CrossbowState READY_TO_ATTACK = new CrossbowState();
        private static final /* synthetic */ CrossbowState[] field_22299;

        public static CrossbowState[] values() {
            return (CrossbowState[])field_22299.clone();
        }

        public static CrossbowState valueOf(String string) {
            return Enum.valueOf(CrossbowState.class, string);
        }

        private static /* synthetic */ CrossbowState[] method_36616() {
            return new CrossbowState[]{UNCHARGED, CHARGING, CHARGED, READY_TO_ATTACK};
        }

        static {
            field_22299 = CrossbowState.method_36616();
        }
    }
}

